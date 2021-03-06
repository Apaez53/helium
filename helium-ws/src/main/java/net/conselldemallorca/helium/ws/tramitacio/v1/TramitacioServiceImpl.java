/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio.v1;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;

import net.conselldemallorca.helium.core.model.dto.ArxiuDto;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.DocumentService;
import net.conselldemallorca.helium.core.model.service.EntornService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService.FiltreAnulat;
import net.conselldemallorca.helium.ws.tramitacio.TramitacioException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Implementació del servei de tramitació d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		endpointInterface = "net.conselldemallorca.helium.ws.tramitacio.v1.TramitacioService",
		targetNamespace = "http://conselldemallorca.net/helium/ws/tramitacio/v1")
public class TramitacioServiceImpl implements TramitacioService {

	private static final boolean PRINT_USUARI = false;

	private EntornService entornService;
	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private TascaService tascaService;
	private DocumentService documentService;
	private PermissionService permissionService;



	public String iniciExpedient(
			String entorn,
			String expedientTipus,
			String numero,
			String titol,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		ExpedientTipus et = findExpedientTipusAmbEntornICodi(e.getId(), expedientTipus);
		if (!validarPermisExpedientTipusCreate(et))
			throw new TramitacioException("No té permisos per iniciar un expedient de tipus '" + expedientTipus + "'");
		if (et == null)
			throw new TramitacioException("No existeix cap tipus d'expedient amb el codi '" + expedientTipus + "'");
		if (numero != null && !et.getTeNumero())
			throw new TramitacioException("Aquest tipus d'expedient no suporta número d'expedient");
		if (titol != null && !et.getTeTitol())
			throw new TramitacioException("Aquest tipus d'expedient no suporta títol d'expedient");
		Map<String, Object> variables = null;
		if (valorsFormulari != null) {
			variables = new HashMap<String, Object>();
			for (ParellaCodiValor parella: valorsFormulari) {
				if (parella.getValor() instanceof XMLGregorianCalendar)
					variables.put(
							parella.getCodi(),
							((XMLGregorianCalendar)parella.getValor()).toGregorianCalendar().getTime());
				else
					variables.put(
							parella.getCodi(),
							parella.getValor());
			}
		}
		try {
			ExpedientDto expedient = expedientService.iniciar(
					e.getId(),
					null,
					et.getId(),
					null,
					null,
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
					variables,
					null,
					IniciadorTipus.INTERN,
					null,
					null,
					null,
					null);
			return expedient.getProcessInstanceId();
		} catch (Exception ex) {
			logger.error("No s'han pogut iniciar l'expedient", ex);
			throw new TramitacioException("No s'han pogut iniciar l'expedient: " + ex.getMessage());
		}
	}

