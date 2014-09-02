/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;


/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientService {

	/**
	 * Crea/inicia un nou expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param usuari
	 *            L'usuari de l'expedient.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            La definició de procés de l'expedient.
	 * @param any
	 *            L'any de l'expedient.
	 * @param numero
	 *            El número de l'expedient.
	 * @param titol
	 *            El títol de l'expedient.
	 * @param registreNumero
	 * @param registreData
	 * @param unitatAdministrativa
	 * @param idioma
	 * @param autenticat
	 * @param tramitadorNif
	 * @param tramitadorNom
	 * @param interessatNif
	 * @param interessatNom
	 * @param representantNif
	 * @param representantNom
	 * @param avisosHabilitats
	 * @param avisosEmail
	 * @param avisosMobil
	 * @param notificacioTelematicaHabilitada
	 * @param variables
	 * @param transitionName
	 * @param iniciadorTipus
	 * @param iniciadorCodi
	 * @param responsableCodi
	 * @param documents
	 * @param adjunts
	 * @return El nou expedient creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientDto create(
			Long entornId,
			String usuari,
			Long expedientTipusId,
			Long definicioProcesId,
			Integer any,
			String numero,
			String titol,
			String registreNumero,
			Date registreData,
			Long unitatAdministrativa,
			String idioma,
			boolean autenticat,
			String tramitadorNif,
			String tramitadorNom,
			String interessatNif,
			String interessatNom,
			String representantNif,
			String representantNom,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil,
			boolean notificacioTelematicaHabilitada,
			Map<String, Object> variables,
			String transitionName,
			IniciadorTipusDto iniciadorTipus,
			String iniciadorCodi,
			String responsableCodi,
			Map<String, DadesDocumentDto> documents,
			List<DadesDocumentDto> adjunts);

	/**
	 * Modifica la informació d'un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol modificar.
	 * @param numero
	 *            Número d'expedient.
	 * @param titol
	 *            Títol de l'expedient.
	 * @param responsableCodi
	 *            Codi de l'usuari responsable de l'expedient.
	 * @param dataInici
	 *            Data d'inici de l'expedient.
	 * @param comentari
	 *            Comentari de l'expedient.
	 * @param estatId
	 *            Estat de l'expedient.
	 * @param geoPosX
	 *            Posició X de la georeferència de l'expedient.
	 * @param geoPosY
	 *            Posició Y de la georeferència de l'expedient.
	 * @param geoReferencia
	 *            Codi de la georeferència de l'expedient.
	 * @param grupCodi
	 *            Codi del grup al qual pertany l'expedient.
	 * @param execucioDinsHandler
	 *            Indica si la invocació prové d'un handler.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void update(
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean execucioDinsHandler);

	/**
	 * Esborra un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void delete(Long id);

	/**
	 * Retorna un expedient donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return L'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientDto findAmbId(Long id);

	/**
	 * Consulta d'expedients per entorn paginada.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param titol
	 *            Fragment del títol de l'expedient.
	 * @param numero
	 *            Fragment del número de l'expedient.
	 * @param dataInici1
	 *            Data d'inici inicial.
	 * @param dataInici2
	 *            Data d'inici final.
	 * @param dataFi1
	 *            Data de fi inicial.
	 * @param dataFi2
	 *            Data de fi final.
	 * @param estatTipus
	 *            Tipus d'estat a consultar (inicial, final o altres).
	 * @param estatId
	 *            Atribut id de l'estat de l'expedient.
	 * @param geoPosX
	 *            Posició X de la georeferència.
	 * @param geoPosY
	 *            Posició Y de la georeferència.
	 * @param geoReferencia
	 *            Codi de la georeferència.
	 * @param nomesAmbTasquesActives
	 *            Check de mostrar només tasques actives.
	 * @param nomesAlertes
	 *            Check de mostrar només alertes.
	 * @param mostrarAnulats
	 *            Check de mostrar només anulats.
	 * @param paginacioParams
	 *            Paràmetres de paginació.
	 * @return La pàgina del llistat d'expedients.
	 * @throws NotFoundException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws NotAllowedException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 */
	public PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesAmbTasquesActives,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta només ids d'expedient per entorn.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param titol
	 *            Fragment del títol de l'expedient.
	 * @param numero
	 *            Fragment del número de l'expedient.
	 * @param dataInici1
	 *            Data d'inici inicial.
	 * @param dataInici2
	 *            Data d'inici final.
	 * @param dataFi1
	 *            Data de fi inicial.
	 * @param dataFi2
	 *            Data de fi final.
	 * @param estatTipus
	 *            Tipus d'estat a consultar (inicial, final o altres).
	 * @param estatId
	 *            Atribut id de l'estat de l'expedient.
	 * @param geoPosX
	 *            Posició X de la georeferència.
	 * @param geoPosY
	 *            Posició Y de la georeferència.
	 * @param geoReferencia
	 *            Codi de la georeferència.
	 * @param nomesAmbTasquesActives
	 *            Check de mostrar només tasques actives.
	 * @param nomesAlertes
	 *            Check de mostrar només alertes.
	 * @param mostrarAnulats
	 *            Check de mostrar només anulats.
	 * @return La pàgina del llistat d'expedients.
	 * @throws NotFoundException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws NotAllowedException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 */
	public List<Long> findIdsAmbFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesAmbTasquesActives,
			boolean nomesAlertes,
			boolean mostrarAnulats);

	/**
	 * Retorna l'arxiu amb la imatge de la definició de procés.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La imatge.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ArxiuDto getImatgeDefinicioProces(
			Long id,
			String processInstanceId);

	/**
	 * Retorna la llista de persones que han fet alguna tasca de
	 * l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista de persones.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<PersonaDto> findParticipants(Long id);

	/**
	 * Retorna la llista d'accions visibles de l'expedient especificat.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista d'accions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<AccioDto> findAccionsVisibles(Long id);

	/**
	 * Retorna la llista de tasques de l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista de tasques.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientTascaDto> findTasques(Long id);

	/**
	 * Retorna la llista de tasques pendents de l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista de tasques pendents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientTascaDto> findTasquesPendents(Long id);

	/**
	 * Retorna la llista de dades d'una instància de procés de
	 * l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La llista de dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDadaDto> findDadesPerInstanciaProces(
			Long id,
			String processInstanceId);

	/**
	 * Retorna la llista d'agrupacions de dades d'una instància
	 * de procés de l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La llista de dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<CampAgrupacioDto> findAgrupacionsDadesPerInstanciaProces(
			Long id,
			String processInstanceId);

	/**
	 * Retorna la llista de documents d'una instància de procés de
	 * l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La llista de documents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDocumentDto> findDocumentsPerInstanciaProces(
			Long id,
			String processInstanceId);

	/**
	 * Retorna l'arxiu del document.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param documentStoreId
	 *            Atribut id del document que es vol descarregar.
	 * @return L'arxiu del document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ArxiuDto getArxiuPerDocument(
			Long id,
			Long documentStoreId);

	/**
	 * Atura la tramitació d'un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol aturar.
	 * @param motiu
	 *            El motiu per aturar la tramitació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void aturar(
			Long id,
			String motiu);

	/**
	 * Reprén la tramitació d'un expedient que prèviament ha estat aturat.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol reprendre.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void reprendre(Long id);

	/**
	 * Anula un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol anular.
	 * @param motiu
	 *            El motiu per anular l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void cancel(
			Long id,
			String motiu);

	/**
	 * Crea una relació entre dos expedients.
	 * 
	 * @param expedientOrigenId
	 *            Atribut id de l'expedient origen de la relació.
	 * @param expedientDestiId
	 *            Atribut id de l'expedient destí de la relació.
	 * @throws NotFoundException
	 *             Si no es troba algun dels expedients amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void createRelacioExpedient(
			Long origenId,
			Long destiId);

	/**
	 * Esborra una relació entre dos expedients.
	 * 
	 * @param origenId
	 *            Atribut id de l'expedient origen de la relació.
	 * @param destiId
	 *            Atribut id de l'expedient destí de la relació.
	 * @throws NotFoundException
	 *             Si no es troba algun dels expedients amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void deleteRelacioExpedient(
			Long origenId,
			Long destiId);

	/**
	 * Retorna la llista d'expedients relacionats amb l'expedient
	 * especificat.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista d'expedients.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDto> findRelacionats(Long id);

	/**
	 * Canvia la versió de la definició de procés.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol actualitzar.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés que es vol actualitzar.
	 * @param versio
	 *            Número de versió de la nova definició de procés.
	 * @return 
	 * @throws NotFoundException
	 *             Si no es troba l'expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public String canviVersioDefinicioProces(
			Long id,
			int versio);

	/**
	 * Consulta els terminis d'un procés de l'expedient
	 * 
	 * @param id
	 * @param processInstanceId
	 * @return Una llista amb els terminis del procés
	 * @throws NotFoundException
	 *             Si no es troba l'expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientTerminiDto> findTerminis(
			Long id,
			String processInstanceId);

	public List<ExpedientDto> findSuggestAmbEntornLikeIdentificador(Long entornid, String text);

	public List<InstanciaProcesDto> getArbreInstanciesProces(
			Long processInstanceId);
	
	/*public ExpedientTascaDto getTascaPerExpedient(
			Long expedientId,
			String tascaId);

	public List<ExpedientDadaDto> findDadesPerInstanciaProces(
			String instanciaProcesId);

	

	public ExpedientDto findAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) throws EntornNotFoundException, ExpedientTipusNotFoundException;

	public ExpedientDto findAmbProcessInstanceId(
			String processInstanceId);

	public void createRelacioExpedient(
			Long expedientOrigenId,
			Long expedientDestiId);
	
	public void processInstanceTokenRedirect(
			long tokenId,
			String nodeName,
			boolean cancelarTasques);

	public void alertaCrear(
			Long entornId,
			Long expedientId,
			Date data,
			String usuariCodi,
			String text) throws EntornNotFoundException, ExpedientNotFoundException;

	public void alertaEsborrarAmbTaskInstanceId(
			long taskInstanceId) throws TaskInstanceNotFoundException;

	public List<ExpedientDadaDto> findDadesPerExpedient(Long expedientId);

	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String variableCodi);

	public List<ExpedientDocumentDto> findDocumentsPerExpedient(Long expedientId);

	public ArxiuDto getArxiuExpedient(
			Long expedientId,
			Long documentStoreId);
	
	public List<CampAgrupacioDto> findAgrupacionsDadesExpedient(Long expedientId);

	

	public List<EnumeracioValorDto> enumeracioConsultar(
			String processInstanceId,
			String enumeracioCodi) throws EnumeracioNotFoundException;

	public ExpedientDto getExpedientIniciant();

	public void editar(
			Long entornId,
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi);
	
	public void editar(
			Long entornId,
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean executatEnHandler);

	
*/
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId);

	public List<RegistreDto> getRegistrePerExpedient(Long expedientId);

	public List<ExpedientLogDto> getLogsPerTascaOrdenatsPerData(ExpedientDto expedient);

	public List<ExpedientLogDto> getLogsOrdenatsPerData(ExpedientDto expedient);
	
	public Map<String, ExpedientTascaDto> getTasquesPerLogExpedient(Long expedientId);
	
	public void retrocedirFinsLog(Long logId, boolean retrocedirPerTasques);
	
	public List<ExpedientLogDto> findLogsTascaOrdenatsPerData(Long targetId);	

	public List<ExpedientLogDto> findLogsRetroceditsOrdenatsPerData(Long logId);
	
	/*
	public void deleteConsulta(Long expedientId);

	


	public void deleteRelacioExpedient(Long expedientIdOrigen, Long expedientIdDesti);
	
	public ExpedientTascaDto getStartTask(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors);

	public ExpedientDto iniciar(Long entornId, String usuari, Long expedientTipusId, Long definicioProcesId, Integer any, String numero, String titol, String registreNumero, Date registreData, Long unitatAdministrativa, String idioma, boolean autenticat, String tramitadorNif, String tramitadorNom, String interessatNif, String interessatNom, String representantNif, String representantNom, boolean avisosHabilitats, String avisosEmail, String avisosMobil, boolean notificacioTelematicaHabilitada, Map<String, Object> variables, String transitionName, IniciadorTipusDto iniciadorTipus, String iniciadorCodi, String responsableCodi, Map<String, DadesDocumentDto> documents, List<DadesDocumentDto> adjunts);

	public String getNumeroExpedientActual(Long id, ExpedientTipusDto expedientTipus, Integer any);

	
	public void anular(Long id, Long expedientId, String motiu);
*/
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol);

	public void cancelarTasca(Long expedientId, Long taskId);

	public void suspendreTasca(Long expedientId, Long taskId);

	public void reprendreTasca(Long expedientId, Long taskId);
	
	public List<Object> findLogIdTasquesById(List<ExpedientTascaDto> tasques);

	public void reassignarTasca(String taskId, String expression);

	public List<TascaDadaDto> findConsultaFiltre(Long consultaId);

	public List<TascaDadaDto> findConsultaInforme(Long consultaId);
	
	public List<ExpedientConsultaDissenyDto> findConsultaDissenyPaginat(
			Long consultaId,
			Map<String, Object> valors,
			PaginacioParamsDto paginacioParams, Boolean nomesPendents, Boolean nomesAlertes, Boolean mostrarAnulats);
	/*
	public List<ExpedientDadaDto> findDadesPerProcessInstance(String processInstanceId);

	public List<ExpedientDto> getExpedientsRelacionats(Long expedientId);

	public List<Long> findIdsPerConsultaGeneral(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesAmbTasquesActives,
			boolean nomesAlertes,
			boolean mostrarAnulats) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException;

	

	public void changeProcessInstanceVersion(String id, int versio);
*/
	public List<Long> findIdsPerConsultaInformePaginat(Long consultaId, Map<String, Object> valors, Boolean nomesPendents, Boolean nomesAlertes, Boolean mostrarAnulats);

	public void evaluateScript(Long expedientId, String script);

	public enum FiltreAnulat {
		ACTIUS		("expedient.consulta.anulats.actius"),
		ANUL_LATS	("expedient.consulta.anulats.anulats"),
		TOTS		("expedient.consulta.anulats.tots");
		private final String codi;
		private final String id;
		FiltreAnulat(String codi) {
			this.codi = codi;
			this.id = this.name();
		}
		public String getCodi(){
			return this.codi;
		}
		public String getId() {
			return id;
		}
	}

	public PaginaDto<ExpedientConsultaDissenyDto> findConsultaInformePaginat(Long consultaId, Map<String, Object> valorsPerService, Boolean nomesPendents, Boolean nomesAlertes, Boolean mostrarAnulats, PaginacioParamsDto paginacioParams) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException;

	public String getNumeroExpedientActual(Long entornId, Long expedientTipusId, Integer any);

	public ExpedientTascaDto getStartTask(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors);
}
