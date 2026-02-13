package org.forpdi.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.forpdi.core.company.CompanyDomain;
import org.forpdi.core.event.Current;
import org.forpdi.core.notification.AuditBS;
import org.forpdi.core.user.User;
import org.forpdi.core.user.auth.UserSession;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.boilerplate.HibernateBusiness;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Business Service para Dashboards Personalizáveis.
 */
@RequestScoped
public class CustomDashboardBS extends HibernateBusiness {

	@Inject
	@Current
	private CompanyDomain domain;
	@Inject
	private UserSession userSession;
	@Inject
	private AuditBS auditBS;

	/**
	 * Criar um novo painel de dashboard.
	 */
	public DashboardPanel createPanel(DashboardPanel panel) {
		panel.setCompany(this.domain.getCompany());
		panel.setOwner(this.userSession.getUser());
		panel.setCreation(new Date());
		panel.setLastUpdate(new Date());
		this.persist(panel);
		this.auditBS.logAction("CREATE", "DashboardPanel", panel.getId(),
				panel.getTitle(), null, null, null);
		return panel;
	}

	/**
	 * Atualizar um painel existente.
	 */
	public DashboardPanel updatePanel(DashboardPanel panel) {
		DashboardPanel existing = this.exists(panel.getId(), DashboardPanel.class);
		if (existing == null) return null;

		existing.setTitle(panel.getTitle());
		existing.setDescription(panel.getDescription());
		existing.setShared(panel.isShared());
		existing.setLayoutConfig(panel.getLayoutConfig());
		existing.setLastUpdate(new Date());
		this.persist(existing);
		return existing;
	}

	/**
	 * Deletar um painel.
	 */
	public void deletePanel(Long id) {
		DashboardPanel panel = this.exists(id, DashboardPanel.class);
		if (panel != null) {
			panel.setDeleted(true);
			this.persist(panel);

			Criteria widgetCriteria = this.dao.newCriteria(DashboardWidget.class);
			widgetCriteria.add(Restrictions.eq("panel", panel));
			widgetCriteria.add(Restrictions.eq("deleted", false));
			List<DashboardWidget> widgets = this.dao.findByCriteria(widgetCriteria, DashboardWidget.class);
			for (DashboardWidget w : widgets) {
				w.setDeleted(true);
				this.persist(w);
			}
		}
	}

	/**
	 * Listar painéis do usuário (incluindo compartilhados).
	 */
	public PaginatedList<DashboardPanel> listPanels() {
		if (this.domain == null || this.domain.getCompany() == null) {
			return new PaginatedList<>(new ArrayList<>(0), 0L);
		}

		Criteria criteria = this.dao.newCriteria(DashboardPanel.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("company", this.domain.getCompany()));

		Disjunction ownerOrShared = Restrictions.disjunction();
		ownerOrShared.add(Restrictions.eq("owner", this.userSession.getUser()));
		ownerOrShared.add(Restrictions.eq("shared", true));
		criteria.add(ownerOrShared);
		criteria.addOrder(Order.desc("lastUpdate"));

		List<DashboardPanel> list = this.dao.findByCriteria(criteria, DashboardPanel.class);
		for (DashboardPanel panel : list) {
			panel.setWidgets(listWidgets(panel.getId()));
		}

		PaginatedList<DashboardPanel> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal((long) list.size());
		return result;
	}

	/**
	 * Criar um widget dentro de um painel.
	 */
	public DashboardWidget createWidget(DashboardWidget widget) {
		widget.setCreation(new Date());
		this.persist(widget);
		return widget;
	}

	/**
	 * Atualizar um widget.
	 */
	public DashboardWidget updateWidget(DashboardWidget widget) {
		DashboardWidget existing = this.exists(widget.getId(), DashboardWidget.class);
		if (existing == null) return null;

		existing.setTitle(widget.getTitle());
		existing.setWidgetType(widget.getWidgetType());
		existing.setDataSource(widget.getDataSource());
		existing.setFilterConfig(widget.getFilterConfig());
		existing.setVisualConfig(widget.getVisualConfig());
		existing.setPositionX(widget.getPositionX());
		existing.setPositionY(widget.getPositionY());
		existing.setWidth(widget.getWidth());
		existing.setHeight(widget.getHeight());
		existing.setRefreshInterval(widget.getRefreshInterval());
		this.persist(existing);
		return existing;
	}

	/**
	 * Deletar um widget.
	 */
	public void deleteWidget(Long id) {
		DashboardWidget widget = this.exists(id, DashboardWidget.class);
		if (widget != null) {
			widget.setDeleted(true);
			this.persist(widget);
		}
	}

	/**
	 * Listar widgets de um painel.
	 */
	public List<DashboardWidget> listWidgets(Long panelId) {
		Criteria criteria = this.dao.newCriteria(DashboardWidget.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("panel.id", panelId));
		criteria.addOrder(Order.asc("positionY"));
		criteria.addOrder(Order.asc("positionX"));
		return this.dao.findByCriteria(criteria, DashboardWidget.class);
	}
}
