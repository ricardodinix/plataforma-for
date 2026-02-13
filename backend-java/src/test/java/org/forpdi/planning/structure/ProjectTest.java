package org.forpdi.planning.structure;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * Testes unitários para a entidade Project.
 */
public class ProjectTest {

	@Test
	public void testCreateProject() {
		Project project = new Project();
		project.setName("Projeto Teste");
		project.setDescription("Descrição do projeto");
		project.setStatus("NOT_STARTED");
		project.setPriority("HIGH");
		project.setBudget(100000.0);
		project.setBudgetExecuted(50000.0);

		assertEquals("Projeto Teste", project.getName());
		assertEquals("Descrição do projeto", project.getDescription());
		assertEquals("NOT_STARTED", project.getStatus());
		assertEquals("HIGH", project.getPriority());
		assertEquals(100000.0, project.getBudget(), 0.001);
		assertEquals(50000.0, project.getBudgetExecuted(), 0.001);
	}

	@Test
	public void testProjectDefaults() {
		Project project = new Project();

		assertNotNull(project.getCreation());
		assertEquals("NOT_STARTED", project.getStatus());
		assertEquals("MEDIUM", project.getPriority());
		assertEquals(0.0, project.getProgress(), 0.001);
		assertEquals(0.0, project.getBudget(), 0.001);
		assertEquals(0.0, project.getBudgetExecuted(), 0.001);
	}

	@Test
	public void testProjectObjectiveLink() {
		StrategicObjective objective = new StrategicObjective();
		objective.setName("Objetivo Estratégico");

		Project project = new Project();
		project.setName("Projeto Vinculado");
		project.setStrategicObjective(objective);

		assertNotNull(project.getStrategicObjective());
		assertEquals("Objetivo Estratégico", project.getStrategicObjective().getName());
	}

	@Test
	public void testProjectBudgetExecution() {
		Project project = new Project();
		project.setBudget(100000.0);
		project.setBudgetExecuted(75000.0);

		double executionPercent = (project.getBudgetExecuted() / project.getBudget()) * 100;
		assertEquals(75.0, executionPercent, 0.001);
	}

	@Test
	public void testProjectDates() {
		Project project = new Project();
		Date start = new Date();
		Date end = new Date(start.getTime() + 86400000L * 90); // 90 days later

		project.setStartDate(start);
		project.setEndDate(end);

		assertTrue(project.getEndDate().after(project.getStartDate()));
	}

	@Test
	public void testProjectPriorities() {
		Project project = new Project();

		project.setPriority("LOW");
		assertEquals("LOW", project.getPriority());

		project.setPriority("MEDIUM");
		assertEquals("MEDIUM", project.getPriority());

		project.setPriority("HIGH");
		assertEquals("HIGH", project.getPriority());

		project.setPriority("CRITICAL");
		assertEquals("CRITICAL", project.getPriority());
	}

	@Test
	public void testProjectActionPlanCount() {
		Project project = new Project();
		project.setActionPlanCount(10);
		assertEquals(10, project.getActionPlanCount());
	}
}
