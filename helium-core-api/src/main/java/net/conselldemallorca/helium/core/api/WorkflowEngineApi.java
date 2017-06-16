package net.conselldemallorca.helium.core.api;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;

/**
 * Interfície comú dels motors de workflow amb els mètodes necessaris per desplegar, consultar,
 * executar i mantenir els workflows de processos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public interface WorkflowEngineApi {
	
	public static final String VAR_TASCA_DELEGACIO = "H3l1um#tasca.delegacio";
	public static final String VAR_TASCA_VALIDADA = "H3l1um#tasca.validada";
	
	public enum MostrarTasquesDto {
		MOSTRAR_TASQUES_TOTS,
		MOSTRAR_TASQUES_NOMES_GROUPS,
		MOSTRAR_TASQUES_NOMES_PERSONALS
	}
	
	// DEFINICIÓ DE PROCÉS
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	// Desplegaments
	////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * Deployment:
	 * 
	 * getId()
	 * 		Activiti: deploymentId
	 * 		Jpmb: processDefinitionId
	 * getKey()
	 * getVersion()
	 * getProcessDefinitions()
	 * 		A Activiti un desplegament pot incloure diferents definicions de procés, 
	 * 		així que hem substituit la cridada getProcessDefinition() utilitzada per a 
	 * 		obtenir les tasques de la definició de procés desplegada, per aquesta, 
	 * 		que retora una llista
	 * 
	 * 		S'han modificat els mètodes de deploy, ja que ara poden generar vàries 
	 * 		definicions de procés. 
	 */
	
	/**
	 * Desplega un model BPMN2.0
	 * 
	 * @param nomArxiu
	 * @param contingut
	 * @return
	 */
	public WDeployment desplegar(
			String nomArxiu, 
			byte[] contingut);
	
	// Afegim el següent mètode per a compatibilitat amb Activiti, on un desplegament pot 
	// incloure diverses definicions de procés. 
	/**
	 * Obté les dades d'un desplegament concret
	 * 
	 * @param deploymentId
	 * @return
	 */
	public WDeployment getDesplegament(String deploymentId);
	
	/**
	 * Elimina un desplegament concret
	 * 
	 * @param deploymentId
	 */
	public void esborrarDesplegament(String deploymentId);
	
	
	/**
	 * Obté els noms dels recursos desplegats en un desplegament concret
	 * 
	 * @param deploymentId
	 * @return
	 */
	public Set<String> getResourceNames(String deploymentId);
	
	/**
	 * Obté el contingut d'un recurs d'un desplegament concret. El recurs s'identifica amb el nom
	 * 
	 * @param deploymentId
	 * @param resourceName
	 * @return
	 */
	public byte[] getResourceBytes(
			String deploymentId, 
			String resourceName);
	
	/**
	 * Actualitza els recursos de tipus acció, sense canviar la versió d'un desplagament
	 * 
	 * @param deploymentId
	 * @param handlers
	 */
	public void updateDeploymentActions(
			Long deploymentId, 
			Map<String, 
			byte[]> handlers);
	
	// Consulta de Definicions de Procés
	////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * getProcessDefinition().getName() == getName()
	 * getVersion()
	 * getKey()
	 * getName()
	 */
	
	/**
	 * Obté una definició de procés donat el codi de desplegament i de la definició de procés 
	 * @param deploymentId
	 * @param processDefinitionId
	 * @return
	 */
	public WProcessDefinition getProcessDefinition(
			String deploymentId, 
			String processDefinitionId);
	
	/**
	 * Obté les definicions de procés dels subprocessos donat el codi de desplegament i de la definició de procés pare
	 * 
	 * @param deploymentId
	 * @param processDefinitionId
	 * @return
	 */
	public List<WProcessDefinition> getSubProcessDefinitions(
			String deploymentId, 
			String processDefinitionId);
	
	/**
	 * Obté els noms de les tasques d'una definició de procés donat el desplegament i el codi de definició de procés
	 * 
	 * @param dpd
	 * @param processDefinitionId
	 * @return
	 */
	public List<String> getTaskNamesFromDeployedProcessDefinition(
			WDeployment dpd, 
			String processDefinitionId);
	
	/**
	 * Obté el nom de la tasca inicial d'una definició de procés
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	public String getStartTaskName(String processDefinitionId);
	
	/**
	 * Obté la definició de procés d'una instància de procés
	 * @param processInstanceId
	 * @return
	 */
	public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId);
		
	
	// DEFINICIÓ DE TASQUES
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	
	// INSTÀNCIA DE PROCÉS
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * getId()
	 * getProcessDefinitionId()
	 * getProcessInstance().getProcessDefinition().getName() ==> Activiti::ProcessInstance.getProcessDefinitionName()
	 * getParentProcessInstanceId()
	 * getProcessInstance().getId()
	 * getEnd() ==> Activiti::HistoricProcessInstance.getEndTime()
	 * getProcessInstance().getTaskMgmtInstance().getUnfinishedTasks(currentToken) ==> Retroces!!!
	 */
	
	// Consulta de instància de procés
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obté totes les instàncies de procés d'una definició de procés, donat el seu codi
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId);
	
	/**
	 * Obté totes les instàncies de procés d'una definició de procés, donat el seu nom
	 * 
	 * @param processName
	 * @return
	 */
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName);
	
	/**
	 * Obté totes les instàncies de procés d'una definició de procés, donat el seu nom i l'entorn Helium
	 * 
	 * @param processName
	 * @param entornId
	 * @return
	 */
	// Com a entornId podem utilitzar el tenantId de la instància de procés, o la categoria de la definició de procés
	public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(
			String processName, 
			Long entornId);

	/**
	 * Obté les instàncies de procés del procés principal, i dels subprocessos donat l'identificador del procés principal
	 * 
	 * @param rootProcessInstanceId
	 * @return
	 */
	public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId);
	
	/**
	 * Obté la instància de procés donat el seu codi
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public WProcessInstance getProcessInstance(String processInstanceId);
	
	/**
	 * Obté la instància de procés principal donat el codi de la instància de procés principal, o d'un dels seus subprocessos
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public WProcessInstance getRootProcessInstance(String processInstanceId);
	
	/**
	 * Obté les instàncies de procés principals filtrades
	 * 
	 * @param actorId
	 * @param processInstanceIds
	 * @param nomesMeves
	 * @param nomesTasquesPersonals
	 * @param nomesTasquesGrup
	 * @return
	 */
	public List<String> findRootProcessInstances(
			String actorId,
			List<String> processInstanceIds,
			boolean nomesMeves,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup);
	
	// Tramitació
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Inicia una nova instància de procés
	 * 
	 * @param actorId
	 * @param processDefinitionId
	 * @param variables
	 * @return
	 */
	public WProcessInstance startProcessInstanceById(
			String actorId, 
			String processDefinitionId, 
			Map<String, Object> variables);
	
	/**
	 * Envia un disparador extern a una instància de procés
	 * 
	 * @param processInstanceId
	 * @param transitionName
	 */
	public void signalProcessInstance(
			String processInstanceId, 
			String transitionName);
	
	/**
	 * Elimina una instància de procés existent
	 * 
	 * @param processInstanceId
	 */
	public void deleteProcessInstance(String processInstanceId);
	
	/**
	 * Suspen les instàncies de procés indicades
	 * 
	 * @param processInstanceIds
	 */
	public void suspendProcessInstances(String[] processInstanceIds);
	
	/**
	 * Activa les instàncies de procés indicades
	 * 
	 * @param processInstanceIds
	 */
	public void resumeProcessInstances(String[] processInstanceIds);
	
	/**
	 * Canvia la versió de la instància de procés indicada
	 * 
	 * @param processInstanceId
	 * @param newVersion
	 */
	public void changeProcessInstanceVersion(
			String processInstanceId, 
			int newVersion);
	
	
	// VARIABLES DE PROCÉS
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	// Consulta de variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obté les variables d'una instància de procés concreta
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public Map<String, Object> getProcessInstanceVariables(String processInstanceId);
	
	/**
	 * Obté una variable d'una instància de procés concreta
	 * 
	 * @param processInstanceId
	 * @param varName
	 * @return
	 */
	public Object getProcessInstanceVariable(
			String processInstanceId, 
			String varName);

	
	// Actualització de variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Assigna el valor indicat a una variable de la instància de procés
	 * 
	 * @param processInstanceId
	 * @param varName
	 * @param value
	 */
	public void setProcessInstanceVariable(
			String processInstanceId, 
			String varName, 
			Object value);
	
	/**
	 * Elimina una variable d'una instància de procés
	 * 
	 * @param processInstanceId
	 * @param varName
	 */
	public void deleteProcessInstanceVariable(
			String processInstanceId, 
			String varName);

	
	// INSTÀNCIA DE TASQUES
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////

	/*
	 * getProcessInstanceId()
	 * getId()
	 * getTaskInstance().getActorId() ==> Activiti::getAssignee()
	 * getTaskInstance().getProcessInstance().getExpedient().getId()
	 * getTaskInstance().getSelectedOutcome()
	 * getTaskInstance().getId()
	 * getProcessDefinitionId()
	 * getTaskInstance().getPooledActors() ==> Activiti::TaskService.getIdentityLinksForTask(String taskId)
	 * 
	 */
	
	// Consulta de tasques
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obté la instància d0una tasca donat el seu codi
	 * 
	 * @param taskId
	 * @return
	 */
	public WTaskInstance getTaskById(String taskId); // Instancia de tasca
	
	/**
	 * Obté la llista de instàncies de tasca d'una instància de procés
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId);
	
	/**
	 * Obté l'identificador de la instància de tasca activa donat el seu token d'execució
	 *  
	 * @param executionTokenId
	 * @return
	 */
	public Long getTaskInstanceIdByExecutionTokenId(Long executionTokenId);
	
	/**
	 * Obté un llistat paginat de instàncies de tasques donat un filtre concret 
	 * 
	 * @param entornId
	 * @param actorId
	 * @param taskName
	 * @param titol
	 * @param expedientId
	 * @param expedientTitol
	 * @param expedientNumero
	 * @param expedientTipusId
	 * @param dataCreacioInici
	 * @param dataCreacioFi
	 * @param prioritat
	 * @param dataLimitInici
	 * @param dataLimitFi
	 * @param mostrarAssignadesUsuari
	 * @param mostrarAssignadesGrup
	 * @param nomesPendents
	 * @param paginacioParams
	 * @param nomesCount
	 * @return
	 */
	public ResultatConsultaPaginada<WTaskInstance> tascaFindByFiltrePaginat(
			Long entornId,
			String actorId,
			String taskName,
			String titol,
			Long expedientId,
			String expedientTitol,
			String expedientNumero,
			Long expedientTipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			boolean mostrarAssignadesUsuari,
			boolean mostrarAssignadesGrup,
			boolean nomesPendents,
			PaginacioParamsDto paginacioParams,
			boolean nomesCount);

	/**
	 * Obté un llistat d'identificadors de instàncies de tasques donat un filtre concret
	 * 
	 * @param responsable
	 * @param tasca
	 * @param tascaSel
	 * @param idsPIExpedients
	 * @param dataCreacioInici
	 * @param dataCreacioFi
	 * @param prioritat
	 * @param dataLimitInici
	 * @param dataLimitFi
	 * @param paginacioParams
	 * @param nomesTasquesPersonals
	 * @param nomesTasquesGrup
	 * @param nomesAmbPendents
	 * @return
	 */
	public LlistatIds tascaIdFindByFiltrePaginat(
			String responsable,
			String tasca,
			String tascaSel,
			List<Long> idsPIExpedients,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Integer prioritat,
			Date dataLimitInici,
			Date dataLimitFi,
			PaginacioParamsDto paginacioParams,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAmbPendents);
	
	
	// Tramitació de tasques
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Agafa una tasca per a ser tramitada per un usuari
	 *  
	 * @param taskId
	 * @param actorId
	 */
	public void takeTaskInstance(
			String taskId, 
			String actorId);
	
	/**
	 * Allibera una tasca per a que pugui ser tramitada per altres usuaris
	 * 
	 * @param taskId
	 */
	public void releaseTaskInstance(String taskId);
	
	/**
	 * Inicia la tramitació d'una tasca
	 * 
	 * @param taskId
	 * @return
	 */
	public WTaskInstance startTaskInstance(String taskId);
