/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * Controlador per a esborrar les dades dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientDadaEsborrarController extends BaseController {

	private ExpedientService expedientService;
	private TascaService tascaService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientDadaEsborrarController(
			TascaService tascaService,
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.tascaService = tascaService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@RequestMapping(value = "/expedient/dadaProcesEsborrar")
	public String dadaProcesEsborrar(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "var", required = true) String var,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				try {
					expedientService.deleteVariable(id, var);
					missatgeInfo(request, getMessage("info.dada.proces.esborrada") );
				} catch (Exception ex) {
					Long entornId = entorn.getId();
					String numeroExpedient = id;
					logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+numeroExpedient+" No s'ha pogut esborrar la dada del procés", ex);	
					missatgeError(request, getMessage("error.esborrar.dada.proces"), ex.getLocalizedMessage());
				}
				return "redirect:/expedient/dades.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
			}
			return "redirect:/expedient/consulta.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/dadaTascaEsborrar")
	public String dadaTascaEsborrar(
			HttpServletRequest request,
			@RequestParam(value = "taskId", required = true) String taskId,
			@RequestParam(value = "var", required = true) String var,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			TascaDto tasca = tascaService.getById(
					entorn.getId(),
					taskId,
					null,
					null,
					false,
					false);
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(tasca.getProcessInstanceId());
			if (potModificarExpedient(expedient)) {
				try {
					tascaService.esborrarVariable(
							entorn.getId(),
							taskId,
							var);
					missatgeInfo(request, getMessage("dada.tasca.esborrada") );
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.esborrar.dada.tasca"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar la dada de la tasca", ex);
				}
				return "redirect:/expedient/dades.html?id=" + tasca.getProcessInstanceId();
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
			}
			return "redirect:/expedient/consulta.html";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}


	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}



	private static final Log logger = LogFactory.getLog(ExpedientDadaEsborrarController.class);

}
