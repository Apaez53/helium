/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.tramitacio;

import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;

/**
 * Interfície per accedir al sistema de tramitacio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TramitacioPlugin {

	public void publicarExpedient(PublicarExpedientRequest request) throws TramitacioPluginException;

	public void publicarEvent(PublicarEventRequest request) throws TramitacioPluginException;

	public DadesTramit obtenirDadesTramit(ObtenirDadesTramitRequest request) throws TramitacioPluginException;

	public void comunicarResultatProcesTramit(ResultatProcesTramitRequest request) throws TramitacioPluginException;

	public DadesVistaDocument obtenirVistaDocument(ObtenirVistaDocumentRequest request) throws TramitacioPluginException;

	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre) throws TramitacioPluginException;

	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio) throws TramitacioPluginException;

	public RespostaJustificantDetallRecepcio obtenirJustificantDetallRecepcio(String numeroRegistre) throws TramitacioPluginException;
}
