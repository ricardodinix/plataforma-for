package org.forpdi.core.notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import org.forpdi.core.abstractions.AbstractController;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.boilerplate.NoCache;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Controller REST para Rastreabilidade e Auditoria.
 * Fornece endpoints para consultar o histórico completo de alterações.
 */
@Controller
public class AuditController extends AbstractController {

	@Inject
	private AuditBS bs;

	@Get(BASEPATH + "/audit/logs")
	@NoCache
	public void listAuditLogs(Long userId, String entityType, String action,
			String startDate, String endDate, Integer page, Integer pageSize) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date start = null;
			Date end = null;
			if (startDate != null && !startDate.isEmpty()) {
				start = sdf.parse(startDate);
			}
			if (endDate != null && !endDate.isEmpty()) {
				end = sdf.parse(endDate);
			}
			PaginatedList<AuditLog> result = this.bs.listAuditLogs(
					userId, entityType, action, start, end, page, pageSize);
			this.success(result);
		} catch (ParseException ex) {
			this.fail("Formato de data inválido. Use yyyy-MM-dd.");
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar logs de auditoria", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/audit/entity-history")
	@NoCache
	public void listEntityHistory(String entityType, Long entityId, Integer page, Integer pageSize) {
		try {
			PaginatedList<AuditLog> result = this.bs.listEntityHistory(entityType, entityId, page, pageSize);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar histórico da entidade", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}
}
