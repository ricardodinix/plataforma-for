import React from "react";
import {Link} from "react-router";
import StrategicObjectiveStore from "forpdi/jsx/planning/store/StrategicObjective.jsx";
import UserSession from "forpdi/jsx/core/store/UserSession.jsx";
import Toastr from 'toastr';
import Modal from "forpdi/jsx/core/widget/Modal.jsx";

export default React.createClass({
	contextTypes: {
		router: React.PropTypes.object,
		accessLevel: React.PropTypes.number.isRequired,
		permissions: React.PropTypes.array.isRequired,
		roles: React.PropTypes.object.isRequired,
	},
	getInitialState() {
		return {
			objectives: [],
			total: 0,
			page: 1,
			loading: true,
			showForm: false,
			editingObjective: null,
			formData: { name: "", description: "", startDate: "", endDate: "", status: "NOT_STARTED", weight: 1.0 }
		};
	},
	componentDidMount() {
		var me = this;

		StrategicObjectiveStore.on("objectivesListed", (response) => {
			if (response.success) {
				me.setState({
					objectives: response.data.list || [],
					total: response.data.total || 0,
					loading: false
				});
			} else {
				me.setState({ loading: false });
				Toastr.error("Erro ao carregar objetivos estratégicos.");
			}
		}, me);

		StrategicObjectiveStore.on("objectiveCreated", (response) => {
			if (response.success) {
				Toastr.success("Objetivo estratégico criado com sucesso.");
				me.setState({ showForm: false, editingObjective: null });
				me.loadObjectives();
			} else {
				Toastr.error(response.message || "Erro ao criar objetivo.");
			}
		}, me);

		StrategicObjectiveStore.on("objectiveUpdated", (response) => {
			if (response.success) {
				Toastr.success("Objetivo atualizado com sucesso.");
				me.setState({ showForm: false, editingObjective: null });
				me.loadObjectives();
			} else {
				Toastr.error(response.message || "Erro ao atualizar objetivo.");
			}
		}, me);

		StrategicObjectiveStore.on("objectiveDeleted", (response) => {
			if (response.success) {
				Toastr.success("Objetivo removido com sucesso.");
				me.loadObjectives();
			}
		}, me);

		this.loadObjectives();
	},
	componentWillUnmount() {
		StrategicObjectiveStore.off(null, null, this);
	},
	loadObjectives() {
		this.setState({ loading: true });
		StrategicObjectiveStore.dispatch({
			action: StrategicObjectiveStore.ACTION_LIST_OBJECTIVES,
			data: { planMacroId: this.props.params.planMacroId, page: this.state.page, pageSize: 10 }
		});
	},
	toggleForm(objective) {
		if (objective) {
			this.setState({
				showForm: true,
				editingObjective: objective,
				formData: {
					name: objective.name,
					description: objective.description || "",
					startDate: objective.startDate ? objective.startDate.substring(0, 10) : "",
					endDate: objective.endDate ? objective.endDate.substring(0, 10) : "",
					status: objective.status || "NOT_STARTED",
					weight: objective.weight || 1.0
				}
			});
		} else {
			this.setState({
				showForm: !this.state.showForm,
				editingObjective: null,
				formData: { name: "", description: "", startDate: "", endDate: "", status: "NOT_STARTED", weight: 1.0 }
			});
		}
	},
	handleInputChange(field, event) {
		var formData = this.state.formData;
		formData[field] = event.target.value;
		this.setState({ formData: formData });
	},
	saveObjective() {
		var data = this.state.formData;
		if (!data.name || data.name.trim() === "") {
			Toastr.error("Nome do objetivo é obrigatório.");
			return;
		}
		var objective = {
			name: data.name,
			description: data.description,
			startDate: data.startDate || null,
			endDate: data.endDate || null,
			status: data.status,
			weight: parseFloat(data.weight) || 1.0,
			planMacro: { id: parseInt(this.props.params.planMacroId) }
		};
		if (this.state.editingObjective) {
			objective.id = this.state.editingObjective.id;
			StrategicObjectiveStore.dispatch({
				action: StrategicObjectiveStore.ACTION_UPDATE_OBJECTIVE,
				data: { objective: objective }
			});
		} else {
			StrategicObjectiveStore.dispatch({
				action: StrategicObjectiveStore.ACTION_CREATE_OBJECTIVE,
				data: { objective: objective }
			});
		}
	},
	deleteObjective(id) {
		Modal.confirmCustom(
			() => {
				StrategicObjectiveStore.dispatch({
					action: StrategicObjectiveStore.ACTION_DELETE_OBJECTIVE,
					data: { objectiveId: id }
				});
				Modal.hide();
			},
			"Tem certeza que deseja excluir este objetivo?",
			() => { Modal.hide(); }
		);
	},
	getStatusLabel(status) {
		var labels = {
			"NOT_STARTED": "Não Iniciado",
			"IN_PROGRESS": "Em Andamento",
			"COMPLETED": "Concluído",
			"DELAYED": "Atrasado",
			"CANCELLED": "Cancelado"
		};
		return labels[status] || status;
	},
	getStatusClass(status) {
		var classes = {
			"NOT_STARTED": "label label-default",
			"IN_PROGRESS": "label label-primary",
			"COMPLETED": "label label-success",
			"DELAYED": "label label-danger",
			"CANCELLED": "label label-warning"
		};
		return classes[status] || "label label-default";
	},
	render() {
		var canEdit = this.context.roles.ADMIN || this.context.accessLevel >= 30;

		return (
			<div className="container-fluid">
				<div className="row">
					<div className="col-md-12">
						<h1 className="marginBottom20">
							<span className="mdi mdi-target icon-link" /> Desdobramento Estratégico
						</h1>

						{canEdit && (
							<button className="btn btn-primary marginBottom20" onClick={() => this.toggleForm(null)}>
								<span className="mdi mdi-plus" /> Novo Objetivo Estratégico
							</button>
						)}

						{this.state.showForm && (
							<div className="panel panel-default marginBottom20">
								<div className="panel-heading">
									<h3 className="panel-title">
										{this.state.editingObjective ? "Editar Objetivo" : "Novo Objetivo Estratégico"}
									</h3>
								</div>
								<div className="panel-body">
									<div className="form-group">
										<label>Nome *</label>
										<input type="text" className="form-control" value={this.state.formData.name}
											onChange={this.handleInputChange.bind(this, "name")}
											placeholder="Nome do objetivo estratégico" maxLength="500" />
									</div>
									<div className="form-group">
										<label>Descrição</label>
										<textarea className="form-control" rows="3" value={this.state.formData.description}
											onChange={this.handleInputChange.bind(this, "description")}
											placeholder="Descrição do objetivo" maxLength="4000" />
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
												<label>Peso</label>
												<input type="number" className="form-control" value={this.state.formData.weight}
													onChange={this.handleInputChange.bind(this, "weight")}
													min="0" max="10" step="0.1" />
											</div>
										</div>
									</div>
									<button className="btn btn-success marginRight10" onClick={this.saveObjective}>
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
							<div>
								{this.state.objectives.length === 0 ? (
									<div className="alert alert-info">
										Nenhum objetivo estratégico cadastrado. Clique em "Novo Objetivo Estratégico" para começar.
									</div>
								) : (
									<div className="table-responsive">
										<table className="table table-hover table-striped">
											<thead>
												<tr>
													<th>Nome</th>
													<th>Status</th>
													<th>Progresso</th>
													<th>Data Início</th>
													<th>Data Fim</th>
													<th>Projetos</th>
													<th>Peso</th>
													{canEdit && <th>Ações</th>}
												</tr>
											</thead>
											<tbody>
												{this.state.objectives.map((obj, idx) => (
													<tr key={"obj-" + idx}>
														<td>
															<Link to={"/strategic-objective/" + obj.id + "/details"}>
																<strong>{obj.name}</strong>
															</Link>
														</td>
														<td><span className={this.getStatusClass(obj.status)}>{this.getStatusLabel(obj.status)}</span></td>
														<td>
															<div className="progress" style={{ marginBottom: 0, minWidth: 80 }}>
																<div className="progress-bar" role="progressbar"
																	style={{ width: (obj.progress || 0) + "%" }}>
																	{(obj.progress || 0).toFixed(1)}%
																</div>
															</div>
														</td>
														<td>{obj.startDate ? new Date(obj.startDate).toLocaleDateString("pt-BR") : "-"}</td>
														<td>{obj.endDate ? new Date(obj.endDate).toLocaleDateString("pt-BR") : "-"}</td>
														<td><span className="badge">{obj.projectCount || 0}</span></td>
														<td>{obj.weight || 1.0}</td>
														{canEdit && (
															<td>
																<button className="btn btn-xs btn-default marginRight5"
																	title="Editar" onClick={() => this.toggleForm(obj)}>
																	<span className="mdi mdi-pencil" />
																</button>
																<button className="btn btn-xs btn-danger"
																	title="Excluir" onClick={() => this.deleteObjective(obj.id)}>
																	<span className="mdi mdi-delete" />
																</button>
															</td>
														)}
													</tr>
												))}
											</tbody>
										</table>
									</div>
								)}

								<div className="text-center">
									<Link to={"/strategic-objective/" + this.props.params.planMacroId + "/export"}
										className="btn btn-default marginRight10">
										<span className="mdi mdi-file-excel" /> Exportar Excel
									</Link>
									<Link to={"/strategic-objective/" + this.props.params.planMacroId + "/audit"}
										className="btn btn-default">
										<span className="mdi mdi-history" /> Histórico de Alterações
									</Link>
								</div>
							</div>
						)}
					</div>
				</div>
			</div>
		);
	}
});
