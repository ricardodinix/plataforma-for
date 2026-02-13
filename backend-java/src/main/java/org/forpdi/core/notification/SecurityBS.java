package org.forpdi.core.notification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.forpdi.core.company.CompanyDomain;
import org.forpdi.core.event.Current;
import org.forpdi.core.user.User;
import org.forpdi.core.user.auth.UserSession;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.boilerplate.HibernateBusiness;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Business Service para Segurança da Informação.
 * Gerencia logs de segurança, detecção de tentativas suspeitas e validação de acessos.
 */
@RequestScoped
public class SecurityBS extends HibernateBusiness {

	@Inject
	@Current
	private CompanyDomain domain;
	@Inject
	private UserSession userSession;
	@Inject
	private HttpServletRequest request;

	private static final int MAX_FAILED_ATTEMPTS = 5;
	private static final int LOCKOUT_MINUTES = 30;

	/**
	 * Registrar evento de segurança.
	 */
	public void logSecurityEvent(String eventType, User user, String details) {
		try {
			SecurityAuditLog log = new SecurityAuditLog();
			log.setEventType(eventType);
			log.setUser(user);
			log.setCreation(new Date());
			log.setDetails(details);

			if (this.domain != null && this.domain.getCompany() != null) {
				log.setCompany(this.domain.getCompany());
			}
			if (this.request != null) {
				log.setIpAddress(getClientIp());
				log.setUserAgent(truncate(this.request.getHeader("User-Agent"), 500));
				log.setRequestPath(truncate(this.request.getRequestURI(), 255));
			}

			this.persist(log);
		} catch (Exception e) {
			LOGGER.error("Erro ao registrar evento de segurança", e);
		}
	}

	/**
	 * Registrar tentativa de login falha.
	 */
	public void logFailedLogin(String username) {
		try {
			SecurityAuditLog log = new SecurityAuditLog();
			log.setEventType("LOGIN_FAILED");
			log.setUsername(username);
			log.setCreation(new Date());

			if (this.request != null) {
				String ip = getClientIp();
				log.setIpAddress(ip);
				log.setUserAgent(truncate(this.request.getHeader("User-Agent"), 500));
				log.setSuspicious(isIpSuspicious(ip));
			}

			if (this.domain != null && this.domain.getCompany() != null) {
				log.setCompany(this.domain.getCompany());
			}

			this.persist(log);
		} catch (Exception e) {
			LOGGER.error("Erro ao registrar falha de login", e);
		}
	}

	/**
	 * Verificar se um IP tem tentativas suspeitas recentes.
	 */
	public boolean isIpSuspicious(String ipAddress) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -LOCKOUT_MINUTES);
		Date threshold = cal.getTime();

		Criteria criteria = this.dao.newCriteria(SecurityAuditLog.class);
		criteria.add(Restrictions.eq("ipAddress", ipAddress));
		criteria.add(Restrictions.eq("eventType", "LOGIN_FAILED"));
		criteria.add(Restrictions.ge("creation", threshold));
		criteria.setProjection(Projections.countDistinct("id"));

		Long count = (Long) criteria.uniqueResult();
		return count != null && count >= MAX_FAILED_ATTEMPTS;
	}

	/**
	 * Verificar se o usuário atual tem acesso ao recurso.
	 */
	public boolean validateAccess(int requiredLevel) {
		if (this.userSession == null || !this.userSession.isLogged()) {
			logSecurityEvent("ACCESS_DENIED", null, "Usuário não autenticado");
			return false;
		}
		if (this.userSession.getAccessLevel() < requiredLevel) {
			logSecurityEvent("ACCESS_DENIED", this.userSession.getUser(),
					"Nível de acesso insuficiente. Requerido: " + requiredLevel +
					", Atual: " + this.userSession.getAccessLevel());
			return false;
		}
		return true;
	}

	/**
	 * Listar logs de segurança.
	 */
	public PaginatedList<SecurityAuditLog> listSecurityLogs(String eventType, Boolean suspicious,
			Date startDate, Date endDate, Integer page, Integer pageSize) {
		if (page == null || page < 1) page = 1;
		if (pageSize == null || pageSize <= 0) pageSize = 20;

		Criteria criteria = this.dao.newCriteria(SecurityAuditLog.class);
		criteria.add(Restrictions.eq("deleted", false));

		Criteria counting = this.dao.newCriteria(SecurityAuditLog.class);
		counting.add(Restrictions.eq("deleted", false));
		counting.setProjection(Projections.countDistinct("id"));

		if (this.domain != null && this.domain.getCompany() != null) {
			criteria.add(Restrictions.eq("company", this.domain.getCompany()));
			counting.add(Restrictions.eq("company", this.domain.getCompany()));
		}
		if (eventType != null && !eventType.isEmpty()) {
			criteria.add(Restrictions.eq("eventType", eventType));
			counting.add(Restrictions.eq("eventType", eventType));
		}
		if (suspicious != null) {
			criteria.add(Restrictions.eq("suspicious", suspicious));
			counting.add(Restrictions.eq("suspicious", suspicious));
		}
		if (startDate != null) {
			criteria.add(Restrictions.ge("creation", startDate));
			counting.add(Restrictions.ge("creation", startDate));
		}
		if (endDate != null) {
			criteria.add(Restrictions.le("creation", endDate));
			counting.add(Restrictions.le("creation", endDate));
		}

		criteria.addOrder(Order.desc("creation"));
		criteria.setFirstResult((page - 1) * pageSize);
		criteria.setMaxResults(pageSize);

		List<SecurityAuditLog> list = this.dao.findByCriteria(criteria, SecurityAuditLog.class);
		Long total = (Long) counting.uniqueResult();

		PaginatedList<SecurityAuditLog> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal(total);
		return result;
	}

	private String getClientIp() {
		if (this.request == null) return null;
		String ip = this.request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = this.request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = this.request.getRemoteAddr();
		}
		if (ip != null && ip.contains(",")) {
			ip = ip.split(",")[0].trim();
		}
		return ip;
	}

	private String truncate(String value, int maxLength) {
		if (value == null) return null;
		return value.length() > maxLength ? value.substring(0, maxLength) : value;
	}
}
