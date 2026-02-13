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
			actionPlans: [],
			loading: true,
			showForm: false,
			editingPlan: null,
			formData: {
				description: "", responsible: "", startDate: "", endDate: "",
				status: "NOT_STARTED", what: "", why: "", whereField: "",
				who: "", how: "", howMuch: ""
			}
		};
	},
	componentDidMount() {
		var me = this;

		StrategicObjectiveStore.on("actionPlansListed", (response) => {
			if (response.success) {
				me.setState({
					actionPlans: response.data.list || response.data || [],
					loading: false
				});
			} else {
				me.setState({ loading: false });
			}
		}, me);

		StrategicObjectiveStore.on("actionPlanCreated", (response) => {
			if (response.success) {
				Toastr.success("Plano de ação criado com sucesso.");
				me.setState({ showForm: false, editingPlan: null });
				me.loadActionPlans();
			} else {
				Toastr.error(response.message || "Erro ao criar plano de ação.");
			}
		}, me);

		StrategicObjectiveStore.on("actionPlanUpdated", (response) => {
			if (response.success) {
				Toastr.success("Plano de ação atualizado.");
				me.setState({ showForm: false, editingPlan: null });
				me.loadActionPlans();
			}
		}, me);

		StrategicObjectiveStore.on("actionPlanDeleted", (response) => {
			if (response.success) {
				Toastr.success("Plano de ação removido.");
				me.loadActionPlans();
			}
		}, me);

		this.loadActionPlans();
	},
	componentWillUnmount() {
		StrategicObjectiveStore.off(null, null, this);
	},
	loadActionPlans() {
		this.setState({ loading: true });
		StrategicObjectiveStore.dispatch({
			action: StrategicObjectiveStore.ACTION_LIST_ACTION_PLANS,
			data: { projectId: this.props.params.projectId }
		});
	},
	toggleForm(plan) {
		if (plan) {
			this.setState({
				showForm: true,
				editingPlan: plan,
				formData: {
					description: plan.description || "",
					responsible: plan.responsible || "",
					startDate: plan.startDate ? plan.startDate.substring(0, 10) : "",
					endDate: plan.endDate ? plan.endDate.substring(0, 10) : "",
					status: plan.status || "NOT_STARTED",
					what: plan.what || "",
					why: plan.why || "",
					whereField: plan.whereField || "",
					who: plan.who || "",
					how: plan.how || "",
					howMuch: plan.howMuch || ""
				}
			});
		} else {
			this.setState({
				showForm: !this.state.showForm,
				editingPlan: null,
				formData: {
					description: "", responsible: "", startDate: "", endDate: "",
					status: "NOT_STARTED", what: "", why: "", whereField: "",
					who: "", how: "", howMuch: ""
				}
			});
		}
	},
	handleInputChange(field, event) {
		var formData = this.state.formData;
		formData[field] = event.target.value;
		this.setState({ formData: formData });
	},
	saveActionPlan() {
		var data = this.state.formData;
		if (!data.description || data.description.trim() === "") {
			Toastr.error("Descrição é obrigatória.");
			return;
		}
		var actionPlan = {
			description: data.description,
			responsible: data.responsible,
			startDate: data.startDate || null,
			endDate: data.endDate || null,
			status: data.status,
			what: data.what,
			why: data.why,
			whereField: data.whereField,
			who: data.who,
			how: data.how,
			howMuch: data.howMuch,
			project: { id: parseInt(this.props.params.projectId) }
		};
		if (this.state.editingPlan) {
			actionPlan.id = this.state.editingPlan.id;
			StrategicObjectiveStore.dispatch({
				action: StrategicObjectiveStore.ACTION_UPDATE_ACTION_PLAN,
				data: { actionPlan: actionPlan }
			});
		} else {
			StrategicObjectiveStore.dispatch({
				action: StrategicObjectiveStore.ACTION_CREATE_ACTION_PLAN,
				data: { actionPlan: actionPlan }
			});
		}
	},
	deleteActionPlan(id) {
		Modal.confirmCustom(
			() => {
				StrategicObjectiveStore.dispatch({
					action: StrategicObjectiveStore.ACTION_DELETE_ACTION_PLAN,
					data: { actionPlanId: id }
				});
				Modal.hide();
			},
			"Tem certeza que deseja excluir este plano de ação?",
			() => { Modal.hide(); }
		);
	},
	getStatusLabel(status) {
		var labels = {
			"NOT_STARTED": "Não Iniciado", "IN_PROGRESS": "Em Andamento",
			"COMPLETED": "Concluído", "DELAYED": "Atrasado", "CANCELLED": "Cancelado"
		};
		return labels[status] || status;
	},
	getStatusClass(status) {
		var classes = {
			"NOT_STARTED": "label label-default", "IN_PROGRESS": "label label-primary",
			"COMPLETED": "label label-success", "DELAYED": "label label-danger",
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
						<h2 className="marginBottom20">
							<span className="mdi mdi-clipboard-check icon-link" /> Planos de Ação (5W2H)
						</h2>

						{canEdit && (
							<button className="btn btn-primary marginBottom20" onClick={() => this.toggleForm(null)}>
								<span className="mdi mdi-plus" /> Novo Plano de Ação
							</button>
						)}

						{this.state.showForm && (
							<div className="panel panel-default marginBottom20">
								<div className="panel-heading">
									<h3 className="panel-title">
										{this.state.editingPlan ? "Editar Plano de Ação" : "Novo Plano de Ação (5W2H)"}
									</h3>
								</div>
								<div className="panel-body">
									<div className="form-group">
										<label>Descrição *</label>
										<textarea className="form-control" rows="2" value={this.state.formData.description}
											onChange={this.handleInputChange.bind(this, "description")}
											placeholder="Descrição do plano de ação" maxLength="4000" />
									</div>
									<div className="row">
										<div className="col-md-4">
											<div className="form-group">
												<label>Responsável</label>
												<input type="text" className="form-control" value={this.state.formData.responsible}
													onChange={this.handleInputChange.bind(this, "responsible")} />
											</div>
										</div>
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
										<div className="col-md-2">
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
									</div>

									<h4 className="marginTop10 marginBottom10">
										<span className="mdi mdi-format-list-checks" /> Metodologia 5W2H
									</h4>
									<div className="row">
										<div className="col-md-6">
											<div className="form-group">
												<label>What (O quê?)</label>
												<textarea className="form-control" rows="2" value={this.state.formData.what}
													onChange={this.handleInputChange.bind(this, "what")}
													placeholder="O que será feito?" />
											</div>
										</div>
										<div className="col-md-6">
											<div className="form-group">
												<label>Why (Por quê?)</label>
												<textarea className="form-control" rows="2" value={this.state.formData.why}
													onChange={this.handleInputChange.bind(this, "why")}
													placeholder="Por que será feito?" />
											</div>
										</div>
									</div>
									<div className="row">
										<div className="col-md-4">
											<div className="form-group">
												<label>Where (Onde?)</label>
												<input type="text" className="form-control" value={this.state.formData.whereField}
													onChange={this.handleInputChange.bind(this, "whereField")}
													placeholder="Onde será feito?" />
											</div>
										</div>
										<div className="col-md-4">
											<div className="form-group">
												<label>Who (Quem?)</label>
												<input type="text" className="form-control" value={this.state.formData.who}
													onChange={this.handleInputChange.bind(this, "who")}
													placeholder="Quem fará?" />
											</div>
										</div>
										<div className="col-md-4">
											<div className="form-group">
												<label>How (Como?)</label>
												<textarea className="form-control" rows="2" value={this.state.formData.how}
													onChange={this.handleInputChange.bind(this, "how")}
													placeholder="Como será feito?" />
											</div>
										</div>
									</div>
									<div className="row">
										<div className="col-md-6">
											<div className="form-group">
												<label>How Much (Quanto custa?)</label>
												<input type="text" className="form-control" value={this.state.formData.howMuch}
													onChange={this.handleInputChange.bind(this, "howMuch")}
													placeholder="Quanto custará?" />
											</div>
										</div>
									</div>

									<button className="btn btn-success marginRight10" onClick={this.saveActionPlan}>
										<span className="mdi mdi-check" /> Salvar
									</button>
									<button className="btn btn-default" onClick={() => this.toggleForm(null)}>
										Cancelar
									</button>
								</div>
							</div>
						)}

						{this.state.loading ? (
							<div className="text-center">
								<span className="mdi mdi-loading mdi-spin mdi-24px" /> Carregando...
							</div>
						) : this.state.actionPlans.length === 0 ? (
							<div className="alert alert-info">
								Nenhum plano de ação cadastrado para este projeto.
							</div>
						) : (
							<div className="table-responsive">
								<table className="table table-hover table-striped">
									<thead>
										<tr>
											<th>Descrição</th>
											<th>Responsável</th>
											<th>Status</th>
											<th>Data Início</th>
											<th>Data Fim</th>
											<th>O quê (What)</th>
											<th>Por quê (Why)</th>
											{canEdit && <th>Ações</th>}
										</tr>
									</thead>
									<tbody>
										{this.state.actionPlans.map((ap, idx) => (
											<tr key={"ap-" + idx}>
												<td><strong>{ap.description}</strong></td>
												<td>{ap.responsible || "-"}</td>
												<td>
													<span className={this.getStatusClass(ap.status)}>
														{this.getStatusLabel(ap.status)}
													</span>
												</td>
												<td>{ap.startDate ? new Date(ap.startDate).toLocaleDateString("pt-BR") : "-"}</td>
												<td>{ap.endDate ? new Date(ap.endDate).toLocaleDateString("pt-BR") : "-"}</td>
												<td>{ap.what ? ap.what.substring(0, 50) : "-"}</td>
												<td>{ap.why ? ap.why.substring(0, 50) : "-"}</td>
												{canEdit && (
													<td>
														<button className="btn btn-xs btn-default marginRight5"
															title="Editar" onClick={() => this.toggleForm(ap)}>
															<span className="mdi mdi-pencil" />
														</button>
														<button className="btn btn-xs btn-danger"
															title="Excluir" onClick={() => this.deleteActionPlan(ap.id)}>
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
					</div>
				</div>
			</div>
		);
	}
});
