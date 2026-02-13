package org.forpdi.planning.fields;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.caelum.vraptor.boilerplate.SimpleLogicalDeletableEntity;

/**
 * Valor de um campo personalizado vinculado a uma entidade específica.
 */
@Entity(name = CustomFieldValue.TABLE)
@Table(name = CustomFieldValue.TABLE, indexes = {
	@Index(columnList = "entityType,entityId")
})
public class CustomFieldValue extends SimpleLogicalDeletableEntity {
	public static final String TABLE = "fpdi_custom_field_value";
	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = CustomField.class, optional = false, fetch = FetchType.EAGER)
	private CustomField customField;

	@Column(nullable = false, length = 100)
	private String entityType;

	@Column(nullable = false)
	private Long entityId;

	@Column(nullable = true, length = 4000)
	private String value;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date creation = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date lastUpdate = new Date();

	public CustomField getCustomField() {
		return customField;
	}

	public void setCustomField(CustomField customField) {
		this.customField = customField;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
}
