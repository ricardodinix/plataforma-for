package org.forpdi.core.notification;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * Testes unitários para a entidade AuditLog.
 */
public class AuditLogTest {

	@Test
	public void testCreateAuditLog() {
		AuditLog log = new AuditLog();
		log.setAction("CREATE");
		log.setEntityType("StrategicObjective");
		log.setEntityId(1L);
		log.setEntityName("Objetivo Teste");
		log.setIpAddress("192.168.1.1");

		assertEquals("CREATE", log.getAction());
		assertEquals("StrategicObjective", log.getEntityType());
		assertEquals(Long.valueOf(1L), log.getEntityId());
		assertEquals("Objetivo Teste", log.getEntityName());
		assertEquals("192.168.1.1", log.getIpAddress());
	}

	@Test
	public void testAuditLogDefaults() {
		AuditLog log = new AuditLog();
		assertNotNull(log.getCreation());
		assertNull(log.getFieldChanged());
		assertNull(log.getPreviousValue());
		assertNull(log.getNewValue());
	}

	@Test
	public void testAuditLogFieldChange() {
		AuditLog log = new AuditLog();
		log.setAction("UPDATE");
		log.setEntityType("Project");
		log.setFieldChanged("name");
		log.setPreviousValue("Projeto Antigo");
		log.setNewValue("Projeto Novo");

		assertEquals("UPDATE", log.getAction());
		assertEquals("name", log.getFieldChanged());
		assertEquals("Projeto Antigo", log.getPreviousValue());
		assertEquals("Projeto Novo", log.getNewValue());
	}

	@Test
	public void testAuditLogActions() {
		String[] validActions = {"CREATE", "UPDATE", "DELETE", "ACCESS", "EXPORT", "LOGIN", "LOGOUT"};
		for (String action : validActions) {
			AuditLog log = new AuditLog();
			log.setAction(action);
			assertEquals(action, log.getAction());
		}
	}

	@Test
	public void testAuditLogDetails() {
		AuditLog log = new AuditLog();
		log.setDetails("Detalhes da operação de exportação em Excel");
		log.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

		assertEquals("Detalhes da operação de exportação em Excel", log.getDetails());
		assertNotNull(log.getUserAgent());
	}
}
