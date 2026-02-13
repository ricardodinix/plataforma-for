package org.forpdi.planning.structure;

import javax.inject.Inject;

import org.forpdi.core.abstractions.AbstractController;
import org.forpdi.core.company.CompanyDomain;
import org.forpdi.core.event.Current;
import org.forpdi.core.user.User;
import org.forpdi.core.user.UserBS;
import org.forpdi.planning.plan.PlanMacro;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.boilerplate.NoCache;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Controller REST para Desdobramento Estratégico.
 * Gerencia o cascateamento de Objetivos Estratégicos -> Projetos -> Planos de Ação.
 */
@Controller
public class StrategicObjectiveController extends AbstractController {

	@Inject
	@Current
	private CompanyDomain domain;
	@Inject
	private StrategicObjectiveBS bs;
	@Inject
	private UserBS userBS;

	// --- Strategic Objectives ---

	@Post(BASEPATH + "/strategic-objective")
	@NoCache
	@Consumes
	public void createObjective(StrategicObjective objective) {
		try {
			if (objective.getName() == null || objective.getName().trim().isEmpty()) {
				this.fail("Nome do objetivo é obrigatório.");
				return;
			}
			StrategicObjective result = this.bs.createObjective(objective);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao criar objetivo estratégico", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Put(BASEPATH + "/strategic-objective")
	@NoCache
	@Consumes
	public void updateObjective(StrategicObjective objective) {
		try {
			StrategicObjective result = this.bs.updateObjective(objective);
			if (result == null) {
				this.fail("Objetivo não encontrado.");
				return;
			}
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao atualizar objetivo estratégico", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Delete(BASEPATH + "/strategic-objective/{id}")
	@NoCache
	public void deleteObjective(Long id) {
		try {
			this.bs.deleteObjective(id);
			this.success();
		} catch (Throwable ex) {
			LOGGER.error("Erro ao deletar objetivo estratégico", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/strategic-objective/list")
	@NoCache
	public void listRootObjectives(Long planMacroId, Integer page, Integer pageSize) {
		try {
			PaginatedList<StrategicObjective> result = this.bs.listRootObjectives(planMacroId, page, pageSize);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar objetivos estratégicos", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/strategic-objective/{id}/children")
	@NoCache
	public void listChildren(Long id) {
		try {
			PaginatedList<StrategicObjective> result = this.bs.listChildren(id);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar filhos do objetivo", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/strategic-objective/{id}/tree")
	@NoCache
	public void retrieveTree(Long id) {
		try {
			StrategicObjective result = this.bs.retrieveTree(id);
			if (result == null) {
				this.fail("Objetivo não encontrado.");
				return;
			}
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao recuperar árvore de desdobramento", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/strategic-objective/{id}/progress")
	@NoCache
	public void calculateProgress(Long id) {
		try {
			Double progress = this.bs.calculateObjectiveProgress(id);
			this.success(progress);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao calcular progresso", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/strategic-objective/responsible/{userId}")
	@NoCache
	public void listByResponsible(Long userId) {
		try {
			PaginatedList<StrategicObjective> result = this.bs.listByResponsible(userId);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar objetivos por responsável", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	// --- Projects ---

	@Post(BASEPATH + "/project")
	@NoCache
	@Consumes
	public void createProject(Project project) {
		try {
			if (project.getName() == null || project.getName().trim().isEmpty()) {
				this.fail("Nome do projeto é obrigatório.");
				return;
			}
			Project result = this.bs.createProject(project);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao criar projeto", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Put(BASEPATH + "/project")
	@NoCache
	@Consumes
	public void updateProject(Project project) {
		try {
			Project result = this.bs.updateProject(project);
			if (result == null) {
				this.fail("Projeto não encontrado.");
				return;
			}
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao atualizar projeto", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Delete(BASEPATH + "/project/{id}")
	@NoCache
	public void deleteProject(Long id) {
		try {
			this.bs.deleteProject(id);
			this.success();
		} catch (Throwable ex) {
			LOGGER.error("Erro ao deletar projeto", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/project/objective/{objectiveId}")
	@NoCache
	public void listProjectsByObjective(Long objectiveId) {
		try {
			PaginatedList<Project> result = this.bs.listProjectsByObjective(objectiveId);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar projetos", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/project/responsible/{userId}")
	@NoCache
	public void listProjectsByResponsible(Long userId) {
		try {
			PaginatedList<Project> result = this.bs.listProjectsByResponsible(userId);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar projetos por responsável", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	// --- Action Plans ---

	@Post(BASEPATH + "/project-action-plan")
	@NoCache
	@Consumes
	public void createActionPlan(ProjectActionPlan actionPlan) {
		try {
			if (actionPlan.getDescription() == null || actionPlan.getDescription().trim().isEmpty()) {
				this.fail("Descrição do plano de ação é obrigatória.");
				return;
			}
			ProjectActionPlan result = this.bs.createActionPlan(actionPlan);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao criar plano de ação", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Put(BASEPATH + "/project-action-plan")
	@NoCache
	@Consumes
	public void updateActionPlan(ProjectActionPlan actionPlan) {
		try {
			ProjectActionPlan result = this.bs.updateActionPlan(actionPlan);
			if (result == null) {
				this.fail("Plano de ação não encontrado.");
				return;
			}
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao atualizar plano de ação", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Delete(BASEPATH + "/project-action-plan/{id}")
	@NoCache
	public void deleteActionPlan(Long id) {
		try {
			this.bs.deleteActionPlan(id);
			this.success();
		} catch (Throwable ex) {
			LOGGER.error("Erro ao deletar plano de ação", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/project-action-plan/project/{projectId}")
	@NoCache
	public void listActionPlansByProject(Long projectId) {
		try {
			PaginatedList<ProjectActionPlan> result = this.bs.listActionPlansByProject(projectId);
			this.success(result);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar planos de ação", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	@Get(BASEPATH + "/project-action-plan/overdue")
	@NoCache
	public void listOverdueActionPlans() {
		try {
			this.success(this.bs.listOverdueActionPlans());
		} catch (Throwable ex) {
			LOGGER.error("Erro ao listar planos de ação atrasados", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}
}
