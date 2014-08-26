/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.handlers.exception.ValidationException;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.mvc.util.TramitacioMassiva;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TerminiTypeEditorHelper;

import org.apache.commons.beanutils.PropertyUtils;
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
import org.springframework.validation.Validator;
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
 * Controlador per a la tramitació de taques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/tasca")
public class TascaTramitacioController extends BaseController {

	protected String TAG_PARAM_REGEXP = "<!--helium:param-(.+?)-->";
	public static final String VARIABLE_SESSIO_CAMP_FOCUS = "helCampFocus";

	@Autowired
	protected ExpedientService expedientService;
	
	@Autowired
	protected TascaService tascaService;
	
	@Autowired
	protected DissenyService dissenyService;
	
	@Autowired
	protected ExecucioMassivaService execucioMassivaService;

	protected Validator validatorGuardar;
	protected Validator validatorValidar;
	
	/*@ModelAttribute("command")
	protected Object populateCommand(
			HttpServletRequest request, 
			String id,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null && id != null) {			
			Object command = null;
			Object commandSessio = TascaFormHelper.recuperarCommandTemporal(request, true);

			List<TascaDadaDto> tascaDadas = tascaService.findDadesPerTasca(id);
			
			ExpedientTascaDto tasca;
			if (commandSessio != null) {
				tasca = tascaService.getById(entorn.getId(), id, null, TascaFormHelper.getValorsFromCommand(tascaDadas, commandSessio, false), true, true);
				command = commandSessio;
			} else {
				tasca = tascaService.getById(entorn.getId(), id, null, null, true, true);
				try {
					Map<String, Object> campsAddicionals = new HashMap<String, Object>();
					campsAddicionals.put("id", id);
					campsAddicionals.put("entornId", entorn.getId());
					campsAddicionals.put("expedientTipusId", expedientService.findById(tasca.getExpedientId()).getId());
					campsAddicionals.put("definicioProcesId", tasca.getDefinicioProces().getId());
					campsAddicionals.put("procesScope", null);
					@SuppressWarnings("rawtypes")
					Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
					campsAddicionalsClasses.put("id", String.class);
					campsAddicionalsClasses.put("entornId", Long.class);
					campsAddicionalsClasses.put("expedientTipusId", Long.class);
					campsAddicionalsClasses.put("definicioProcesId", Long.class);
					campsAddicionalsClasses.put("procesScope", Map.class);
					
					command = TascaFormHelper.getCommandForCamps(tascaDadas, null, request, campsAddicionals, campsAddicionalsClasses, false);
				} catch (TascaNotFoundException ex) {
					MissatgesHelper.error(request, ex.getMessage());
					logger.error("No s'han pogut encontrar la tasca: " + ex.getMessage(), ex);
				} catch (Exception ignored) {
				} 
			}
			if (tasca.getRecursForm() != null && tasca.getRecursForm().length() > 0) {
				try {
					byte[] contingut = dissenyService.getDeploymentResource(tasca.getDefinicioProces().getId(), tasca.getRecursForm());
					model.addAttribute("formRecursParams", getFormRecursParams(new String(contingut, "UTF-8")));
				} catch (Exception ex) {
					logger.error("No s'han pogut extreure els parametres del recurs", ex);
				}
			}
			model.addAttribute("tasca", tasca);
			return command;
		}
		return null;
	}*/

