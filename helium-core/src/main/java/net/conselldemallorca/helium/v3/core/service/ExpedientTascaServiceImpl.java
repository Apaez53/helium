/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.api.WProcessInstance;
import net.conselldemallorca.helium.core.api.WTaskInstance;
import net.conselldemallorca.helium.core.api.WorkflowEngineApi;
import net.conselldemallorca.helium.core.api.WorkflowRetroaccioApi;
import net.conselldemallorca.helium.core.api.WorkflowRetroaccioApi.ExpedientRetroaccioTipus;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.TascaHelper;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTascaService;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;


/**
 * Implementació dels mètodes del servei ExpedientService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientTascaServiceImpl implements ExpedientTascaService {

	@Resource
	private RegistreRepository registreRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientTascaDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consulta de tasques de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		boolean mostrarTasquesAltresUsuaris = expedientHelper.isGrantedAny(
				expedient,
				new Permission[] {
						ExtendedPermission.TASK_SUPERV,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		List<ExpedientTascaDto> tasques = tascaHelper.findTasquesPerExpedientPerInstanciaProces(
				expedient,
				processInstanceId,
				false,
				mostrarTasquesAltresUsuaris);
		tasques.addAll(
				tascaHelper.findTasquesPerExpedientPerInstanciaProces(
						expedient,
						processInstanceId,
						true,
						mostrarTasquesAltresUsuaris));
		return tasques;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientTascaDto> findPendents(
			Long expedientId,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) throws NoTrobatException, PermisDenegatException {
		logger.debug("Consulta de tasques pendents de l'expedient (" +
				"id=" + expedientId + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		boolean mostrarTasquesAltresUsuaris = expedientHelper.isGrantedAny(
				expedient,
				new Permission[] {
						ExtendedPermission.TASK_SUPERV,
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.ADMINISTRATION});
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		for (WProcessInstance jpi: workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId())) {
			resposta.addAll(
					tascaHelper.findTasquesPerExpedientPerInstanciaProces(
							jpi.getId(),
							expedient,
							mostrarTasquesAltresUsuaris,
							nomesTasquesPersonals,
							nomesTasquesGrup));
		}
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void cancelar(
			Long expedientId,
			String tascaId) throws NoTrobatException, ValidacioException {
		logger.debug("Cancelar tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TASK_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		if (!expedient.isAnulat()) {
			WTaskInstance task = tascaHelper.comprovarTascaPertanyExpedient(
							tascaId,
							expedient);
			workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
					task.getProcessInstanceId(),
					ExpedientRetroaccioTipus.TASCA_CANCELAR,
					null);
			workflowEngineApi.cancelTaskInstance(String.valueOf(tascaId));
			crearRegistreTasca(
					expedientId,
					String.valueOf(tascaId),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.CANCELAR);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void suspendre(
			Long expedientId,
			String tascaId) throws NoTrobatException, ValidacioException {
		logger.debug("Reprende tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TASK_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		if (!expedient.isAnulat()) {
			WTaskInstance task = tascaHelper.comprovarTascaPertanyExpedient(
					tascaId,
					expedient);
			workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
					task.getProcessInstanceId(),
					ExpedientRetroaccioTipus.TASCA_SUSPENDRE,
					null);
			workflowEngineApi.suspendTaskInstance(String.valueOf(tascaId));
			crearRegistreTasca(
					expedientId,
					String.valueOf(tascaId),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.ATURAR);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void reprendre(
			Long expedientId,
			String tascaId) throws NoTrobatException, ValidacioException {
		logger.debug("Reprende tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TASK_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		if (!expedient.isAnulat()) {
			WTaskInstance task = tascaHelper.comprovarTascaPertanyExpedient(
					tascaId,
					expedient);
			workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
					task.getProcessInstanceId(),
					ExpedientRetroaccioTipus.TASCA_CONTINUAR,
					null);
			workflowEngineApi.resumeTaskInstance(String.valueOf(tascaId));
			crearRegistreTasca(
					expedientId,
					String.valueOf(tascaId),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					Registre.Accio.REPRENDRE);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void reassignar(
			Long expedientId,
			String tascaId,
			String expressio) throws NoTrobatException, ValidacioException {
		logger.debug("Reassignar tasca l'expedient (id=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.TASK_MANAGE,
						ExtendedPermission.TASK_ASSIGN,
						ExtendedPermission.REASSIGNMENT,
						ExtendedPermission.ADMINISTRATION});
		tascaHelper.comprovarTascaPertanyExpedient(
				tascaId,
				expedient);
		String previousActors = tascaHelper.getActorsPerReassignacioTasca(tascaId);
		Long informacioRetroaccioId = workflowRetroaccioApi.afegirInformacioRetroaccioPerTasca(
				tascaId,
				ExpedientRetroaccioTipus.TASCA_REASSIGNAR,
				null);
		if (!expedient.isAturat()) {
			workflowEngineApi.reassignTaskInstance(tascaId, expressio, expedient.getEntorn().getId());
			String currentActors = tascaHelper.getActorsPerReassignacioTasca(tascaId);
			workflowRetroaccioApi.actualitzaParametresAccioInformacioRetroaccio(
					informacioRetroaccioId, 
					previousActors + "::" + currentActors);
			String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
			crearRegistreReassignarTasca(
					expedient.getId(),
					tascaId,
					usuari,
					expressio);
		} else {
			throw new ValidacioException("L'expedient " + expedient.getIdentificador() + " està aturat");
		}
	}



	private Registre crearRegistreTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.TASCA,
				tascaId);
		return registreRepository.save(registre);
	}
	private Registre crearRegistreReassignarTasca(
			Long expedientId,
			String tascaId,
			String responsableCodi,
			String expression) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				Registre.Accio.MODIFICAR,
				Registre.Entitat.TASCA,
				tascaId);
		registre.setMissatge("Reassignació de la tasca amb l'expressió \"" + expression + "\"");
		return registreRepository.save(registre);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaServiceImpl.class);

}
