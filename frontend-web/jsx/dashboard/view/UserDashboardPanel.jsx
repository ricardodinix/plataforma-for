import React from "react";
import {Link} from "react-router";
import StrategicObjectiveStore from "forpdi/jsx/planning/store/StrategicObjective.jsx";
import UserSession from "forpdi/jsx/core/store/UserSession.jsx";
import Toastr from 'toastr';

/**
 * Tela inicial individualizada - Interface por Usuário.
 * Mostra responsabilidades e status das ações de cada colaborador.
 */
export default React.createClass({
	contextTypes: {
		router: React.PropTypes.object,
		accessLevel: React.PropTypes.number.isRequired,
		roles: React.PropTypes.object.isRequired,
	},
	getInitialState() {
		return {
			summary: null,
			loading: true,
			user: UserSession.get("user")
		};
	},
	componentDidMount() {
		var me = this;

		StrategicObjectiveStore.on("userSummaryRetrieved", (response) => {
			if (response.success) {
				me.setState({
					summary: response.data,
					loading: false
				});
			} else {
				me.setState({ loading: false });
			}
		}, me);

		StrategicObjectiveStore.dispatch({
			action: StrategicObjectiveStore.ACTION_GET_USER_SUMMARY,
			data: {}
		});
	},
	componentWillUnmount() {
		StrategicObjectiveStore.off(null, null, this);
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
			"COMPLETED": "label label-success", "DELAYED": "label label-danger", "CANCELLED": "label label-warning"
		};
		return classes[status] || "label label-default";
	},
	render() {
		var summary = this.state.summary;

		return (
			<div className="container-fluid">
				<div className="row marginBottom20">
					<div className="col-md-12">
						<h1>
							<span className="mdi mdi-account-circle icon-link" /> Meu Painel
							{this.state.user ? " - " + this.state.user.name : ""}
						</h1>
					</div>
				</div>

				{this.state.loading ? (
					<div className="text-center marginTop50">
						<span className="mdi mdi-loading mdi-spin mdi-48px" /><br />
						Carregando seu painel...
					</div>
				) : summary ? (
					<div>
						{/* Summary Cards */}
						<div className="row marginBottom20">
							<div className="col-md-3">
								<div className="panel panel-primary">
									<div className="panel-heading">
										<h3 className="panel-title">
											<span className="mdi mdi-target" /> Objetivos
										</h3>
									</div>
									<div className="panel-body text-center">
										<h2>{summary.objectiveCount || 0}</h2>
										<p>sob sua responsabilidade</p>
									</div>
								</div>
							</div>
							<div className="col-md-3">
								<div className="panel panel-info">
									<div className="panel-heading">
										<h3 className="panel-title">
											<span className="mdi mdi-folder-multiple" /> Projetos
										</h3>
									</div>
									<div className="panel-body text-center">
										<h2>{summary.projectCount || 0}</h2>
										<p>sob sua responsabilidade</p>
									</div>
								</div>
							</div>
							<div className="col-md-3">
								<div className="panel panel-danger">
									<div className="panel-heading">
										<h3 className="panel-title">
											<span className="mdi mdi-alert" /> Atrasados
										</h3>
									</div>
									<div className="panel-body text-center">
										<h2 style={{color: "#d9534f"}}>{summary.overdueCount || 0}</h2>
										<p>planos de ação atrasados</p>
									</div>
								</div>
							</div>
							<div className="col-md-3">
								<div className="panel panel-warning">
									<div className="panel-heading">
										<h3 className="panel-title">
											<span className="mdi mdi-clock-alert" /> Vencendo
										</h3>
									</div>
									<div className="panel-body text-center">
										<h2 style={{color: "#f0ad4e"}}>{summary.nearDeadlineCount || 0}</h2>
										<p>próximos do vencimento</p>
									</div>
								</div>
							</div>
						</div>

						{/* Overall Progress */}
						<div className="row marginBottom20">
							<div className="col-md-12">
								<div className="panel panel-default">
									<div className="panel-heading">
										<h3 className="panel-title">
											<span className="mdi mdi-chart-arc" /> Progresso Geral
										</h3>
									</div>
									<div className="panel-body">
										<div className="progress" style={{ height: 30 }}>
											<div className="progress-bar progress-bar-success" role="progressbar"
												style={{ width: (summary.overallProgress || 0) + "%", lineHeight: "30px" }}>
												{(summary.overallProgress || 0).toFixed(1)}%
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

						{/* Overdue Action Plans */}
						{summary.overdueActions && summary.overdueActions.length > 0 && (
							<div className="row marginBottom20">
								<div className="col-md-12">
									<div className="panel panel-danger">
										<div className="panel-heading">
											<h3 className="panel-title">
												<span className="mdi mdi-alert-circle" /> Planos de Ação Atrasados
											</h3>
										</div>
										<div className="panel-body">
											<table className="table table-condensed">
												<thead>
													<tr>
														<th>Descrição</th>
														<th>Data Fim</th>
														<th>Status</th>
													</tr>
												</thead>
												<tbody>
													{summary.overdueActions.map((action, idx) => (
														<tr key={"overdue-" + idx}>
															<td>{action.description}</td>
															<td style={{color: "#d9534f", fontWeight: "bold"}}>
																{action.endDate ? new Date(action.endDate).toLocaleDateString("pt-BR") : "-"}
															</td>
															<td><span className={this.getStatusClass(action.status)}>
																{this.getStatusLabel(action.status)}</span></td>
														</tr>
													))}
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>
						)}

						{/* Near Deadline Action Plans */}
						{summary.nearDeadlineActions && summary.nearDeadlineActions.length > 0 && (
							<div className="row marginBottom20">
								<div className="col-md-12">
									<div className="panel panel-warning">
										<div className="panel-heading">
											<h3 className="panel-title">
												<span className="mdi mdi-clock-alert" /> Próximos do Vencimento
											</h3>
										</div>
										<div className="panel-body">
											<table className="table table-condensed">
												<thead>
													<tr>
														<th>Descrição</th>
														<th>Data Fim</th>
														<th>Status</th>
													</tr>
												</thead>
												<tbody>
													{summary.nearDeadlineActions.map((action, idx) => (
														<tr key={"near-" + idx}>
															<td>{action.description}</td>
															<td style={{color: "#f0ad4e", fontWeight: "bold"}}>
																{action.endDate ? new Date(action.endDate).toLocaleDateString("pt-BR") : "-"}
															</td>
															<td><span className={this.getStatusClass(action.status)}>
																{this.getStatusLabel(action.status)}</span></td>
														</tr>
													))}
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>
						)}

						{/* My Projects */}
						{summary.projects && summary.projects.length > 0 && (
							<div className="row marginBottom20">
								<div className="col-md-12">
									<div className="panel panel-default">
										<div className="panel-heading">
											<h3 className="panel-title">
												<span className="mdi mdi-folder-multiple" /> Meus Projetos
											</h3>
										</div>
										<div className="panel-body">
											<table className="table table-hover">
												<thead>
													<tr>
														<th>Nome</th>
														<th>Status</th>
														<th>Progresso</th>
														<th>Prioridade</th>
													</tr>
												</thead>
												<tbody>
													{summary.projects.map((p, idx) => (
														<tr key={"proj-" + idx}>
															<td><strong>{p.name}</strong></td>
															<td><span className={this.getStatusClass(p.status)}>
																{this.getStatusLabel(p.status)}</span></td>
															<td>
																<div className="progress" style={{ marginBottom: 0, minWidth: 80 }}>
																	<div className="progress-bar" style={{ width: (p.progress || 0) + "%" }}>
																		{(p.progress || 0).toFixed(1)}%
																	</div>
																</div>
															</td>
															<td>{p.priority || "MEDIUM"}</td>
														</tr>
													))}
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>
						)}
					</div>
				) : (
					<div className="alert alert-info text-center">
						Nenhum dado encontrado para o seu painel. Comece criando objetivos estratégicos e projetos.
					</div>
				)}
			</div>
		);
	}
});
