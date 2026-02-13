import React from "react";
import {Link} from "react-router";
import StrategicObjectiveStore from "forpdi/jsx/planning/store/StrategicObjective.jsx";
import Toastr from 'toastr';
import Modal from "forpdi/jsx/core/widget/Modal.jsx";

export default React.createClass({
	contextTypes: {
		router: React.PropTypes.object,
		accessLevel: React.PropTypes.number.isRequired,
		roles: React.PropTypes.object.isRequired,
	},
	getInitialState() {
		return {
			projects: [],
			objective: null,
			loading: true,
			showForm: false,
			editingProject: null,
			formData: {
				name: "", description: "", startDate: "", endDate: "",
				status: "NOT_STARTED", priority: "MEDIUM", budget: 0, budgetExecuted: 0
			}
		};
	},
	componentDidMount() {
		var me = this;

		StrategicObjectiveStore.on("projectsListed", (response) => {
			if (response.success) {
				me.setState({
					projects: response.data.list || [],
					loading: false
				});
			} else {
				me.setState({ loading: false });
			}
		}, me);

		StrategicObjectiveStore.on("projectCreated", (response) => {
			if (response.success) {
				Toastr.success("Projeto criado com sucesso.");
				me.setState({ showForm: false, editingProject: null });
				me.loadProjects();
			} else {
				Toastr.error(response.message || "Erro ao criar projeto.");
			}
		}, me);

		StrategicObjectiveStore.on("projectUpdated", (response) => {
			if (response.success) {
				Toastr.success("Projeto atualizado com sucesso.");
				me.setState({ showForm: false, editingProject: null });
				me.loadProjects();
			}
		}, me);

		StrategicObjectiveStore.on("projectDeleted", (response) => {
			if (response.success) {
				Toastr.success("Projeto removido com sucesso.");
				me.loadProjects();
			}
		}, me);

		this.loadProjects();
	},
	componentWillUnmount() {
		StrategicObjectiveStore.off(null, null, this);
	},
	loadProjects() {
		this.setState({ loading: true });
		StrategicObjectiveStore.dispatch({
			action: StrategicObjectiveStore.ACTION_LIST_PROJECTS,
			data: { objectiveId: this.props.params.objectiveId }
		});
	},
	toggleForm(project) {
		if (project) {
			this.setState({
				showForm: true,
				editingProject: project,
				formData: {
					name: project.name,
					description: project.description || "",
					startDate: project.startDate ? project.startDate.substring(0, 10) : "",
					endDate: project.endDate ? project.endDate.substring(0, 10) : "",
					status: project.status || "NOT_STARTED",
					priority: project.priority || "MEDIUM",
					budget: project.budget || 0,
					budgetExecuted: project.budgetExecuted || 0
				}
			});
		} else {
			this.setState({
				showForm: !this.state.showForm,
				editingProject: null,
				formData: {
					name: "", description: "", startDate: "", endDate: "",
					status: "NOT_STARTED", priority: "MEDIUM", budget: 0, budgetExecuted: 0
				}
			});
		}
	},
	handleInputChange(field, event) {
		var formData = this.state.formData;
		formData[field] = event.target.value;
		this.setState({ formData: formData });
	},
	saveProject() {
		var data = this.state.formData;
		if (!data.name || data.name.trim() === "") {
			Toastr.error("Nome do projeto é obrigatório.");
			return;
		}
		var project = {
			name: data.name,
			description: data.description,
			startDate: data.startDate || null,
			endDate: data.endDate || null,
			status: data.status,
			priority: data.priority,
			budget: parseFloat(data.budget) || 0,
			budgetExecuted: parseFloat(data.budgetExecuted) || 0,
			strategicObjective: { id: parseInt(this.props.params.objectiveId) }
		};
		if (this.state.editingProject) {
			project.id = this.state.editingProject.id;
			StrategicObjectiveStore.dispatch({
				action: StrategicObjectiveStore.ACTION_UPDATE_PROJECT,
				data: { project: project }
			});
		} else {
			StrategicObjectiveStore.dispatch({
				action: StrategicObjectiveStore.ACTION_CREATE_PROJECT,
				data: { project: project }
			});
		}
	},
	deleteProject(id) {
		Modal.confirmCustom(
			() => {
				StrategicObjectiveStore.dispatch({
					action: StrategicObjectiveStore.ACTION_DELETE_PROJECT,
					data: { projectId: id }
				});
				Modal.hide();
			},
			"Tem certeza que deseja excluir este projeto?",
			() => { Modal.hide(); }
		);
	},
	getPriorityLabel(priority) {
		var labels = { "LOW": "Baixa", "MEDIUM": "Média", "HIGH": "Alta", "CRITICAL": "Crítica" };
		return labels[priority] || priority;
	},
	getPriorityClass(priority) {
		var classes = {
			"LOW": "label label-info", "MEDIUM": "label label-warning",
			"HIGH": "label label-danger", "CRITICAL": "label label-danger"
		};
		return classes[priority] || "label label-default";
	},
	render() {
		var canEdit = this.context.roles.ADMIN || this.context.accessLevel >= 30;

		return (
			<div className="container-fluid">
				<div className="row">
					<div className="col-md-12">
						<h2 className="marginBottom20">
							<span className="mdi mdi-folder-multiple icon-link" /> Projetos do Objetivo
						</h2>

						{canEdit && (
							<button className="btn btn-primary marginBottom20" onClick={() => this.toggleForm(null)}>
								<span className="mdi mdi-plus" /> Novo Projeto
							</button>
						)}

						{this.state.showForm && (
							<div className="panel panel-default marginBottom20">
								<div className="panel-heading">
									<h3 className="panel-title">
										{this.state.editingProject ? "Editar Projeto" : "Novo Projeto"}
									</h3>
								</div>
								<div className="panel-body">
									<div className="form-group">
										<label>Nome *</label>
										<input type="text" className="form-control" value={this.state.formData.name}
											onChange={this.handleInputChange.bind(this, "name")}
											placeholder="Nome do projeto" maxLength="500" />
									</div>
									<div className="form-group">
										<label>Descrição</label>
										<textarea className="form-control" rows="3" value={this.state.formData.description}
											onChange={this.handleInputChange.bind(this, "description")} maxLength="4000" />
									</div>
									<div className="row">
										<div className="col-md-3">
											<div className="form-group">
												<label>Data Início</label>
												<input type="date" className="form-control" value={this.state.formData.startDate}
													onChange={this.handleInputChange.bind(this, "startDate")} />
											</div>
										</div>
										<div className="col-md-3">
											<div className="form-group">
												<label>Data Fim</label>
												<input type="date" className="form-control" value={this.state.formData.endDate}
													onChange={this.handleInputChange.bind(this, "endDate")} />
											</div>
										</div>
										<div className="col-md-3">
											<div className="form-group">
												<label>Status</label>
												<select className="form-control" value={this.state.formData.status}
													onChange={this.handleInputChange.bind(this, "status")}>
													<option value="NOT_STARTED">Não Iniciado</option>
													<option value="IN_PROGRESS">Em Andamento</option>
													<option value="COMPLETED">Concluído</option>
													<option value="DELAYED">Atrasado</option>
													<option value="CANCELLED">Cancelado</option>
												</select>
											</div>
										</div>
										<div className="col-md-3">
											<div className="form-group">
												<label>Prioridade</label>
												<select className="form-control" value={this.state.formData.priority}
													onChange={this.handleInputChange.bind(this, "priority")}>
													<option value="LOW">Baixa</option>
													<option value="MEDIUM">Média</option>
													<option value="HIGH">Alta</option>
													<option value="CRITICAL">Crítica</option>
												</select>
											</div>
										</div>
									</div>
									<div className="row">
										<div className="col-md-6">
											<div className="form-group">
												<label>Orçamento (R$)</label>
												<input type="number" className="form-control" value={this.state.formData.budget}
													onChange={this.handleInputChange.bind(this, "budget")} min="0" step="0.01" />
											</div>
										</div>
										<div className="col-md-6">
											<div className="form-group">
												<label>Orçamento Executado (R$)</label>
												<input type="number" className="form-control" value={this.state.formData.budgetExecuted}
													onChange={this.handleInputChange.bind(this, "budgetExecuted")} min="0" step="0.01" />
											</div>
										</div>
									</div>
									<button className="btn btn-success marginRight10" onClick={this.saveProject}>
										<span className="mdi mdi-check" /> Salvar
									</button>
									<button className="btn btn-default" onClick={() => this.toggleForm(null)}>
										Cancelar
									</button>
								</div>
							</div>
						)}

						{this.state.loading ? (
							<div className="text-center"><span className="mdi mdi-loading mdi-spin mdi-24px" /> Carregando...</div>
						) : (
							<div className="table-responsive">
								{this.state.projects.length === 0 ? (
									<div className="alert alert-info">Nenhum projeto cadastrado para este objetivo.</div>
								) : (
									<table className="table table-hover table-striped">
										<thead>
											<tr>
												<th>Nome</th>
												<th>Status</th>
												<th>Prioridade</th>
												<th>Progresso</th>
												<th>Orçamento</th>
												<th>Planos de Ação</th>
												{canEdit && <th>Ações</th>}
											</tr>
										</thead>
										<tbody>
											{this.state.projects.map((p, idx) => (
												<tr key={"proj-" + idx}>
													<td>
														<Link to={"/project/" + p.id + "/actions"}>
															<strong>{p.name}</strong>
														</Link>
													</td>
													<td><span className={this.getPriorityClass(p.status === "DELAYED" ? "HIGH" : "MEDIUM")}>
														{this.getPriorityLabel(p.status) || p.status}</span></td>
													<td><span className={this.getPriorityClass(p.priority)}>{this.getPriorityLabel(p.priority)}</span></td>
													<td>
														<div className="progress" style={{ marginBottom: 0, minWidth: 80 }}>
															<div className="progress-bar" style={{ width: (p.progress || 0) + "%" }}>
																{(p.progress || 0).toFixed(1)}%
															</div>
														</div>
													</td>
													<td>R$ {(p.budget || 0).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}</td>
													<td><span className="badge">{p.actionPlanCount || 0}</span></td>
													{canEdit && (
														<td>
															<button className="btn btn-xs btn-default marginRight5"
																onClick={() => this.toggleForm(p)}>
																<span className="mdi mdi-pencil" />
															</button>
															<button className="btn btn-xs btn-danger"
																onClick={() => this.deleteProject(p.id)}>
																<span className="mdi mdi-delete" />
															</button>
														</td>
													)}
												</tr>
											))}
										</tbody>
									</table>
								)}
							</div>
						)}
					</div>
				</div>
			</div>
		);
	}
});
