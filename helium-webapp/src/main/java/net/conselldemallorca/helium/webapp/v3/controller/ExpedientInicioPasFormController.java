/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador pel pas de formulari de l'inici d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInicioPasFormController extends BaseExpedientController {
	
	@Autowired
	private PluginService pluginService;
	
	@Autowired
	protected TascaService tascaService;

	@Autowired
	protected ExpedientService expedientService;
	
	@RequestMapping(value = "/iniciarPasForm", method = RequestMethod.POST)
	public String iniciarPasFormPost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			SessionStatus status, 
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		if (potIniciarExpedientTipus(expedientTipus) && entorn != null) {
			
			ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
			
			List<TascaDadaDto> dades = tascaService.findDadesPerTascaDto(tasca);
			Validator validatorTasca = new TascaFormValidatorHelper(expedientService);
						
			Map<String, Object> valors = new HashMap<String, Object>();
			@SuppressWarnings("rawtypes")
			Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
			campsAddicionalsClasses.put("listaDadas", List.class);
			Map<String, String> errors = new HashMap<String, String>();			
			for (TascaDadaDto dada : dades) {
				List<TascaDadaDto> lista = null;
				if (dada.getRegistreDades()!=null) {
					lista = dada.getRegistreDades();
				} else if (dada.getMultipleDades()!=null) {
					lista = dada.getMultipleDades();
				}
				
				if (lista != null) {
					Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			        campsAddicionals.put("listaDadas", lista);
					Object cmd = TascaFormHelper.getCommandForCamps(lista, null, request, campsAddicionals, campsAddicionalsClasses, false);
					BindingResult res = new BeanPropertyBindingResult(cmd, "cmd");
					Validator validatorDades = TascaFormHelper.getBeanValidatorForCommand(lista);
			        validatorTasca.validate(cmd, res);
					validatorDades.validate(cmd, res);
					
					valors.putAll(TascaFormHelper.getValorsFromCommand(lista, cmd, false));
					
					for ( FieldError rs : res.getFieldErrors()) {
						errors.put(rs.getField(), getMessage(request, rs.getCode()));
					}
				}				
			}
			
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			campsAddicionals.put("listaDadas", dades);
			Validator validatorDades = TascaFormHelper.getBeanValidatorForCommand(dades);
			Object cmd = TascaFormHelper.getCommandForCamps(dades, null, request, campsAddicionals, campsAddicionalsClasses, false);
			BindingResult res = new BeanPropertyBindingResult(cmd, "cmd");
	        validatorTasca.validate(cmd, res);
			validatorDades.validate(cmd, res);
			
			valors.putAll(TascaFormHelper.getValorsFromCommand(dades, cmd, false));
			
			for ( FieldError rs : res.getFieldErrors()) {
				errors.put(rs.getField(), getMessage(request, rs.getCode()));
			}
			
			if (!errors.isEmpty()) {
				MissatgesHelper.error(request, getMessage(request, "error.validacio"));
				
				model.addAttribute("errors", errors);
				model.addAttribute("tasca", tasca);
				model.addAttribute("dades", dades);
				model.addAttribute("entornId", entorn.getId());
				model.addAttribute("expedientTipus", expedientTipus);
				model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
				
				return "v3/expedient/iniciarPasForm";
			}
			
			// Si l'expedient ha de demanar titol i/o número redirigeix al pas per demanar aquestes dades
			if (expedientTipus.isDemanaNumero() || expedientTipus.isDemanaTitol() || expedientTipus.isSeleccionarAny()) {
				if (definicioProcesId != null) {
					model.addAttribute("definicioProcesId", definicioProcesId);
				}
				model.addAttribute("entornId", entorn.getId());
				model.addAttribute("anysSeleccionables", getAnysSeleccionables());
				model.addAttribute("expedientTipus", expedientTipus);
				model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
				
				request.getSession().setAttribute(ExpedientInicioController.CLAU_SESSIO_FORM_VALORS,valors);
				
				return "v3/expedient/iniciarPasTitol";
			} else {
				try {
					ExpedientDto iniciat = iniciarExpedient(
							entorn.getId(),
							expedientTipusId,
							definicioProcesId,
							(String)request.getSession().getAttribute(ExpedientInicioController.CLAU_SESSIO_NUMERO),
							(String)request.getSession().getAttribute(ExpedientInicioController.CLAU_SESSIO_TITOL),
							(Integer)request.getSession().getAttribute(ExpedientInicioController.CLAU_SESSIO_ANY),
							valors);
					MissatgesHelper.info(request, getMessage(request, "info.expedient.iniciat", new Object[] {iniciat.getIdentificador()}));
				    ExpedientInicioController.netejarSessio(request);
				} catch (Exception ex) {
					MissatgesHelper.error(request, getMessage(request, "error.iniciar.expedient"));
					logger.error("No s'ha pogut iniciar l'expedient", ex);
				}
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permisos.iniciar.tipus.exp") );
		}
		
	    return "redirect:/v3/expedient/iniciar";
	}
	
	private ExpedientTascaDto obtenirTascaInicial(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors,
			HttpServletRequest request) {
		ExpedientTascaDto tasca = expedientService.getStartTask( entornId, expedientTipusId, definicioProcesId, valors);
		tasca.setId((String)request.getSession().getAttribute(ExpedientInicioController.CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(ExpedientInicioController.CLAU_SESSIO_FORM_VALIDAT); 
		if (validat != null)
			tasca.setValidada(true);
		return tasca;
	}

	public List<ParellaCodiValorDto> getAnysSeleccionables() {
		List<ParellaCodiValorDto> anys = new ArrayList<ParellaCodiValorDto>();
		int anyActual = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 10; i++) {
			ParellaCodiValorDto par = new ParellaCodiValorDto();
			par.setCodi(String.valueOf(anyActual - i));
			par.setValor(anyActual - i);
			anys.add(par);
		}
		return anys;
	}
	
	private synchronized ExpedientDto iniciarExpedient(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			String numero,
			String titol,
			Integer any,
			Map<String, Object> valors) {
		return expedientService.iniciar(
				entornId,
				null,
				expedientTipusId,
				definicioProcesId,
				any,
				numero,
				titol,
				null,
				null,
				null,
				null,
				false,
				null,
				null,
				null,
				null,
				null,
				null,
				false,
				null,
				null,
				false,
				valors,
				null,
				IniciadorTipusDto.INTERN,
				null,
				null,
				null,
				null);
	}

	private static final Log logger = LogFactory.getLog(ExpedientInicioController.class);
}
