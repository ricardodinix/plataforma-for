package org.forpdi.system;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.forpdi.core.abstractions.AbstractController;
import org.forpdi.core.notification.AuditBS;
import org.forpdi.core.notification.AuditLog;
import org.forpdi.planning.structure.Project;
import org.forpdi.planning.structure.ProjectActionPlan;
import org.forpdi.planning.structure.StrategicObjective;
import org.forpdi.planning.structure.StrategicObjectiveBS;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.boilerplate.NoCache;
import br.com.caelum.vraptor.boilerplate.bean.PaginatedList;

/**
 * Controller REST para Integração de Dados.
 * Fornece API nativa para conexão com ferramentas de BI (Power BI)
 * e exportação em múltiplos formatos (Excel, CSV, JSON, PDF).
 */
@Controller
public class DataIntegrationController extends AbstractController {

	@Inject
	private StrategicObjectiveBS strategicBS;
	@Inject
	private AuditBS auditBS;

	/**
	 * API endpoint para ferramentas de BI - Objetivos Estratégicos.
	 * Compatível com Power BI, Tableau e outras ferramentas de BI via REST.
	 */
	@Get(BASEPATH + "/data/objectives")
	@NoCache
	public void getObjectivesData(Long planMacroId) {
		try {
			PaginatedList<StrategicObjective> objectives = this.strategicBS.listRootObjectives(
					planMacroId, 1, 1000);
			this.success(objectives);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao exportar dados de objetivos", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	/**
	 * API endpoint para ferramentas de BI - Projetos.
	 */
	@Get(BASEPATH + "/data/projects")
	@NoCache
	public void getProjectsData(Long objectiveId) {
		try {
			PaginatedList<Project> projects = this.strategicBS.listProjectsByObjective(objectiveId);
			this.success(projects);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao exportar dados de projetos", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	/**
	 * API endpoint para ferramentas de BI - Planos de Ação.
	 */
	@Get(BASEPATH + "/data/action-plans")
	@NoCache
	public void getActionPlansData(Long projectId) {
		try {
			PaginatedList<ProjectActionPlan> plans = this.strategicBS.listActionPlansByProject(projectId);
			this.success(plans);
		} catch (Throwable ex) {
			LOGGER.error("Erro ao exportar dados de planos de ação", ex);
			this.fail("Erro inesperado: " + ex.getMessage());
		}
	}

	/**
	 * Exportação em formato Excel (.xlsx) dos objetivos estratégicos.
	 */
	@Get(BASEPATH + "/data/export/excel/objectives")
	@NoCache
	public void exportObjectivesExcel(Long planMacroId) {
		try {
			PaginatedList<StrategicObjective> objectives = this.strategicBS.listRootObjectives(
					planMacroId, 1, 1000);
			List<StrategicObjective> list = objectives.getList();

			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Objetivos Estratégicos");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFont(headerFont);

			Row header = sheet.createRow(0);
			String[] columns = {"ID", "Nome", "Descrição", "Status", "Progresso (%)",
					"Data Início", "Data Fim", "Responsável", "Nível", "Peso"};
			for (int i = 0; i < columns.length; i++) {
				Cell cell = header.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerStyle);
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			int rowIdx = 1;
			for (StrategicObjective obj : list) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(obj.getId());
				row.createCell(1).setCellValue(obj.getName());
				row.createCell(2).setCellValue(obj.getDescription() != null ? obj.getDescription() : "");
				row.createCell(3).setCellValue(obj.getStatus());
				row.createCell(4).setCellValue(obj.getProgress() != null ? obj.getProgress() : 0);
				row.createCell(5).setCellValue(obj.getStartDate() != null ? sdf.format(obj.getStartDate()) : "");
				row.createCell(6).setCellValue(obj.getEndDate() != null ? sdf.format(obj.getEndDate()) : "");
				row.createCell(7).setCellValue(obj.getResponsible() != null ? obj.getResponsible().getName() : "");
				row.createCell(8).setCellValue(obj.getLevel());
				row.createCell(9).setCellValue(obj.getWeight() != null ? obj.getWeight() : 1.0);
			}

			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			workbook.close();

			this.httpResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			this.httpResponse.setHeader("Content-Disposition",
					"attachment; filename=objetivos_estrategicos_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx");
			this.httpResponse.setContentLength(baos.size());
			OutputStream out = this.httpResponse.getOutputStream();
			baos.writeTo(out);
			out.flush();

			this.auditBS.logActionWithDetails("EXPORT", "StrategicObjective", null,
					"Exportação Excel", "Exportação de " + list.size() + " objetivos estratégicos em Excel");
		} catch (Throwable ex) {
			LOGGER.error("Erro ao exportar objetivos em Excel", ex);
			this.fail("Erro ao gerar arquivo Excel: " + ex.getMessage());
		}
	}

	/**
	 * Exportação em formato Excel (.xlsx) dos projetos.
	 */
	@Get(BASEPATH + "/data/export/excel/projects")
	@NoCache
	public void exportProjectsExcel(Long objectiveId) {
		try {
			PaginatedList<Project> projects = this.strategicBS.listProjectsByObjective(objectiveId);
			List<Project> list = projects.getList();

			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Projetos");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFont(headerFont);

			Row header = sheet.createRow(0);
			String[] columns = {"ID", "Nome", "Descrição", "Status", "Progresso (%)",
					"Orçamento", "Orçamento Executado", "Prioridade", "Data Início", "Data Fim", "Responsável"};
			for (int i = 0; i < columns.length; i++) {
				Cell cell = header.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerStyle);
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			int rowIdx = 1;
			for (Project p : list) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(p.getId());
				row.createCell(1).setCellValue(p.getName());
				row.createCell(2).setCellValue(p.getDescription() != null ? p.getDescription() : "");
				row.createCell(3).setCellValue(p.getStatus());
				row.createCell(4).setCellValue(p.getProgress() != null ? p.getProgress() : 0);
				row.createCell(5).setCellValue(p.getBudget() != null ? p.getBudget() : 0);
				row.createCell(6).setCellValue(p.getBudgetExecuted() != null ? p.getBudgetExecuted() : 0);
				row.createCell(7).setCellValue(p.getPriority() != null ? p.getPriority() : "");
				row.createCell(8).setCellValue(p.getStartDate() != null ? sdf.format(p.getStartDate()) : "");
				row.createCell(9).setCellValue(p.getEndDate() != null ? sdf.format(p.getEndDate()) : "");
				row.createCell(10).setCellValue(p.getResponsible() != null ? p.getResponsible().getName() : "");
			}

			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			workbook.close();

			this.httpResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			this.httpResponse.setHeader("Content-Disposition",
					"attachment; filename=projetos_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx");
			this.httpResponse.setContentLength(baos.size());
			OutputStream out = this.httpResponse.getOutputStream();
			baos.writeTo(out);
			out.flush();

			this.auditBS.logActionWithDetails("EXPORT", "Project", null,
					"Exportação Excel", "Exportação de " + list.size() + " projetos em Excel");
		} catch (Throwable ex) {
			LOGGER.error("Erro ao exportar projetos em Excel", ex);
			this.fail("Erro ao gerar arquivo Excel: " + ex.getMessage());
		}
	}

	/**
	 * Exportação em formato CSV dos dados (para integração com BI).
	 */
	@Get(BASEPATH + "/data/export/csv/objectives")
	@NoCache
	public void exportObjectivesCsv(Long planMacroId) {
		try {
			PaginatedList<StrategicObjective> objectives = this.strategicBS.listRootObjectives(
					planMacroId, 1, 1000);
			List<StrategicObjective> list = objectives.getList();

			StringBuilder csv = new StringBuilder();
			csv.append("ID;Nome;Descrição;Status;Progresso;Data Início;Data Fim;Nível;Peso\n");

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			for (StrategicObjective obj : list) {
				csv.append(obj.getId()).append(";");
				csv.append(escapeCsv(obj.getName())).append(";");
				csv.append(escapeCsv(obj.getDescription())).append(";");
				csv.append(obj.getStatus()).append(";");
				csv.append(obj.getProgress() != null ? obj.getProgress() : 0).append(";");
				csv.append(obj.getStartDate() != null ? sdf.format(obj.getStartDate()) : "").append(";");
				csv.append(obj.getEndDate() != null ? sdf.format(obj.getEndDate()) : "").append(";");
				csv.append(obj.getLevel()).append(";");
				csv.append(obj.getWeight() != null ? obj.getWeight() : 1.0).append("\n");
			}

			byte[] bytes = csv.toString().getBytes("UTF-8");
			this.httpResponse.setContentType("text/csv; charset=UTF-8");
			this.httpResponse.setHeader("Content-Disposition",
					"attachment; filename=objetivos_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv");
			this.httpResponse.setContentLength(bytes.length);
			OutputStream out = this.httpResponse.getOutputStream();
			out.write(bytes);
			out.flush();
		} catch (Throwable ex) {
			LOGGER.error("Erro ao exportar objetivos em CSV", ex);
			this.fail("Erro ao gerar arquivo CSV: " + ex.getMessage());
		}
	}

	/**
	 * Exportação de logs de auditoria em Excel.
	 */
	@Get(BASEPATH + "/data/export/excel/audit")
	@NoCache
	public void exportAuditExcel(Integer page, Integer pageSize) {
		try {
			PaginatedList<AuditLog> logs = this.auditBS.listAuditLogs(
					null, null, null, null, null, 1, 10000);
			List<AuditLog> list = logs.getList();

			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Auditoria");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFont(headerFont);

			Row header = sheet.createRow(0);
			String[] columns = {"ID", "Data/Hora", "Usuário", "Ação", "Entidade",
					"ID Entidade", "Nome Entidade", "Campo", "Valor Anterior", "Novo Valor", "IP"};
			for (int i = 0; i < columns.length; i++) {
				Cell cell = header.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerStyle);
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			int rowIdx = 1;
			for (AuditLog log : list) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(log.getId());
				row.createCell(1).setCellValue(sdf.format(log.getCreation()));
				row.createCell(2).setCellValue(log.getUser() != null ? log.getUser().getName() : "");
				row.createCell(3).setCellValue(log.getAction());
				row.createCell(4).setCellValue(log.getEntityType());
				row.createCell(5).setCellValue(log.getEntityId() != null ? log.getEntityId() : 0);
				row.createCell(6).setCellValue(log.getEntityName() != null ? log.getEntityName() : "");
				row.createCell(7).setCellValue(log.getFieldChanged() != null ? log.getFieldChanged() : "");
				row.createCell(8).setCellValue(log.getPreviousValue() != null ? log.getPreviousValue() : "");
				row.createCell(9).setCellValue(log.getNewValue() != null ? log.getNewValue() : "");
				row.createCell(10).setCellValue(log.getIpAddress() != null ? log.getIpAddress() : "");
			}

			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			workbook.close();

			this.httpResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			this.httpResponse.setHeader("Content-Disposition",
					"attachment; filename=auditoria_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx");
			this.httpResponse.setContentLength(baos.size());
			OutputStream out = this.httpResponse.getOutputStream();
			baos.writeTo(out);
			out.flush();
		} catch (Throwable ex) {
			LOGGER.error("Erro ao exportar auditoria em Excel", ex);
			this.fail("Erro ao gerar arquivo Excel: " + ex.getMessage());
		}
	}

	private String escapeCsv(String value) {
		if (value == null) return "";
		if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
			return "\"" + value.replace("\"", "\"\"") + "\"";
		}
		return value;
	}
}
