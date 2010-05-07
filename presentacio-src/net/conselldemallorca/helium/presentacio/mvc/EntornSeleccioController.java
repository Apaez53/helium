/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.UsuariPreferencies;
import net.conselldemallorca.helium.model.service.EntornService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.model.service.PersonaService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;
import net.conselldemallorca.helium.security.permission.ExtendedPermission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador per seleccionar l'entorn actiu
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class EntornSeleccioController extends BaseController {

	private EntornService entornService;
	private PersonaService personaService;
	private PermissionService permissionService;



	@Autowired
	public EntornSeleccioController(
			EntornService entornService,
			PersonaService personaService,
			PermissionService permissionService) {
		this.entornService = entornService;
		this.personaService = personaService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("preferencies")
	public UsuariPreferencies populateUsuariPreferencies() {
		return personaService.getUsuariPreferencies();
	}

	@ModelAttribute("entorns")
	public List<Entorn> populateEntorns() {
		List<Entorn> entorns = null;
		try {
			entorns = entornService.findActius();
			permissionService.filterAllowed(
					entorns,
					Entorn.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION,
						ExtendedPermission.READ});
			
		} catch (Exception ex) {
			logger.error("Error cercant entorns actius", ex);
		}
		return entorns;
	}

	@RequestMapping(value = "/entorn/seleccio", method = RequestMethod.GET)
	public String seleccionar() {
		return "entorn/seleccio";
	}

	@RequestMapping(value = "/entorn/configDefault", method = RequestMethod.GET)
	public String configDefault(
			HttpServletRequest request,
			@RequestParam(value = "entornId", required = false) Long entornId,
			ModelMap model) {
		if (entornId == null) {
			personaService.savePrefDefaultEntorn(null);
		} else {
			Entorn entorn = entornService.getById(entornId);
			if (entorn != null)
				personaService.savePrefDefaultEntorn(entorn.getCodi());
		}
		model.addAttribute("preferencies", personaService.getUsuariPreferencies());
		return "entorn/seleccio";
	}



	private static final Log logger = LogFactory.getLog(PermisosEntornController.class);

}