	public List<TascaTramitacio> consultaTasquesPersonals(
			String entorn) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			List<TascaLlistatDto> tasques = tascaService.findTasquesPersonalsTramitacio(e.getId(), null, null, true);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaLlistatDto tasca: tasques)
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	public List<TascaTramitacio> consultaTasquesGrup(
			String entorn) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			List<TascaLlistatDto> tasques = tascaService.findTasquesGrupTramitacio(e.getId(), null, null, true);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaLlistatDto tasca: tasques)
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	public void agafarTasca(
			String entorn,
			String tascaId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		boolean agafada = false;
		try {
			if (tascaService.isTasquesGrupTramitacio(e.getId(), tascaId, null)) {
				tascaService.agafar(e.getId(), tascaId);
				agafada = true;
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut agafar la tasca", ex);
			throw new TramitacioException("No s'ha pogut agafar la tasca: " + ex.getMessage());
		}
		if (!agafada)
			throw new TramitacioException("Aquest usuari no té la tasca " + tascaId + " assignada");
	}

	public void alliberarTasca(
			String entorn,
			String tascaId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		boolean alliberada = false;
		try {
			List<TascaLlistatDto> tasques = tascaService.findTasquesPersonalsTramitacio(e.getId(), null, null, false);
			for (TascaLlistatDto tasca: tasques) {
				if (tasca.getId().equals(tascaId)) {
					tascaService.alliberar(e.getId(), tascaId, true);
					alliberada = true;
					break;
				}
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut alliberar la tasca", ex);
			throw new TramitacioException("No s'ha pogut alliberar la tasca: " + ex.getMessage());
		}
		if (!alliberada)
			throw new TramitacioException("Aquest usuari no té la tasca " + tascaId + " assignada");
	}

	public List<CampTasca> consultaFormulariTasca(
			String entorn,
			String tascaId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		TascaDto tasca = tascaService.getById(
				e.getId(),
				tascaId,
				null,
				null,
				true,
				false);
		List<CampTasca> resposta = new ArrayList<CampTasca>();
		for (net.conselldemallorca.helium.core.model.hibernate.CampTasca campTasca: tasca.getCamps())
			resposta.add(convertirCampTasca(
					campTasca,
					tasca.getVariable(campTasca.getCamp().getCodi())));
		return resposta;
	}

	public void setDadesFormulariTasca(
			String entorn,
			String tascaId,
			List<ParellaCodiValor> valors) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Map<String, Object> variables = null;
		if (valors != null) {
			variables = new HashMap<String, Object>();
			for (ParellaCodiValor parella: valors) {
				if (parella.getValor() instanceof XMLGregorianCalendar) {
					variables.put(
							parella.getCodi(),
							((XMLGregorianCalendar)parella.getValor()).toGregorianCalendar().getTime());
				} else if (parella.getValor() instanceof Object[]) {
					Object[] multiple = (Object[])parella.getValor();
					// Converteix les dates dins registres i vars múltiples
					// al tipus corecte 
					for (int i = 0; i < multiple.length; i++) {
						if (multiple[i] instanceof Object[]) {
							Object[] fila = (Object[])multiple[i];
							for (int j = 0; j < fila.length; j++) {
								if (fila[j] instanceof XMLGregorianCalendar) {
									fila[j] = ((XMLGregorianCalendar)fila[j]).toGregorianCalendar().getTime();
								}
							}
						} else if (multiple[i] instanceof XMLGregorianCalendar) {
							multiple[i] = ((XMLGregorianCalendar)multiple[i]).toGregorianCalendar().getTime();
						}
					}
					variables.put(
							parella.getCodi(),
							parella.getValor());
				} else {
					variables.put(
							parella.getCodi(),
							parella.getValor());
				}
			}
		}
		try {
			tascaService.validar(
					e.getId(),
					tascaId,
					variables,
					true);
		} catch (Exception ex) {
			logger.error("No s'han pogut guardar les variables a la tasca", ex);
			throw new TramitacioException("No s'han pogut guardar les variables a la tasca: " + ex.getMessage());
		}
	}

	public List<DocumentTasca> consultaDocumentsTasca(
			String entorn,
			String tascaId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		TascaDto tasca = tascaService.getById(
				e.getId(),
				tascaId,
				null,
				null,
				true,
				false);
		List<DocumentTasca> resposta = new ArrayList<DocumentTasca>();
		for (net.conselldemallorca.helium.core.model.hibernate.DocumentTasca documentTasca: tasca.getDocuments())
			resposta.add(convertirDocumentTasca(
					documentTasca,
					tasca.getVarsDocuments().get(documentTasca.getDocument().getCodi())));
		return resposta;
	}

	public void setDocumentTasca(
			String entorn,
			String tascaId,
			String arxiu,
			String nom,
			Date data,
			byte[] contingut) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			tascaService.comprovarTascaAssignadaIValidada(e.getId(), tascaId, null);
			documentService.guardarDocumentTasca(
					e.getId(),
					tascaId,
					arxiu,
					data,
					nom,
					contingut);
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document a la tasca", ex);
			throw new TramitacioException("No s'ha pogut guardar el document a la tasca: " + ex.getMessage());
		}
	}
	public void esborrarDocumentTasca(
			String entorn,
			String tascaId,
			String document) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			tascaService.comprovarTascaAssignadaIValidada(e.getId(), tascaId, null);
			documentService.esborrarDocument(
					tascaId,
					null,
					document);
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar el document de la tasca", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document de la tasca: " + ex.getMessage());
		}
	}

	public void finalitzarTasca(
			String entorn,
			String tascaId,
			String transicio) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			tascaService.completar(
					e.getId(),
					tascaId,
					true,
					transicio);
		} catch (Exception ex) {
			logger.error("No s'ha pogut completar la tasca", ex);
			throw new TramitacioException("No s'ha pogut completar la tasca: " + ex.getMessage());
		}
	}

	public List<CampProces> consultarVariablesProces(
			String entorn,
			String processInstanceId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
		if (!validarPermisExpedientTipusRead(expedient.getTipus()))
			throw new TramitacioException("No té permisos per llegir les dades del proces '" + processInstanceId + "'");
		try {
			List<CampProces> resposta = new ArrayList<CampProces>();
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId, true, true, true);
			if (instanciaProces.getVariables() != null) {
				for (String var: instanciaProces.getVariables().keySet()) {
					Camp campVar = null;
					for (Camp camp: instanciaProces.getCamps()) {
						if (camp.getCodi().equals(var)) {
							campVar = camp;
							break;
						}
					}
					if (campVar == null) {
						campVar = new Camp();
						campVar.setCodi(var);
						campVar.setEtiqueta(var);
						campVar.setTipus(TipusCamp.STRING);
					}
					resposta.add(convertirCampProces(
							campVar,
							instanciaProces.getVariable(var)));
				}
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar la variable al procés: " + ex.getMessage());
		}
	}
	public void setVariableProces(
			String entorn,
			String processInstanceId,
			String varCodi,
			Object valor) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			if (valor instanceof XMLGregorianCalendar) {
				expedientService.updateVariable(
						processInstanceId,
						varCodi,
						((XMLGregorianCalendar)valor).toGregorianCalendar().getTime());
			} else if (valor instanceof Object[]) {
				Object[] multiple = (Object[])valor;
				// Converteix les dates dins registres i vars múltiples
				// al tipus corecte 
				for (int i = 0; i < multiple.length; i++) {
					if (multiple[i] instanceof Object[]) {
						Object[] fila = (Object[])multiple[i];
						for (int j = 0; j < fila.length; j++) {
							if (fila[j] instanceof XMLGregorianCalendar) {
								fila[j] = ((XMLGregorianCalendar)fila[j]).toGregorianCalendar().getTime();
							}
						}
					} else if (multiple[i] instanceof XMLGregorianCalendar) {
						multiple[i] = ((XMLGregorianCalendar)multiple[i]).toGregorianCalendar().getTime();
					}
				}
				expedientService.updateVariable(
						processInstanceId,
						varCodi,
						valor);
			} else {
				expedientService.updateVariable(
						processInstanceId,
						varCodi,
						valor);
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar la variable al procés: " + ex.getMessage());
		}
	}
	public void esborrarVariableProces(
			String entorn,
			String processInstanceId,
			String varCodi) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			expedientService.deleteVariable(
					processInstanceId,
					varCodi);
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut esborrar la variable al procés: " + ex.getMessage());
		}
	}

	public List<DocumentProces> consultarDocumentsProces(
			String entorn,
			String processInstanceId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
		if (!validarPermisExpedientTipusRead(expedient.getTipus()))
			throw new TramitacioException("No té permisos per accedir a les dades del proces '" + processInstanceId + "'");
		try {
			List<DocumentProces> resposta = new ArrayList<DocumentProces>();
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId, true, true, true);
			for (DocumentDto document: instanciaProces.getVarsDocuments().values()) {
				resposta.add(convertirDocumentProces(document));
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'han pogut consultar el documents del procés", ex);
			throw new TramitacioException("No s'han pogut consultar el documents del procés: " + ex.getMessage());
		}
	}
	public ArxiuProces getArxiuProces(
			Long documentId) throws TramitacioException {
		try {
			ArxiuProces resposta = null;
			if (documentId != null) {
				DocumentDto docInfo = documentService.documentInfo(documentId);
				String processInstanceId = docInfo.getProcessInstanceId();
				Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
				if (!validarPermisExpedientTipusRead(expedient.getTipus()))
					throw new TramitacioException("No té permisos per llegir l'arxiu '" + documentId + "'");
				ArxiuDto arxiu = documentService.arxiuDocumentPerMostrar(documentId);
				if (arxiu != null) {
					resposta = new ArxiuProces();
					resposta.setNom(arxiu.getNom());
					resposta.setContingut(arxiu.getContingut());
				}
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir l'arxiu del procés", ex);
			throw new TramitacioException("No s'ha pogut obtenir l'arxiu del procés: " + ex.getMessage());
		}
	}
	public Long setDocumentProces(
			String entorn,
			String processInstanceId,
			String documentCodi,
			String arxiu,
			Date data,
			byte[] contingut) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId, false, false, false);
			if (instanciaProces == null)
				throw new TramitacioException("No s'ha pogut trobar la instancia de proces amb id " + processInstanceId);
			Document document = dissenyService.findDocumentAmbDefinicioProcesICodi(
					instanciaProces.getDefinicioProces().getId(),
					documentCodi);
			if (document == null)
				throw new TramitacioException("No s'ha pogut trobar el document amb codi " + documentCodi);
			return documentService.guardarDocumentProces(
					processInstanceId,
					documentCodi,
					null,
					data,
					arxiu,
					contingut,
					false);
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar el document al procés: " + ex.getMessage());
		}
	}
	public void esborrarDocumentProces(
			String entorn,
			String processInstanceId,
			Long documentId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			documentService.esborrarDocument(
					null,
					processInstanceId,
					documentService.getDocumentCodiPerDocumentStoreId(documentId));
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar el document del procés", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document del procés: " + ex.getMessage());
		}
	}

	public void executarAccioProces(
			String entorn,
			String processInstanceId,
			String accio) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		DefinicioProces dp = expedientService.getDefinicioProcesPerProcessInstanceId(processInstanceId);
		Accio ac = dissenyService.findAccioAmbDefinicioProcesICodi(dp.getId(), accio);
		if (ac == null || ac.isOculta())
			throw new TramitacioException("No existeix cap accio amb el codi '" + accio + "'");
		Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
		if (ac.isPublica()) {
			if (!validarPermisExpedientTipusRead(expedient.getTipus()) && !validarPermisExpedientTipusWrite(expedient.getTipus()))
				throw new TramitacioException("No té permisos per executar l'acció '" + accio + "'");
		} else {
			if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
				throw new TramitacioException("No té permisos per executar l'acció '" + accio + "'");
		}
		try {
			expedientService.executarAccio(processInstanceId, accio);
		} catch (Exception ex) {
			logger.error("No s'ha pogut executar l'acciÃ³", ex);
			throw new TramitacioException("No s'ha pogut executar l'acció: " + ex.getMessage());
		}
	}
	public void executarScriptProces(
			String entorn,
			String processInstanceId,
			String script) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			expedientService.evaluateScript(
					processInstanceId,
					script,
					null);
		} catch (Exception ex) {
			logger.error("No s'ha pogut executar l'script", ex);
			throw new TramitacioException("No s'ha pogut executar l'script: " + ex.getMessage());
		}
	}
	public void aturarExpedient(
			String entorn,
			String processInstanceId,
			String motiu) throws TramitacioException{
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			expedientService.aturar(
					processInstanceId,
					motiu,
					null);
		} catch (Exception ex) {
			logger.error("No s'ha pogut aturar l'expedient", ex);
			throw new TramitacioException("No s'ha pogut aturar l'expedient: " + ex.getMessage());
		}
	}
	public void reprendreExpedient(
			String entorn,
			String processInstanceId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Expedient expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			expedientService.reprendre(
					processInstanceId,
					null);
		} catch (Exception ex) {
			logger.error("No s'ha pogut reprendre l'expedient", ex);
			throw new TramitacioException("No s'ha pogut reprendre l'expedient: " + ex.getMessage());
		}
	}
	public List<ExpedientInfo> consultaExpedients(
			String entorn,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			String expedientTipusCodi,
			String estatCodi,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		// Obtencio de dades per a fer la consulta
		ExpedientTipus expedientTipus = null;
		Long estatId = null;
		if (expedientTipusCodi != null && expedientTipusCodi.length() > 0) {
			expedientTipus = dissenyService.findExpedientTipusAmbEntornICodi(
				e.getId(),
				expedientTipusCodi);
			if (estatCodi != null && estatCodi.length() > 0) {
				for (Estat estat: expedientTipus.getEstats()) {
					if (estat.getCodi().equals(estatCodi)) {
						estatId = estat.getId();
						break;
					}
				}
			}
		} else {
			for (Estat estat: dissenyService.findEstatAmbEntorn(e.getId())) {
				if (estat.getCodi().equals(estatCodi)) {
					estatId = estat.getId();
					break;
				}
			}
		}
		// Consulta d'expedients
		List<ExpedientDto> expedients = expedientService.findAmbEntornConsultaGeneral(
				e.getId(),
				titol,
				numero,
				dataInici1,
				dataInici2,
				(expedientTipus != null? expedientTipus.getId() : null),
				estatId,
				iniciat,
				finalitzat,
				geoPosX,
				geoPosY,
				geoReferencia,
				FiltreAnulat.ACTIUS);
		// Construcció de la resposta
		List<ExpedientInfo> resposta = new ArrayList<ExpedientInfo>();
		for (ExpedientDto dto: expedients)
			resposta.add(toExpedientInfo(dto));
		return resposta;
	}

	public void deleteExpedient(
			String entorn,
			String processInstanceId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(
				processInstanceId);
		if (!validarPermisExpedientTipusDelete(expedient.getTipus()))
			throw new TramitacioException("No té permisos per a esborrar l'expedient del proces '" + processInstanceId + "'");
		expedientService.delete(
				e.getId(),
				expedient.getId());
	}



	@Autowired
	public void setEntornService(EntornService entornService) {
		this.entornService = entornService;
	}
	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	@Autowired
	public void setExpedientService(ExpedientService expedientService) {
		this.expedientService = expedientService;
	}
	@Autowired
	public void setTascaService(TascaService tascaService) {
		this.tascaService = tascaService;
	}
	@Autowired
	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}
	@Autowired
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}



	private Entorn findEntornAmbCodi(String codi) {
		Entorn entorn = entornService.findAmbCodi(codi);
		if (entorn != null)
			EntornActual.setEntornId(entorn.getId());
		return entorn;
	}

	private ExpedientTipus findExpedientTipusAmbEntornICodi(Long entornId, String codi) {
		return dissenyService.findExpedientTipusAmbEntornICodi(entornId, codi);
	}
	private TascaTramitacio convertirTascaTramitacio(TascaLlistatDto tasca) {
		TascaTramitacio tt = new TascaTramitacio();
		tt.setId(tasca.getId());
		tt.setCodi(tasca.getCodi());
		tt.setTitol(tasca.getTitol());
		tt.setExpedient(tasca.getExpedientNumero());
		tt.setMissatgeInfo(tasca.getMissatgeInfo());
		tt.setMissatgeWarn(tasca.getMissatgeWarn());
		tt.setResponsable(tasca.getResponsable());
		tt.setResponsables(tasca.getResponsables());
		tt.setDataCreacio(tasca.getDataCreacio());
		tt.setDataInici(tasca.getDataInici());
		tt.setDataFi(tasca.getDataFi());
		tt.setDataLimit(tasca.getDataLimit());
		tt.setPrioritat(tasca.getPrioritat());
		tt.setOpen(tasca.isOberta());
		tt.setCompleted(tasca.isCompletada());
		tt.setCancelled(tasca.isCancelada());
		tt.setSuspended(tasca.isSuspesa());
		tt.setTransicionsSortida(tasca.getResultats());
		tt.setProcessInstanceId(tasca.getProcessInstanceId());
		return tt;
	}
	private CampTasca convertirCampTasca(
			net.conselldemallorca.helium.core.model.hibernate.CampTasca campTasca,
			Object valor) {
		CampTasca ct = new CampTasca();
		ct.setCodi(campTasca.getCamp().getCodi());
		ct.setEtiqueta(campTasca.getCamp().getEtiqueta());
		ct.setTipus(campTasca.getCamp().getTipus().name());
		ct.setObservacions(campTasca.getCamp().getObservacions());
		ct.setDominiId(campTasca.getCamp().getDominiId());
		ct.setDominiParams(campTasca.getCamp().getDominiParams());
		ct.setDominiCampValor(campTasca.getCamp().getDominiCampValor());
		ct.setDominiCampText(campTasca.getCamp().getDominiCampText());
		ct.setJbpmAction(campTasca.getCamp().getJbpmAction());
		ct.setReadFrom(campTasca.isReadFrom());
		ct.setWriteTo(campTasca.isWriteTo());
		ct.setRequired(campTasca.isRequired());
		ct.setReadOnly(campTasca.isReadOnly());
		ct.setMultiple(campTasca.getCamp().isMultiple());
		ct.setOcult(campTasca.getCamp().isOcult());
		ct.setValor(valor);
		return ct;
	}
	private DocumentTasca convertirDocumentTasca(
			net.conselldemallorca.helium.core.model.hibernate.DocumentTasca documentTasca,
			DocumentDto document) {
		DocumentTasca dt = new DocumentTasca();
		dt.setId(document.getId());
		dt.setCodi(documentTasca.getDocument().getCodi());
		dt.setNom(documentTasca.getDocument().getNom());
		dt.setDescripcio(documentTasca.getDocument().getDescripcio());
		dt.setArxiu(document.getArxiuNom());
		dt.setData(document.getDataDocument());
		if (document.isSignat()) {
			dt.setUrlCustodia(document.getUrlVerificacioCustodia());
		}
		return dt;
	}
	private CampProces convertirCampProces(
			Camp camp,
			Object valor) {
		CampProces ct = new CampProces();
		if (camp != null) {
			ct.setCodi(camp.getCodi());
			ct.setEtiqueta(camp.getEtiqueta());
			ct.setTipus(camp.getTipus().name());
			ct.setObservacions(camp.getObservacions());
			ct.setDominiId(camp.getDominiId());
			ct.setDominiParams(camp.getDominiParams());
			ct.setDominiCampValor(camp.getDominiCampValor());
			ct.setDominiCampText(camp.getDominiCampText());
			ct.setJbpmAction(camp.getJbpmAction());
			ct.setMultiple(camp.isMultiple());
			ct.setOcult(camp.isOcult());
			ct.setValor(valor);
		}
		return ct;
	}
	private DocumentProces convertirDocumentProces(DocumentDto document) {
		DocumentProces dt = new DocumentProces();
		dt.setId(document.getId());
		dt.setCodi(document.getDocumentCodi());
		dt.setNom(document.getDocumentNom());
		dt.setArxiu(document.getArxiuNom());
		dt.setData(document.getDataDocument());
		return dt;
	}
	private ExpedientInfo toExpedientInfo(Expedient expedient) {
		if (expedient != null) {
			ExpedientInfo resposta = new ExpedientInfo();
			resposta.setTitol(expedient.getTitol());
			resposta.setNumero(expedient.getNumero());
			resposta.setNumeroDefault(expedient.getNumeroDefault());
			resposta.setIdentificador(expedient.getIdentificador());
			resposta.setDataInici(expedient.getDataInici());
			resposta.setDataFi(expedient.getDataFi());
			resposta.setComentari(expedient.getComentari());
			resposta.setInfoAturat(expedient.getInfoAturat());
			if (expedient.getIniciadorTipus().equals(net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus.INTERN))
				resposta.setIniciadorTipus(net.conselldemallorca.helium.ws.tramitacio.v1.ExpedientInfo.IniciadorTipus.INTERN);
			else if (expedient.getIniciadorTipus().equals(net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus.SISTRA))
				resposta.setIniciadorTipus(net.conselldemallorca.helium.ws.tramitacio.v1.ExpedientInfo.IniciadorTipus.SISTRA);
			resposta.setIniciadorCodi(expedient.getIniciadorCodi());
			resposta.setResponsableCodi(expedient.getResponsableCodi());
			resposta.setGeoPosX(expedient.getGeoPosX());
			resposta.setGeoPosY(expedient.getGeoPosY());
			resposta.setGeoReferencia(expedient.getGeoReferencia());
			resposta.setRegistreNumero(expedient.getRegistreNumero());
			resposta.setRegistreData(expedient.getRegistreData());
			resposta.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
			resposta.setIdioma(expedient.getIdioma());
			resposta.setAutenticat(expedient.isAutenticat());
			resposta.setTramitadorNif(expedient.getTramitadorNif());
			resposta.setTramitadorNom(expedient.getTramitadorNom());
			resposta.setInteressatNif(expedient.getInteressatNif());
			resposta.setInteressatNom(expedient.getInteressatNom());
			resposta.setRepresentantNif(expedient.getRepresentantNif());
			resposta.setRepresentantNom(expedient.getRepresentantNom());
			resposta.setAvisosHabilitats(expedient.isAvisosHabilitats());
			resposta.setAvisosEmail(expedient.getAvisosEmail());
			resposta.setAvisosMobil(expedient.getAvisosMobil());
			resposta.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
			resposta.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
			resposta.setTramitExpedientClau(expedient.getTramitExpedientClau());
			resposta.setExpedientTipusCodi(expedient.getTipus().getCodi());
			resposta.setExpedientTipusNom(expedient.getTipus().getNom());
			resposta.setEntornCodi(expedient.getEntorn().getCodi());
			if (expedient.getEstat() != null) {
				resposta.setEstatCodi(expedient.getEstat().getCodi());
				resposta.setEstatNom(expedient.getEstat().getNom());
			}
			resposta.setProcessInstanceId(new Long(expedient.getProcessInstanceId()).longValue());
			return resposta;
		}
		return null;
	}

	private boolean validarPermisEntornRead(Entorn entorn) {
		printUsuari();
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.READ}) != null;
	}
	private boolean validarPermisExpedientTipusCreate(ExpedientTipus expedientTipus) {
		printUsuari();
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.CREATE}) != null;
	}
	private boolean validarPermisExpedientTipusRead(ExpedientTipus expedientTipus) {
		printUsuari();
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.READ}) != null;
	}
	private boolean validarPermisExpedientTipusWrite(ExpedientTipus expedientTipus) {
		printUsuari();
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}
	private boolean validarPermisExpedientTipusDelete(ExpedientTipus expedientTipus) {
		printUsuari();
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DELETE}) != null;
	}
	private void printUsuari() {
		if (PRINT_USUARI) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			logger.info(">>> " + auth.getName());
		}
	}

	public List<TascaTramitacio> consultaTasquesPersonalsByCodi(
			String entorn, 
			String codi) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			List<TascaLlistatDto> tasques = tascaService.findTasquesPersonalsTramitacio(e.getId(), null, codi, true);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaLlistatDto tasca: tasques)
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	public List<TascaTramitacio> consultaTasquesGrupByCodi(
			String entorn, 
			String codi) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			List<TascaLlistatDto> tasques = tascaService.findTasquesGrupTramitacio(e.getId(), null, codi, true);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaLlistatDto tasca: tasques)
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	private static final Log logger = LogFactory.getLog(TramitacioServiceImpl.class);

}
