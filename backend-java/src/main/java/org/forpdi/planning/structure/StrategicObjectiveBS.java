package org.forpdi.planning.structure;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.forpdi.core.company.CompanyDomain;
import org.forpdi.core.event.Current;
import org.forpdi.core.notification.AuditLog;
import org.forpdi.core.notification.AuditBS;
import org.forpdi.core.user.User;
import org.forpdi.core.user.auth.UserSession;
import org.forpdi.planning.plan.PlanMacro;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.boilerplate.HibernateBusiness;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Business Service para Desdobramento Estratégico.
 * Gerencia o cascateamento de Objetivos Estratégicos até Projetos e Planos de Ação.
 */
@RequestScoped
public class StrategicObjectiveBS extends HibernateBusiness {

	@Inject
	@Current
	private CompanyDomain domain;
	@Inject
	private UserSession userSession;
	@Inject
	private AuditBS auditBS;

	/**
	 * Criar um novo Objetivo Estratégico.
	 */
	public StrategicObjective createObjective(StrategicObjective objective) {
		objective.setCompany(this.domain.getCompany());
		objective.setCreation(new Date());
		this.persist(objective);
		this.auditBS.logAction("CREATE", "StrategicObjective", objective.getId(),
				objective.getName(), null, null, null);
		return objective;
	}

	/**
	 * Atualizar um Objetivo Estratégico existente.
	 */
	public StrategicObjective updateObjective(StrategicObjective objective) {
		StrategicObjective existing = this.exists(objective.getId(), StrategicObjective.class);
		if (existing == null) {
			return null;
		}
		String oldName = existing.getName();
		existing.setName(objective.getName());
		existing.setDescription(objective.getDescription());
		existing.setStartDate(objective.getStartDate());
		existing.setEndDate(objective.getEndDate());
		existing.setStatus(objective.getStatus());
		existing.setProgress(objective.getProgress());
		existing.setWeight(objective.getWeight());
		if (objective.getResponsible() != null) {
			existing.setResponsible(objective.getResponsible());
		}
		this.persist(existing);
		this.auditBS.logAction("UPDATE", "StrategicObjective", existing.getId(),
				existing.getName(), "name", oldName, existing.getName());
		return existing;
	}

	/**
	 * Deletar (soft delete) um Objetivo Estratégico.
	 */
	public void deleteObjective(Long id) {
		StrategicObjective objective = this.exists(id, StrategicObjective.class);
		if (objective != null) {
			objective.setDeleted(true);
			this.persist(objective);
			this.auditBS.logAction("DELETE", "StrategicObjective", objective.getId(),
					objective.getName(), null, null, null);
		}
	}

	/**
	 * Listar objetivos raiz (sem pai) de um plano macro.
	 */
	public PaginatedList<StrategicObjective> listRootObjectives(Long planMacroId, Integer page, Integer pageSize) {
		if (page == null || page < 1) page = 1;
		if (pageSize == null || pageSize <= 0) pageSize = 10;

		PlanMacro planMacro = this.exists(planMacroId, PlanMacro.class);
		if (planMacro == null) {
			return new PaginatedList<>(new ArrayList<>(0), 0L);
		}

		Criteria criteria = this.dao.newCriteria(StrategicObjective.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("planMacro", planMacro));
		criteria.add(Restrictions.isNull("parent"));
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult((page - 1) * pageSize);
		criteria.setMaxResults(pageSize);

		Criteria counting = this.dao.newCriteria(StrategicObjective.class);
		counting.add(Restrictions.eq("deleted", false));
		counting.add(Restrictions.eq("planMacro", planMacro));
		counting.add(Restrictions.isNull("parent"));
		counting.setProjection(Projections.countDistinct("id"));

		List<StrategicObjective> list = this.dao.findByCriteria(criteria, StrategicObjective.class);
		Long total = (Long) counting.uniqueResult();

		for (StrategicObjective obj : list) {
			obj.setChildCount(countChildren(obj.getId()));
			obj.setProjectCount(countProjects(obj.getId()));
		}

		PaginatedList<StrategicObjective> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal(total);
		return result;
	}

	/**
	 * Listar filhos de um objetivo estratégico (cascateamento).
	 */
	public PaginatedList<StrategicObjective> listChildren(Long parentId) {
		Criteria criteria = this.dao.newCriteria(StrategicObjective.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("parent.id", parentId));
		criteria.addOrder(Order.asc("name"));

		List<StrategicObjective> list = this.dao.findByCriteria(criteria, StrategicObjective.class);
		for (StrategicObjective obj : list) {
			obj.setChildCount(countChildren(obj.getId()));
			obj.setProjectCount(countProjects(obj.getId()));
		}

		PaginatedList<StrategicObjective> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal((long) list.size());
		return result;
	}

