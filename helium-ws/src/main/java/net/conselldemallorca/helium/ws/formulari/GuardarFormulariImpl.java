/**
 * 
 */
package net.conselldemallorca.helium.ws.formulari;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import net.conselldemallorca.helium.core.extern.formulari.GuardarFormulari;
import net.conselldemallorca.helium.core.extern.formulari.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.service.TascaService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementació del servei per guardar les dades dels formularis externs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.core.extern.formulari.GuardarFormulari")
public class GuardarFormulariImpl implements GuardarFormulari {

	private TascaService tascaService;



	public void guardar(String formulariId, List<ParellaCodiValor> valors) {
		Map<String, Object> valorsTasca = new HashMap<String, Object>();
		for (ParellaCodiValor parella: valors)
			valorsTasca.put(parella.getCodi(), parella.getValor());
		tascaService.guardarFormulariExtern(formulariId, valorsTasca);
	}

	@Autowired
	public void setTascaService(TascaService tascaService) {
		this.tascaService = tascaService;
	}

}
