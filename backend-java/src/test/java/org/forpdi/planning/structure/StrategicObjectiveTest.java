package org.forpdi.planning.structure;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * Testes unitários para a entidade StrategicObjective.
 */
public class StrategicObjectiveTest {

	@Test
	public void testCreateObjective() {
		StrategicObjective objective = new StrategicObjective();
		objective.setName("Objetivo Teste");
		objective.setDescription("Descrição do objetivo de teste");
		objective.setStatus("NOT_STARTED");
		objective.setLevel(0);
		objective.setWeight(1.0);
		objective.setProgress(0.0);

		assertEquals("Objetivo Teste", objective.getName());
		assertEquals("Descrição do objetivo de teste", objective.getDescription());
		assertEquals("NOT_STARTED", objective.getStatus());
		assertEquals(0, objective.getLevel());
		assertEquals(1.0, objective.getWeight(), 0.001);
		assertEquals(0.0, objective.getProgress(), 0.001);
	}

	@Test
	public void testObjectiveDefaults() {
		StrategicObjective objective = new StrategicObjective();

		assertNotNull(objective.getCreation());
		assertEquals("NOT_STARTED", objective.getStatus());
		assertEquals(0.0, objective.getProgress(), 0.001);
		assertEquals(1.0, objective.getWeight(), 0.001);
		assertEquals(0, objective.getLevel());
		assertNull(objective.getParent());
		assertNull(objective.getResponsible());
	}

	@Test
	public void testObjectiveHierarchy() {
		StrategicObjective parent = new StrategicObjective();
		parent.setName("Objetivo Pai");
		parent.setLevel(0);

		StrategicObjective child = new StrategicObjective();
		child.setName("Objetivo Filho");
		child.setLevel(1);
		child.setParent(parent);

		assertEquals(parent, child.getParent());
		assertEquals(0, parent.getLevel());
		assertEquals(1, child.getLevel());
	}

	@Test
	public void testObjectiveDates() {
		StrategicObjective objective = new StrategicObjective();
		Date start = new Date();
		Date end = new Date(start.getTime() + 86400000L * 30); // 30 days later

		objective.setStartDate(start);
		objective.setEndDate(end);

		assertEquals(start, objective.getStartDate());
		assertEquals(end, objective.getEndDate());
		assertTrue(objective.getEndDate().after(objective.getStartDate()));
	}

	@Test
	public void testObjectiveStatusTransitions() {
		StrategicObjective objective = new StrategicObjective();

		objective.setStatus("NOT_STARTED");
		assertEquals("NOT_STARTED", objective.getStatus());

		objective.setStatus("IN_PROGRESS");
		assertEquals("IN_PROGRESS", objective.getStatus());

		objective.setStatus("COMPLETED");
		assertEquals("COMPLETED", objective.getStatus());
		objective.setProgress(100.0);
		assertEquals(100.0, objective.getProgress(), 0.001);

		objective.setStatus("DELAYED");
		assertEquals("DELAYED", objective.getStatus());

		objective.setStatus("CANCELLED");
		assertEquals("CANCELLED", objective.getStatus());
	}

	@Test
	public void testObjectiveChildAndProjectCounts() {
		StrategicObjective objective = new StrategicObjective();
		objective.setChildCount(5);
		objective.setProjectCount(3);

		assertEquals(5, objective.getChildCount());
		assertEquals(3, objective.getProjectCount());
	}
}
