/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.Tasca.TipusTasca;
import net.conselldemallorca.helium.core.model.hibernate.Validacio;


/**
 * DTO amb informació d'una tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaDto implements Comparable<TascaDto> {

	private String id;
	private String nom;
	private String missatgeInfo;
	private String missatgeWarn;
	private TipusTasca tipus;
	private String jbpmName;
	private String description;
	private String assignee;
	private Set<String> pooledActors;
	private Date createTime;
	private Date startTime;
	private Date endTime;
	private Date dueDate;
	private int priority;
	private boolean open;
	private boolean completed;
	private boolean cancelled;
	private boolean suspended;
	private String processInstanceId;
	private String recursForm;
	private String formExtern;

	private boolean delegable;
	private boolean delegada;
	private boolean delegacioOriginal;
	private Date delegacioData;
	private String delegacioComentari;
	private boolean delegacioSupervisada;
	private PersonaDto delegacioPersona;

	private boolean validada;
	private boolean documentsComplet;
	private boolean signaturesComplet;

	private DefinicioProces definicioProces;
	private Expedient expedient;

	private Map<String, PersonaDto> personesMap;

	private List<String> outcomes;
	private List<Validacio> validacions;
	private List<CampTasca> camps;
	private List<DocumentTasca> documents;
	private List<FirmaTasca> signatures;

	private Map<String, Object> variables;
	private Map<String, DocumentDto> varsDocuments;
	private Map<String, DocumentDto> varsDocumentsPerSignar;
	private Map<String, ParellaCodiValorDto> valorsDomini;
	private Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini;
	private Map<String, Object> varsComText;

	private Long tascaId;
	private boolean agafada;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getMissatgeInfo() {
		return missatgeInfo;
	}
	public void setMissatgeInfo(String missatgeInfo) {
		this.missatgeInfo = missatgeInfo;
	}
	public String getMissatgeWarn() {
		return missatgeWarn;
	}
	public void setMissatgeWarn(String missatgeWarn) {
		this.missatgeWarn = missatgeWarn;
	}
	public TipusTasca getTipus() {
		return tipus;
	}
	public void setTipus(TipusTasca tipus) {
		this.tipus = tipus;
	}
	public String getJbpmName() {
		return jbpmName;
	}
	public void setJbpmName(String jbpmName) {
		this.jbpmName = jbpmName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public Set<String> getPooledActors() {
		return pooledActors;
	}
	public void setPooledActors(Set<String> pooledActors) {
		this.pooledActors = pooledActors;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public boolean isSuspended() {
		return suspended;
	}
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getRecursForm() {
		return recursForm;
	}
	public void setRecursForm(String recursForm) {
		this.recursForm = recursForm;
	}
	public String getFormExtern() {
		return formExtern;
	}
	public void setFormExtern(String formExtern) {
		this.formExtern = formExtern;
	}
	public boolean isDelegable() {
		return delegable;
	}
	public void setDelegable(boolean delegable) {
		this.delegable = delegable;
	}
	public boolean isDelegada() {
		return delegada;
	}
	public void setDelegada(boolean delegada) {
		this.delegada = delegada;
	}
	public boolean isDelegacioOriginal() {
		return delegacioOriginal;
	}
	public void setDelegacioOriginal(boolean delegacioOriginal) {
		this.delegacioOriginal = delegacioOriginal;
	}
	public Date getDelegacioData() {
		return delegacioData;
	}
	public void setDelegacioData(Date delegacioData) {
		this.delegacioData = delegacioData;
	}
	public String getDelegacioComentari() {
		return delegacioComentari;
	}
	public void setDelegacioComentari(String delegacioComentari) {
		this.delegacioComentari = delegacioComentari;
	}
	public boolean isDelegacioSupervisada() {
		return delegacioSupervisada;
	}
	public void setDelegacioSupervisada(boolean delegacioSupervisada) {
		this.delegacioSupervisada = delegacioSupervisada;
	}
	public PersonaDto getDelegacioPersona() {
		return delegacioPersona;
	}
	public void setDelegacioPersona(PersonaDto delegacioPersona) {
		this.delegacioPersona = delegacioPersona;
	}
	public boolean isValidada() {
		return validada;
	}
	public void setValidada(boolean validada) {
		this.validada = validada;
	}
	public boolean isDocumentsComplet() {
		return documentsComplet;
	}
	public void setDocumentsComplet(boolean documentsComplet) {
		this.documentsComplet = documentsComplet;
	}
	public boolean isSignaturesComplet() {
		return signaturesComplet;
	}
	public void setSignaturesComplet(boolean signaturesComplet) {
		this.signaturesComplet = signaturesComplet;
	}
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}
	public Expedient getExpedient() {
		return expedient;
	}
	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}
	public Map<String, PersonaDto> getPersonesMap() {
		return personesMap;
	}
	public void setPersonesMap(Map<String, PersonaDto> personesMap) {
		this.personesMap = personesMap;
	}
	public List<String> getOutcomes() {
		return outcomes;
	}
	public void setOutcomes(List<String> outcomes) {
		this.outcomes = outcomes;
	}
	public List<DocumentTasca> getDocuments() {
		return this.documents;
	}
	public void setDocuments(List<DocumentTasca> documents) {
		this.documents = documents;
	}
	public List<Validacio> getValidacions() {
		return validacions;
	}
	public void setValidacions(List<Validacio> validacions) {
		this.validacions = validacions;
	}
	public List<CampTasca> getCamps() {
		return camps;
	}
	public void setCamps(List<CampTasca> camps) {
		this.camps = camps;
	}
	public List<FirmaTasca> getSignatures() {
		return signatures;
	}
	public void setSignatures(List<FirmaTasca> signatures) {
		this.signatures = signatures;
	}
	public Map<String, Object> getVariables() {
		return variables;
	}
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}
	public Object getVariable(String varName) {
		if (variables == null)
			return null;
		return variables.get(varName);
	}
	public Map<String, DocumentDto> getVarsDocuments() {
		return varsDocuments;
	}
	public void setVarsDocuments(Map<String, DocumentDto> varsDocuments) {
		this.varsDocuments = varsDocuments;
	}
	public Map<String, DocumentDto> getVarsDocumentsPerSignar() {
		return varsDocumentsPerSignar;
	}
	public void setVarsDocumentsPerSignar(Map<String, DocumentDto> varsDocumentsPerSignar) {
		this.varsDocumentsPerSignar = varsDocumentsPerSignar;
	}
	public Map<String, ParellaCodiValorDto> getValorsDomini() {
		return valorsDomini;
	}
	public void setValorsDomini(Map<String, ParellaCodiValorDto> valorsDomini) {
		this.valorsDomini = valorsDomini;
	}
	public Map<String, List<ParellaCodiValorDto>> getValorsMultiplesDomini() {
		return valorsMultiplesDomini;
	}
	public void setValorsMultiplesDomini(
			Map<String, List<ParellaCodiValorDto>> valorsMultiplesDomini) {
		this.valorsMultiplesDomini = valorsMultiplesDomini;
	}
	public Map<String, Object> getVarsComText() {
		return varsComText;
	}
	public void setVarsComText(Map<String, Object> varsComText) {
		this.varsComText = varsComText;
	}

	public Set<String> getVariableKeys() {
		if (variables == null)
			return null;
		return variables.keySet();
	}
	public Set<String> getDocumentKeys() {
		if (varsDocuments == null)
			return null;
		return varsDocuments.keySet();
	}

	public boolean isCampsNotReadOnly() {
		for (CampTasca camp: camps) {
			if (!camp.isReadOnly())
				return true;
		}
		return false;
	}
	public boolean isDocumentsNotReadOnly() {
		for (DocumentTasca document: documents) {
			if (!document.isReadOnly())
				return true;
		}
		return false;
	}

	public String getNomLimitat() {
		if (nom.length() > 100)
			return nom.substring(0, 100) + " (...)";
		else
			return nom;
	}

	public List<DocumentTasca> getDocumentsOrdenatsPerMostrarTasca() {
		List<DocumentTasca> resposta = new ArrayList<DocumentTasca>();
		// Afegeix primer els documents que ja estan adjuntats a la tasca
		for (DocumentTasca dt: documents) {
			if (varsDocuments.get(dt.getDocument().getCodi()) != null) {
				resposta.add(dt);
			}
		}
		// Despres afegeix els altres documents
		for (DocumentTasca dt: documents) {
			if (varsDocuments.get(dt.getDocument().getCodi()) == null) {
				resposta.add(dt);
			}
		}
		return resposta;
	}

	public Long getTascaId() {
		return tascaId;
	}
	public void setTascaId(Long tascaId) {
		this.tascaId = tascaId;
	}
	public boolean isAgafada() {
		return agafada;
	}
	public void setAgafada(boolean agafada) {
		this.agafada = agafada;
	}

	public int compareTo(TascaDto aThat) {
	    if (this == aThat) return 0;
    	return this.getCreateTime().compareTo(aThat.getCreateTime());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TascaDto other = (TascaDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
