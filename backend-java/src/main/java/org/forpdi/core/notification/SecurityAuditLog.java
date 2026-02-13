package org.forpdi.core.notification;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.forpdi.core.company.Company;
import org.forpdi.core.user.User;

import br.com.caelum.vraptor.boilerplate.SimpleLogicalDeletableEntity;
import br.com.caelum.vraptor.serialization.SkipSerialization;

/**
 * Log de segurança para rastrear eventos de autenticação, autorização
 * e tentativas de acesso indevido.
 */
@Entity(name = SecurityAuditLog.TABLE)
@Table(name = SecurityAuditLog.TABLE, indexes = {
	@Index(columnList = "eventType,creation"),
	@Index(columnList = "ipAddress")
})
public class SecurityAuditLog extends SimpleLogicalDeletableEntity {
	public static final String TABLE = "fpdi_security_audit_log";
	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = User.class, optional = true, fetch = FetchType.EAGER)
	private User user;

	@SkipSerialization
	@ManyToOne(targetEntity = Company.class, optional = true, fetch = FetchType.EAGER)
	private Company company;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date creation = new Date();

	@Column(nullable = false, length = 50)
	private String eventType; // LOGIN, LOGOUT, LOGIN_FAILED, ACCESS_DENIED, TOKEN_EXPIRED, PASSWORD_CHANGE, ACCOUNT_LOCKED

	@Column(nullable = true, length = 100)
	private String ipAddress;

	@Column(nullable = true, length = 500)
	private String userAgent;

	@Column(nullable = true, length = 255)
	private String requestPath;

	@Column(nullable = true, length = 1000)
	private String details;

	@Column(nullable = false)
	private boolean suspicious = false;

	@Column(nullable = true, length = 255)
	private String username; // Para tentativas de login com usuário inexistente

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public boolean isSuspicious() {
		return suspicious;
	}

	public void setSuspicious(boolean suspicious) {
		this.suspicious = suspicious;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
