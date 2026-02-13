import React from "react";
import StrategicObjectiveStore from "forpdi/jsx/planning/store/StrategicObjective.jsx";
import UserSession from "forpdi/jsx/core/store/UserSession.jsx";
import Toastr from 'toastr';
import Modal from "forpdi/jsx/core/widget/Modal.jsx";

/**
 * Dashboards Personalizáveis - Módulo com visualizações em tempo real
 * e gráficos customizáveis, superando as limitações de "texto corrido".
 */
export default React.createClass({
	contextTypes: {
		accessLevel: React.PropTypes.number.isRequired,
		roles: React.PropTypes.object.isRequired,
	},
	getInitialState() {
		return {
			panels: [],
			loading: true,
			showPanelForm: false,
			showWidgetForm: false,
			selectedPanel: null,
			panelFormData: { title: "", description: "", shared: false },
			widgetFormData: {
				title: "", widgetType: "BAR_CHART", dataSource: "GOALS",
				width: 6, height: 4, positionX: 0, positionY: 0
			}
		};
	},
	componentDidMount() {
		var me = this;

		StrategicObjectiveStore.on("dashboardPanelsListed", (response) => {
			if (response.success) {
				me.setState({
					panels: response.data.list || [],
					loading: false
				});
			} else {
				me.setState({ loading: false });
			}
		}, me);

		StrategicObjectiveStore.on("dashboardPanelCreated", (response) => {
			if (response.success) {
				Toastr.success("Painel criado com sucesso.");
				me.setState({ showPanelForm: false });
				me.loadPanels();
			}
		}, me);

		StrategicObjectiveStore.on("dashboardPanelDeleted", (response) => {
			if (response.success) {
				Toastr.success("Painel removido.");
				me.loadPanels();
			}
		}, me);

		StrategicObjectiveStore.on("widgetCreated", (response) => {
			if (response.success) {
				Toastr.success("Widget adicionado.");
				me.setState({ showWidgetForm: false });
				me.loadPanels();
			}
		}, me);

		StrategicObjectiveStore.on("widgetDeleted", (response) => {
			if (response.success) {
				Toastr.success("Widget removido.");
				me.loadPanels();
			}
		}, me);

		this.loadPanels();
	},
	componentWillUnmount() {
		StrategicObjectiveStore.off(null, null, this);
	},
	loadPanels() {
		this.setState({ loading: true });
		StrategicObjectiveStore.dispatch({
			action: StrategicObjectiveStore.ACTION_LIST_DASHBOARD_PANELS,
			data: {}
		});
	},
	handlePanelInput(field, event) {
		var formData = this.state.panelFormData;
		formData[field] = field === "shared" ? event.target.checked : event.target.value;
		this.setState({ panelFormData: formData });
	},
	handleWidgetInput(field, event) {
		var formData = this.state.widgetFormData;
		formData[field] = event.target.value;
		this.setState({ widgetFormData: formData });
	},
	savePanel() {
		if (!this.state.panelFormData.title.trim()) {
			Toastr.error("Título do painel é obrigatório.");
			return;
		}
		StrategicObjectiveStore.dispatch({
			action: StrategicObjectiveStore.ACTION_CREATE_DASHBOARD_PANEL,
			data: { panel: this.state.panelFormData }
		});
	},
	deletePanel(id) {
		Modal.confirmCustom(
			() => {
				StrategicObjectiveStore.dispatch({
					action: StrategicObjectiveStore.ACTION_DELETE_DASHBOARD_PANEL,
					data: { panelId: id }
				});
				Modal.hide();
			},
			"Tem certeza que deseja excluir este painel?",
			() => { Modal.hide(); }
		);
	},
	addWidget(panel) {
		this.setState({ selectedPanel: panel, showWidgetForm: true });
	},
	saveWidget() {
		if (!this.state.widgetFormData.title.trim()) {
			Toastr.error("Título do widget é obrigatório.");
			return;
		}
		var widget = Object.assign({}, this.state.widgetFormData, {
			panel: { id: this.state.selectedPanel.id },
			width: parseInt(this.state.widgetFormData.width),
			height: parseInt(this.state.widgetFormData.height)
		});
		StrategicObjectiveStore.dispatch({
			action: StrategicObjectiveStore.ACTION_CREATE_WIDGET,
			data: { widget: widget }
		});
	},
	deleteWidget(id) {
		StrategicObjectiveStore.dispatch({
			action: StrategicObjectiveStore.ACTION_DELETE_WIDGET,
			data: { widgetId: id }
		});
	},
	getWidgetIcon(type) {
		var icons = {
			"BAR_CHART": "mdi-chart-bar", "PIE_CHART": "mdi-chart-pie",
			"LINE_CHART": "mdi-chart-line", "NUMBER_INDICATOR": "mdi-numeric",
			"TABLE": "mdi-table", "GAUGE": "mdi-gauge", "PROGRESS_BAR": "mdi-progress-check"
		};
		return icons[type] || "mdi-chart-bar";
	},
	getWidgetTypeName(type) {
		var names = {
			"BAR_CHART": "Gráfico de Barras", "PIE_CHART": "Gráfico de Pizza",
			"LINE_CHART": "Gráfico de Linhas", "NUMBER_INDICATOR": "Indicador Numérico",
			"TABLE": "Tabela", "GAUGE": "Velocímetro", "PROGRESS_BAR": "Barra de Progresso"
		};
		return names[type] || type;
	},
	render() {
		return (
			<div className="container-fluid">
				<h2 className="marginBottom20">
					<span className="mdi mdi-view-dashboard icon-link" /> Dashboards Personalizáveis
				</h2>

				<button className="btn btn-primary marginBottom20" onClick={() => this.setState({ showPanelForm: true })}>
					<span className="mdi mdi-plus" /> Novo Painel
				</button>

				{/* Panel Creation Form */}
				{this.state.showPanelForm && (
					<div className="panel panel-default marginBottom20">
						<div className="panel-heading"><h4 className="panel-title">Novo Painel</h4></div>
						<div className="panel-body">
							<div className="form-group">
								<label>Título *</label>
								<input type="text" className="form-control" value={this.state.panelFormData.title}
									onChange={this.handlePanelInput.bind(this, "title")} placeholder="Título do painel" />
							</div>
							<div className="form-group">
								<label>Descrição</label>
								<textarea className="form-control" rows="2" value={this.state.panelFormData.description}
									onChange={this.handlePanelInput.bind(this, "description")} />
							</div>
							<div className="checkbox">
								<label>
									<input type="checkbox" checked={this.state.panelFormData.shared}
										onChange={this.handlePanelInput.bind(this, "shared")} />
									Compartilhar com outros usuários
								</label>
							</div>
							<button className="btn btn-success marginRight10" onClick={this.savePanel}>
								<span className="mdi mdi-check" /> Criar Painel
							</button>
							<button className="btn btn-default" onClick={() => this.setState({ showPanelForm: false })}>
								Cancelar
							</button>
						</div>
					</div>
				)}

				{/* Widget Creation Form */}
				{this.state.showWidgetForm && (
					<div className="panel panel-info marginBottom20">
						<div className="panel-heading">
							<h4 className="panel-title">
								Adicionar Widget ao painel: {this.state.selectedPanel ? this.state.selectedPanel.title : ""}
							</h4>
						</div>
						<div className="panel-body">
							<div className="row">
								<div className="col-md-4">
									<div className="form-group">
										<label>Título *</label>
										<input type="text" className="form-control" value={this.state.widgetFormData.title}
											onChange={this.handleWidgetInput.bind(this, "title")} />
									</div>
								</div>
								<div className="col-md-4">
									<div className="form-group">
										<label>Tipo de Visualização</label>
										<select className="form-control" value={this.state.widgetFormData.widgetType}
											onChange={this.handleWidgetInput.bind(this, "widgetType")}>
											<option value="BAR_CHART">Gráfico de Barras</option>
											<option value="PIE_CHART">Gráfico de Pizza</option>
											<option value="LINE_CHART">Gráfico de Linhas</option>
											<option value="NUMBER_INDICATOR">Indicador Numérico</option>
											<option value="TABLE">Tabela</option>
											<option value="GAUGE">Velocímetro</option>
											<option value="PROGRESS_BAR">Barra de Progresso</option>
										</select>
									</div>
								</div>
								<div className="col-md-4">
									<div className="form-group">
										<label>Fonte de Dados</label>
										<select className="form-control" value={this.state.widgetFormData.dataSource}
											onChange={this.handleWidgetInput.bind(this, "dataSource")}>
											<option value="GOALS">Metas</option>
											<option value="OBJECTIVES">Objetivos Estratégicos</option>
											<option value="ACTION_PLANS">Planos de Ação</option>
											<option value="BUDGET">Orçamento</option>
											<option value="PROJECTS">Projetos</option>
											<option value="RISKS">Riscos</option>
										</select>
									</div>
								</div>
							</div>
							<div className="row">
								<div className="col-md-3">
									<div className="form-group">
										<label>Largura (1-12)</label>
										<input type="number" className="form-control" value={this.state.widgetFormData.width}
											onChange={this.handleWidgetInput.bind(this, "width")} min="1" max="12" />
									</div>
								</div>
								<div className="col-md-3">
									<div className="form-group">
										<label>Altura</label>
										<input type="number" className="form-control" value={this.state.widgetFormData.height}
											onChange={this.handleWidgetInput.bind(this, "height")} min="1" max="12" />
									</div>
								</div>
							</div>
							<button className="btn btn-success marginRight10" onClick={this.saveWidget}>
								<span className="mdi mdi-check" /> Adicionar Widget
							</button>
							<button className="btn btn-default" onClick={() => this.setState({ showWidgetForm: false })}>
								Cancelar
							</button>
						</div>
					</div>
				)}

				{this.state.loading ? (
					<div className="text-center"><span className="mdi mdi-loading mdi-spin mdi-24px" /> Carregando...</div>
				) : this.state.panels.length === 0 ? (
					<div className="alert alert-info">
						Nenhum painel criado. Clique em "Novo Painel" para criar seu primeiro dashboard personalizado.
					</div>
				) : (
					<div>
						{this.state.panels.map((panel, idx) => (
							<div key={"panel-" + idx} className="panel panel-default marginBottom20">
								<div className="panel-heading">
									<h3 className="panel-title" style={{ display: "inline" }}>
										<span className="mdi mdi-view-dashboard" /> {panel.title}
										{panel.shared && <span className="label label-info marginLeft10">Compartilhado</span>}
									</h3>
									<div className="pull-right">
										<button className="btn btn-xs btn-primary marginRight5"
											onClick={() => this.addWidget(panel)}>
											<span className="mdi mdi-plus" /> Widget
										</button>
										<button className="btn btn-xs btn-danger"
											onClick={() => this.deletePanel(panel.id)}>
											<span className="mdi mdi-delete" />
										</button>
									</div>
									<div className="clearfix" />
								</div>
								<div className="panel-body">
									{panel.description && <p className="text-muted">{panel.description}</p>}
									<div className="row">
										{panel.widgets && panel.widgets.length > 0 ? panel.widgets.map((widget, wIdx) => (
											<div key={"widget-" + wIdx} className={"col-md-" + Math.min(widget.width, 12)}>
												<div className="panel panel-default" style={{ minHeight: 150 }}>
													<div className="panel-heading">
														<span className={"mdi " + this.getWidgetIcon(widget.widgetType)} />
														{" " + widget.title}
														<button className="btn btn-xs btn-danger pull-right"
															onClick={() => this.deleteWidget(widget.id)}>
															<span className="mdi mdi-close" />
														</button>
													</div>
													<div className="panel-body text-center">
														<span className={"mdi " + this.getWidgetIcon(widget.widgetType) + " mdi-48px"} style={{ color: "#ccc" }} />
														<p className="text-muted">{this.getWidgetTypeName(widget.widgetType)}</p>
														<p className="text-muted small">Fonte: {widget.dataSource}</p>
													</div>
												</div>
											</div>
										)) : (
											<div className="col-md-12 text-center text-muted">
												<p>Nenhum widget adicionado. Clique em "+ Widget" para adicionar.</p>
											</div>
										)}
									</div>
								</div>
							</div>
						))}
					</div>
				)}
			</div>
		);
	}
});
