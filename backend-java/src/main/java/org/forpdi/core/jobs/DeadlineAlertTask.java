package org.forpdi.core.jobs;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.forpdi.core.company.CompanyDomain;
import org.forpdi.core.event.Current;
import org.forpdi.core.notification.NotificationBS;
import org.forpdi.core.notification.NotificationType;
import org.forpdi.planning.structure.ProjectActionPlan;
import org.forpdi.planning.structure.StrategicObjectiveBS;

/**
 * Tarefa agendada para verificar vencimentos de prazos e enviar alertas automáticos.
 * Verifica Planos de Ação atrasados e próximos do vencimento.
 */
@RequestScoped
public class DeadlineAlertTask {

	@Inject
	private StrategicObjectiveBS strategicBS;
	@Inject
	private NotificationBS notificationBS;
	@Inject
	@Current
	private CompanyDomain domain;

	/**
	 * Verificar prazos e enviar alertas para planos de ação atrasados.
	 */
	public void checkOverdueActionPlans() {
		try {
			List<ProjectActionPlan> overdue = this.strategicBS.listOverdueActionPlans();
			for (ProjectActionPlan plan : overdue) {
				if (plan.getResponsible() != null) {
					this.notificationBS.sendNotification(
						NotificationType.LATE_ACTION_PLAN,
						plan.getDescription(),
						plan.getProject() != null ? plan.getProject().getName() : "",
						plan.getResponsible().getId(),
						this.domain != null ? this.domain.getBaseUrl() : ""
					);
				}
			}
		} catch (Exception e) {
			// Log silenciosamente - tarefas agendadas não devem parar o sistema
		}
	}

	/**
	 * Verificar prazos próximos do vencimento e enviar alertas.
	 */
	public void checkNearDeadlineActionPlans() {
		try {
			List<ProjectActionPlan> nearDeadline = this.strategicBS.listNearDeadlineActionPlans();
			for (ProjectActionPlan plan : nearDeadline) {
				if (plan.getResponsible() != null) {
					this.notificationBS.sendNotification(
						NotificationType.ACTION_PLAN_CLOSE_TO_MATURITY,
						plan.getDescription(),
						plan.getProject() != null ? plan.getProject().getName() : "",
						plan.getResponsible().getId(),
						this.domain != null ? this.domain.getBaseUrl() : ""
					);
				}
			}
		} catch (Exception e) {
			// Log silenciosamente
		}
	}
}