	@RequestMapping(value = "/{expedientId}/{tascaId}/tramitar", method = RequestMethod.GET)
	public String tramitar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		if (ModalHelper.isModal(request))
			return "redirect:/modal/v3/tasca/" + expedientId + "/" + tascaId + "/form";
		else
			return "redirect:/v3/tasca/" + expedientId + "/" + tascaId + "/form";
	}

	@RequestMapping(value = "/{expedientId}/{tascaId}/form", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		//NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
		model.addAttribute(
				"tasca",
				expedientService.getTascaPerExpedient(expedientId, tascaId));
		// Omple les dades del formulari i les de només lectura
		List<TascaDadaDto> dades = tascaService.findDadesPerTasca(tascaId);
		model.addAttribute("dades", dades);
		List<TascaDadaDto> dadesNomesLectura = new ArrayList<TascaDadaDto>();
		Iterator<TascaDadaDto> itDades = dades.iterator();
		while (itDades.hasNext()) {
			TascaDadaDto dada = itDades.next();
			if (dada.isReadOnly()) {
				if (dada.getText() != null && !dada.getText().isEmpty())
					dadesNomesLectura.add(dada);
				itDades.remove();
			}
		}
		model.addAttribute("dadesNomesLectura", dadesNomesLectura);
		// Omple els documents per adjuntar i els de només lectura
		List<TascaDocumentDto> documents = tascaService.findDocumentsPerTasca(tascaId);
		model.addAttribute("documents", documents);
		List<TascaDocumentDto> documentsNomesLectura = new ArrayList<TascaDocumentDto>();
		Iterator<TascaDocumentDto> itDocuments = documents.iterator();
		while (itDocuments.hasNext()) {
			TascaDocumentDto document = itDocuments.next();
			if (document.isReadOnly()) {
				if (document.getId() != null)
					documentsNomesLectura.add(document);
				itDocuments.remove();
			}
		}
		model.addAttribute("documentsNomesLectura", documentsNomesLectura);
		return "v3/expedientTascaTramitacio";
	}
	@RequestMapping(value = "/{expedientId}/{tascaId}/form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "submitar", required = false) String submitar,
			@RequestParam(value = "helMultipleIndex", required = false) Integer index,
			@RequestParam(value = "helMultipleField", required = false) String field,
			@RequestParam(value = "iframe", required = false) String iframe,
			@RequestParam(value = "registreEsborrarId", required = false) Long registreEsborrarId,
			@RequestParam(value = "registreEsborrarIndex", required = false) Integer registreEsborrarIndex,
			@RequestParam(value = "helAccioCamp", required = false) String accioCamp,
			@RequestParam(value = "helCampFocus", required = false) String campFocus,
			@RequestParam(value = "helFinalitzarAmbOutcome", required = false) String finalitzarAmbOutcome,
			@ModelAttribute("command") Object command, 
			BindingResult result, 
			SessionStatus status, 
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {			
			this.validatorGuardar = new TascaFormValidatorHelper(tascaService, false);
			this.validatorValidar = new TascaFormValidatorHelper(tascaService);
			
			boolean opValidar = "validate".equals(submit) || "validate".equals(submitar);
			boolean opSubmit = "submit".equals(submit)  || "submit".equals(submitar);
			boolean opRestore = "restore".equals(submit) || "restore".equals(submitar);
			
			ExpedientTascaDto tasca = (ExpedientTascaDto) model.get("tasca");
			List<TascaDadaDto> tascaDadas = tascaService.findDadesPerTasca(id);

			if (campFocus != null) {
				String[] partsCampFocus = campFocus.split("#");
				if (partsCampFocus.length == 2) {
					request.getSession().setAttribute(VARIABLE_SESSIO_CAMP_FOCUS, partsCampFocus[0]);
				} else {
					request.getSession().setAttribute(VARIABLE_SESSIO_CAMP_FOCUS, campFocus);
				}
			}
			if (opSubmit || opValidar || "@@@".equals(finalitzarAmbOutcome)) {
				validatorGuardar.validate(command, result);
				if (result.hasErrors()) {
					MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
					return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
				}
				boolean ok = accioGuardarForm(request, entorn.getId(), id, tascaDadas, command);
				if (!ok) {
					MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
					return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
				} else if (!opValidar){
					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.guardar"));
				}
				if (accioCamp != null && accioCamp.length() > 0) {
					ok = accioExecutarAccio(request, entorn.getId(), id, accioCamp);
					if (!ok) {
						MissatgesHelper.error(request, getMessage(request, "error.guardar.dades"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
				}
				if (opValidar || "@@@".equals(finalitzarAmbOutcome)) {
					validatorValidar.validate(command, result);
					try {
						afegirVariablesDelProces(command, tasca);
						Validator validator = TascaFormHelper.getBeanValidatorForCommand(tascaDadas);
						Map<String, Object> valorsCommand = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
						validator.validate(TascaFormHelper.getCommandForCamps(tascaDadas,valorsCommand,request, null,null,false), result);
					} catch (Exception ex) {
						logger.error("S'han produit errors de validació", ex);
						MissatgesHelper.error(request, getMessage(request, "error.validacio"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
					if (result.hasErrors()) {
						MissatgesHelper.error(request, result, getMessage(request, "error.validacio"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
					ok = accioValidarForm(request, entorn.getId(), id, tascaDadas, command);
					if (!ok) {
						MissatgesHelper.error(request, getMessage(request, "error.validacio"));
						return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
					}
				}
				status.setComplete();
				if (finalitzarAmbOutcome != null && !finalitzarAmbOutcome.equals("@#@")) {
					boolean okCompletar = accioCompletarTasca(
							request,
							entorn.getId(),
							id,
							finalitzarAmbOutcome);
					if (okCompletar) {
						return "redirect:/v3/expedient/"+expedientId;
					}
				}
			} else if (opRestore) {
				boolean ok = accioRestaurarForm(request, entorn.getId(), id, tascaDadas, command);
				if (ok) {
					status.setComplete();
				} else {
					MissatgesHelper.error(request, getMessage(request, "error.validacio"));
				}
			} else if ("multipleAdd".equals(submit)) {
				try {
					if (field != null) {
						PropertyUtils.setSimpleProperty(command, field, TascaFormHelper.addMultiple(field, command, tascaDadas));
					}
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.afegir.camp.multiple"));
					logger.error("No s'ha pogut afegir el camp múltiple", ex);
				}
			} else if ("multipleRemove".equals(submit)) {
				try {
					if (field != null && index != null) {
						PropertyUtils.setSimpleProperty(command, field, TascaFormHelper.deleteMultiple(field, command, tascaDadas, index));
					}
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.esborrar.camp.multiple"));
					logger.error("No s'ha pogut esborrar el camp múltiple", ex);
				}
			} else {
				status.setComplete();
				if (registreEsborrarId != null && registreEsborrarIndex != null) {
					accioEsborrarRegistre(request, entorn.getId(), id, registreEsborrarId, registreEsborrarIndex);
				}
				TascaFormHelper.guardarCommandTemporal(request, command);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "redirect:/v3/expedient/"+expedientId+"/tasca/"+tascaId+"/form";
	}

	@RequestMapping(value = "/{expedientId}/{tascaId}/camp/{campId}/valorsSeleccio", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			Model model) {
		return tascaService.findOpcionsSeleccioPerCampTasca(tascaId, campId);
	}

	@RequestMapping(value = "/{expedientId}/{tascaId}/completar", method = RequestMethod.POST)
	public String completar(
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "submit", required = false) String submit,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			boolean ok = accioCompletarTasca(
					request,
					entorn.getId(),
					id,
					submit);
			if (ok) {
				return "redirect:/v3/expedient/" + expedientId;
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		return "redirect:/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
	}
	
	@RequestMapping(value = "/{expedientId}/{tascaId}/tascaAgafar", method = RequestMethod.GET)
	public String agafar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			try {
				tascaService.agafar(entorn.getId(), tascaId);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.disponible.personals"));
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio"));
				e.printStackTrace();
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "redirect:/v3/expedient/" + expedientId + "tasca/" + tascaId;
	}

	@RequestMapping(value = "/{expedientId}/{tascaId}/tascaAlliberar", method = RequestMethod.GET)
	public String alliberar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {		
			try {
				tascaService.alliberar(
						entorn.getId(),
						tascaId,
						false);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.alliberada"));
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio"));
				e.printStackTrace();
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "redirect:/v3/expedient/" + expedientId + "tasca/" + tascaId;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
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
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				TerminiDto.class,
				new TerminiTypeEditorHelper());
	}



	private Map<String, String> getFormRecursParams(String text) {
		Map<String, String> params = new HashMap<String, String>();
		Pattern pattern = Pattern.compile(TAG_PARAM_REGEXP);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String[] paramParts = matcher.group(1).split(":");
			if (paramParts.length == 2) {
				params.put(paramParts[0], paramParts[1]);
			}
		}
		return params;
	}

	private void guardarVariablesReg(HttpServletRequest request, TascaDadaDto camp, String id) {	
		int i = 1;
		
		borrarTodosRegistres(request, id, camp.getVarCodi());
		
		while (i < request.getParameterMap().size()) {
			Map<String, Object> variablesMultReg = new HashMap<String, Object>();
			int salir = 0;
			for (TascaDadaDto registreMembre : camp.getRegistreDades()) {
				boolean sinValor = false;
				if (registreMembre.getCampTipus().equals(CampTipusDto.BOOLEAN)) {
					variablesMultReg.put(registreMembre.getVarCodi(), false);
				} else {
					variablesMultReg.put(registreMembre.getVarCodi(), "");
				}
				if (request.getParameterMap().containsKey(camp.getVarCodi()+"["+i+"]["+registreMembre.getVarCodi()+"]")) {
					Object valor = request.getParameterMap().get(camp.getVarCodi()+"["+i+"]["+registreMembre.getVarCodi()+"]");
					valor = String.valueOf(((String[])valor)[0]);
					if (registreMembre.getCampTipus().equals(CampTipusDto.BOOLEAN)) {						
						valor = "on".equals(valor);
					} else if ("".equals(valor)) {
						sinValor = true;					
					}
					variablesMultReg.put(registreMembre.getVarCodi(), valor);
				} else {
					sinValor = true;
				}
				
				if (sinValor) {
					salir++;
					if (camp.getRegistreDades().size() == salir) {
						variablesMultReg.clear();
						break;
					}			
				} 
			}
			
			if (!variablesMultReg.isEmpty()) {
				List<Object> variablesRegTmp = new ArrayList<Object>();
				for (TascaDadaDto registreMembre : camp.getRegistreDades()) {
					String campMembre = registreMembre.getVarCodi();
					variablesRegTmp.add(variablesMultReg.get(campMembre));
				}
				
				guardarRegistre(request, id, camp.getVarCodi(), camp.isCampMultiple(), variablesRegTmp.toArray());
			}
			
			i++;
		}
	}

	private void borrarTodosRegistres(
			HttpServletRequest request,
			String id,
			String campCodi) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
				
		tascaService.borrarVariables(
				entorn.getId(),
				id,
				campCodi,
				request.getUserPrincipal().getName());
	}
	
	private boolean accioValidarForm(HttpServletRequest request, Long entornId, String id, List<TascaDadaDto> tascaDadas, Object command) {
		boolean resposta = true;
		ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);
		if (TramitacioMassiva.isTramitacioMassivaActiu(request, id)) {
			String[] tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			try {
				Long expTipusId = expedientService.findById(task.getExpedientId()).getTipus().getId();
				Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
				
				// Restauram la primera tasca
				tascaService.validar(entornId, id, variables, true);

				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					
					// Obtenim informació de l'execució massiva
					
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try {
							dInici = sdf.parse(parametresTram[0]);
						} catch (ParseException pe) {
						}
					}
					
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true"))
						bCorreu = true;

					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("Validar");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = variables;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.validar", new Object[] { tIds.length }));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				tascaService.validar(entornId, id, TascaFormHelper.getValorsFromCommand(tascaDadas, command, false), true);
				MissatgesHelper.info(request, getMessage(request, "info.formulari.validat"));
			} catch (Exception ex) {
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				MissatgesHelper.error(request, getMessage(request, "error.validar.formulari") + " " + tascaIdLog);
				logger.error("No s'ha pogut validar el formulari en la tasca " + tascaIdLog, ex);
				resposta = false;
			}
		}
		return resposta;
	}

	private boolean accioRestaurarForm(HttpServletRequest request, Long entornId, String id, List<TascaDadaDto> tascaDadas, Object command) {
		boolean resposta = false;
		if (TramitacioMassiva.isTramitacioMassivaActiu(request, id)) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			String[] tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = expedientService.findById(task.getExpedientId()).getTipus().getId();

				// Restauram la primera tasca
				tascaService.restaurar(entornId, id);

				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}

					// Obtenim informació de l'execució massiva
					
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try {
							dInici = sdf.parse(parametresTram[0]);
						} catch (ParseException pe) {
						}
					}
					
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true"))
						bCorreu = true;

					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("Restaurar");
					Long params = entornId;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);

					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.restaurar", new Object[] { tIds.length }));
					resposta = true;
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
			}
		} else {
			try {
				tascaService.restaurar(entornId, id);
				MissatgesHelper.info(request, getMessage(request, "info.formulari.restaurat"));
			} catch (Exception ex) {
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				MissatgesHelper.error(request, getMessage(request, "error.restaurar.formulari") + " " + tascaIdLog);
				logger.error("No s'ha pogut restaurar el formulari en la tasca " + tascaIdLog, ex);
			}
		}
		return resposta;
	}

	private boolean accioExecutarAccio(HttpServletRequest request, Long entornId, String id, String accio) {
		boolean resposta = true;
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				Long expTipusId = expedientService.findById(task.getExpedientId()).getTipus().getId();

				// Restauram la primera tasca
				tascaService.executarAccio(entornId, id, accio);

				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					
					// Obtenim informació de l'execució massiva
					
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try {
							dInici = sdf.parse(parametresTram[0]);
						} catch (ParseException pe) {
						}
					}
					
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true"))
						bCorreu = true;

					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("Accio");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = accio;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);

					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.accio", new Object[] { tIds.length }));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				tascaService.executarAccio(entornId, id, accio);
				MissatgesHelper.info(request, getMessage(request, "info.accio.executat"));
			} catch (Exception ex) {
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(request, getMessage(request, "error.validacio.tasca") + " " + tascaIdLog + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(request, getMessage(request, "error.executar.accio") + " " + tascaIdLog);
					logger.error("No s'ha pogut executar l'acció '" + accio + "' en la tasca " + tascaIdLog, ex);
				}
				resposta = false;
			}
		}
		return resposta;
	}

	private String getIdTascaPerLogs(Long entornId, String tascaId) {
		ExpedientTascaDto tascaActual = tascaService.getById(
				entornId,
				tascaId,
				null,
				null,
				false,
				false);
		return tascaActual.getNom() + " - " + tascaActual.getExpedientIdentificador();
	}

	private boolean accioEsborrarRegistre(HttpServletRequest request, Long entornId, String id, Long registreEsborrarId, Integer registreEsborrarIndex) {
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		CampDto camp = tascaService.findCampTasca(registreEsborrarId);
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);
				Long expTipusId = expedientService.findById(task.getExpedientId()).getTipus().getId();

				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.esborrarRegistre(entornId, id, camp.getCodi(), registreEsborrarIndex.intValue());

				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					// ------------------------------------------
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					// Obtenim informació de l'execució massiva
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try {
							dInici = sdf.parse(parametresTram[0]);
						} catch (ParseException pe) {
						}
					}
					
					// Enviar correu
					Boolean bCorreu = (parametresTram[1] != null && parametresTram[1].equals("true"));

					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("RegEsborrar");
					Object[] params = new Object[3];
					params[0] = entornId;
					params[1] = camp.getCodi();
					params[2] = registreEsborrarIndex;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);

					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.registre.esborrar", new Object[] { tIds.length }));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				return false;
			}
		} else {
			try {
				tascaService.esborrarRegistre(entornId, id, camp.getCodi(), registreEsborrarIndex.intValue());
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.esborrar.registre"));
				logger.error("No s'ha pogut esborrar el registre", ex);
			}
		}
		return true;
	}
	
	private void guardarRegistre(
			HttpServletRequest request,
			String id,
			String campCodi,
			boolean multiple,
			Object[] valors) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();		
		tascaService.guardarRegistre(entorn.getId(), id, campCodi, valors, request.getUserPrincipal().getName());
		
		if (TramitacioMassiva.isTramitacioMassivaActiu(request, id)) {
			String[] tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			if (tascaIds.length > 1) {
				String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
				try {
					ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);
					Long expTipusId = expedientService.findById(task.getExpedientId()).getTipus().getId();
					
					// La primera tasca ja s'ha executat. Programam massivament la resta de tasques
					// ----------------------------------------------------------------------------
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					// Obtenim informació de l'execució massiva
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try { dInici = sdf.parse(parametresTram[0]); } catch (ParseException pe) {};
					}
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true")) bCorreu = true;
					
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("RegGuardar");
					Object[] params = new Object[4];
					params[0] = entorn.getId();
					params[1] = campCodi;
					params[2] = valors;
					params[3] = Integer.valueOf(-1);
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.completar", new Object[] {tIds.length}));
				} catch (Exception e) {
					MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				}
			}
		}
	}

	private void afegirVariablesDelProces(Object command, ExpedientTascaDto tasca) throws Exception {
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(tasca.getProcessInstanceId());
		PropertyUtils.setSimpleProperty(command, "procesScope", instanciaProces.getVariables());
	}

	private boolean accioGuardarForm(HttpServletRequest request, Long entornId, String id, List<TascaDadaDto> tascaDadas, Object command) {
		boolean resposta = true;
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(id);	
		
		Map<String, Object> variables = TascaFormHelper.getValorsFromCommand(tascaDadas, command, false);
				
		for (TascaDadaDto camp : tascaDadas) {
			if (camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
				guardarVariablesReg(request, camp, id);
			}
		}

		tascaService.guardarVariables(entornId, id, variables, null);
		
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {				
				// Restauram la primera tasca

				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					
					// Obtenim informació de l'execució massiva
					
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try {
							dInici = sdf.parse(parametresTram[0]);
						} catch (ParseException pe) {
						}
					}
					
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true"))
						bCorreu = true;

					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);					
					dto.setExpedientTipusId(expedientService.findById(task.getExpedientId()).getTipus().getId());
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("Guardar");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = variables;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.completar", new Object[] {tIds.length}));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				MissatgesHelper.info(request, getMessage(request, "info.dades.form.guardat"));
			} catch (Exception ex) {
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				MissatgesHelper.error(request, getMessage(request, "error.proces.peticio") + " " + tascaIdLog);
				logger.error("No s'ha pogut guardar les dades del formulari en la tasca " + tascaIdLog, ex);
				resposta = false;
			}
		}
		return resposta;
	}
	
	private boolean accioCompletarTasca(
			HttpServletRequest request,
			Long entornId,
			String id,
			String submit) {
		ExpedientTascaDto tasca = tascaService.getById(
				entornId,
				id,
				null,
				null,
				false,
				false);
		String transicio = null;
		for (String outcome: tasca.getOutcomes()) {
			if (outcome != null && outcome.equals(submit.trim())) {
				transicio = outcome;
				break;
			}
		}
		boolean resposta = true;
		boolean massivaActiu = TramitacioMassiva.isTramitacioMassivaActiu(request, id);
		String[] tascaIds;
		if (massivaActiu) {
			String[] parametresTram = TramitacioMassiva.getParamsTramitacioMassiva(request, id);
			tascaIds = TramitacioMassiva.getTasquesTramitacioMassiva(request, id);
			try {
				ExpedientTascaDto task = tascaService.getByIdSenseComprovacio(tascaIds[0]);
				Long expTipusId = expedientService.findById(task.getExpedientId()).getTipus().getId();
				
				// Restauram la primera tasca
				// ------------------------------------------
				tascaService.completar(entornId, id, true, null, transicio);
				
				if (tascaIds.length > 1) {
					// Programam massivament la resta de tasques
					// ------------------------------------------
					String[] tIds = new String[tascaIds.length - 1];
					int j = 0;
					for (int i = 0; i < tascaIds.length; i++) {
						if (!tascaIds[i].equals(id)) {
							tIds[j++] = tascaIds[i];
						}
					}
					// Obtenim informació de l'execució massiva
					// Data d'inici
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date dInici = new Date();
					if (parametresTram[0] != null) {
						try { dInici = sdf.parse(parametresTram[0]); } catch (ParseException pe) {};
					}
					// Enviar correu
					Boolean bCorreu = false;
					if (parametresTram[1] != null && parametresTram[1].equals("true")) bCorreu = true;
					
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setDataInici(dInici);
					dto.setEnviarCorreu(bCorreu);
					dto.setTascaIds(tIds);
					dto.setExpedientTipusId(expTipusId);
					dto.setTipus(ExecucioMassivaTipusDto.EXECUTAR_TASCA);
					dto.setParam1("Completar");
					Object[] params = new Object[2];
					params[0] = entornId;
					params[1] = transicio;
					dto.setParam2(execucioMassivaService.serialize(params));
					execucioMassivaService.crearExecucioMassiva(dto);
					
					MissatgesHelper.info(request, getMessage(request, "info.tasca.massiu.completar", new Object[] {tIds.length}));
				}
			} catch (Exception e) {
				MissatgesHelper.error(request, getMessage(request, "error.no.massiu"));
				resposta = false;
			}
		} else {
			try {
				tascaService.completar(entornId, id, true, null, transicio);
				MissatgesHelper.info(request, getMessage(request, "info.tasca.completat"));
			} catch (Exception ex) {
				resposta = false;
				String tascaIdLog = getIdTascaPerLogs(entornId, id);
				if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
					MissatgesHelper.error(request, getMessage(request, "error.validacio.tasca") + " " + tascaIdLog + ": " + ex.getCause().getMessage());
				} else {
					MissatgesHelper.error(request, getMessage(request, "error.finalitzar.tasca") + " " + tascaIdLog);
					logger.error("No s'ha pogut finalitzar la tasca " + tascaIdLog, ex);
				}
			}
		}
		return resposta;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(TascaTramitacioController.class);
}