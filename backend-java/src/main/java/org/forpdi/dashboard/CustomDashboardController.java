package org.forpdi.dashboard;

import javax.inject.Inject;

import org.forpdi.core.abstractions.AbstractController;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.boilerplate.NoCache;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Controller REST para Dashboards Personalizáveis.
 * Permite criar, editar e gerenciar painéis com widgets customizáveis.
 */
@Controller
public class CustomDashboardController extends AbstractController {

	@Inject
	private CustomDashboardBS bs;

	// --- Panels ---

	@Post(BASEPATH + "/dashboard/panel")
	@NoCache
	@Consumes
	public void createPanel(DashboardPanel panel) {
		try {
			if (panel.getTitle() == null || panel.getTitle().trim().isEmpty()) {
				this.fail("Título do painel é obrigatório.");
				return;
			}
			DashboardPanel result = this.bs.createPanel(panel);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao criar painel", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Put(BASEPATH + "/dashboard/panel")
	@NoCache
	@Consumes
	public void updatePanel(DashboardPanel panel) {
		try {
			DashboardPanel result = this.bs.updatePanel(panel);
			if (result == null) {
				this.fail("Painel não encontrado.");
				return;
			}
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao atualizar painel", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Delete(BASEPATH + "/dashboard/panel/{id}")
	@NoCache
	public void deletePanel(Long id) {
		try {
			this.bs.deletePanel(id);
			this.success();
		} catch (Throwable ex) {
			LOGGER.error("Erro ao deletar painel", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/dashboard/panel/list")
	@NoCache
	public void listPanels() {
		try {
			PaginatedList<DashboardPanel> result = this.bs.listPanels();
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar painéis", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	// --- Widgets ---

	@Post(BASEPATH + "/dashboard/widget")
	@NoCache
	@Consumes
	public void createWidget(DashboardWidget widget) {
		try {
			if (widget.getTitle() == null || widget.getTitle().trim().isEmpty()) {
				this.fail("Título do widget é obrigatório.");
				return;
			}
			DashboardWidget result = this.bs.createWidget(widget);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao criar widget", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Put(BASEPATH + "/dashboard/widget")
	@NoCache
	@Consumes
	public void updateWidget(DashboardWidget widget) {
		try {
			DashboardWidget result = this.bs.updateWidget(widget);
			if (result == null) {
				this.fail("Widget não encontrado.");
				return;
			}
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao atualizar widget", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Delete(BASEPATH + "/dashboard/widget/{id}")
	@NoCache
	public void deleteWidget(Long id) {
		try {
			this.bs.deleteWidget(id);
			this.success();
		} catch (Throwable ex) {
			LOGGER.error("Erro ao deletar widget", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/dashboard/widget/list")
	@NoCache
	public void listWidgets(Long panelId) {
		try {
			this.success(this.bs.listWidgets(panelId));
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar widgets", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}
}
