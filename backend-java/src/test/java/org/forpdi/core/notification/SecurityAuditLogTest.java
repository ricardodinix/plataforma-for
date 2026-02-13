package org.forpdi.core.notification;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * Testes unitários para a entidade SecurityAuditLog.
 */
public class SecurityAuditLogTest {

	@Test
	public void testCreateSecurityLog() {
		SecurityAuditLog log = new SecurityAuditLog();
		log.setEventType("LOGIN");
		log.setIpAddress("192.168.1.100");
		log.setUserAgent("Mozilla/5.0");
		log.setRequestPath("/api/user/login");
		log.setSuspicious(false);

		assertEquals("LOGIN", log.getEventType());
		assertEquals("192.168.1.100", log.getIpAddress());
		assertEquals("Mozilla/5.0", log.getUserAgent());
		assertEquals("/api/user/login", log.getRequestPath());
		assertFalse(log.isSuspicious());
	}

	@Test
	public void testSecurityLogDefaults() {
		SecurityAuditLog log = new SecurityAuditLog();
		assertNotNull(log.getCreation());
		assertFalse(log.isSuspicious());
		assertNull(log.getUser());
		assertNull(log.getUsername());
	}

	@Test
	public void testFailedLoginLog() {
		SecurityAuditLog log = new SecurityAuditLog();
		log.setEventType("LOGIN_FAILED");
		log.setUsername("usuario@invalido.com");
		log.setIpAddress("10.0.0.1");
		log.setSuspicious(true);
		log.setDetails("Tentativa de login com credenciais inválidas");

		assertEquals("LOGIN_FAILED", log.getEventType());
		assertEquals("usuario@invalido.com", log.getUsername());
		assertTrue(log.isSuspicious());
		assertNotNull(log.getDetails());
	}

	@Test
	public void testSecurityEventTypes() {
		String[] eventTypes = {"LOGIN", "LOGOUT", "LOGIN_FAILED", "ACCESS_DENIED",
			"TOKEN_EXPIRED", "PASSWORD_CHANGE", "ACCOUNT_LOCKED"};
		for (String type : eventTypes) {
			SecurityAuditLog log = new SecurityAuditLog();
			log.setEventType(type);
			assertEquals(type, log.getEventType());
		}
	}

	@Test
	public void testSuspiciousFlag() {
		SecurityAuditLog log = new SecurityAuditLog();
		assertFalse(log.isSuspicious());

		log.setSuspicious(true);
		assertTrue(log.isSuspicious());
	}

	@Test
	public void testAccessDeniedLog() {
		SecurityAuditLog log = new SecurityAuditLog();
		log.setEventType("ACCESS_DENIED");
		log.setRequestPath("/api/security/logs");
		log.setDetails("Nível de acesso insuficiente. Requerido: 50, Atual: 15");

		assertEquals("ACCESS_DENIED", log.getEventType());
		assertNotNull(log.getRequestPath());
		assertTrue(log.getDetails().contains("Nível de acesso insuficiente"));
	}
}
