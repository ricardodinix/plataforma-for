package org.forpdi.planning.fields;

import java.util.List;

import javax.inject.Inject;

import org.forpdi.core.abstractions.AbstractController;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.boilerplate.NoCache;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Controller REST para Campos Personalizados e Customização Metodológica.
 * Permite criar campos personalizados com suporte a metodologias como 5W2H.
 */
@Controller
public class CustomFieldController extends AbstractController {

	@Inject
	private CustomFieldBS bs;

	// --- Custom Fields ---

	@Post(BASEPATH + "/custom-field")
	@NoCache
	@Consumes
	public void createField(CustomField field) {
		try {
			if (field.getName() == null || field.getName().trim().isEmpty()) {
				this.fail("Nome do campo é obrigatório.");
				return;
			}
			if (field.getFieldType() == null || field.getFieldType().trim().isEmpty()) {
				this.fail("Tipo do campo é obrigatório.");
				return;
			}
			CustomField result = this.bs.createField(field);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao criar campo personalizado", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Put(BASEPATH + "/custom-field")
	@NoCache
	@Consumes
	public void updateField(CustomField field) {
		try {
			CustomField result = this.bs.updateField(field);
			if (result == null) {
				this.fail("Campo não encontrado.");
				return;
			}
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao atualizar campo personalizado", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Delete(BASEPATH + "/custom-field/{id}")
	@NoCache
	public void deleteField(Long id) {
		try {
			this.bs.deleteField(id);
			this.success();
		} catch (Throwable ex) {
			LOGGER.error("Erro ao deletar campo personalizado", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/custom-field/entity-type/{entityType}")
	@NoCache
	public void listByEntityType(String entityType) {
		try {
			PaginatedList<CustomField> result = this.bs.listByEntityType(entityType);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar campos personalizados", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/custom-field/methodology/{methodology}")
	@NoCache
	public void listByMethodology(String methodology) {
		try {
			PaginatedList<CustomField> result = this.bs.listByMethodology(methodology);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar campos por metodologia", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Post(BASEPATH + "/custom-field/template/5w2h")
	@NoCache
	@Consumes
	public void create5W2HTemplate(String entityType) {
		try {
			List<CustomField> fields = this.bs.create5W2HTemplate(entityType);
			this.success(fields);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao criar template 5W2H", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	// --- Custom Field Values ---

	@Post(BASEPATH + "/custom-field-value")
	@NoCache
	@Consumes
	public void saveFieldValue(CustomFieldValue fieldValue) {
		try {
			CustomFieldValue result = this.bs.saveFieldValue(fieldValue);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao salvar valor do campo", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/custom-field-value/list")
	@NoCache
	public void listFieldValues(String entityType, Long entityId) {
		try {
			List<CustomFieldValue> result = this.bs.listFieldValues(entityType, entityId);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar valores dos campos", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}
}
