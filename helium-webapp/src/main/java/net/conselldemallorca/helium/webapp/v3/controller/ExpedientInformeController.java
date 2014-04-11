/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.report.FieldValue;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.mvc.JasperReportsView;
import net.conselldemallorca.helium.webapp.v3.datatables.DatatablesPagina;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper.SessionManager;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/informe")
public class ExpedientInformeController extends BaseExpedientController {
	private static final String VARIABLE_SESSIO_COMMAND_VALUES = "consultaCommandValues";
	public static final String VARIABLE_FILTRE_CONSULTA_TIPUS = "filtreConsultaTipus";

	// Variables exportació
	private HSSFWorkbook wb;
	private HSSFCellStyle headerStyle;
	private HSSFCellStyle cellStyle;
	private HSSFCellStyle dStyle;
	private HSSFFont bold;
	private HSSFCellStyle cellGreyStyle;
	private HSSFCellStyle greyStyle;
	private HSSFCellStyle dGreyStyle;
	private HSSFFont greyFont;

	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private DissenyService dissenyService;
	
	@RequestMapping(value = "/{expedientTipusId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Long consultaId,
			@ModelAttribute("expedientInformeCommand") Object filtreCommand,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		model.addAttribute("consultes", dissenyService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornActual.getId(),expedientTipusId));
		model.addAttribute("expedientTipusId", expedientTipusId);