//	public void endTaskInstance(String taskId, String outcome);
	
	/**
	 * Completa la tramitació d'una tasca
	 * 
	 * @param task
	 * @param outcome
	 * @return
	 */
	public ResultatCompleteTask completeTaskInstance(
			WTaskInstance task, 
			String outcome);
	
	/**
	 * Cancel·la una tasca i continua amb l'execució de la instància de procés
	 * 
	 * @param taskId
	 * @return
	 */
	public WTaskInstance cancelTaskInstance(String taskId);
	
	/**
	 * Suspén una tasca
	 * 
	 * @param taskId
	 * @return
	 */
	public WTaskInstance suspendTaskInstance(String taskId);
	
	/**
	 * Activa una tasca suspesa
	 * 
	 * @param taskId
	 * @return
	 */
	public WTaskInstance resumeTaskInstance(String taskId);
	
	// Reassignació / Delegació
	
	/**
	 * Reassigna una instància de tasca segons la expressió donada
	 * 
	 * @param taskId
	 * @param expression
	 * @param entornId
	 * @return
	 */
	public WTaskInstance reassignTaskInstance(
			String taskId, 
			String expression, 
			Long entornId);
	
	/**
	 * Delega una tasca a un altre usuari
	 * 
	 * @param task
	 * @param actorId
	 * @param comentari
	 * @param supervisada
	 */
	public void delegateTaskInstance(
			WTaskInstance task, 
			String actorId,
			String comentari,
			boolean supervisada);
	
	/**
	 * Obté la informació d'una delegació realitzada
	 * 
	 * @param taskId
	 * @param includeActors
	 * @return
	 */
	public DelegationInfo getDelegationTaskInstanceInfo(
			String taskId, 
			boolean includeActors);
	
	/**
	 * Cancel·la una delegació realitzada, i retorna la tasca a l'usuari original
	 * @param task
	 */
	public void cancelDelegationTaskInstance(WTaskInstance task);
	
	// Caché
	
	/**
	 * Desa la informació de caché de la tasca
	 * 
	 * @param taskId
	 * @param titol
	 * @param description
	 */
	public void updateTaskInstanceInfoCache(
			String taskId, 
			String titol, 
			String infoCache);
	
	// VARIABLES DE TASQUES
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obté les variables de la instància de procés.
	 * @param taskId
	 * @return Retorna un Map de codi i valor de les variables de la instància de procés.
	 */
	public Map<String, Object> getTaskInstanceVariables(String taskId);
	
	/** 
	 * Obé el valor d'una variable d'una instàcia de procés.
	 * @param taskId
	 * @param varName
	 * @return
	 */
	public Object getTaskInstanceVariable(String taskId, String varName);
	
	/** 
	 * Fixa el valor de la variable de la instància de procés.
	 * @param taskId
	 * @param varName
	 * @param valor
	 */
	public void setTaskInstanceVariable(String taskId, String varName, Object valor);
	
	/**
	 * Fixa el valor de vàries variables a la vegada de la instància de la tasca. 
	 * Es pot especificar si esborrar abans les variables.
	 * @param taskId
	 * @param variables
	 * @param deleteFirst
	 */
	public void setTaskInstanceVariables(String taskId, Map<String, Object> variables, boolean deleteFirst);
	
	/** Esborra una variable de la instància de la tasca
	 * 
	 * @param taskId
	 * @param varName
	 */
	public void deleteTaskInstanceVariable(String taskId, String varName);
	
	//TODO: Comprovar si s'ha d'implementr el mètode per finalitzar expedients demanat en la versió 3.2.45 
	// finalitzarExpedient(, boolean cancel·larTasquesActives)
	
	// FILS D'EXECUCIÓ (Token / Execution path)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Obté la informació del token per identificador.
	 * 
	 * @param tokenId
	 * @return
	 */
	public WToken getTokenById(String tokenId);
	
	/** Consulta la llista de tokens actius per una instància de procés.
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public Map<String, WToken> getActiveTokens(String processInstanceId);
	
	/** Retorna una llista de tots els tokens d'una instància de procés.
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public Map<String, WToken> getAllTokens(String processInstanceId);
	
	/** Mètode per redirigir la execució cap a un altre token
	 * 
	 * @param tokenId
	 * @param nodeName
	 * @param cancelTasks
	 * @param enterNodeIfTask
	 * @param executeNode
	 */
	public void tokenRedirect(long tokenId, String nodeName, boolean cancelTasks, boolean enterNodeIfTask, boolean executeNode);
	
	/** Mètode per activar o desactivar un token.
	 * 
	 * @param tokenId
	 * @param activar
	 * @return
	 */
	public boolean tokenActivar(long tokenId, boolean activar);
	
	/** Mètode per enviar un senyal a un token per a que avanci per una transició.
	 * 
	 * @param tokenId
	 * @param transitionName
	 */
	public void signalToken(long tokenId, String transitionName);
	
	// ACCIONS
	////////////////////////////////////////////////////////////////////////////////
	public Map<String, Object> evaluateScript(String processInstanceId, String script, Set<String> outputNames);
	public Object evaluateExpression(String taskInstanceInstanceId, String processInstanceId, String expression, Map<String, Object> valors);
	
	public List<String> listActions(String jbpmId);
	public void executeActionInstanciaProces(String processInstanceId, String actionName);
	public void executeActionInstanciaTasca(String taskInstanceId, String actionName);
	public void retrocedirAccio(String processInstanceId, String actionName, List<String> params);

	// TIMERS
	////////////////////////////////////////////////////////////////////////////////
	//public List<Timer> findTimersWithProcessInstanceId(String processInstanceId);
	public void suspendTimer(long timerId, Date dueDate);
	public void resumeTimer(long timerId, Date dueDate);
	
	// A ELIMINAR
	////////////////////////////////////////////////////////////////////////////////
	public List<WTaskInstance> findTasks(List<Long> ids); // 2.6
	public List<WTaskInstance> findPersonalTasks(String usuariBo); // 2.6

	// A MOURE FORA DE LA FUNCIONALITAT DEL MOTOR DE WORKFLOW
	////////////////////////////////////////////////////////////////////////////////
	
	// Transicions (Sequence flow)
	public List<String> findStartTaskOutcomes(String jbpmId, String taskName);
	public List<String> findTaskInstanceOutcomes(String taskInstanceId);
	public List<String> findArrivingNodeNames(String tokenId); // Retrocedir??
		
	
	// Expedients
	public ExpedientDto expedientFindByProcessInstanceId(String processInstanceId);
	public ResultatConsultaPaginada<Long> expedientFindByFiltre(
			Long entornId,
			String actorId,
			Collection<Long> tipusIdPermesos,
			String titol,
			String numero,
			Long tipusId,
			Date dataCreacioInici,
			Date dataCreacioFi,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesIniciats,
			boolean nomesFinalitzats,
			boolean mostrarAnulats,
			boolean mostrarNomesAnulats,
			boolean nomesAlertes,
			boolean nomesErrors,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesTasquesMeves,
			PaginacioParamsDto paginacioParams,
			boolean nomesCount);

			/*
			| V3
			|- ExpedientServiceImpl
			|		- consultaFindNomesIdsPaginat
			|		- consultaFindPaginat
			|		- findAmbFiltrePaginat
			|		- findIdsAmbFiltre
			*/
	public void desfinalitzarExpedient(String processInstanceId);
		
	/** Mètode per finalitzar l'expedient. */
	public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio);

	
	// Tasques en segón pla 
	public void marcarFinalitzar(String taskId, Date marcadaFinalitzar, String outcome, String rols);
	public void marcarIniciFinalitzacioSegonPla(String taskId, Date iniciFinalitzacio);
	public void guardarErrorFinalitzacio(String taskId, String errorFinalitzacio);
	public List<Object[]> getTasquesSegonPlaPendents();
	
	// Eliminació de definicions de procés
	public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(Long entornId);
	public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(Long expedientTipusId);
	public List<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
			Long expedientTipusId,
			Long processDefinitionId);
	
	// Avaluació d'expressions
	
	/** Avalua una expressió amb uns valors de variables en el contexte.
	 * 
	 * @param expression
	 * @param expectedClass
	 * @param context
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Object evaluateExpression( 
			String expression,
			Class expectedClass,
			Map<String, Object> context); 
	
	// Retroacció
//	public Map<Token, List<ProcessLog>> getProcessInstanceLogs(String processInstanceId);
//	public long addProcessInstanceMessageLog(String processInstanceId, String message);
//	public long addTaskInstanceMessageLog(String taskInstanceId, String message);
//	public Long getVariableIdFromVariableLog(long variableLogId);
//	public Long getTaskIdFromVariableLog(long variableLogId);	
//	public void cancelProcessInstance(long id);
//	public void revertProcessInstanceEnd(long id);
//	public void cancelToken(long id);
//	public void revertTokenEnd(long id);
//	public WTaskInstance findEquivalentTaskInstance(long tokenId, long taskInstanceId);
//	public boolean isProcessStateNodeJoinOrFork(long processInstanceId, String nodeName);
//	public boolean isJoinNode(long processInstanceId, String nodeName);
//	public ProcessLog getProcessLogById(Long id);
//	public Node getNodeByName(long processInstanceId, String nodeName);
//	public boolean hasStartBetweenLogs(long begin, long end, long taskInstanceId);
//	public void deleteProcessInstanceTreeLogs(String rootProcessInstanceId);
//	public void setTaskInstanceActorId(String taskInstanceId, String actorId);
//	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors);

	/** Mètode per obtenir una definició de procés a partir del contingut comprimit del mateix.
	 * 
	 * @param zipInputStream
	 * @return
	 * @throws Exception
	 */
	public WProcessDefinition parse(ZipInputStream zipInputStream ) throws Exception;

}
