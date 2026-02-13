package org.forpdi.planning.fields;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.forpdi.core.company.CompanyDomain;
import org.forpdi.core.event.Current;
import org.forpdi.core.notification.AuditBS;
import org.forpdi.core.user.auth.UserSession;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.boilerplate.HibernateBusiness;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Business Service para Campos Personalizados.
 * Permite customização metodológica (5W2H, PDCA, etc.).
 */
@RequestScoped
public class CustomFieldBS extends HibernateBusiness {

	@Inject
	@Current
	private CompanyDomain domain;
	@Inject
	private UserSession userSession;
	@Inject
	private AuditBS auditBS;

	/**
	 * Criar um campo personalizado.
	 */
	public CustomField createField(CustomField field) {
		field.setCompany(this.domain.getCompany());
		field.setCreation(new Date());
		this.persist(field);
		this.auditBS.logAction("CREATE", "CustomField", field.getId(),
				field.getName(), null, null, null);
		return field;
	}

	/**
	 * Atualizar um campo personalizado.
	 */
	public CustomField updateField(CustomField field) {
		CustomField existing = this.exists(field.getId(), CustomField.class);
		if (existing == null) return null;

		existing.setName(field.getName());
		existing.setDescription(field.getDescription());
		existing.setFieldType(field.getFieldType());
		existing.setRequired(field.isRequired());
		existing.setSelectOptions(field.getSelectOptions());
		existing.setDefaultValue(field.getDefaultValue());
		existing.setMethodology(field.getMethodology());
		existing.setSortOrder(field.getSortOrder());
		existing.setActive(field.isActive());
		this.persist(existing);
		return existing;
	}

	/**
	 * Deletar um campo personalizado.
	 */
	public void deleteField(Long id) {
		CustomField field = this.exists(id, CustomField.class);
		if (field != null) {
			field.setDeleted(true);
			this.persist(field);
		}
	}

	/**
	 * Listar campos por tipo de entidade.
	 */
	public PaginatedList<CustomField> listByEntityType(String entityType) {
		if (this.domain == null || this.domain.getCompany() == null) {
			return new PaginatedList<>(new ArrayList<>(0), 0L);
		}

		Criteria criteria = this.dao.newCriteria(CustomField.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("active", true));
		criteria.add(Restrictions.eq("company", this.domain.getCompany()));
		criteria.add(Restrictions.eq("entityType", entityType));
		criteria.addOrder(Order.asc("sortOrder"));

		List<CustomField> list = this.dao.findByCriteria(criteria, CustomField.class);
		PaginatedList<CustomField> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal((long) list.size());
		return result;
	}

	/**
	 * Listar campos por metodologia.
	 */
	public PaginatedList<CustomField> listByMethodology(String methodology) {
		if (this.domain == null || this.domain.getCompany() == null) {
			return new PaginatedList<>(new ArrayList<>(0), 0L);
		}

		Criteria criteria = this.dao.newCriteria(CustomField.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("active", true));
		criteria.add(Restrictions.eq("company", this.domain.getCompany()));
		criteria.add(Restrictions.eq("methodology", methodology));
		criteria.addOrder(Order.asc("sortOrder"));

		List<CustomField> list = this.dao.findByCriteria(criteria, CustomField.class);
		PaginatedList<CustomField> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal((long) list.size());
		return result;
	}

	/**
	 * Criar templates de campos 5W2H para uma entidade.
	 */
	public List<CustomField> create5W2HTemplate(String entityType) {
		List<CustomField> fields = new ArrayList<>();
		String[][] fieldDefs = {
			{"O Quê (What)", "TEXT", "Descrição da ação a ser realizada"},
			{"Por Quê (Why)", "TEXTAREA", "Justificativa da ação"},
			{"Onde (Where)", "TEXT", "Local de execução"},
			{"Quem (Who)", "TEXT", "Responsável pela execução"},
			{"Quando (When)", "DATE", "Prazo de execução"},
			{"Como (How)", "TEXTAREA", "Método de execução"},
			{"Quanto Custa (How Much)", "CURRENCY", "Custo estimado"}
		};

		for (int i = 0; i < fieldDefs.length; i++) {
			CustomField field = new CustomField();
			field.setName(fieldDefs[i][0]);
			field.setFieldType(fieldDefs[i][1]);
			field.setDescription(fieldDefs[i][2]);
			field.setEntityType(entityType);
			field.setMethodology("5W2H");
			field.setSortOrder(i);
			field.setRequired(true);
			field.setCompany(this.domain.getCompany());
			field.setCreation(new Date());
			this.persist(field);
			fields.add(field);
		}

		return fields;
	}

	// --- Custom Field Values ---

	/**
	 * Salvar ou atualizar valores de campos personalizados.
	 */
	public CustomFieldValue saveFieldValue(CustomFieldValue fieldValue) {
		Criteria criteria = this.dao.newCriteria(CustomFieldValue.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("customField", fieldValue.getCustomField()));
		criteria.add(Restrictions.eq("entityType", fieldValue.getEntityType()));
		criteria.add(Restrictions.eq("entityId", fieldValue.getEntityId()));

		List<CustomFieldValue> existing = this.dao.findByCriteria(criteria, CustomFieldValue.class);
		if (!existing.isEmpty()) {
			CustomFieldValue existingValue = existing.get(0);
			String oldValue = existingValue.getValue();
			existingValue.setValue(fieldValue.getValue());
			existingValue.setLastUpdate(new Date());
			this.persist(existingValue);
			this.auditBS.logAction("UPDATE", "CustomFieldValue", existingValue.getId(),
					existingValue.getCustomField().getName(), "value", oldValue, fieldValue.getValue());
			return existingValue;
		} else {
			fieldValue.setCreation(new Date());
			fieldValue.setLastUpdate(new Date());
			this.persist(fieldValue);
			return fieldValue;
		}
	}

	/**
	 * Listar valores de campos personalizados de uma entidade.
	 */
	public List<CustomFieldValue> listFieldValues(String entityType, Long entityId) {
		Criteria criteria = this.dao.newCriteria(CustomFieldValue.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("entityType", entityType));
		criteria.add(Restrictions.eq("entityId", entityId));
		criteria.addOrder(Order.asc("id"));

		return this.dao.findByCriteria(criteria, CustomFieldValue.class);
	}
}
