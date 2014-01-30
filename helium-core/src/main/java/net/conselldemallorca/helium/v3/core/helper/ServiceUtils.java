/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.service.LuceneHelper;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.Registre;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.service.PermissionService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;

import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

/**
 * Utilitats comunes pels serveis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("serviceUtilsV3")
public class ServiceUtils {
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private LuceneHelper luceneHelper;
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource(name="permissionServiceV3")
	private PermissionService permissionService;
	@Resource
	private MessageSource messageSource;

	/**
	 * Mètodes per a la reindexació d'expedients
	 */
	public void expedientIndexLuceneCreate(String processInstanceId) {
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientHelper.findAmbProcessInstanceId(rootProcessInstance.getId());
		Map<String, Set<Camp>> mapCamps = getMapCamps(expedient.getProcessInstanceId());
		Map<String, Map<String, Object>> mapValors = getMapValors(expedient.getProcessInstanceId());
		luceneHelper.createExpedientAsync(
				expedient,
				getMapDefinicionsProces(expedient.getProcessInstanceId()),
				mapCamps,
				mapValors,
				getMapValorsDomini(mapCamps, mapValors),
				isExpedientFinalitzat(expedient));
	}
	public void expedientIndexLuceneUpdate(String processInstanceId) {
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientHelper.findAmbProcessInstanceId(rootProcessInstance.getId());
		Map<String, Set<Camp>> mapCamps = getMapCamps(rootProcessInstance.getId());
		Map<String, Map<String, Object>> mapValors = getMapValors(rootProcessInstance.getId());
		luceneHelper.updateExpedientCampsAsync(
				expedient,
				getMapDefinicionsProces(rootProcessInstance.getId()),
				mapCamps,
				mapValors,
				getMapValorsDomini(mapCamps, mapValors),
				isExpedientFinalitzat(expedient));
	}
	public void expedientIndexLuceneRecrear(String processInstanceId) {
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientHelper.findAmbProcessInstanceId(rootProcessInstance.getId());
		luceneHelper.deleteExpedientAsync(expedient);
		Map<String, Set<Camp>> mapCamps = getMapCamps(rootProcessInstance.getId());
		Map<String, Map<String, Object>> mapValors = getMapValors(rootProcessInstance.getId());
		luceneHelper.createExpedientAsync(
				expedient,
				getMapDefinicionsProces(rootProcessInstance.getId()),
				mapCamps,
				mapValors,
				getMapValorsDomini(mapCamps, mapValors),
				isExpedientFinalitzat(expedient));
	}
	public List<Map<String, DadaIndexadaDto>> expedientIndexLucenGetDades(String processInstanceId) {
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientHelper.findAmbProcessInstanceId(rootProcessInstance.getId());
		List<Camp> informeCamps = new ArrayList<Camp>();
		Map<String, Set<Camp>> camps = getMapCamps(rootProcessInstance.getId());
		for (String clau: camps.keySet()) {
			for (Camp camp: camps.get(clau))
				informeCamps.add(camp);
		}
		informeCamps.addAll(findAllCampsExpedientConsulta());
		return luceneHelper.getDadesExpedientV3(
				expedient.getEntorn().getCodi(),
				expedient,
				informeCamps);
	}

	/*
	 * Mètodes per a obtenir els camps de les consultes per tipus
	 */
	public List<Camp> findCampsPerCampsConsulta(Consulta consulta, TipusConsultaCamp tipus) {
		List<Camp> resposta = new ArrayList<Camp>();
		List<ConsultaCamp> camps = null;
		if (tipus != null)
			camps = consultaCampRepository.findCampsConsulta(consulta.getId(), tipus);
		else
			camps = new ArrayList<ConsultaCamp>(consulta.getCamps());
		for (ConsultaCamp camp: camps) {
			if (!camp.getCampCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
				DefinicioProces definicioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
						camp.getDefprocJbpmKey(),
						camp.getDefprocVersio());
				if (definicioProces != null) {
					resposta.add(campRepository.findByDefinicioProcesAndCodi(
							definicioProces,
							camp.getCampCodi()));
				} else {
					resposta.add(
							new Camp(
									null,
									camp.getCampCodi(),
									TipusCamp.STRING,
									camp.getCampCodi()));
				}
			} else {
				Camp campExpedient = getCampExpedient(camp.getCampCodi());
				if (campExpedient != null) {
					resposta.add(campExpedient);
				} else {
					resposta.add(
							new Camp(
									null,
									camp.getCampCodi(),
									TipusCamp.STRING,
									camp.getCampCodi()));
				}
			}
		}
		return resposta;
	}
	public List<Camp> findAllCampsExpedientConsulta() {
		List<Camp> resposta = new ArrayList<Camp>();
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ID));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_NUMERO));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_TITOL));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_COMENTARI));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_INICIADOR));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ESTAT));
		return resposta;
	}
	
	private Camp getCampExpedient(String campCodi) {
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.id"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.numero"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.titol"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.comentari"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.iniciador"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.responsable"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.DATE);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.data_ini"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SELECCIO);
			campExpedient.setEtiqueta(getMessage("etiqueta.exp.estat"));
			return campExpedient;
		}
		return null;
	}

	/*
	 * Mètodes pel multiidioma
	 */
	public String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}
	public String getMessage(String key) {
		return getMessage(key, null);
	}

	/*
	 * Comprovació de permisos
	 */
	@SuppressWarnings("rawtypes")
	public void filterAllowed(
			List list,
			Class clazz,
			Permission[] permissions) {
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object entry = it.next();
			if (!isGrantedAny((Long)entry, clazz, permissions))
				it.remove();
		}
	}
	@SuppressWarnings("rawtypes")
	public Object filterAllowed(
			Long object,
			Class clazz,
			Permission[] permissions) {
		if (isGrantedAny(object, clazz, permissions)) {
			return object;
		} else {
			return null;
		}
	}

	/*
	 * Obtenció de valors i descripcions de variables emmagatzemades
	 * a dins els processos jBPM
	 */
	public Object getVariableJbpmTascaValor(
			String taskId,
			String varCodi) {
		Object valor = jbpmHelper.getTaskInstanceVariable(taskId, varCodi);
		return valorVariableJbpmRevisat(valor);
	}
	public Map<String, Object> getVariablesJbpmTascaValor(
			String taskId) {
		Map<String, Object> valors = jbpmHelper.getTaskInstanceVariables(taskId);
		Map<String, Object> valorsRevisats = new HashMap<String, Object>();
		for (String varCodi: valors.keySet()) {
			Object valor = valors.get(varCodi);
			valorsRevisats.put(varCodi, valorVariableJbpmRevisat(valor));
		}
		return valorsRevisats;
	}
	public Object getVariableJbpmProcesValor(
			String processInstanceId,
			String varCodi) {
		Object valor = jbpmHelper.getProcessInstanceVariable(processInstanceId, varCodi);
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getCodi();
		} else {
			return valor;
		}
	}
	public Map<String, Object> getVariablesJbpmProcesValor(
			String processInstanceId) {
		Map<String, Object> valors = jbpmHelper.getProcessInstanceVariables(processInstanceId);
		Map<String, Object> valorsRevisats = new HashMap<String, Object>();
		if (valors != null) {
			for (String varCodi: valors.keySet()) {
				Object valor = valors.get(varCodi);
				valorsRevisats.put(varCodi, valorVariableJbpmRevisat(valor));
			}
		}
		return valorsRevisats;
	}
	public void revisarVariablesJbpm(Map<String, Object> variables) {
		if (variables != null) {
			for (String varCodi: variables.keySet()) {
				Object valor = variables.get(varCodi);
				variables.put(varCodi, valorVariableJbpmRevisat(valor));
			}
		}
	}

	/*
	 * Varis
	 */
	public boolean isExpedientFinalitzat(Expedient expedient) {
		if (expedient.getProcessInstanceId() != null) {
			JbpmProcessInstance processInstance = jbpmHelper.getProcessInstance(expedient.getProcessInstanceId());
			return processInstance.getEnd() != null;
		}
		return false;
	}

	private Map<String, DefinicioProces> getMapDefinicionsProces(String processInstanceId) {
		Map<String, DefinicioProces> resposta = new HashMap<String, DefinicioProces>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()));
		return resposta;
	}
	
	private Map<String, Set<Camp>> getMapCamps(String processInstanceId) {
		Map<String, Set<Camp>> resposta = new HashMap<String, Set<Camp>>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree) {
			resposta.put(
					pi.getId(),
					definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()).getCamps());
		}
		return resposta;
	}
	
	private Map<String, Map<String, Object>> getMapValors(String processInstanceId) {
		Map<String, Map<String, Object>> resposta = new HashMap<String, Map<String, Object>>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					getVariablesJbpmProcesValor(pi.getId()));
		return resposta;
	}
	
	private Map<String, Map<String, String>> getMapValorsDomini(
			Map<String, Set<Camp>> mapCamps,
			Map<String, Map<String, Object>> mapValors) {
		Map<String, Map<String, String>> resposta = new HashMap<String, Map<String, String>>();
		for (String clau: mapCamps.keySet()) {
			Map<String, String> textDominis = new HashMap<String, String>();
			for (Camp camp: mapCamps.get(clau)) {
				if (mapValors.get(clau) != null) {
					Object valor = mapValors.get(clau).get(camp.getCodi());
					if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
						if (valor != null) {
							String[] columnesRegistre = new String[camp.getRegistreMembres().size()];
							for (int i = 0; i < camp.getRegistreMembres().size(); i++) {
								columnesRegistre[i] = camp.getRegistreMembres().get(i).getMembre().getCodi();
							}
							List<Registre> registres = new ArrayList<Registre>();
							if (camp.isMultiple()) {
								Object[] filesValor = (Object[])valor;
								for (int i = 0; i < filesValor.length; i++) {
									registres.add(
											new Registre(columnesRegistre, (Object[])filesValor[i]));
								}
							} else {
								registres.add(
										new Registre(columnesRegistre, (Object[])valor));
							}
							for (Registre registre: registres) {
								for (CampRegistre campRegistre: camp.getRegistreMembres()) {
									guardarValorDominiPerCamp(
											textDominis,
											clau,
											campRegistre.getMembre(),
											registre.getValor(campRegistre.getMembre().getCodi()));
								}
							}
						}
					} else {
						guardarValorDominiPerCamp(
								textDominis,
								clau,
								camp,
								valor);
					}
				}
			}
			resposta.put(clau, textDominis);
		}
		return resposta;
	}
	private void guardarValorDominiPerCamp(
			Map<String, String> textDominis,
			String processInstanceId,
			Camp camp,
			Object valor) {
		if (!(valor instanceof String) || ((String)valor).length() > 0) {
			if (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST)) {
				if (valor != null) {
					String valorDomini = dtoConverter.getCampText(
							null,
							processInstanceId,
							new ModelMapper().map(camp, CampDto.class),
							valor);
					textDominis.put(
							camp.getCodi() + "@" + valor.toString(),
							valorDomini);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private boolean isGrantedAny(
			Long object,
			Class clazz,
			Permission[] permissions) {
		return permissionService.isGrantedAny(object , clazz, permissions);
		/*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga: auth.getAuthorities()) {
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));
		}
		try {
			Acl acl = aclServiceDao.readAclById(new ObjectIdentityImpl(clazz, object.getId()));
			for (Permission perm : permissions) {
				try {
					if (acl.isGranted(
							new Permission[]{perm},
							sids.toArray(new Sid[sids.size()]),
							false)) {
						return true;
					}
				} catch (NotFoundException ex) {}
			}
			return false;
		} catch (NotFoundException ex) {
			return false;
		}*/
	}

	private Object valorVariableJbpmRevisat(Object valor) {
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getCodi();
		} else {
			return valor;
		}
	}

}