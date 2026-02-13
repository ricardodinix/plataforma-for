package org.forpdi.core.notification;

import java.util.ArrayList;
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
 * Business Service para rastreabilidade e auditoria.
 * Registra todas as alterações com identificação dos responsáveis.
 */
@RequestScoped
public class AuditBS extends HibernateBusiness {

	@Inject
	@Current
	private CompanyDomain domain;
	@Inject
	private UserSession userSession;
	@Inject
	private HttpServletRequest request;

	/**
	 * Registrar uma ação de auditoria.
	 */
	public void logAction(String action, String entityType, Long entityId,
			String entityName, String fieldChanged, String previousValue, String newValue) {
		try {
			if (this.domain == null || this.domain.getCompany() == null) return;
			if (this.userSession == null || this.userSession.getUser() == null) return;

			AuditLog log = new AuditLog();
			log.setUser(this.userSession.getUser());
			log.setCompany(this.domain.getCompany());
			log.setCreation(new Date());
			log.setAction(action);
			log.setEntityType(entityType);
			log.setEntityId(entityId);
			log.setEntityName(entityName);
			log.setFieldChanged(fieldChanged);
			log.setPreviousValue(truncate(previousValue, 4000));
			log.setNewValue(truncate(newValue, 4000));

			if (this.request != null) {
				log.setIpAddress(getClientIp());
				log.setUserAgent(truncate(this.request.getHeader("User-Agent"), 500));
			}

			this.persist(log);
		} catch (Exception e) {
			LOGGER.error("Erro ao registrar auditoria", e);
		}
	}

	/**
	 * Registrar uma ação de auditoria com detalhes extras.
	 */
	public void logActionWithDetails(String action, String entityType, Long entityId,
			String entityName, String details) {
		try {
			if (this.domain == null || this.domain.getCompany() == null) return;
			if (this.userSession == null || this.userSession.getUser() == null) return;

			AuditLog log = new AuditLog();
			log.setUser(this.userSession.getUser());
			log.setCompany(this.domain.getCompany());
			log.setCreation(new Date());
			log.setAction(action);
			log.setEntityType(entityType);
			log.setEntityId(entityId);
			log.setEntityName(entityName);
			log.setDetails(truncate(details, 1000));

			if (this.request != null) {
				log.setIpAddress(getClientIp());
				log.setUserAgent(truncate(this.request.getHeader("User-Agent"), 500));
			}

			this.persist(log);
		} catch (Exception e) {
			LOGGER.error("Erro ao registrar auditoria", e);
		}
	}

	/**
	 * Listar logs de auditoria com filtros.
	 */
	public PaginatedList<AuditLog> listAuditLogs(Long userId, String entityType, String action,
			Date startDate, Date endDate, Integer page, Integer pageSize) {
		if (page == null || page < 1) page = 1;
		if (pageSize == null || pageSize <= 0) pageSize = 20;

		if (this.domain == null || this.domain.getCompany() == null) {
			return new PaginatedList<>(new ArrayList<>(0), 0L);
		}

		Criteria criteria = this.dao.newCriteria(AuditLog.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("company", this.domain.getCompany()));

		Criteria counting = this.dao.newCriteria(AuditLog.class);
		counting.add(Restrictions.eq("deleted", false));
		counting.add(Restrictions.eq("company", this.domain.getCompany()));
		counting.setProjection(Projections.countDistinct("id"));

		if (userId != null) {
			User user = this.exists(userId, User.class);
			if (user != null) {
				criteria.add(Restrictions.eq("user", user));
				counting.add(Restrictions.eq("user", user));
			}
		}
		if (entityType != null && !entityType.isEmpty()) {
			criteria.add(Restrictions.eq("entityType", entityType));
			counting.add(Restrictions.eq("entityType", entityType));
		}
		if (action != null && !action.isEmpty()) {
			criteria.add(Restrictions.eq("action", action));
			counting.add(Restrictions.eq("action", action));
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

		List<AuditLog> list = this.dao.findByCriteria(criteria, AuditLog.class);
		Long total = (Long) counting.uniqueResult();

		PaginatedList<AuditLog> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal(total);
		return result;
	}

	/**
	 * Listar logs de auditoria de uma entidade específica.
	 */
	public PaginatedList<AuditLog> listEntityHistory(String entityType, Long entityId,
			Integer page, Integer pageSize) {
		if (page == null || page < 1) page = 1;
		if (pageSize == null || pageSize <= 0) pageSize = 20;

		Criteria criteria = this.dao.newCriteria(AuditLog.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("entityType", entityType));
		criteria.add(Restrictions.eq("entityId", entityId));
		criteria.addOrder(Order.desc("creation"));
		criteria.setFirstResult((page - 1) * pageSize);
		criteria.setMaxResults(pageSize);

		Criteria counting = this.dao.newCriteria(AuditLog.class);
		counting.add(Restrictions.eq("deleted", false));
		counting.add(Restrictions.eq("entityType", entityType));
		counting.add(Restrictions.eq("entityId", entityId));
		counting.setProjection(Projections.countDistinct("id"));

		List<AuditLog> list = this.dao.findByCriteria(criteria, AuditLog.class);
		Long total = (Long) counting.uniqueResult();

		PaginatedList<AuditLog> result = new PaginatedList<>();
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
