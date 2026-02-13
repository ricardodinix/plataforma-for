package org.forpdi.dashboard;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.forpdi.core.company.Company;
import org.forpdi.core.user.User;

import br.com.caelum.vraptor.boilerplate.SimpleLogicalDeletableEntity;
import br.com.caelum.vraptor.serialization.SkipSerialization;

/**
 * Painel de Dashboard personalizável pelo usuário.
 * Permite configurar visualizações em tempo real com gráficos customizáveis.
 */
@Entity(name = DashboardPanel.TABLE)
@Table(name = DashboardPanel.TABLE)
public class DashboardPanel extends SimpleLogicalDeletableEntity {
	public static final String TABLE = "fpdi_dashboard_panel";
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(nullable = true, length = 1000)
	private String description;

	@SkipSerialization
	@ManyToOne(targetEntity = Company.class, optional = false, fetch = FetchType.EAGER)
	private Company company;

	@ManyToOne(targetEntity = User.class, optional = false, fetch = FetchType.EAGER)
	private User owner;

	@Column(nullable = false)
	private boolean shared = false;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date creation = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date lastUpdate = new Date();

	@Column(nullable = true, length = 5000)
	private String layoutConfig; // JSON layout configuration

	@Transient
	private List<DashboardWidget> widgets;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getLayoutConfig() {
		return layoutConfig;
	}

	public void setLayoutConfig(String layoutConfig) {
		this.layoutConfig = layoutConfig;
	}

	public List<DashboardWidget> getWidgets() {
		return widgets;
	}

	public void setWidgets(List<DashboardWidget> widgets) {
		this.widgets = widgets;
	}
}
