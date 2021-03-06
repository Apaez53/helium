/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ArxiuDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.DocumentService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controlador per a la descàrrega d'arxius
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class DocumentArxiuController extends BaseController {

	private DocumentService documentService;



	@Autowired
	public DocumentArxiuController(
			DocumentService documentService) {
		this.documentService  = documentService;
	}

	@RequestMapping(value = "/document/arxiuMostrar")
	public String arxiuMostrar(
			HttpServletRequest request,
			@RequestParam(value = "token", required = true) String token,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ArxiuDto arxiu = null;
			if (token != null)
				try {
					arxiu = documentService.arxiuDocumentPerMostrar(token);
				} catch (Exception e) {
					return e.getCause().getLocalizedMessage();
				}
			if (arxiu != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
			}
			return "arxiuView";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/document/arxiuPerSignar")
	public String arxiuPerSignar(
			HttpServletRequest request,
			@RequestParam(value = "token", required = true) String token,
			@RequestParam(value = "noe", required = false) Boolean noe,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			boolean estampar = (noe != null) ? !noe.booleanValue() : true;
			try {
				ArxiuDto arxiu = documentService.arxiuDocumentPerSignar(token, estampar);
				if (arxiu != null) {
					model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, arxiu.getNom());
					model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_DATA, arxiu.getContingut());
				}
			} catch (Exception e) {
				return e.getCause().getLocalizedMessage();
			}
	        return "arxiuView";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

}