	/**
	 * Recuperar árvore completa de desdobramento.
	 */
	public StrategicObjective retrieveTree(Long objectiveId) {
		StrategicObjective objective = this.exists(objectiveId, StrategicObjective.class);
		if (objective == null) return null;

		List<StrategicObjective> children = listChildren(objectiveId).getList();
		for (StrategicObjective child : children) {
			child.setChildren(listChildren(child.getId()).getList());
		}
		objective.setChildren(children);
		objective.setProjects(listProjectsByObjective(objectiveId).getList());
		return objective;
	}

	/**
	 * Contar filhos de um objetivo.
	 */
	private int countChildren(Long parentId) {
		Criteria criteria = this.dao.newCriteria(StrategicObjective.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("parent.id", parentId));
		criteria.setProjection(Projections.countDistinct("id"));
		Long count = (Long) criteria.uniqueResult();
		return count != null ? count.intValue() : 0;
	}

	/**
	 * Contar projetos de um objetivo.
	 */
	private int countProjects(Long objectiveId) {
		Criteria criteria = this.dao.newCriteria(Project.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("strategicObjective.id", objectiveId));
		criteria.setProjection(Projections.countDistinct("id"));
		Long count = (Long) criteria.uniqueResult();
		return count != null ? count.intValue() : 0;
	}

	// --- Project management ---

	public Project createProject(Project project) {
		project.setCompany(this.domain.getCompany());
		project.setCreation(new Date());
		this.persist(project);
		this.auditBS.logAction("CREATE", "Project", project.getId(),
				project.getName(), null, null, null);
		return project;
	}

	public Project updateProject(Project project) {
		Project existing = this.exists(project.getId(), Project.class);
		if (existing == null) return null;
		String oldName = existing.getName();
		existing.setName(project.getName());
		existing.setDescription(project.getDescription());
		existing.setStartDate(project.getStartDate());
		existing.setEndDate(project.getEndDate());
		existing.setStatus(project.getStatus());
		existing.setProgress(project.getProgress());
		existing.setBudget(project.getBudget());
		existing.setBudgetExecuted(project.getBudgetExecuted());
		existing.setPriority(project.getPriority());
		if (project.getResponsible() != null) {
			existing.setResponsible(project.getResponsible());
		}
		this.persist(existing);
		this.auditBS.logAction("UPDATE", "Project", existing.getId(),
				existing.getName(), "name", oldName, existing.getName());
		return existing;
	}

	public void deleteProject(Long id) {
		Project project = this.exists(id, Project.class);
		if (project != null) {
			project.setDeleted(true);
			this.persist(project);
			this.auditBS.logAction("DELETE", "Project", project.getId(),
					project.getName(), null, null, null);
		}
	}

	public PaginatedList<Project> listProjectsByObjective(Long objectiveId) {
		Criteria criteria = this.dao.newCriteria(Project.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("strategicObjective.id", objectiveId));
		criteria.addOrder(Order.asc("name"));

		List<Project> list = this.dao.findByCriteria(criteria, Project.class);
		for (Project p : list) {
			p.setActionPlanCount(countActionPlans(p.getId()));
		}
		PaginatedList<Project> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal((long) list.size());
		return result;
	}

	private int countActionPlans(Long projectId) {
		Criteria criteria = this.dao.newCriteria(ProjectActionPlan.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("project.id", projectId));
		criteria.setProjection(Projections.countDistinct("id"));
		Long count = (Long) criteria.uniqueResult();
		return count != null ? count.intValue() : 0;
	}

	// --- Action Plan management ---

	public ProjectActionPlan createActionPlan(ProjectActionPlan actionPlan) {
		actionPlan.setCreation(new Date());
		this.persist(actionPlan);
		this.auditBS.logAction("CREATE", "ProjectActionPlan", actionPlan.getId(),
				actionPlan.getDescription(), null, null, null);
		return actionPlan;
	}

	public ProjectActionPlan updateActionPlan(ProjectActionPlan actionPlan) {
		ProjectActionPlan existing = this.exists(actionPlan.getId(), ProjectActionPlan.class);
		if (existing == null) return null;
		String oldDesc = existing.getDescription();
		existing.setDescription(actionPlan.getDescription());
		existing.setWhat(actionPlan.getWhat());
		existing.setWhy(actionPlan.getWhy());
		existing.setWhereField(actionPlan.getWhereField());
		existing.setWho(actionPlan.getWho());
		existing.setWhenDate(actionPlan.getWhenDate());
		existing.setHow(actionPlan.getHow());
		existing.setHowMuch(actionPlan.getHowMuch());
		existing.setStartDate(actionPlan.getStartDate());
		existing.setEndDate(actionPlan.getEndDate());
		existing.setStatus(actionPlan.getStatus());
		existing.setProgress(actionPlan.getProgress());
		existing.setChecked(actionPlan.isChecked());
		if (actionPlan.getResponsible() != null) {
			existing.setResponsible(actionPlan.getResponsible());
		}
		this.persist(existing);
		this.auditBS.logAction("UPDATE", "ProjectActionPlan", existing.getId(),
				existing.getDescription(), "description", oldDesc, existing.getDescription());
		return existing;
	}

