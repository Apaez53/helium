/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogEstat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.LogInfo;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.ExpedientLogHelper;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentDescarregarException;
import net.conselldemallorca.helium.v3.core.api.exception.DominiNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EnumeracioNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.v3.core.helper.DominiHelper;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.LuceneHelper;
import net.conselldemallorca.helium.v3.core.helper.MesuresTemporalsHelper;
import net.conselldemallorca.helium.v3.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.v3.core.helper.PersonaHelper;
import net.conselldemallorca.helium.v3.core.helper.TascaHelper;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per a gestionar expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("expedientServiceV3")
public class ExpedientServiceImpl implements ExpedientService {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;

	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private PersonaHelper personaHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private LuceneHelper luceneHelper;
	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;

	@Resource
	private LuceneDao luceneDao;
	@Resource
	private ExpedientLogHelper expedientLogHelper;



	@Transactional
	public void modificar(
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			String estatCodi,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean execucioDinsHandler) throws ExpedientNotFoundException, EstatNotFoundException {
		logger.debug(
				"Modificar informació de l'expedient (id=" + id +
				", numero=" + numero +
				", titol=" + titol +
				", responsableCodi=" + responsableCodi +
				", dataInici=" + dataInici +
				", comentari=" + comentari +
				", estatCodi=" + estatCodi +
				", geoPosX=" + geoPosX +
				", geoPosY=" + geoPosY +
				", geoReferencia=" + geoReferencia +
				", grupCodi=" + grupCodi + ")");
		Expedient expedient = expedientRepository.findOne(id);
		if (expedient == null)
			throw new ExpedientNotFoundException();
		if (!execucioDinsHandler) {
			ExpedientLog elog = expedientLogHelper.afegirLogExpedientPerExpedient(
				id,
				ExpedientLogAccioTipus.EXPEDIENT_MODIFICAR,
				null);
			elog.setEstat(ExpedientLogEstat.IGNORAR);
		}
		// Numero
		if (expedient.getTipus().getTeNumero()) {
			if (!StringUtils.equals(expedient.getNumero(), numero)) {
				expedientLogHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.NUMERO + "#@#" + expedient.getNumero());
				expedient.setNumero(numero);
			}
		}
		// Titol
		if (expedient.getTipus().getTeTitol()) {
			if (!StringUtils.equals(expedient.getTitol(), titol)) {
				expedientLogHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.TITOL + "#@#" + expedient.getTitol());
				expedient.setTitol(titol);
			}
		}
		// Responsable
		if (!StringUtils.equals(expedient.getResponsableCodi(), responsableCodi)) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.RESPONSABLE + "#@#" + expedient.getResponsableCodi());
			expedient.setResponsableCodi(responsableCodi);
		}
		// Data d'inici
		if (expedient.getDataInici() != dataInici) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String inici = sdf.format(dataInici);
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.INICI + "#@#" + inici);
			expedient.setDataInici(dataInici);
		}
		// Comentari
		if (!StringUtils.equals(expedient.getComentari(), comentari)) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.COMENTARI + "#@#" + expedient.getComentari());
			expedient.setComentari(comentari);
		}
		// Estat
		if (estatCodi != null) {
			if (expedient.getEstat() == null || expedient.getEstat().getCodi() != estatCodi) {
				expedientLogHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.ESTAT + "#@#" + "---");
				Estat estat = estatRepository.findByExpedientTipusAndCodi(
						expedient.getTipus(),
						estatCodi);
				if (estat == null)
					throw new EstatNotFoundException();
				expedient.setEstat(estat);
			}
		} else if (expedient.getEstat() != null) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.ESTAT + "#@#" + expedient.getEstat().getId());
			expedient.setEstat(null);
		}
		// Geoposició
		if (expedient.getGeoPosX() != geoPosX) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GEOPOSICIOX + "#@#" + expedient.getGeoPosX());
			expedient.setGeoPosX(geoPosX);
		}
		if (expedient.getGeoPosY() != geoPosY) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GEOPOSICIOY + "#@#" + expedient.getGeoPosY());
			expedient.setGeoPosY(geoPosY);
		}
		// Georeferencia
		if (!StringUtils.equals(expedient.getGeoReferencia(), geoReferencia)) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GEOREFERENCIA + "#@#" + expedient.getGeoReferencia());
			expedient.setGeoReferencia(geoReferencia);
		}
		// Grup
		if (!StringUtils.equals(expedient.getGrupCodi(), grupCodi)) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GRUP + "#@#" + expedient.getGrupCodi());
			expedient.setGrupCodi(grupCodi);
		}
		luceneDao.updateExpedientCapsalera(
				expedient,
				expedientHelper.isFinalitzat(expedient));
	}

	@Transactional
	public void aturar(
			Long expedientId,
			String motiu) throws ExpedientNotFoundException {
		logger.debug("Aturada l'expedient (id=" + expedientId + ", motiu=" + motiu + ")");
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new ExpedientNotFoundException();
		logger.debug("Afegint log EXPEDIENT_ATURAR a l'expedient (id=" + expedientId + ")");
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_ATURAR,
				motiu);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Suspenent les instàncies de procés associades a l'expedient (id=" + expedientId + ")");
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.suspendProcessInstances(ids);
		expedient.setInfoAturat(motiu);
	}

	@Transactional
	public void reprendre(Long expedientId) throws ExpedientNotFoundException {
		logger.debug("Represa l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new ExpedientNotFoundException();
		logger.debug("Afegint log EXPEDIENT_REPRENDRE a l'expedient (id=" + expedientId + ")");
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		logger.debug("Reprenent les instàncies de procés associades a l'expedient (id=" + expedientId + ")");
		List<JbpmProcessInstance> processInstancesTree = jbpmHelper.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmHelper.resumeProcessInstances(ids);
	}

	@Transactional(readOnly = true)
	public ExpedientDto findById(Long expedientId) throws ExpedientNotFoundException {
		logger.debug("Consultant l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		return conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
	}

	@Transactional(readOnly = true)
	public ExpedientDto findAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) throws EntornNotFoundException, ExpedientTipusNotFoundException {
		logger.debug("Consulta d'expedient (entornId=" + entornId + ", expedientTipusCodi=" + expedientTipusCodi + ", numero=" + numero + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new ExpedientTipusNotFoundException();
		Expedient expedient = expedientRepository.findByTipusAndNumero(
				expedientTipus,
				numero);
		return conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
	}

	@Transactional(readOnly = true)
	public ExpedientDto findAmbProcessInstanceId(String processInstanceId) {
		logger.debug("Consulta de l'expedient (processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientRepository.findByProcessInstanceId(processInstanceId);
		return conversioTipusHelper.convertir(
				expedient,
				ExpedientDto.class);
	}

	@Transactional(readOnly = true)
	public PaginaDto<ExpedientDto> findPerConsultaGeneralPaginat(
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
			PaginacioParamsDto paginacioParams) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException {
		mesuresTemporalsHelper.mesuraIniciar("EXPCONGEN_TOTAL");
		mesuresTemporalsHelper.mesuraIniciar("EXPCONGEN_0");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta general d'expedients paginada (entornId=" + entornId + "expedientTipusId=" + expedientTipusId + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null) {
			logger.debug("No s'ha trobat l'entorn (entornId=" + entornId + ")");
			throw new EntornNotFoundException();
		} else {
			boolean ambPermis = permisosHelper.isGrantedAny(
					entorn.getId(),
					Entorn.class,
					new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION},
					auth);
			if (!ambPermis) {
				logger.debug("No es tenen permisos per accedir a l'entorn (entornId=" + entornId + ")");
				throw new EntornNotFoundException();
			}
		}
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
			if (expedientTipus == null) {
				logger.debug("No s'ha trobat l'expedientTipus (expedientTipusId=" + expedientTipusId + ")");
				throw new ExpedientTipusNotFoundException();
			} else {
				boolean ambPermis = permisosHelper.isGrantedAny(
						expedientTipus.getId(),
						ExpedientTipus.class,
						new Permission[] {
							ExtendedPermission.READ,
							ExtendedPermission.ADMINISTRATION},
						auth);
				if (!ambPermis) {
					logger.debug("No es tenen permisos per accedir a l'expedientTipus (expedientTipusId=" + expedientTipusId + ")");
					throw new ExpedientTipusNotFoundException();
				}
			}
		}
		// Comprova l'accés a l'estat
		Estat estat = null;
		if (estatId != null) {
			estat = estatRepository.findByExpedientTipusAndId(expedientTipus, estatId);
			if (estat == null) {
				logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
				throw new EstatNotFoundException();
			}
		}
		// Calcula la data fi pel filtre
		if (dataInici2 != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici2);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			dataInici2.setTime(cal.getTime().getTime());
		}
		// Obté la llista de tipus d'expedient permesos
		List<ExpedientTipus> tipusPermesos = expedientTipusRepository.findByEntorn(entorn);
		permisosHelper.filterGrantedAny(
				tipusPermesos,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION},
				auth);
		mesuresTemporalsHelper.mesuraCalcular("EXPCONGEN_0");
		mesuresTemporalsHelper.mesuraIniciar("EXPCONGEN_1");
		// Obté la llista d'ids d'expedient de l'entorn actual que
		// tenen alguna tasca activa per a l'usuari actual.
		Set<Long> expedientIdsAmbTasquesActives = null;
		if (nomesAmbTasquesActives) {
			Set<String> processInstancesAmbTasquesActives = null;
			processInstancesAmbTasquesActives = new HashSet<String>();
			List<String> ids = jbpmHelper.findProcessInstanceIdsWithActiveTasksForActorId(auth.getName());
			for (String id: ids)
					processInstancesAmbTasquesActives.add(id);
		}
		
		mesuresTemporalsHelper.mesuraCalcular("EXPCONGEN_1");
		mesuresTemporalsHelper.mesuraIniciar("EXPCONGEN_2");
		// Fa la consulta
		Page<Expedient> paginaResultats = expedientRepository.findByFiltreGeneralPaginat(
				entornId,
				tipusPermesos,
				(expedientTipusId == null),
				expedientTipusId,
				(titol == null),
				titol,
				(numero == null),
				numero,
				(dataInici1 == null),
				dataInici1,
				(dataInici2 == null),
				dataInici2,
				EstatTipusDto.INICIAT.equals(estatTipus),
				EstatTipusDto.FINALITZAT.equals(estatTipus),
				(!EstatTipusDto.CUSTOM.equals(estatTipus) || estatId == null),
				estatId,
				(geoPosX == null),
				geoPosX,
				(geoPosY == null),
				geoPosY,
				(geoReferencia == null),
				geoReferencia,
				nomesAmbTasquesActives,
				expedientIdsAmbTasquesActives,
				mostrarAnulats,
				paginacioHelper.toSpringDataPageable(paginacioParams));
		mesuresTemporalsHelper.mesuraCalcular("EXPCONGEN_2");
		mesuresTemporalsHelper.mesuraIniciar("EXPCONGEN_3");
		PaginaDto<ExpedientDto> resposta = paginacioHelper.toPaginaDto(
				paginaResultats,
				ExpedientDto.class);
		mesuresTemporalsHelper.mesuraCalcular("EXPCONGEN_3");
		mesuresTemporalsHelper.mesuraCalcular("EXPCONGEN_TOTAL");
		return resposta;
	}

	@Transactional
	public void createRelacioExpedient(
			Long expedientOrigenId,
			Long expedientDestiId) {
		logger.debug("Nova relació d'expedients (expedientOrigenId=" + expedientOrigenId + "expedientDestiId=" + expedientDestiId + ")");
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerExpedient(
				expedientOrigenId,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				expedientDestiId.toString());
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		Expedient origen = expedientRepository.findOne(expedientOrigenId);
		Expedient desti = expedientRepository.findOne(expedientDestiId);
		boolean existeix = false;
		for (Expedient relacionat: origen.getRelacionsOrigen()) {
			if (relacionat.getId().longValue() == expedientDestiId.longValue()) {
				existeix = true;
				break;
			}
		}
		if (!existeix)
			origen.addRelacioOrigen(desti);
		existeix = false;
		for (Expedient relacionat: desti.getRelacionsOrigen()) {
			if (relacionat.getId().longValue() == expedientOrigenId.longValue()) {
				existeix = true;
				break;
			}
		}
		if (!existeix)
			desti.addRelacioOrigen(origen);
	}

	@Transactional
	public void luceneIndexUpdate(Long expedientId) throws ExpedientNotFoundException {
		logger.debug("Reindexació de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new ExpedientNotFoundException();
		luceneHelper.expedientIndexLuceneUpdate(expedient);
	}

	@Transactional
	public void processInstanceTokenRedirect(
			long tokenId,
			String nodeName,
			boolean cancelarTasques) {
		logger.debug("Redirigir token (tokenId=" + tokenId + ", nodeName=" + nodeName + ", cancelarTasques=" + cancelarTasques + ")");
		jbpmHelper.tokenRedirect(
				tokenId,
				nodeName,
				cancelarTasques,
				true,
				false);
	}

	@Transactional
	public void alertaCrear(
			Long entornId,
			Long expedientId,
			Date data,
			String usuariCodi,
			String text) throws EntornNotFoundException, ExpedientNotFoundException {
		logger.debug("Crear alerta per expedient (entornId=" + entornId + ", expedientId=" + expedientId + ", usuariCodi=" + usuariCodi + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new ExpedientNotFoundException();
		Alerta alerta = new Alerta(
				data,
				usuariCodi,
				text,
				entorn);
		alerta.setExpedient(expedient);
		alertaRepository.save(alerta);
	}
	@Transactional
	public void alertaEsborrarAmbTaskInstanceId(
			long taskInstanceId) throws TaskInstanceNotFoundException {
		Date ara = new Date();
		List<TerminiIniciat> terminis = terminiIniciatRepository.findByTaskInstanceId(
				new Long(taskInstanceId).toString());
		for (TerminiIniciat termini: terminis) {
			for (Alerta alerta: termini.getAlertes())
				alerta.setDataEliminacio(ara);
		}
		
	}

	@Transactional(readOnly = true)
	public List<ExpedientDadaDto> findDadesPerExpedient(Long expedientId) {
		logger.debug("Consulta de dades de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		return variableHelper.findDadesPerInstanciaProces(
				expedient.getProcessInstanceId());
	}

	@Transactional(readOnly = true)
	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String variableCodi) {
		logger.debug("Obtenir dada per la variable (processInstanceId=" + processInstanceId + ", variableCodi=" + variableCodi + ")");
		ExpedientDadaDto dto = variableHelper.getDadaPerInstanciaProces(
				processInstanceId,
				variableCodi);
		return dto;
	}

	@Transactional(readOnly = true)
	public List<ExpedientDocumentDto> findDocumentsPerExpedient(Long expedientId) {
		logger.debug("Consulta de documents de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		return documentHelper.findDocumentsPerInstanciaProces(
				expedient.getProcessInstanceId());
	}

	@Transactional(readOnly = true)
	public ArxiuDto getArxiuExpedient(
			Long expedientId,
			Long documentStoreId) {
		logger.debug("Obtenint contingut de l'arxiu per l'expedient (expedientId=" + expedientId + ", documentStoreId=" + documentStoreId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		DocumentStore documentStore = documentStoreRepository.findOne(documentStoreId);
		if (documentStore == null)
			throw new DocumentDescarregarException("No s'ha trobat el documentStore (id=" + documentStoreId + ", expedientId=" + expedientId + ")");
		List<JbpmProcessInstance> processInstances = jbpmHelper.getProcessInstanceTree(expedient.getProcessInstanceId());
		boolean trobat = false;
		for (JbpmProcessInstance processInstance: processInstances) {
			String processInstanceId = new Long(processInstance.getId()).toString();
			if (processInstanceId.equals(documentStore.getProcessInstanceId())) {
				trobat = true;
				break;
			}
		}
		if (!trobat)
			throw new DocumentDescarregarException("El documentStore no pertany a l'expedient (id=" + documentStoreId + ", expedientId=" + expedientId + ", documentStore.processInstanceId=" + documentStore.getProcessInstanceId() + ")");
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
				false,
				false);
	}

	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public List<ExpedientTascaDto> findTasquesPerExpedient(Long expedientId) {
		logger.debug("Consulta de tasques de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		return tascaHelper.findTasquesPerExpedient(
				expedient);
	}
	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public ExpedientTascaDto getTascaPerExpedient(
			Long expedientId,
			String tascaId) {
		logger.debug("Obtenció de la tasca de l'expedient (expedientId=" + expedientId + ", tascaId=" + tascaId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		ExpedientTascaDto resposta = tascaHelper.getTascaPerExpedient(
				expedient,
				tascaId,
				true,
				true);
		return resposta;
	}

	@Transactional(readOnly = true)
	public List<PersonaDto> findParticipantsPerExpedient(Long expedientId) {
		logger.debug("Consulta de tasques de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		List<ExpedientTascaDto> tasques = tascaHelper.findTasquesPerExpedient(
				expedient);
		Set<String> codisPersona = new HashSet<String>();
		List<PersonaDto> resposta = new ArrayList<PersonaDto>();
		for (ExpedientTascaDto tasca: tasques) {
			if (tasca.getResponsableCodi() != null && !codisPersona.contains(tasca.getResponsableCodi())) {
				resposta.add(tasca.getResponsable());
				codisPersona.add(tasca.getResponsableCodi());
			}
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	public List<CampAgrupacioDto> findAgrupacionsDadesExpedient(Long expedientId) {
		logger.debug("Consulta de les agrupacions de dades de l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.geExpedientComprovantPermisosAny(
				expedientId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(expedient.getProcessInstanceId());
		return conversioTipusHelper.convertirList(
				definicioProces.getAgrupacions(),
				CampAgrupacioDto.class);
	}

	@Transactional(readOnly = true)
	public List<DominiRespostaFilaDto> dominiConsultar(
			String processInstanceId,
			String dominiCodi,
			String dominiId,
			Map<String, Object> parametres) throws DominiNotFoundException, SistemaExternException {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Domini domini = dominiRepository.findByExpedientTipusAndCodi(
				expedient.getTipus(),
				dominiCodi);
		if (domini == null)
			domini = dominiRepository.findByEntornAndCodi(
					expedient.getEntorn(),
					dominiCodi);
		try {
			List<FilaResultat> resposta = dominiHelper.consultar(
					domini,
					dominiId,
					parametres);
			return conversioTipusHelper.convertirList(
					resposta,
					DominiRespostaFilaDto.class);
		} catch (Exception ex) {
			throw new SistemaExternException(ex);
		}
	}
	@Transactional(readOnly = true)
	public List<EnumeracioValorDto> enumeracioConsultar(
			String processInstanceId,
			String enumeracioCodi) throws EnumeracioNotFoundException {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Enumeracio enumeracio = enumeracioRepository.findByExpedientTipusAndCodi(
				expedient.getTipus(),
				enumeracioCodi);
		if (enumeracio == null)
			enumeracio = enumeracioRepository.findByEntornAndCodi(
					expedient.getEntorn(),
					enumeracioCodi);
		return conversioTipusHelper.convertirList(
				enumeracio.getEnumeracioValors(),
				EnumeracioValorDto.class);
	}

	@Transactional
	public ExpedientDto getExpedientIniciant() {
		return conversioTipusHelper.convertir(
				ExpedientIniciantDto.getExpedient(),
				ExpedientDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

}