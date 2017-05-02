/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;

/**
 * Helper per a gestionar els estats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EstatHelper {

	@Resource
	private EstatRepository estatRepository;

	/** Consulta l'estat d'un tipus d'expedient per codi. Si no es troba i s'inclou la herència se cercarà
	 * en el tipus d'expedient pare.
	 * 
	 * @param expedientTipus
	 * @param codi
	 * @param incloureHeretats
	 * @return
	 */
	public Estat findEstat(
			ExpedientTipus expedientTipus, 
			String codi, 
			boolean incloureHeretats) {
		Estat estat = estatRepository.findByExpedientTipusIdAndCodi(expedientTipus.getId(), codi);
		if (estat == null && incloureHeretats && expedientTipus.getExpedientTipusPare() != null)
			estat = estatRepository.findByExpedientTipusIdAndCodi(expedientTipus.getExpedientTipusPare().getId(), codi);
		return estat;
	}	
}
