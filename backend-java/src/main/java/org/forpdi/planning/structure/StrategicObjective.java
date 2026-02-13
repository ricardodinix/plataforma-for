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
import org.forpdi.planning.plan.PlanMacro;

import br.com.caelum.vraptor.boilerplate.SimpleLogicalDeletableEntity;
import br.com.caelum.vraptor.serialization.SkipSerialization;

/**
 * Entidade que representa um Objetivo Estratégico que pode ser cascateado
 * até o nível de Projetos e Planos de Ação.
 */
@Entity(name = StrategicObjective.TABLE)
@Table(name = StrategicObjective.TABLE)
public class StrategicObjective extends SimpleLogicalDeletableEntity {
	public static final String TABLE = "fpdi_strategic_objective";
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, length = 500)
	private String name;

	@Column(nullable = true, length = 4000)
	private String description;

	@SkipSerialization
	@ManyToOne(targetEntity = Company.class, optional = false, fetch = FetchType.EAGER)
	private Company company;

	@ManyToOne(targetEntity = PlanMacro.class, optional = false, fetch = FetchType.EAGER)
	private PlanMacro planMacro;

	@ManyToOne(targetEntity = StrategicObjective.class, optional = true, fetch = FetchType.EAGER)
	private StrategicObjective parent;

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

	@Column(nullable = false)
	private int level = 0; // 0=Strategic, 1=Tactical, 2=Operational

	@Column(nullable = false, length = 50)
	private String status = "NOT_STARTED"; // NOT_STARTED, IN_PROGRESS, COMPLETED, DELAYED, CANCELLED

	@Column(nullable = true)
	private Double weight = 1.0;

	@Transient
	private List<StrategicObjective> children;

	@Transient
	private List<Project> projects;

	@Transient
	private int childCount;

	@Transient
	private int projectCount;

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

	public PlanMacro getPlanMacro() {
		return planMacro;
	}

	public void setPlanMacro(PlanMacro planMacro) {
		this.planMacro = planMacro;
	}

	public StrategicObjective getParent() {
		return parent;
	}

	public void setParent(StrategicObjective parent) {
		this.parent = parent;
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public List<StrategicObjective> getChildren() {
		return children;
	}

	public void setChildren(List<StrategicObjective> children) {
		this.children = children;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public int getChildCount() {
		return childCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	public int getProjectCount() {
		return projectCount;
	}

	public void setProjectCount(int projectCount) {
		this.projectCount = projectCount;
	}
}