	public void deleteActionPlan(Long id) {
		ProjectActionPlan actionPlan = this.exists(id, ProjectActionPlan.class);
		if (actionPlan != null) {
			actionPlan.setDeleted(true);
			this.persist(actionPlan);
			this.auditBS.logAction("DELETE", "ProjectActionPlan", actionPlan.getId(),
					actionPlan.getDescription(), null, null, null);
		}
	}

	public PaginatedList<ProjectActionPlan> listActionPlansByProject(Long projectId) {
		Criteria criteria = this.dao.newCriteria(ProjectActionPlan.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("project.id", projectId));
		criteria.addOrder(Order.asc("description"));

		List<ProjectActionPlan> list = this.dao.findByCriteria(criteria, ProjectActionPlan.class);
		PaginatedList<ProjectActionPlan> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal((long) list.size());
		return result;
	}

	/**
	 * Calcular progresso de um objetivo com base nos projetos vinculados.
	 */
	public Double calculateObjectiveProgress(Long objectiveId) {
		List<Project> projects = listProjectsByObjective(objectiveId).getList();
		if (projects.isEmpty()) return 0.0;

		double totalProgress = 0.0;
		for (Project p : projects) {
			totalProgress += (p.getProgress() != null ? p.getProgress() : 0.0);
		}
		return totalProgress / projects.size();
	}

	/**
	 * Listar todos os objetivos e projetos vinculados a um usuário responsável.
	 */
	public PaginatedList<StrategicObjective> listByResponsible(Long userId) {
		User user = this.exists(userId, User.class);
		if (user == null) {
			return new PaginatedList<>(new ArrayList<>(0), 0L);
		}

		Criteria criteria = this.dao.newCriteria(StrategicObjective.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("responsible", user));
		criteria.add(Restrictions.eq("company", this.domain.getCompany()));
		criteria.addOrder(Order.asc("name"));

		List<StrategicObjective> list = this.dao.findByCriteria(criteria, StrategicObjective.class);
		PaginatedList<StrategicObjective> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal((long) list.size());
		return result;
	}

	/**
	 * Listar projetos do usuário responsável.
	 */
	public PaginatedList<Project> listProjectsByResponsible(Long userId) {
		User user = this.exists(userId, User.class);
		if (user == null) {
			return new PaginatedList<>(new ArrayList<>(0), 0L);
		}

		Criteria criteria = this.dao.newCriteria(Project.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("responsible", user));
		criteria.add(Restrictions.eq("company", this.domain.getCompany()));
		criteria.addOrder(Order.asc("name"));

		List<Project> list = this.dao.findByCriteria(criteria, Project.class);
		PaginatedList<Project> result = new PaginatedList<>();
		result.setList(list);
		result.setTotal((long) list.size());
		return result;
	}

	/**
	 * Listar planos de ação atrasados ou próximos do vencimento.
	 */
	public List<ProjectActionPlan> listOverdueActionPlans() {
		Date now = new Date();
		Criteria criteria = this.dao.newCriteria(ProjectActionPlan.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("checked", false));
		criteria.add(Restrictions.lt("endDate", now));
		criteria.add(Restrictions.ne("status", "COMPLETED"));
		criteria.add(Restrictions.ne("status", "CANCELLED"));

		return this.dao.findByCriteria(criteria, ProjectActionPlan.class);
	}

	/**
	 * Listar planos de ação próximos do vencimento (nos próximos 10 dias).
	 */
	public List<ProjectActionPlan> listNearDeadlineActionPlans() {
		Date now = new Date();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(now);
		cal.add(java.util.Calendar.DAY_OF_MONTH, 10);
		Date deadline = cal.getTime();

		Criteria criteria = this.dao.newCriteria(ProjectActionPlan.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("checked", false));
		criteria.add(Restrictions.ge("endDate", now));
		criteria.add(Restrictions.le("endDate", deadline));
		criteria.add(Restrictions.ne("status", "COMPLETED"));
		criteria.add(Restrictions.ne("status", "CANCELLED"));

		return this.dao.findByCriteria(criteria, ProjectActionPlan.class);
	}
}
