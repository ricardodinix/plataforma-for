package org.forpdi.planning.fields;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

/**
 * Testes unitários para as entidades CustomField e CustomFieldValue.
 */
public class CustomFieldTest {

	@Test
	public void testCreateCustomField() {
		CustomField field = new CustomField();
		field.setName("Campo Teste");
		field.setDescription("Descrição do campo");
		field.setFieldType("TEXT");
		field.setEntityType("PROJECT");
		field.setRequired(true);
		field.setMethodology("5W2H");
		field.setSortOrder(1);

		assertEquals("Campo Teste", field.getName());
		assertEquals("Descrição do campo", field.getDescription());
		assertEquals("TEXT", field.getFieldType());
		assertEquals("PROJECT", field.getEntityType());
		assertTrue(field.isRequired());
		assertEquals("5W2H", field.getMethodology());
		assertEquals(1, field.getSortOrder());
	}

	@Test
	public void testCustomFieldDefaults() {
		CustomField field = new CustomField();
		assertNotNull(field.getCreation());
		assertTrue(field.isActive());
		assertFalse(field.isRequired());
		assertEquals(0, field.getSortOrder());
	}

	@Test
	public void testFieldTypes() {
		String[] fieldTypes = {"TEXT", "NUMBER", "DATE", "SELECT", "TEXTAREA", "CHECKBOX", "CURRENCY"};
		for (String type : fieldTypes) {
			CustomField field = new CustomField();
			field.setFieldType(type);
			assertEquals(type, field.getFieldType());
		}
	}

	@Test
	public void testEntityTypes() {
		String[] entityTypes = {"STRATEGIC_OBJECTIVE", "PROJECT", "ACTION_PLAN"};
		for (String type : entityTypes) {
			CustomField field = new CustomField();
			field.setEntityType(type);
			assertEquals(type, field.getEntityType());
		}
	}

	@Test
	public void testSelectOptions() {
		CustomField field = new CustomField();
		field.setFieldType("SELECT");
		field.setSelectOptions("[\"Opção A\", \"Opção B\", \"Opção C\"]");

		assertEquals("[\"Opção A\", \"Opção B\", \"Opção C\"]", field.getSelectOptions());
	}

	@Test
	public void testCustomFieldValue() {
		CustomField field = new CustomField();
		field.setName("Nome do Campo");
		field.setFieldType("TEXT");

		CustomFieldValue value = new CustomFieldValue();
		value.setCustomField(field);
		value.setEntityType("PROJECT");
		value.setEntityId(1L);
		value.setValue("Valor preenchido pelo usuário");

		assertNotNull(value.getCustomField());
		assertEquals("PROJECT", value.getEntityType());
		assertEquals(Long.valueOf(1L), value.getEntityId());
		assertEquals("Valor preenchido pelo usuário", value.getValue());
	}

	@Test
	public void testCustomFieldValueDefaults() {
		CustomFieldValue value = new CustomFieldValue();
		assertNotNull(value.getCreation());
		assertNotNull(value.getLastUpdate());
	}

	@Test
	public void testCustomFieldActiveToggle() {
		CustomField field = new CustomField();
		assertTrue(field.isActive());

		field.setActive(false);
		assertFalse(field.isActive());
	}

	@Test
	public void test5W2HMethodology() {
		String[] fieldNames = {
			"O Quê (What)", "Por Quê (Why)", "Onde (Where)",
			"Quem (Who)", "Quando (When)", "Como (How)", "Quanto Custa (How Much)"
		};
		String[] fieldTypes = {"TEXT", "TEXTAREA", "TEXT", "TEXT", "DATE", "TEXTAREA", "CURRENCY"};

		assertEquals(7, fieldNames.length);
		assertEquals(7, fieldTypes.length);

		for (int i = 0; i < fieldNames.length; i++) {
			CustomField field = new CustomField();
			field.setName(fieldNames[i]);
			field.setFieldType(fieldTypes[i]);
			field.setMethodology("5W2H");
			field.setSortOrder(i);

			assertEquals(fieldNames[i], field.getName());
			assertEquals(fieldTypes[i], field.getFieldType());
			assertEquals("5W2H", field.getMethodology());
			assertEquals(i, field.getSortOrder());
		}
	}
}
