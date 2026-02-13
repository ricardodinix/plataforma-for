package org.forpdi.planning.structure;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.forpdi.core.company.Company;
import org.forpdi.core.user.User;
import org.forpdi.planning.fields.actionplan.ActionPlan;

import br.com.caelum.vraptor.boilerplate.SimpleLogicalDeletableEntity;
import br.com.caelum.vraptor.serialization.SkipSerialization;

/**
 * Entidade que representa um Projeto vinculado a um Objetivo Estratégico,
 * com seus respectivos Planos de Ação.
 */
@Entity(name = Project.TABLE)
@Table(name = Project.TABLE)
public class Project extends SimpleLogicalDeletableEntity {
	public static final String TABLE = "fpdi_project";
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, length = 500)
	private String name;

	@Column(nullable = true, length = 4000)
	private String description;

	@SkipSerialization
	@ManyToOne(targetEntity = Company.class, optional = false, fetch = FetchType.EAGER)
	private Company company;

	@ManyToOne(targetEntity = StrategicObjective.class, optional = false, fetch = FetchType.EAGER)
	private StrategicObjective strategicObjective;

	@ManyToOne(targetEntity = User.class, optional = true, fetch = FetchType.EAGER)
	private User responsible;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date creation = new Date();

	@Temporal(TemporalType.DATE)
	@Column(nullable = true)
	private Date startDate;

	@Temporal(TemporalType.DATE)
	@Column(nullable = true)
	private Date endDate;

	@Column(nullable = true)
	private Double progress = 0.0;

	@Column(nullable = false, length = 50)
	private String status = "NOT_STARTED";

	@Column(nullable = true)
	private Double budget = 0.0;

	@Column(nullable = true)
	private Double budgetExecuted = 0.0;

	@Column(nullable = true, length = 50)
	private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, CRITICAL

	@Transient
	private List<ProjectActionPlan> actionPlans;

	@Transient
	private int actionPlanCount;

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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public StrategicObjective getStrategicObjective() {
		return strategicObjective;
	}

	public void setStrategicObjective(StrategicObjective strategicObjective) {
		this.strategicObjective = strategicObjective;
	}

	public User getResponsible() {
		return responsible;
	}

	public void setResponsible(User responsible) {
		this.responsible = responsible;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Double getProgress() {
		return progress;
	}

	public void setProgress(Double progress) {
		this.progress = progress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public Double getBudgetExecuted() {
		return budgetExecuted;
	}

	public void setBudgetExecuted(Double budgetExecuted) {
		this.budgetExecuted = budgetExecuted;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public List<ProjectActionPlan> getActionPlans() {
		return actionPlans;
	}

	public void setActionPlans(List<ProjectActionPlan> actionPlans) {
		this.actionPlans = actionPlans;
	}

	public int getActionPlanCount() {
		return actionPlanCount;
	}

	public void setActionPlanCount(int actionPlanCount) {
		this.actionPlanCount = actionPlanCount;
	}
}
