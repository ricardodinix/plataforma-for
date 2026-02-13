package org.forpdi.core.notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import org.forpdi.core.abstractions.AbstractController;
import org.forpdi.core.user.authz.AccessLevels;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.boilerplate.NoCache;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Controller REST para Segurança da Informação.
 * Fornece endpoints para consulta de logs de segurança e validação de acessos.
 */
@Controller
public class SecurityController extends AbstractController {

	@Inject
	private SecurityBS bs;

	@Get(BASEPATH + "/security/logs")
	@NoCache
	public void listSecurityLogs(String eventType, Boolean suspicious,
			String startDate, String endDate, Integer page, Integer pageSize) {
		try {
			if (this.userSession.getAccessLevel() < AccessLevels.COMPANY_ADMIN.getLevel()) {
				this.fail("Acesso negado. Somente administradores podem acessar logs de segurança.");
				return;
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date start = null;
			Date end = null;
			if (startDate != null && !startDate.isEmpty()) {
				start = sdf.parse(startDate);
			}
			if (endDate != null && !endDate.isEmpty()) {
				end = sdf.parse(endDate);
			}

			PaginatedList<SecurityAuditLog> result = this.bs.listSecurityLogs(
					eventType, suspicious, start, end, page, pageSize);
			this.success(result);
		} catch (ParseException ex) {
			this.fail("Formato de data inválido. Use yyyy-MM-dd.");
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar logs de segurança", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/security/check-ip/{ipAddress}")
	@NoCache
	public void checkSuspiciousIp(String ipAddress) {
		try {
			if (this.userSession.getAccessLevel() < AccessLevels.COMPANY_ADMIN.getLevel()) {
				this.fail("Acesso negado.");
				return;
			}
			boolean suspicious = this.bs.isIpSuspicious(ipAddress);
			this.success(suspicious);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao verificar IP", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}
}
