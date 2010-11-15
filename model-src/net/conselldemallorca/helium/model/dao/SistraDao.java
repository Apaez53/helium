/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import net.conselldemallorca.helium.integracio.bantel.client.wsdl.ReferenciaEntrada;
import net.conselldemallorca.helium.integracio.bantel.client.wsdl.TramiteBTE;
import net.conselldemallorca.helium.integracio.zonaper.wsdl.DocumentoExpediente;
import net.conselldemallorca.helium.integracio.zonaper.wsdl.DocumentosExpediente;
import net.conselldemallorca.helium.integracio.zonaper.wsdl.EventoExpediente;
import net.conselldemallorca.helium.integracio.zonaper.wsdl.Expediente;
import net.conselldemallorca.helium.model.exception.SistraBackofficeException;
import net.conselldemallorca.helium.model.hibernate.Expedient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dao per accedir a SISTRA emprant web services
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class SistraDao {

	private net.conselldemallorca.helium.integracio.bantel.client.wsdl.BackofficeFacade bantelBackofficeFacade;
	private net.conselldemallorca.helium.integracio.zonaper.wsdl.BackofficeFacade zonaperBackofficeFacade;



	public void zonaperExpedientIniciar(
			Expedient expedient,
			String descripcio) {
		TramiteBTE tramitSistra = getTramiteSistra(expedient);
		if (tramitSistra == null)
			throw new SistraBackofficeException("No s'ha pogut trobar el tràmit per l'expedient " + expedient.getId());
		try {
			Expediente expediente = expedientPerTramitSistra(
					expedient,
					tramitSistra,
					descripcio);
			zonaperBackofficeFacade.altaExpediente(expediente);
			logger.info("Expedient creat a la zona personal, ref bantel: " + expedient.getNumeroEntradaSistra());
		} catch (Exception ex) {
			throw new SistraBackofficeException("Error iniciat l'expedient al SISTRA", ex);
		}
	}

	public void zonaperExpedientEvent(
			Expedient expedient,
			String titol,
			String text,
			String textSms,
			String enllasConsulta,
			Date data,
			String adjuntTitol,
			String adjuntArxiuNom,
			byte[] adjuntArxiuContingut,
			String adjuntModel,
			Integer adjuntVersio) {
		TramiteBTE tramitSistra = getTramiteSistra(expedient);
		if (tramitSistra == null)
			throw new SistraBackofficeException("No s'ha pogut trobar el tràmit per l'expedient " + expedient.getId());
		try {
			Expediente expediente = expedientPerTramitSistra(
					expedient,
					tramitSistra,
					null);
			EventoExpediente event = new EventoExpediente();
			event.setTitulo(titol);
			event.setTexto(text);
			if (textSms != null)
				event.setTextoSMS(new JAXBElement<String>(new QName("textoSMS"), String.class, textSms));
			if (enllasConsulta != null)
				event.setEnlaceConsulta(new JAXBElement<String>(new QName("enlaceConsulta"), String.class, enllasConsulta));
			if (data != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				event.setFecha(new JAXBElement<String>(new QName("fecha"), String.class, sdf.format(data)));
			}
			if (adjuntTitol != null && adjuntArxiuNom != null && adjuntArxiuContingut != null) {
				DocumentosExpediente docs = new DocumentosExpediente();
				DocumentoExpediente doc = new DocumentoExpediente();
				doc.setTitulo(new JAXBElement<String>(new QName("titulo"), String.class, adjuntTitol));
				doc.setNombre(new JAXBElement<String>(new QName("nombre"), String.class, adjuntArxiuNom));
				doc.setContenidoFichero(new JAXBElement<byte[]>(new QName("contenidoFichero"), byte[].class, adjuntArxiuContingut));
				doc.setModeloRDS(new JAXBElement<String>(new QName("modeloRDS"), String.class, adjuntModel));
				doc.setVersionRDS(new JAXBElement<Integer>(new QName("versionRDS"), Integer.class, adjuntVersio));
				docs.getDocumento().add(doc);
				event.setDocumentos(new JAXBElement<DocumentosExpediente>(new QName("documentos"), DocumentosExpediente.class, docs));
			}
			zonaperBackofficeFacade.altaEventoExpediente(
					expediente.getUnidadAdministrativa(),
					expediente.getIdentificadorExpediente(),
					expediente.getClaveExpediente(),
					event);
		} catch (Exception ex) {
			throw new SistraBackofficeException("Error al crear event per l'expedient SISTRA", ex);
		}
	}

	public TramiteBTE getTramitPerExpedient(Expedient expedient) {
		return getTramiteSistra(expedient);
	}



	public void setBantelBackofficeFacade(
			net.conselldemallorca.helium.integracio.bantel.client.wsdl.BackofficeFacade bantelBackofficeFacade) {
		this.bantelBackofficeFacade = bantelBackofficeFacade;
	}
	public void setZonaperBackofficeFacade(
			net.conselldemallorca.helium.integracio.zonaper.wsdl.BackofficeFacade zonaperBackofficeFacade) {
		this.zonaperBackofficeFacade = zonaperBackofficeFacade;
	}



	private Expediente expedientPerTramitSistra(
			Expedient expedient,
			TramiteBTE tramitSistra,
			String descripcio) {
		Expediente expediente = new Expediente();
		expediente.setIdentificadorExpediente(expedient.getNumeroIdentificador());
		expediente.setIdioma(tramitSistra.getIdioma());
		expediente.setUnidadAdministrativa(tramitSistra.getUnidadAdministrativa());
		expediente.setDescripcion(descripcio);
		expediente.setNumeroEntradaBTE(new JAXBElement<String>(new QName("numeroEntradaBTE"), String.class, expedient.getNumeroEntradaSistra()));
		String nivelAutenticacion = tramitSistra.getNivelAutenticacion();
		if ("A".equals(nivelAutenticacion)) {
			expediente.setAutenticado(false);
		} else {
			expediente.setAutenticado(true);
			String usuariSeycon = null;
			if (tramitSistra.getUsuarioSeycon() != null)
				usuariSeycon = tramitSistra.getUsuarioSeycon().getValue();
			expediente.setIdentificadorUsuario(new JAXBElement<String>(new QName("identificadorUsuario"), String.class, usuariSeycon));
			String representadoNif = null;
			if (tramitSistra.getRepresentadoNif() != null)
				representadoNif = tramitSistra.getRepresentadoNif().getValue();
			expediente.setNifRepresentado(new JAXBElement<String>(new QName("nifRepresentado"), String.class, representadoNif));
			String representadoNombre = null;
			if (tramitSistra.getRepresentadoNombre() != null)
				representadoNombre = tramitSistra.getRepresentadoNombre().getValue();
			expediente.setNombreRepresentado(new JAXBElement<String>(new QName("nombreRepresentado"), String.class, representadoNombre));
		}
		return expediente;
	}

	private TramiteBTE getTramiteSistra(Expedient expedient) {
		try {
			ReferenciaEntrada refTramit = new ReferenciaEntrada();
			refTramit.setNumeroEntrada(expedient.getNumeroEntradaSistra());
			refTramit.setClaveAcceso(new JAXBElement<String>(new QName("claveAcceso"), String.class, expedient.getClaveAccesoSistra()));
			return bantelBackofficeFacade.obtenerEntrada(refTramit);
		} catch (Exception ex) {
			return null;
		}
	}

	private static Log logger = LogFactory.getLog(SistraDao.class);

}
