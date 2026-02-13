import Fluxbone from "forpdi/jsx/core/store/Fluxbone.jsx";
import $ from 'jquery';

var URL = Fluxbone.BACKEND_URL + "strategic-objective";
var PROJECT_URL = Fluxbone.BACKEND_URL + "project";
var ACTION_PLAN_URL = Fluxbone.BACKEND_URL + "project-action-plan";
var CUSTOM_FIELD_URL = Fluxbone.BACKEND_URL + "custom-field";
var DASHBOARD_URL = Fluxbone.BACKEND_URL + "dashboard";
var AUDIT_URL = Fluxbone.BACKEND_URL + "audit";
var DATA_URL = Fluxbone.BACKEND_URL + "data";
var USER_DASHBOARD_URL = Fluxbone.BACKEND_URL + "dashboard/user";

var StrategicObjectiveModel = Fluxbone.Model.extend({
	url: URL
});

var StrategicObjectiveStore = Fluxbone.Store.extend({
	ACTION_LIST_OBJECTIVES: 'strategic-listObjectives',
	ACTION_LIST_CHILDREN: 'strategic-listChildren',
	ACTION_GET_TREE: 'strategic-getTree',
	ACTION_CREATE_OBJECTIVE: 'strategic-createObjective',
	ACTION_UPDATE_OBJECTIVE: 'strategic-updateObjective',
	ACTION_DELETE_OBJECTIVE: 'strategic-deleteObjective',
	ACTION_LIST_PROJECTS: 'strategic-listProjects',
	ACTION_CREATE_PROJECT: 'strategic-createProject',
	ACTION_UPDATE_PROJECT: 'strategic-updateProject',
	ACTION_DELETE_PROJECT: 'strategic-deleteProject',
	ACTION_LIST_ACTION_PLANS: 'strategic-listActionPlans',
	ACTION_CREATE_ACTION_PLAN: 'strategic-createActionPlan',
	ACTION_UPDATE_ACTION_PLAN: 'strategic-updateActionPlan',
	ACTION_DELETE_ACTION_PLAN: 'strategic-deleteActionPlan',
	ACTION_GET_PROGRESS: 'strategic-getProgress',
	ACTION_LIST_BY_RESPONSIBLE: 'strategic-listByResponsible',
	ACTION_LIST_OVERDUE: 'strategic-listOverdue',
	ACTION_GET_USER_SUMMARY: 'strategic-getUserSummary',
	ACTION_LIST_DASHBOARD_PANELS: 'strategic-listDashboardPanels',
	ACTION_CREATE_DASHBOARD_PANEL: 'strategic-createDashboardPanel',
	ACTION_UPDATE_DASHBOARD_PANEL: 'strategic-updateDashboardPanel',
	ACTION_DELETE_DASHBOARD_PANEL: 'strategic-deleteDashboardPanel',
	ACTION_CREATE_WIDGET: 'strategic-createWidget',
	ACTION_UPDATE_WIDGET: 'strategic-updateWidget',
	ACTION_DELETE_WIDGET: 'strategic-deleteWidget',
	ACTION_LIST_AUDIT_LOGS: 'strategic-listAuditLogs',
	ACTION_LIST_ENTITY_HISTORY: 'strategic-listEntityHistory',
	ACTION_LIST_CUSTOM_FIELDS: 'strategic-listCustomFields',
	ACTION_CREATE_CUSTOM_FIELD: 'strategic-createCustomField',
	ACTION_SAVE_FIELD_VALUE: 'strategic-saveFieldValue',
	ACTION_LIST_FIELD_VALUES: 'strategic-listFieldValues',
	ACTION_CREATE_5W2H_TEMPLATE: 'strategic-create5W2HTemplate',
	ACTION_EXPORT_EXCEL: 'strategic-exportExcel',
	dispatchAcceptRegex: /^strategic-[a-zA-Z0-9]+$/,
	url: URL,
	model: StrategicObjectiveModel,

	// --- Strategic Objectives ---
	listObjectives(data) {
		var me = this;
		$.ajax({
			url: URL + "/list",
			method: 'GET',
			dataType: 'json',
			data: { planMacroId: data.planMacroId, page: data.page || 1, pageSize: data.pageSize || 10 },
			success(response) { me.trigger("objectivesListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	listChildren(data) {
		var me = this;
		$.ajax({
			url: URL + "/" + data.parentId + "/children",
			method: 'GET',
			dataType: 'json',
			success(response) { me.trigger("childrenListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	getTree(data) {
		var me = this;
		$.ajax({
			url: URL + "/" + data.objectiveId + "/tree",
			method: 'GET',
			dataType: 'json',
			success(response) { me.trigger("treeRetrieved", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	createObjective(data) {
		var me = this;
		$.ajax({
			url: URL,
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.objective),
			success(response) { me.trigger("objectiveCreated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	updateObjective(data) {
		var me = this;
		$.ajax({
			url: URL,
			method: 'PUT',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.objective),
			success(response) { me.trigger("objectiveUpdated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	deleteObjective(data) {
		var me = this;
		$.ajax({
			url: URL + "/" + data.objectiveId,
			method: 'DELETE',
			dataType: 'json',
			success(response) { me.trigger("objectiveDeleted", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	getProgress(data) {
		var me = this;
		$.ajax({
			url: URL + "/" + data.objectiveId + "/progress",
			method: 'GET',
			dataType: 'json',
			success(response) { me.trigger("progressRetrieved", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	listByResponsible(data) {
		var me = this;
		$.ajax({
			url: URL + "/responsible/" + data.userId,
			method: 'GET',
			dataType: 'json',
			success(response) { me.trigger("objectivesByResponsibleListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},

	// --- Projects ---
	listProjects(data) {
		var me = this;
		$.ajax({
			url: PROJECT_URL + "/objective/" + data.objectiveId,
			method: 'GET',
			dataType: 'json',
			success(response) { me.trigger("projectsListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	createProject(data) {
		var me = this;
		$.ajax({
			url: PROJECT_URL,
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.project),
			success(response) { me.trigger("projectCreated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	updateProject(data) {
		var me = this;
		$.ajax({
			url: PROJECT_URL,
			method: 'PUT',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.project),
			success(response) { me.trigger("projectUpdated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	deleteProject(data) {
		var me = this;
		$.ajax({
			url: PROJECT_URL + "/" + data.projectId,
			method: 'DELETE',
			dataType: 'json',
			success(response) { me.trigger("projectDeleted", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},

	// --- Action Plans ---
	listActionPlans(data) {
		var me = this;
		$.ajax({
			url: ACTION_PLAN_URL + "/project/" + data.projectId,
			method: 'GET',
			dataType: 'json',
			success(response) { me.trigger("actionPlansListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	createActionPlan(data) {
		var me = this;
		$.ajax({
			url: ACTION_PLAN_URL,
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.actionPlan),
			success(response) { me.trigger("actionPlanCreated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	updateActionPlan(data) {
		var me = this;
		$.ajax({
			url: ACTION_PLAN_URL,
			method: 'PUT',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.actionPlan),
			success(response) { me.trigger("actionPlanUpdated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	deleteActionPlan(data) {
		var me = this;
		$.ajax({
			url: ACTION_PLAN_URL + "/" + data.actionPlanId,
			method: 'DELETE',
			dataType: 'json',
			success(response) { me.trigger("actionPlanDeleted", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	listOverdue(data) {
		var me = this;
		$.ajax({
			url: ACTION_PLAN_URL + "/overdue",
			method: 'GET',
			dataType: 'json',
			success(response) { me.trigger("overdueActionsListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},

	// --- User Dashboard ---
	getUserSummary(data) {
		var me = this;
		$.ajax({
			url: USER_DASHBOARD_URL + "/summary",
			method: 'GET',
			dataType: 'json',
			success(response) { me.trigger("userSummaryRetrieved", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},

	// --- Dashboard Panels ---
	listDashboardPanels(data) {
		var me = this;
		$.ajax({
			url: DASHBOARD_URL + "/panel/list",
			method: 'GET',
			dataType: 'json',
			success(response) { me.trigger("dashboardPanelsListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	createDashboardPanel(data) {
		var me = this;
		$.ajax({
			url: DASHBOARD_URL + "/panel",
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.panel),
			success(response) { me.trigger("dashboardPanelCreated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	updateDashboardPanel(data) {
		var me = this;
		$.ajax({
			url: DASHBOARD_URL + "/panel",
			method: 'PUT',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.panel),
			success(response) { me.trigger("dashboardPanelUpdated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	deleteDashboardPanel(data) {
		var me = this;
		$.ajax({
			url: DASHBOARD_URL + "/panel/" + data.panelId,
			method: 'DELETE',
			dataType: 'json',
			success(response) { me.trigger("dashboardPanelDeleted", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},

	// --- Widgets ---
	createWidget(data) {
		var me = this;
		$.ajax({
			url: DASHBOARD_URL + "/widget",
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.widget),
			success(response) { me.trigger("widgetCreated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	updateWidget(data) {
		var me = this;
		$.ajax({
			url: DASHBOARD_URL + "/widget",
			method: 'PUT',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.widget),
			success(response) { me.trigger("widgetUpdated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	deleteWidget(data) {
		var me = this;
		$.ajax({
			url: DASHBOARD_URL + "/widget/" + data.widgetId,
			method: 'DELETE',
			dataType: 'json',
			success(response) { me.trigger("widgetDeleted", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},

	// --- Audit ---
	listAuditLogs(data) {
		var me = this;
		$.ajax({
			url: AUDIT_URL + "/logs",
			method: 'GET',
			dataType: 'json',
			data: data,
			success(response) { me.trigger("auditLogsListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	listEntityHistory(data) {
		var me = this;
		$.ajax({
			url: AUDIT_URL + "/entity-history",
			method: 'GET',
			dataType: 'json',
			data: { entityType: data.entityType, entityId: data.entityId, page: data.page, pageSize: data.pageSize },
			success(response) { me.trigger("entityHistoryListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},

	// --- Custom Fields ---
	listCustomFields(data) {
		var me = this;
		$.ajax({
			url: CUSTOM_FIELD_URL + "/entity-type/" + data.entityType,
			method: 'GET',
			dataType: 'json',
			success(response) { me.trigger("customFieldsListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	createCustomField(data) {
		var me = this;
		$.ajax({
			url: CUSTOM_FIELD_URL,
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.field),
			success(response) { me.trigger("customFieldCreated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	saveFieldValue(data) {
		var me = this;
		$.ajax({
			url: CUSTOM_FIELD_URL + "-value",
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(data.fieldValue),
			success(response) { me.trigger("fieldValueSaved", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	listFieldValues(data) {
		var me = this;
		$.ajax({
			url: CUSTOM_FIELD_URL + "-value/list",
			method: 'GET',
			dataType: 'json',
			data: { entityType: data.entityType, entityId: data.entityId },
			success(response) { me.trigger("fieldValuesListed", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	create5W2HTemplate(data) {
		var me = this;
		$.ajax({
			url: CUSTOM_FIELD_URL + "/template/5w2h",
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify({ entityType: data.entityType }),
			success(response) { me.trigger("template5W2HCreated", response); },
			error(opts) { me.handleRequestErrors([], opts); }
		});
	},
	exportExcel(data) {
		window.open(DATA_URL + "/export/excel/" + data.type +
			(data.planMacroId ? "?planMacroId=" + data.planMacroId : "") +
			(data.objectiveId ? "?objectiveId=" + data.objectiveId : ""), "_blank");
	}
});

export default new StrategicObjectiveStore();
