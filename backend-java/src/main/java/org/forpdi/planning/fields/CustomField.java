package org.forpdi.planning.fields;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.forpdi.core.company.Company;

import br.com.caelum.vraptor.boilerplate.SimpleLogicalDeletableEntity;
import br.com.caelum.vraptor.serialization.SkipSerialization;

/**
 * Campo personalizado que permite customização metodológica.
 * Suporta diferentes tipos de campo (texto, número, data, seleção, etc.)
 * e pode ser vinculado a entidades como Projetos, Objetivos e Planos de Ação.
 */
@Entity(name = CustomField.TABLE)
@Table(name = CustomField.TABLE)
public class CustomField extends SimpleLogicalDeletableEntity {
	public static final String TABLE = "fpdi_custom_field";
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(nullable = true, length = 500)
	private String description;

	@Column(nullable = false, length = 50)
	private String fieldType; // TEXT, NUMBER, DATE, SELECT, TEXTAREA, CHECKBOX, CURRENCY

	@Column(nullable = false, length = 100)
	private String entityType; // STRATEGIC_OBJECTIVE, PROJECT, ACTION_PLAN

	@SkipSerialization
	@ManyToOne(targetEntity = Company.class, optional = false, fetch = FetchType.EAGER)
	private Company company;

	@Column(nullable = false)
	private boolean required = false;

	@Column(nullable = true, length = 2000)
	private String selectOptions; // JSON array of options for SELECT type

	@Column(nullable = true, length = 500)
	private String defaultValue;

	@Column(nullable = true, length = 100)
	private String methodology; // 5W2H, PDCA, SWOT, etc.

	@Column(nullable = false)
	private int sortOrder = 0;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date creation = new Date();

	@Column(nullable = false)
	private boolean active = true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getSelectOptions() {
		return selectOptions;
	}

	public void setSelectOptions(String selectOptions) {
		this.selectOptions = selectOptions;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getMethodology() {
		return methodology;
	}

	public void setMethodology(String methodology) {
		this.methodology = methodology;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
