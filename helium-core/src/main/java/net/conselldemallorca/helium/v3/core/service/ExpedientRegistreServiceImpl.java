/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.api.WorkflowEngineApi;
import net.conselldemallorca.helium.core.api.WorkflowRetroaccioApi;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.TascaHelper;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InformacioRetroaccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientRegistreService;
import net.conselldemallorca.helium.v3.core.repository.ExpedientLoggerRepository;


/**
 * Implementació dels mètodes del servei ExpedientService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientRegistreServiceImpl implements ExpedientRegistreService {

	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;
	
	@Resource
	private ExpedientLoggerRepository expedientLogRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public SortedSet<Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>> findInformacioRetroaccioExpedientOrdenatPerData(
			Long expedientId,
			boolean detall) throws NoTrobatException, PermisDenegatException {
		
		SortedSet<Map.Entry<InstanciaProcesDto, List<InformacioRetroaccioDto>>> sortedEntries = null;
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});
		
		mesuresTemporalsHelper.mesuraIniciar("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "findAmbExpedientIdOrdenatsPerData");
		
		sortedEntries = workflowRetroaccioApi.findInformacioRetroaccioExpedientOrdenatPerData(
				expedientId, 
				expedient.getProcessInstanceId(), 
				detall);
		
		mesuresTemporalsHelper.mesuraCalcular("Expedient REGISTRE", "expedient", expedient.getTipus().getNom(), null, "obtenir tokens tasca");
		return sortedEntries;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<String, ExpedientTascaDto> findTasquesExpedientPerRetroaccio(
			Long expedientId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consultant tasques per la pipella de registre de l'expedient (expedientId=" + expedientId + ")");
		@SuppressWarnings("unused")
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});
		return workflowRetroaccioApi.findTasquesExpedientPerRetroaccio(expedientId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void executaRetroaccio(
			Long expedientId,
			Long informacioRetroaccioId,
			boolean retrocedirPerTasques) throws NoTrobatException, PermisDenegatException {
		logger.debug("Retrocedint expedient amb els logs (" +
				"expedientId=" + expedientId + ", " +
				"informacioRetroaccioId=" + informacioRetroaccioId + ", " +
				"retrocedirPerTasques=" + retrocedirPerTasques + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_MANAGE,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		mesuresTemporalsHelper.mesuraIniciar("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedient.getTipus().getNom());
		workflowRetroaccioApi.executaRetroaccio(
				informacioRetroaccioId, 
				retrocedirPerTasques);
		mesuresTemporalsHelper.mesuraCalcular("Retrocedir" + (retrocedirPerTasques ? " per tasques" : ""), "expedient", expedient.getTipus().getNom());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void eliminaInformacioRetroaccio(
			Long expedientId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Buidant logs de l'expedient("
				+ "expedientId=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_MANAGE,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		workflowRetroaccioApi.eliminaInformacioRetroaccio(expedient.getProcessInstanceId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<InformacioRetroaccioDto> findInformacioRetroaccioTascaOrdenatPerData(
			Long expedientId,
			Long informacioRetroaccioId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consultant logs d'una tasca de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"informacioRetroaccioId=" + informacioRetroaccioId + ")");
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});
		return workflowRetroaccioApi.findInformacioRetroaccioTascaOrdenatPerData(informacioRetroaccioId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<InformacioRetroaccioDto> findInformacioRetroaccioAccioRetrocesOrdenatsPerData(
			Long expedientId,
			Long informacioRetroaccioId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consultant logs d'un retrocés (" +
				"expedientId=" + expedientId + ", " +
				"informacioRetroaccioId=" + informacioRetroaccioId + ")");
		expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});
		return workflowRetroaccioApi.findInformacioRetroaccioAccioRetrocesOrdenatsPerData(informacioRetroaccioId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InformacioRetroaccioDto findInformacioRetroaccioById(
			Long informacioRetroaccioId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consultant log donat el seu id (logId=" + informacioRetroaccioId + ")");
		/*expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.LOG_READ,
						ExtendedPermission.ADMINISTRATION});*/
		return workflowRetroaccioApi.findInformacioRetroaccioById(informacioRetroaccioId);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientRegistreServiceImpl.class);

}
