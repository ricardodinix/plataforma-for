import React from "react";
import StrategicObjectiveStore from "forpdi/jsx/planning/store/StrategicObjective.jsx";
import Toastr from 'toastr';

/**
 * Visualização de logs de auditoria e rastreabilidade.
 * Registro completo de histórico de alterações com identificação dos responsáveis.
 */
export default React.createClass({
	contextTypes: {
		accessLevel: React.PropTypes.number.isRequired,
		roles: React.PropTypes.object.isRequired,
	},
	getInitialState() {
		return {
			logs: [],
			total: 0,
			page: 1,
			pageSize: 20,
			loading: true,
			filters: { entityType: "", action: "", startDate: "", endDate: "" }
		};
	},
	componentDidMount() {
		var me = this;
		StrategicObjectiveStore.on("auditLogsListed", (response) => {
			if (response.success) {
				me.setState({
					logs: response.data.list || [],
					total: response.data.total || 0,
					loading: false
				});
			} else {
				me.setState({ loading: false });
				Toastr.error("Erro ao carregar logs de auditoria.");
			}
		}, me);

		this.loadLogs();
	},
	componentWillUnmount() {
		StrategicObjectiveStore.off(null, null, this);
	},
	loadLogs() {
		this.setState({ loading: true });
		StrategicObjectiveStore.dispatch({
			action: StrategicObjectiveStore.ACTION_LIST_AUDIT_LOGS,
			data: {
				page: this.state.page,
				pageSize: this.state.pageSize,
				entityType: this.state.filters.entityType || undefined,
				action: this.state.filters.action || undefined,
				startDate: this.state.filters.startDate || undefined,
				endDate: this.state.filters.endDate || undefined
			}
		});
	},
	handleFilterChange(field, event) {
		var filters = this.state.filters;
		filters[field] = event.target.value;
		this.setState({ filters: filters });
	},
	applyFilters() {
		this.setState({ page: 1 }, this.loadLogs);
	},
	clearFilters() {
		this.setState({
			filters: { entityType: "", action: "", startDate: "", endDate: "" },
			page: 1
		}, this.loadLogs);
	},
	changePage(newPage) {
		this.setState({ page: newPage }, this.loadLogs);
	},
	getActionLabel(action) {
		var labels = {
			"CREATE": "Criação", "UPDATE": "Atualização", "DELETE": "Exclusão",
			"ACCESS": "Acesso", "EXPORT": "Exportação", "LOGIN": "Login", "LOGOUT": "Logout"
		};
		return labels[action] || action;
	},
	getActionClass(action) {
		var classes = {
			"CREATE": "label label-success", "UPDATE": "label label-info", "DELETE": "label label-danger",
			"ACCESS": "label label-default", "EXPORT": "label label-primary", "LOGIN": "label label-success",
			"LOGOUT": "label label-warning"
		};
		return classes[action] || "label label-default";
	},
	render() {
		var totalPages = Math.ceil(this.state.total / this.state.pageSize);

		return (
			<div className="container-fluid">
				<h2 className="marginBottom20">
					<span className="mdi mdi-history icon-link" /> Rastreabilidade e Auditoria
				</h2>

				{/* Filters */}
				<div className="panel panel-default marginBottom20">
					<div className="panel-heading"><h4 className="panel-title">Filtros</h4></div>
					<div className="panel-body">
						<div className="row">
							<div className="col-md-3">
								<div className="form-group">
									<label>Tipo de Entidade</label>
									<select className="form-control" value={this.state.filters.entityType}
										onChange={this.handleFilterChange.bind(this, "entityType")}>
										<option value="">Todos</option>
										<option value="StrategicObjective">Objetivo Estratégico</option>
										<option value="Project">Projeto</option>
										<option value="ProjectActionPlan">Plano de Ação</option>
										<option value="DashboardPanel">Painel Dashboard</option>
										<option value="CustomField">Campo Personalizado</option>
									</select>
								</div>
							</div>
							<div className="col-md-3">
								<div className="form-group">
									<label>Ação</label>
									<select className="form-control" value={this.state.filters.action}
										onChange={this.handleFilterChange.bind(this, "action")}>
										<option value="">Todas</option>
										<option value="CREATE">Criação</option>
										<option value="UPDATE">Atualização</option>
										<option value="DELETE">Exclusão</option>
										<option value="EXPORT">Exportação</option>
									</select>
								</div>
							</div>
							<div className="col-md-3">
								<div className="form-group">
									<label>Data Início</label>
									<input type="date" className="form-control" value={this.state.filters.startDate}
										onChange={this.handleFilterChange.bind(this, "startDate")} />
								</div>
							</div>
							<div className="col-md-3">
								<div className="form-group">
									<label>Data Fim</label>
									<input type="date" className="form-control" value={this.state.filters.endDate}
										onChange={this.handleFilterChange.bind(this, "endDate")} />
								</div>
							</div>
						</div>
						<button className="btn btn-primary marginRight10" onClick={this.applyFilters}>
							<span className="mdi mdi-magnify" /> Filtrar
						</button>
						<button className="btn btn-default" onClick={this.clearFilters}>Limpar Filtros</button>
					</div>
				</div>

				{/* Results */}
				{this.state.loading ? (
					<div className="text-center"><span className="mdi mdi-loading mdi-spin mdi-24px" /> Carregando...</div>
				) : (
					<div>
						<p className="text-muted">Total de registros: {this.state.total}</p>
						<div className="table-responsive">
							<table className="table table-hover table-striped table-condensed">
								<thead>
									<tr>
										<th>Data/Hora</th>
										<th>Usuário</th>
										<th>Ação</th>
										<th>Entidade</th>
										<th>Nome</th>
										<th>Campo Alterado</th>
										<th>Valor Anterior</th>
										<th>Novo Valor</th>
										<th>IP</th>
									</tr>
								</thead>
								<tbody>
									{this.state.logs.length === 0 ? (
										<tr><td colSpan="9" className="text-center">Nenhum registro encontrado.</td></tr>
									) : this.state.logs.map((log, idx) => (
										<tr key={"log-" + idx}>
											<td style={{ whiteSpace: "nowrap" }}>
												{new Date(log.creation).toLocaleString("pt-BR")}
											</td>
											<td>{log.user ? log.user.name : "-"}</td>
											<td><span className={this.getActionClass(log.action)}>{this.getActionLabel(log.action)}</span></td>
											<td>{log.entityType || "-"}</td>
											<td>{log.entityName || "-"}</td>
											<td>{log.fieldChanged || "-"}</td>
											<td title={log.previousValue || ""}>{log.previousValue ? log.previousValue.substring(0, 50) : "-"}</td>
											<td title={log.newValue || ""}>{log.newValue ? log.newValue.substring(0, 50) : "-"}</td>
											<td>{log.ipAddress || "-"}</td>
										</tr>
									))}
								</tbody>
							</table>
						</div>

						{/* Pagination */}
						{totalPages > 1 && (
							<nav className="text-center">
								<ul className="pagination">
									{this.state.page > 1 && (
										<li><a onClick={() => this.changePage(this.state.page - 1)}>Anterior</a></li>
									)}
									{Array.from({ length: Math.min(totalPages, 10) }, (_, i) => i + 1).map(p => (
										<li key={"page-" + p} className={p === this.state.page ? "active" : ""}>
											<a onClick={() => this.changePage(p)}>{p}</a>
										</li>
									))}
									{this.state.page < totalPages && (
										<li><a onClick={() => this.changePage(this.state.page + 1)}>Próxima</a></li>
									)}
								</ul>
							</nav>
						)}

						{/* Export */}
						<div className="text-center marginTop20">
							<button className="btn btn-default" onClick={() => {
								StrategicObjectiveStore.dispatch({
									action: StrategicObjectiveStore.ACTION_EXPORT_EXCEL,
									data: { type: "audit" }
								});
							}}>
								<span className="mdi mdi-file-excel" /> Exportar Auditoria em Excel
							</button>
						</div>
					</div>
				)}
			</div>
		);
	}
});
