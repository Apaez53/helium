/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTerminiModificarCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTerminiModificarCommand.TerminiModificacioTipus;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per a la pàgina d'informació de l'termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTerminisController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private TerminiService terminiService;

	@RequestMapping(value = "/{expedientId}/terminis", method = RequestMethod.GET)
	public String terminis(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potConsultarExpedient(expedient)) {
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(expedient.getProcessInstanceId());
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"terminis",
						dissenyService.findTerminisAmbDefinicioProcesId(instanciaProces.getDefinicioProces().getId()));
				model.addAttribute(
						"iniciats",
						dissenyService.findIniciatsAmbProcessInstanceId(expedient.getProcessInstanceId()));
				
				return "v3/expedient/terminis";
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.consultar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
		}
		
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/{processInstanceId}/{terminiId}/terminiIniciar", method = RequestMethod.GET)
	public String terminiIniciar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long terminiId,
			Model model) {
		try {
			if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
				mostrarInformacioExpedientPerPipella(request, expedientId, model, "terminis", expedientService);
			}
			terminiService.iniciar(terminiId,processInstanceId,new Date(),true);
			MissatgesHelper.info(request, getMessage(request, "info.termini.iniciat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.iniciar.termini"));
        	logger.error("No s'ha pogut iniciar el termini", ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/{terminiId}/terminiPausar", method = RequestMethod.GET)
	public String terminiPausar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			Model model) {
		try {
			if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
				mostrarInformacioExpedientPerPipella(request, expedientId, model, "terminis", expedientService);
			}
			terminiService.pausar(terminiId, new Date());
			MissatgesHelper.info(request, getMessage(request, "info.termini.aturat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.aturar.termini"));
			logger.error("No s'ha pogut aturar el termini", ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/{terminiId}/terminiContinuar", method = RequestMethod.GET)
	public String terminiContinuar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			Model model) {
		try {
			if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
				mostrarInformacioExpedientPerPipella(request, expedientId, model, "terminis", expedientService);
			}
			terminiService.continuar(terminiId, new Date());
			MissatgesHelper.info(request, getMessage(request, "info.termini.continuat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.continuat.termini"));
			logger.error("No s'ha pogut continuat el termini", ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/{terminiId}/terminiCancelar", method = RequestMethod.GET)
	public String terminiCancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			Model model) {
		try {
			if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
				mostrarInformacioExpedientPerPipella(request, expedientId, model, "terminis", expedientService);
			}
			terminiService.cancelar(terminiId, new Date());
			MissatgesHelper.info(request, getMessage(request, "info.termini.cancelat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.cancelat.termini"));
			logger.error("No s'ha pogut cancel·lar el termini", ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/{terminiId}/terminiModificar", method = RequestMethod.GET)
	public String terminiModificar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potConsultarExpedient(expedient)) {
				NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
				TerminiIniciatDto terminiIniciat = dissenyService.findIniciatAmbId(terminiId);
				ExpedientTerminiModificarCommand expedientTerminiModificarCommand = new ExpedientTerminiModificarCommand();
				expedientTerminiModificarCommand.setTerminiId(terminiId);
				expedientTerminiModificarCommand.setAnys(terminiIniciat.getAnys());
				expedientTerminiModificarCommand.setMesos(terminiIniciat.getMesos());
				expedientTerminiModificarCommand.setDies(terminiIniciat.getDies());
				expedientTerminiModificarCommand.setDataInici(terminiIniciat.getDataInici());
				expedientTerminiModificarCommand.setDataFi(terminiIniciat.getDataFi());
				model.addAttribute("termini", terminiIniciat.getTermini());
				model.addAttribute("command", expedientTerminiModificarCommand);
				model.addAttribute("tipus", TerminiModificacioTipus.values());
				model.addAttribute(expedientTerminiModificarCommand);
				return "v3/expedient/terminiModificar";
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.consultar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
		}
		
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/{terminiId}/terminiModificar", method = RequestMethod.POST)
	public String terminiModificar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long terminiId,
			@Valid @ModelAttribute ExpedientTerminiModificarCommand expedientTerminiModificarCommand,
			BindingResult result,
			SessionStatus status,
			Model model) {
		ExpedientDto expedient = expedientService.findById(expedientId);
		if (potModificarExpedient(expedient)) {
			try {
				TerminiIniciatDto terminiIniciat = dissenyService.findIniciatAmbId(terminiId);
				Date inicio = null;
				boolean esDataFi = TerminiModificacioTipus.DATA_FI.equals(expedientTerminiModificarCommand.getTipus());
				terminiService.cancelar(terminiId, new Date());
				if (TerminiModificacioTipus.DURADA.equals(expedientTerminiModificarCommand.getTipus())) {
					inicio = terminiIniciat.getDataInici();
				} else if (TerminiModificacioTipus.DATA_INICI.equals(expedientTerminiModificarCommand.getTipus())) {
					inicio = expedientTerminiModificarCommand.getDataInici();
				} else {
					inicio = expedientTerminiModificarCommand.getDataFi();
				}
				terminiService.iniciar(
						terminiIniciat.getTermini().getId(),
						expedient.getProcessInstanceId(),
						inicio,
						expedientTerminiModificarCommand.getAnys(),
						expedientTerminiModificarCommand.getMesos(),
						expedientTerminiModificarCommand.getDies(),
						esDataFi);
				MissatgesHelper.info(request, getMessage(request, "info.termini.modificat"));
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.modificar.termini"));
	        	logger.error("No s'ha pogut modificar el termini", ex);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
		}
		return modalUrlTancar();
	}	

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTerminisController.class);
}
