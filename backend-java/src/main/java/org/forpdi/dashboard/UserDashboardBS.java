package org.forpdi.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.forpdi.core.company.CompanyDomain;
import org.forpdi.core.event.Current;
import org.forpdi.core.user.User;
import org.forpdi.core.user.auth.UserSession;
import org.forpdi.planning.structure.Project;
import org.forpdi.planning.structure.ProjectActionPlan;
import org.forpdi.planning.structure.StrategicObjective;
import org.forpdi.planning.structure.StrategicObjectiveBS;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.boilerplate.HibernateBusiness;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Business Service para a Interface por Usuário.
 * Fornece dados para a tela inicial individualizada com responsabilidades e status.
 */
@RequestScoped
public class UserDashboardBS extends HibernateBusiness {

	@Inject
	@Current
	private CompanyDomain domain;
	@Inject
	private UserSession userSession;
	@Inject
	private StrategicObjectiveBS strategicBS;

	/**
	 * Recuperar o resumo do dashboard do usuário logado.
	 */
	public Map<String, Object> getUserDashboardSummary() {
		Map<String, Object> summary = new HashMap<>();
		User user = this.userSession.getUser();

		if (user == null || this.domain == null || this.domain.getCompany() == null) {
			return summary;
		}

		// Objetivos do usuário
		PaginatedList<StrategicObjective> objectives = this.strategicBS.listByResponsible(user.getId());
		summary.put("objectives", objectives.getList());
		summary.put("objectiveCount", objectives.getTotal());

		// Projetos do usuário
		PaginatedList<Project> projects = this.strategicBS.listProjectsByResponsible(user.getId());
		summary.put("projects", projects.getList());
		summary.put("projectCount", projects.getTotal());

		// Planos de ação atrasados
		List<ProjectActionPlan> overdueActions = listUserOverdueActions(user);
		summary.put("overdueActions", overdueActions);
		summary.put("overdueCount", overdueActions.size());

		// Planos de ação próximos do vencimento
		List<ProjectActionPlan> nearDeadlineActions = listUserNearDeadlineActions(user);
		summary.put("nearDeadlineActions", nearDeadlineActions);
		summary.put("nearDeadlineCount", nearDeadlineActions.size());

		// Contadores de status
		Map<String, Integer> statusCounts = calculateStatusCounts(projects.getList());
		summary.put("statusCounts", statusCounts);

		// Progresso geral
		double overallProgress = calculateOverallProgress(projects.getList());
		summary.put("overallProgress", overallProgress);

		return summary;
	}

	/**
	 * Listar planos de ação atrasados do usuário.
	 */
	private List<ProjectActionPlan> listUserOverdueActions(User user) {
		Date now = new Date();
		Criteria criteria = this.dao.newCriteria(ProjectActionPlan.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("responsible", user));
		criteria.add(Restrictions.eq("checked", false));
		criteria.add(Restrictions.lt("endDate", now));
		criteria.add(Restrictions.ne("status", "COMPLETED"));
		criteria.add(Restrictions.ne("status", "CANCELLED"));

		return this.dao.findByCriteria(criteria, ProjectActionPlan.class);
	}

	/**
	 * Listar planos de ação próximos do vencimento do usuário.
	 */
	private List<ProjectActionPlan> listUserNearDeadlineActions(User user) {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_MONTH, 10);
		Date deadline = cal.getTime();

		Criteria criteria = this.dao.newCriteria(ProjectActionPlan.class);
		criteria.add(Restrictions.eq("deleted", false));
		criteria.add(Restrictions.eq("responsible", user));
		criteria.add(Restrictions.eq("checked", false));
		criteria.add(Restrictions.ge("endDate", now));
		criteria.add(Restrictions.le("endDate", deadline));
		criteria.add(Restrictions.ne("status", "COMPLETED"));
		criteria.add(Restrictions.ne("status", "CANCELLED"));

		return this.dao.findByCriteria(criteria, ProjectActionPlan.class);
	}

	/**
	 * Calcular contadores de status para os projetos.
	 */
	private Map<String, Integer> calculateStatusCounts(List<Project> projects) {
		Map<String, Integer> counts = new HashMap<>();
		counts.put("NOT_STARTED", 0);
		counts.put("IN_PROGRESS", 0);
		counts.put("COMPLETED", 0);
		counts.put("DELAYED", 0);
		counts.put("CANCELLED", 0);

		for (Project p : projects) {
			String status = p.getStatus() != null ? p.getStatus() : "NOT_STARTED";
			counts.put(status, counts.getOrDefault(status, 0) + 1);
		}
		return counts;
	}

	/**
	 * Calcular progresso geral do usuário.
	 */
	private double calculateOverallProgress(List<Project> projects) {
		if (projects.isEmpty()) return 0.0;
		double total = 0.0;
		for (Project p : projects) {
			total += (p.getProgress() != null ? p.getProgress() : 0.0);
		}
		return total / projects.size();
	}
}
