package org.forpdi.planning.structure;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.forpdi.core.user.User;

import br.com.caelum.vraptor.boilerplate.SimpleLogicalDeletableEntity;

/**
 * Plano de Ação vinculado a um Projeto no contexto de desdobramento estratégico.
 * Suporta a metodologia 5W2H com campos dedicados.
 */
@Entity(name = ProjectActionPlan.TABLE)
@Table(name = ProjectActionPlan.TABLE)
public class ProjectActionPlan extends SimpleLogicalDeletableEntity {
	public static final String TABLE = "fpdi_project_action_plan";
	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = Project.class, optional = false, fetch = FetchType.EAGER)
	private Project project;

	@Column(nullable = false, length = 500)
	private String description;

	// 5W2H fields
	@Column(nullable = true, length = 1000)
	private String what; // O quê

	@Column(nullable = true, length = 1000)
	private String why; // Por quê

	@Column(nullable = true, length = 500)
	private String whereField; // Onde

	@Column(nullable = true, length = 500)
	private String who; // Quem

	@Temporal(TemporalType.DATE)
	@Column(nullable = true)
	private Date whenDate; // Quando

	@Column(nullable = true, length = 1000)
	private String how; // Como

	@Column(nullable = true)
	private Double howMuch = 0.0; // Quanto custa

	@ManyToOne(targetEntity = User.class, optional = true, fetch = FetchType.EAGER)
	private User responsible;

	@Temporal(TemporalType.DATE)
	@Column(nullable = true)
	private Date startDate;

	@Temporal(TemporalType.DATE)
	@Column(nullable = true)
	private Date endDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date creation = new Date();

	@Column(nullable = false)
	private boolean checked = false;

	@Column(nullable = false, length = 50)
	private String status = "NOT_STARTED";

	@Column(nullable = true)
	private Double progress = 0.0;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWhat() {
		return what;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	public String getWhy() {
		return why;
	}

	public void setWhy(String why) {
		this.why = why;
	}

	public String getWhereField() {
		return whereField;
	}

	public void setWhereField(String whereField) {
		this.whereField = whereField;
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public Date getWhenDate() {
		return whenDate;
	}

	public void setWhenDate(Date whenDate) {
		this.whenDate = whenDate;
	}

	public String getHow() {
		return how;
	}

	public void setHow(String how) {
		this.how = how;
	}

	public Double getHowMuch() {
		return howMuch;
	}

	public void setHowMuch(Double howMuch) {
		this.howMuch = howMuch;
	}

	public User getResponsible() {
		return responsible;
	}

	public void setResponsible(User responsible) {
		this.responsible = responsible;
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

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getProgress() {
		return progress;
	}

	public void setProgress(Double progress) {
		this.progress = progress;
	}
}
