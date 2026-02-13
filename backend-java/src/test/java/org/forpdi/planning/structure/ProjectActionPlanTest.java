package org.forpdi.planning.structure;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * Testes unitários para a entidade ProjectActionPlan (5W2H).
 */
public class ProjectActionPlanTest {

	@Test
	public void testCreateActionPlan() {
		ProjectActionPlan plan = new ProjectActionPlan();
		plan.setDescription("Plano de ação de teste");
		plan.setStatus("NOT_STARTED");
		plan.setChecked(false);

		assertEquals("Plano de ação de teste", plan.getDescription());
		assertEquals("NOT_STARTED", plan.getStatus());
		assertFalse(plan.isChecked());
	}

	@Test
	public void testActionPlanDefaults() {
		ProjectActionPlan plan = new ProjectActionPlan();

		assertNotNull(plan.getCreation());
		assertEquals("NOT_STARTED", plan.getStatus());
		assertFalse(plan.isChecked());
		assertEquals(0.0, plan.getProgress(), 0.001);
	}

	@Test
	public void test5W2HFields() {
		ProjectActionPlan plan = new ProjectActionPlan();
		plan.setWhat("Implementar novo sistema");
		plan.setWhy("Para melhorar a eficiência operacional");
		plan.setWhereField("Departamento de TI");
		plan.setWho("João Silva");
		plan.setHow("Utilizando metodologia ágil");
		plan.setHowMuch(50000.0);

		Date when = new Date();
		plan.setWhenDate(when);

		assertEquals("Implementar novo sistema", plan.getWhat());
		assertEquals("Para melhorar a eficiência operacional", plan.getWhy());
		assertEquals("Departamento de TI", plan.getWhereField());
		assertEquals("João Silva", plan.getWho());
		assertEquals("Utilizando metodologia ágil", plan.getHow());
		assertEquals(50000.0, plan.getHowMuch(), 0.001);
		assertEquals(when, plan.getWhenDate());
	}

	@Test
	public void testActionPlanProjectLink() {
		Project project = new Project();
		project.setName("Projeto X");

		ProjectActionPlan plan = new ProjectActionPlan();
		plan.setDescription("Ação vinculada ao projeto");
		plan.setProject(project);

		assertNotNull(plan.getProject());
		assertEquals("Projeto X", plan.getProject().getName());
	}

	@Test
	public void testActionPlanCompletion() {
		ProjectActionPlan plan = new ProjectActionPlan();
		plan.setStatus("NOT_STARTED");
		plan.setChecked(false);
		plan.setProgress(0.0);

		// Simulate progress
		plan.setStatus("IN_PROGRESS");
		plan.setProgress(50.0);
		assertEquals("IN_PROGRESS", plan.getStatus());
		assertEquals(50.0, plan.getProgress(), 0.001);

		// Complete
		plan.setStatus("COMPLETED");
		plan.setChecked(true);
		plan.setProgress(100.0);
		assertEquals("COMPLETED", plan.getStatus());
		assertTrue(plan.isChecked());
		assertEquals(100.0, plan.getProgress(), 0.001);
	}

	@Test
	public void testActionPlanDates() {
		ProjectActionPlan plan = new ProjectActionPlan();
		Date start = new Date();
		Date end = new Date(start.getTime() + 86400000L * 30);

		plan.setStartDate(start);
		plan.setEndDate(end);

		assertTrue(plan.getEndDate().after(plan.getStartDate()));
	}
}
