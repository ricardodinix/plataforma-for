package org.forpdi.dashboard;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.caelum.vraptor.boilerplate.SimpleLogicalDeletableEntity;

/**
 * Widget individual dentro de um painel de dashboard.
 * Suporta diferentes tipos de visualização: gráficos de barra, pizza, linha,
 * indicadores numéricos, tabelas, etc.
 */
@Entity(name = DashboardWidget.TABLE)
@Table(name = DashboardWidget.TABLE)
public class DashboardWidget extends SimpleLogicalDeletableEntity {
	public static final String TABLE = "fpdi_dashboard_widget";
	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = DashboardPanel.class, optional = false, fetch = FetchType.EAGER)
	private DashboardPanel panel;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(nullable = false, length = 50)
	private String widgetType; // BAR_CHART, PIE_CHART, LINE_CHART, NUMBER_INDICATOR, TABLE, GAUGE, PROGRESS_BAR

	@Column(nullable = false, length = 100)
	private String dataSource; // GOALS, OBJECTIVES, ACTION_PLANS, BUDGET, PROJECTS, RISKS

	@Column(nullable = true, length = 5000)
	private String filterConfig; // JSON filter criteria

	@Column(nullable = true, length = 5000)
	private String visualConfig; // JSON visual configuration (colors, labels, etc.)

	@Column(nullable = false)
	private int positionX = 0;

	@Column(nullable = false)
	private int positionY = 0;

	@Column(nullable = false)
	private int width = 6; // Grid-based width (1-12)

	@Column(nullable = false)
	private int height = 4; // Grid-based height

	@Column(nullable = true)
	private Long refreshInterval; // Auto-refresh in seconds

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date creation = new Date();

	public DashboardPanel getPanel() {
		return panel;
	}

	public void setPanel(DashboardPanel panel) {
		this.panel = panel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(String filterConfig) {
		this.filterConfig = filterConfig;
	}

	public String getVisualConfig() {
		return visualConfig;
	}

	public void setVisualConfig(String visualConfig) {
		this.visualConfig = visualConfig;
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Long getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(Long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}
}