		return "v3/expedientInforme";
	}
	
	private Object getFiltreCommand(
			HttpServletRequest request,
			Long expedientTipusId,
			Long consultaId) {
		Object filtreCommand = SessionHelper.getSessionManager(request).getFiltreInforme(expedientTipusId, consultaId);
		if (filtreCommand == null && consultaId != null) {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			campsAddicionals.put("consultaId", consultaId);
			campsAddicionals.put("expedientTipusId", expedientTipusId);
			campsAddicionals.put("nomesPendents", false);
			campsAddicionals.put("nomesAlertes", false);
			campsAddicionals.put("mostrarAnulats", false);
			campsAddicionals.put("filtreDesplegat", true);
			campsAddicionals.put("tramitacioMassivaActivada", true);
			@SuppressWarnings("rawtypes")
			Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
			campsAddicionalsClasses.put("consultaId", Long.class);
			campsAddicionalsClasses.put("expedientTipusId", Long.class);
			campsAddicionalsClasses.put("nomesPendents", Boolean.class);
			campsAddicionalsClasses.put("nomesAlertes", Boolean.class);
			campsAddicionalsClasses.put("mostrarAnulats", Boolean.class);
			campsAddicionalsClasses.put("filtreDesplegat", Boolean.class);
			campsAddicionalsClasses.put("tramitacioMassivaActivada", Boolean.class);
			
			List<TascaDadaDto> campsFiltre = expedientService.findConsultaFiltre(consultaId);
			filtreCommand = TascaFormHelper.getCommandForFiltre(campsFiltre, null, campsAddicionals, campsAddicionalsClasses);
			
			SessionHelper.getSessionManager(request).setFiltreInforme(
					filtreCommand,
					expedientTipusId,
					consultaId);
		}
		return filtreCommand;
	}

	@ModelAttribute("valorsBoolea")
	public List<ParellaCodiValorDto> valorsBoolea(HttpServletRequest request) {
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		resposta.add(new ParellaCodiValorDto("true",   getMessage(request, "comuns.si")));
		resposta.add(new ParellaCodiValorDto("false",  getMessage(request, "comuns.no")));
		return resposta;
	}
	
	@ModelAttribute("expedientInformeCommand")
	public Object populateExpedientInformeCommand(
			HttpServletRequest request, 
			HttpSession session,
			Long expedientTipusId,
			Long consultaId,
			ModelMap model)  {
		List<TascaDadaDto> campsFiltre = new ArrayList<TascaDadaDto>();
		List<TascaDadaDto> campsInforme = new ArrayList<TascaDadaDto>();
		
		Object filtreCommand = getFiltreCommand(request, expedientTipusId, consultaId);
		if (consultaId != null) {
			ConsultaDto consulta = dissenyService.findConsulteById(consultaId);
			campsFiltre = expedientService.findConsultaFiltre(consultaId);
			campsInforme = expedientService.findConsultaInforme(consultaId);
			
			if (consulta.getExpedientTipus() != null) {
				List<EstatDto> estats = dissenyService.findEstatByExpedientTipus(consulta.getExpedientTipus().getId());
				afegirEstatsInicialIFinal(request, estats);
				model.addAttribute("estats", estats);
			}
			model.addAttribute("consulta", consulta);
			model.addAttribute("consultaId", consulta.getId());
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			model.addAttribute("consultes", dissenyService.findConsultesActivesAmbEntornIExpedientTipusOrdenat(entornActual.getId(),expedientTipusId));
			model.addAttribute("expedientTipusId", consulta.getExpedientTipus().getId());
		}
		
		if (filtreCommand != null) {
			model.addAttribute(filtreCommand);
		}
		model.addAttribute("campsFiltre", campsFiltre);	
		model.addAttribute("campsInforme", campsInforme);
		
		return filtreCommand;
	}

	private void afegirEstatsInicialIFinal(HttpServletRequest request, List<EstatDto> estats) {
		EstatDto iniciat = new EstatDto();
		iniciat.setId(Long.parseLong("0"));
		iniciat.setCodi("0");
		iniciat.setNom( getMessage(request, "expedient.consulta.iniciat") );
		estats.add(0, iniciat);
		EstatDto finalitzat = new EstatDto();
		finalitzat.setId(Long.parseLong("-1"));
		finalitzat.setCodi("-1");
		finalitzat.setNom( getMessage(request, "expedient.consulta.finalitzat") );
		estats.add(finalitzat);
	}
	
	@RequestMapping(value = "/consulta/{consultaId}", method = RequestMethod.GET)
	public String getConsulta(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			@ModelAttribute("expedientInformeCommand") Object filtreCommand,
			BindingResult result,
			SessionStatus status,
			ModelMap model)  {
		ConsultaDto consulta = dissenyService.findConsulteById(consultaId);
		filtreCommand = populateExpedientInformeCommand(
						request, 
						request.getSession(),
						consulta.getExpedientTipus().getId(),
						consultaId,
						model);

		SessionHelper.setAttribute(
		request,
		VARIABLE_FILTRE_CONSULTA_TIPUS,
		filtreCommand);
		
		get(
				request,
				consulta.getExpedientTipus().getId(),
				consultaId,
				filtreCommand,
				result,
				status,
				model);
		
		return "redirect:/v3/informe/"+consulta.getExpedientTipus().getId();
	}
	
	@RequestMapping(value = "/{expedientTipusId}", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Valid Object filtreCommand,
			Long consultaId,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			ModelMap model)  {
		
		Object filtreAnt = SessionHelper.getAttribute(request,VARIABLE_FILTRE_CONSULTA_TIPUS);
		try {
			if (!consultaId.equals(PropertyUtils.getSimpleProperty(filtreAnt, "consultaId"))) {
				populateExpedientInformeCommand(
						request, 
						request.getSession(),
						expedientTipusId,
						consultaId,
						model);
			}
		} catch (Exception e) {}
		
		SessionHelper.setAttribute(
				request,
				VARIABLE_FILTRE_CONSULTA_TIPUS,
				filtreCommand);
		
		return "v3/expedientInforme";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{expedientTipusId}/{consultaId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public  DatatablesPagina<ExpedientConsultaDissenyDto>  datatable(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			@PathVariable Long expedientTipusId,
			HttpSession session,
			ModelMap model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		Map<String, Object> valors = new HashMap<String, Object>();
		PaginaDto<ExpedientConsultaDissenyDto> listaExpedients = new PaginaDto<ExpedientConsultaDissenyDto>();
		Object filtreCommand = getFiltreCommand(request, expedientTipusId, consultaId);
		List<TascaDadaDto> campsFiltre = new ArrayList<TascaDadaDto>();
		List<TascaDadaDto> campsInforme = new ArrayList<TascaDadaDto>();
		if (consultaId != null) {
			populateExpedientInformeCommand(request, session, expedientTipusId, consultaId, model);
			campsFiltre = (List<TascaDadaDto>) model.get("campsFiltre");
			campsInforme = (List<TascaDadaDto>) model.get("campsInforme");
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();			

			valors = TascaFormHelper.getValorsFromCommand(
					campsFiltre,
					filtreCommand,
					true);

			listaExpedients = expedientService.findPerConsultaInformePaginat(
				entornActual.getId(),
				consultaId,
				expedientTipusId,
				getValorsPerService(campsFiltre, valors),
				ExpedientCamps.EXPEDIENT_CAMP_ID,
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesPendents"),
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
				(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarAnulats"),
				PaginacioHelper.getPaginacioDtoFromDatatable(request)
			);
		}
		
		SessionHelper.setAttribute(
				request,
				VARIABLE_SESSIO_COMMAND_VALUES,
				valors);
		
		SessionHelper.setAttribute(
				request,
				VARIABLE_FILTRE_CONSULTA_TIPUS,
				filtreCommand);
		
		model.addAttribute("campsFiltre", campsFiltre);
		model.addAttribute("campsInforme", campsInforme);
				
		return PaginacioHelper.getPaginaPerDatatables(
				request,
				listaExpedients);
	}

	@RequestMapping(value = "/{expedientTipusId}/{consultaId}/exportar_excel", method = RequestMethod.GET)
	public void exportarExcel(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			HttpSession session,
			ModelMap model)  {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();		
		if (entornActual != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> valors = (Map<String, Object>) session.getAttribute(VARIABLE_SESSIO_COMMAND_VALUES);
			ConsultaDto consulta = dissenyService.findConsulteById(consultaId);
			List<TascaDadaDto> campsInforme = expedientService.findConsultaInforme(consultaId);
			
			List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto = expedientService.findAmbEntornConsultaDisseny(
					entornActual.getId(),
					consulta.getId(),
					valors,
					null);
			
			exportXLS(request, response, session, consulta, campsInforme, expedientsConsultaDissenyDto);
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
	}

	@RequestMapping(value = "/{expedientTipusId}/{consultaId}/mostrar_informe", method = RequestMethod.GET)
	public  String  descargar(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@PathVariable Long consultaId,
			HttpSession session,
			ModelMap model)  {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();		
		if (entornActual != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> valors = (Map<String, Object>) session.getAttribute(VARIABLE_SESSIO_COMMAND_VALUES);
			ConsultaDto consulta = dissenyService.findConsulteById(consultaId);
						
			List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto = expedientService.findAmbEntornConsultaDisseny(
					entornActual.getId(),
					consulta.getId(),
					valors,
					null);
			
			model.addAttribute(
					JasperReportsView.MODEL_ATTRIBUTE_REPORTDATA,
					getDadesDatasource(request, expedientsConsultaDissenyDto));
			
			String extensio = consulta.getInformeNom().substring(
					consulta.getInformeNom().lastIndexOf(".") + 1).toLowerCase();
			
			String nom = consulta.getInformeNom().substring(0,
					consulta.getInformeNom().lastIndexOf("."));
			
			if ("zip".equals(extensio)) {
				HashMap<String, byte[]> reports = unZipReports(consulta
						.getInformeContingut());
				model.addAttribute(
						JasperReportsView.MODEL_ATTRIBUTE_REPORTCONTENT,
						reports.get(nom + ".jrxml"));
				reports.remove(nom + ".jrxml");
				model.addAttribute(
						JasperReportsView.MODEL_ATTRIBUTE_SUBREPORTS,
						reports);
			} else {
				model.addAttribute(
						JasperReportsView.MODEL_ATTRIBUTE_REPORTCONTENT,
						consulta.getInformeContingut());
				
				request.setAttribute("formatJR", consulta.getFormatExport());
				model.addAttribute(
						JasperReportsView.MODEL_ATTRIBUTE_CONSULTA,
						consulta.getCodi());
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
			return "redirect:/v3/";
		}
		
		return "jasperReportsView";
	}
	
	public HashMap<String, byte[]> unZipReports(byte[] zipContent) {
		byte[] buffer = new byte[4096];
		HashMap<String, byte[]> docs = new HashMap<String, byte[]>();

		try {
			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipContent));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				String fileName = ze.getName();
				byte[] fileContent;

				ByteArrayOutputStream fos = new ByteArrayOutputStream();

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				fileContent = fos.toByteArray();
				docs.put(fileName, fileContent);
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return docs;
	}
	
	private List<Map<String, FieldValue>> getDadesDatasource(HttpServletRequest request, List<ExpedientConsultaDissenyDto> expedients) {
		List<Map<String, FieldValue>> dadesDataSource = new ArrayList<Map<String, FieldValue>>();
		for (ExpedientConsultaDissenyDto dadesExpedient: expedients) {
			ExpedientDto expedient = dadesExpedient.getExpedient();
			Map<String, FieldValue> mapFila = new HashMap<String, FieldValue>();
			for (String clau: dadesExpedient.getDadesExpedient().keySet()) {
				DadaIndexadaDto dada = dadesExpedient.getDadesExpedient().get(clau);
				mapFila.put(
						dada.getReportFieldName(),
						toReportField(request, expedient, dada));
			}
			dadesDataSource.add(mapFila);
		}
		return dadesDataSource;
	}
	
	private FieldValue toReportField(HttpServletRequest request, ExpedientDto expedient, DadaIndexadaDto dadaIndex) {
		FieldValue field = new FieldValue(
				dadaIndex.getDefinicioProcesCodi(),
				dadaIndex.getReportFieldName(),
				dadaIndex.getEtiqueta());
		if (!dadaIndex.isMultiple()) {
			field.setValor(dadaIndex.getValor());
			if ("expedient%estat".equals(field.getCampCodi())) {
				if (expedient.getDataFi() != null) {
					field.setValorMostrar(getMessage(request, "expedient.consulta.finalitzat"));
				} else {
					if (expedient.getEstat() != null)
						field.setValorMostrar(expedient.getEstat().getNom());
					else
						field.setValorMostrar(getMessage(request, "expedient.consulta.iniciat"));
				}
			} else {
				field.setValorMostrar(dadaIndex.getValorMostrar());
			}
			if (dadaIndex.isOrdenarPerValorMostrar())
				field.setValorOrdre(dadaIndex.getValorMostrar());
			else
				field.setValorOrdre(dadaIndex.getValorIndex());
		} else {
			field.setValorMultiple(dadaIndex.getValorMultiple());
			field.setValorMostrarMultiple(dadaIndex.getValorMostrarMultiple());
			field.setValorOrdreMultiple(dadaIndex.getValorIndexMultiple());
			if (dadaIndex.isOrdenarPerValorMostrar()){
				field.setValorOrdreMultiple(dadaIndex.getValorMostrarMultiple());
			}
			else {
				field.setValorOrdreMultiple(dadaIndex.getValorIndexMultiple());
			}
		}
		field.setMultiple(dadaIndex.isMultiple());
		return field;
	}
	
	private Map<String, Object> getValorsPerService(List<TascaDadaDto> camps, Map<String, Object> valors) {
		Map<String, Object> valorsPerService = new HashMap<String, Object>();
		for (TascaDadaDto camp : camps) {
			valorsPerService.put(camp.getVarCodi(), valors.get(camp.getVarCodi()));
		}
		return valorsPerService;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAutoGrowNestedPaths(false);
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(true));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
	private void createHeader(HSSFSheet sheet, List<TascaDadaDto> campsInforme) {
		int rowNum = 0;
		int colNum = 0;

		// Capçalera
		HSSFRow xlsRow = sheet.createRow(rowNum++);

		HSSFCell cell;
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize("Expedient")));
		cell.setCellStyle(headerStyle);
		
		for (TascaDadaDto campInforme : campsInforme) {
			sheet.autoSizeColumn(colNum);
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(StringUtils.capitalize(campInforme.getCampEtiqueta())));
			cell.setCellStyle(headerStyle);
		}
	}
	
	private void exportXLS(HttpServletRequest request, HttpServletResponse response, HttpSession session, ConsultaDto consulta, List<TascaDadaDto> campsInforme, List<ExpedientConsultaDissenyDto> expedientsConsultaDissenyDto) {
		wb = new HSSFWorkbook();
	
		bold = wb.createFont();
		bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		bold.setColor(HSSFColor.WHITE.index);
		greyFont = wb.createFont();
		greyFont.setColor(HSSFColor.GREY_25_PERCENT.index);
	
		cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellStyle.setWrapText(true);
		cellGreyStyle = wb.createCellStyle();
		cellGreyStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellGreyStyle.setWrapText(true);
		cellGreyStyle.setFont(greyFont);
		
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(HSSFColor.GREY_80_PERCENT.index);
		headerStyle.setFont(bold);
	
		greyStyle = wb.createCellStyle();
		greyStyle.setFont(greyFont);
	
		DataFormat format = wb.createDataFormat();
		dStyle = wb.createCellStyle();
		dStyle.setDataFormat(format.getFormat("0.00"));
	
		dGreyStyle = wb.createCellStyle();
		dGreyStyle.setFont(greyFont);
		dGreyStyle.setDataFormat(format.getFormat("0.00"));
	
		// GENERAL
		HSSFSheet sheet = wb.createSheet("Hoja 1");
	
		createHeader(sheet, campsInforme);
	
		int rowNum = 1;
		
		for (ExpedientConsultaDissenyDto  expedientConsultaDissenyDto : expedientsConsultaDissenyDto) {
			try {
				HSSFRow xlsRow = sheet.createRow(rowNum++);
				int colNum = 0;
				
				ExpedientDto exp = expedientConsultaDissenyDto.getExpedient();
				Map<String, DadaIndexadaDto> dades = expedientConsultaDissenyDto.getDadesExpedient();
				
				String titol = "";
				if (exp != null) { 
					if (exp.getNumero() != null)
						titol = "[" + exp.getNumero() + "]";
		    		if (exp.getTitol() != null) 
		    			titol += (titol.length() > 0 ? " " : "") + exp.getTitol();
		    		if (titol.length() == 0)
		    			titol = exp.getNumeroDefault();
				}
				
				sheet.autoSizeColumn(colNum);
				HSSFCell cell = xlsRow.createCell(colNum++);
				cell.setCellValue(titol);
				cell.setCellStyle(dStyle);
				
				Iterator<Entry<String, DadaIndexadaDto>> it = dades.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, DadaIndexadaDto> e = (Map.Entry<String, DadaIndexadaDto>)it.next();
					sheet.autoSizeColumn(colNum);
					cell = xlsRow.createCell(colNum++);
					DadaIndexadaDto val = e.getValue();
					cell.setCellValue((val == null || val.getValorMostrar() == null) ? "" : val.getValorMostrar());
					cell.setCellStyle(dStyle);
				}
			} catch (Exception e) {
				logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum + " - amb ID: " + expedientConsultaDissenyDto.getExpedient().getId(), e);
			}
		}
		
		try {
			String fileName = "Informe.xls";
			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
			wb.write( response.getOutputStream() );
		} catch (Exception e) {
			logger.error("Mesures temporals: No s'ha pogut realitzar la exportació.");
		}
	}

	@RequestMapping(value = "/{expedientTipusId}/{consultaId}/seleccio", method = RequestMethod.POST)
	@ResponseBody
	public Set<Long> seleccio(
			HttpServletRequest request,
			@RequestParam(value = "ids", required = false) String ids) {
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaGeneral();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaGeneral(seleccio);
		}
		if (ids != null) {
			String[] idsparts = (ids.contains(",")) ? ids.split(",") : new String[] {ids};
			for (String id: idsparts) {
				try {
					long l = Long.parseLong(id.trim());
					if (l >= 0) {
						//System.out.println(">>> SEL afegir " + l);
						seleccio.add(l);
					} else {
						//System.out.println(">>> SEL llevar " + l);
						seleccio.remove(-l);
					}
				} catch (NumberFormatException ex) {}
			}
		}
		return seleccio;
	}

	@RequestMapping(value = "/{expedientTipusId}/{consultaId}/seleccionarTots", method = RequestMethod.POST)
	@ResponseBody
	public Set<Long> seleccionarTots(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			@PathVariable Long expedientTipusId,
			HttpSession session,
			ModelMap model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		Object filtreCommand = getFiltreCommand(request, expedientTipusId, consultaId);
		
		populateExpedientInformeCommand(request, session, expedientTipusId, consultaId, model);
		@SuppressWarnings("unchecked")
		List<TascaDadaDto> campsFiltre = (List<TascaDadaDto>) model.get("campsFiltre");
		
		Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
				campsFiltre,
				filtreCommand,
				true);

		List<Long> ids = expedientService.findIdsPerConsultaInformePaginat(
			entornActual.getId(),
			consultaId,
			expedientTipusId,
			getValorsPerService(campsFiltre, valors),
			ExpedientCamps.EXPEDIENT_CAMP_ID,
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesPendents"),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "nomesAlertes"),
			(Boolean) PropertyUtils.getSimpleProperty(filtreCommand, "mostrarAnulats")
		);
		SessionManager sessionManager = SessionHelper.getSessionManager(request);
		Set<Long> seleccio = sessionManager.getSeleccioConsultaGeneral();
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			sessionManager.setSeleccioConsultaGeneral(seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				try {
					if (id >= 0) {
						seleccio.add(id);
					} else {
						seleccio.remove(-id);
					}
				} catch (NumberFormatException ex) {}
			}
			
			Iterator<Long> iterador = seleccio.iterator();
			while( iterador.hasNext() ) {
				if (!ids.contains(iterador.next())) {
					iterador.remove();
				}
			}
		}
		return seleccio;
	}

	@RequestMapping(value = "/filtre/netejar", method = RequestMethod.GET)
	public String filtreNetejar(HttpServletRequest request) {
		SessionHelper.removeAttribute(
				request,
				VARIABLE_FILTRE_CONSULTA_TIPUS);
		return "redirect:../../expedientInforme";
	}

	@RequestMapping(value = "/{expedientId}/suspend", method = RequestMethod.POST)
	public String suspend(HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@RequestParam(value = "motiu", required = true) String motiu,
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.anular(entorn.getId(), expedientId, motiu);
					MissatgesHelper.info(request, getMessage(request, "info.expedient.anulat") );
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.anular.expedient"));
		        	logger.error("No s'ha pogut anular el registre", ex);
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.anular.expedient"));				
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/delete", method = RequestMethod.GET)
	public String deleteAction(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.delete(entorn.getId(), expedientId);
					MissatgesHelper.info(request, getMessage(request, "info.expedient.esborrat") );
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.esborrar.expedient") );
		        	logger.error("No s'ha pogut esborrar el registre", ex);
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.esborrar.expedient") );
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
		}
		return "redirect:/v3/expedient";
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientInformeController.class);
}