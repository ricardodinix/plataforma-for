package org.forpdi.dashboard;

import java.util.Map;

import javax.inject.Inject;

import org.forpdi.core.abstractions.AbstractController;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.boilerplate.NoCache;

/**
 * Controller REST para a Interface por Usuário.
 * Fornece dados para a tela inicial individualizada.
 */
@Controller
public class UserDashboardController extends AbstractController {

	@Inject
	private UserDashboardBS bs;

	@Get(BASEPATH + "/dashboard/user/summary")
	@NoCache
	public void getUserSummary() {
		try {
			Map<String, Object> summary = this.bs.getUserDashboardSummary();
			this.success(summary);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao recuperar resumo do usuário", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}
}
