package org.forpdi.dashboard;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

/**
 * Testes unitários para as entidades de Dashboard Personalizável.
 */
public class DashboardPanelTest {

	@Test
	public void testCreatePanel() {
		DashboardPanel panel = new DashboardPanel();
		panel.setTitle("Meu Dashboard");
		panel.setDescription("Dashboard customizado para acompanhamento");
		panel.setShared(false);

		assertEquals("Meu Dashboard", panel.getTitle());
		assertEquals("Dashboard customizado para acompanhamento", panel.getDescription());
		assertFalse(panel.isShared());
	}

	@Test
	public void testPanelDefaults() {
		DashboardPanel panel = new DashboardPanel();
		assertNotNull(panel.getCreation());
		assertNotNull(panel.getLastUpdate());
		assertFalse(panel.isShared());
	}

	@Test
	public void testPanelWithWidgets() {
		DashboardPanel panel = new DashboardPanel();
		panel.setTitle("Dashboard com Widgets");

		List<DashboardWidget> widgets = new ArrayList<>();
		DashboardWidget widget1 = new DashboardWidget();
		widget1.setTitle("Gráfico de Metas");
		widget1.setWidgetType("BAR_CHART");
		widget1.setDataSource("GOALS");
		widgets.add(widget1);

		DashboardWidget widget2 = new DashboardWidget();
		widget2.setTitle("Indicador de Progresso");
		widget2.setWidgetType("PROGRESS_BAR");
		widget2.setDataSource("PROJECTS");
		widgets.add(widget2);

		panel.setWidgets(widgets);

		assertEquals(2, panel.getWidgets().size());
		assertEquals("Gráfico de Metas", panel.getWidgets().get(0).getTitle());
		assertEquals("BAR_CHART", panel.getWidgets().get(0).getWidgetType());
	}

	@Test
	public void testCreateWidget() {
		DashboardWidget widget = new DashboardWidget();
		widget.setTitle("Widget Teste");
		widget.setWidgetType("PIE_CHART");
		widget.setDataSource("OBJECTIVES");
		widget.setPositionX(0);
		widget.setPositionY(0);
		widget.setWidth(6);
		widget.setHeight(4);

		assertEquals("Widget Teste", widget.getTitle());
		assertEquals("PIE_CHART", widget.getWidgetType());
		assertEquals("OBJECTIVES", widget.getDataSource());
		assertEquals(0, widget.getPositionX());
		assertEquals(0, widget.getPositionY());
		assertEquals(6, widget.getWidth());
		assertEquals(4, widget.getHeight());
	}

	@Test
	public void testWidgetDefaults() {
		DashboardWidget widget = new DashboardWidget();
		assertNotNull(widget.getCreation());
		assertEquals(0, widget.getPositionX());
		assertEquals(0, widget.getPositionY());
		assertEquals(6, widget.getWidth());
		assertEquals(4, widget.getHeight());
	}

	@Test
	public void testWidgetTypes() {
		String[] widgetTypes = {"BAR_CHART", "PIE_CHART", "LINE_CHART",
			"NUMBER_INDICATOR", "TABLE", "GAUGE", "PROGRESS_BAR"};
		for (String type : widgetTypes) {
			DashboardWidget widget = new DashboardWidget();
			widget.setWidgetType(type);
			assertEquals(type, widget.getWidgetType());
		}
	}

	@Test
	public void testWidgetDataSources() {
		String[] dataSources = {"GOALS", "OBJECTIVES", "ACTION_PLANS",
			"BUDGET", "PROJECTS", "RISKS"};
		for (String source : dataSources) {
			DashboardWidget widget = new DashboardWidget();
			widget.setDataSource(source);
			assertEquals(source, widget.getDataSource());
		}
	}

	@Test
	public void testPanelSharing() {
		DashboardPanel panel = new DashboardPanel();
		panel.setShared(false);
		assertFalse(panel.isShared());

		panel.setShared(true);
		assertTrue(panel.isShared());
	}

	@Test
	public void testWidgetConfig() {
		DashboardWidget widget = new DashboardWidget();
		widget.setFilterConfig("{\"planMacroId\": 1}");
		widget.setVisualConfig("{\"color\": \"#0383D9\", \"showLegend\": true}");

		assertEquals("{\"planMacroId\": 1}", widget.getFilterConfig());
		assertEquals("{\"color\": \"#0383D9\", \"showLegend\": true}", widget.getVisualConfig());
	}
}
