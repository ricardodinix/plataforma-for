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
 * Registro de auditoria completo para rastreabilidade de alterações.
 * Armazena quem fez, o que fez, quando fez e os valores antes/depois da modificação.
 */
@Entity(name = AuditLog.TABLE)
@Table(name = AuditLog.TABLE, indexes = {
	@Index(columnList = "entityType,entityId"),
	@Index(columnList = "user_id,creation")
})
public class AuditLog extends SimpleLogicalDeletableEntity {
	public static final String TABLE = "fpdi_audit_log";
	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = User.class, optional = false, fetch = FetchType.EAGER)
	private User user;

	@SkipSerialization
	@ManyToOne(targetEntity = Company.class, optional = false, fetch = FetchType.EAGER)
	private Company company;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date creation = new Date();

	@Column(nullable = false, length = 50)
	private String action; // CREATE, UPDATE, DELETE, ACCESS, EXPORT, LOGIN, LOGOUT

	@Column(nullable = false, length = 100)
	private String entityType; // Nome da entidade modificada

	@Column(nullable = true)
	private Long entityId;

	@Column(nullable = true, length = 500)
	private String entityName;

	@Column(nullable = true, length = 500)
	private String fieldChanged;

	@Column(nullable = true, length = 4000)
	private String previousValue;

	@Column(nullable = true, length = 4000)
	private String newValue;

	@Column(nullable = true, length = 100)
	private String ipAddress;

	@Column(nullable = true, length = 500)
	private String userAgent;

	@Column(nullable = true, length = 1000)
	private String details;

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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getFieldChanged() {
		return fieldChanged;
	}

	public void setFieldChanged(String fieldChanged) {
		this.fieldChanged = fieldChanged;
	}

	public String getPreviousValue() {
		return previousValue;
	}

	public void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
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

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
