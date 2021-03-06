/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * DTO amb informació d'una tasca per al llistat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaLlistatDto implements Comparable<TascaLlistatDto> {

	private String id;
	private String codi;
	private String titol;
	private String missatgeInfo;
	private String missatgeWarn;
	private String expedientTitol;
	private String expedientTitolOrdenacio;
	private String processInstanceId;
	private Long expedientTipusId;
	private String expedientTipusNom;
	private String expedientNumero;
	private String expedientProcessInstanceId;
	private Date dataCreacio;
	private Date dataInici;
	private Date dataFi;
	private Date dataLimit;
	private int prioritat;
	private boolean tramitacioMassiva;
	private String responsable;
	private Set<String> responsables;
	private boolean oberta;
	private boolean completada;
	private boolean cancelada;
	private boolean suspesa;
	private List<String> resultats;
	
	private boolean delegable;
	private boolean delegada;
	private boolean delegacioOriginal;
	private Date delegacioData;
	private String delegacioComentari;
	private boolean delegacioSupervisada;
	private PersonaDto delegacioPersona;

	private boolean agafada;

	private Map<String, PersonaDto> personesMap;
	

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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
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
	public String getExpedientTitol() {
		return expedientTitol;
	}
	public void setExpedientTitol(String expedientTitol) {
		this.expedientTitol = expedientTitol;
	}
	public String getExpedientTitolOrdenacio() {
		return expedientTitolOrdenacio;
	}
	public void setExpedientTitolOrdenacio(String expedientTitolOrdenacio) {
		this.expedientTitolOrdenacio = expedientTitolOrdenacio;
	}
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}
	public String getExpedientProcessInstanceId() {
		return expedientProcessInstanceId;
	}
	public void setExpedientProcessInstanceId(String expedientProcessInstanceId) {
		this.expedientProcessInstanceId = expedientProcessInstanceId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public String getExpedientTipusNom() {
		return expedientTipusNom;
	}
	public void setExpedientTipusNom(String expedientTipusNom) {
		this.expedientTipusNom = expedientTipusNom;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public Date getDataLimit() {
		return dataLimit;
	}
	public void setDataLimit(Date dataLimit) {
		this.dataLimit = dataLimit;
	}
	public int getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(int prioritat) {
		this.prioritat = prioritat;
	}
	public boolean isTramitacioMassiva() {
		return tramitacioMassiva;
	}
	public void setTramitacioMassiva(boolean tramitacioMassiva) {
		this.tramitacioMassiva = tramitacioMassiva;
	}
	public String getResponsable() {
		return responsable;
	}
	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}
	public Set<String> getResponsables() {
		return responsables;
	}
	public void setResponsables(Set<String> responsables) {
		this.responsables = responsables;
	}
	public boolean isOberta() {
		return oberta;
	}
	public void setOberta(boolean oberta) {
		this.oberta = oberta;
	}
	public boolean isCompletada() {
		return completada;
	}
	public void setCompletada(boolean completada) {
		this.completada = completada;
	}
	public boolean isCancelada() {
		return cancelada;
	}
	public void setCancelada(boolean cancelada) {
		this.cancelada = cancelada;
	}
	public boolean isSuspesa() {
		return suspesa;
	}
	public void setSuspesa(boolean suspesa) {
		this.suspesa = suspesa;
	}
	public List<String> getResultats() {
		return resultats;
	}
	public void setResultats(List<String> resultats) {
		this.resultats = resultats;
	}
	public boolean isAgafada() {
		return agafada;
	}
	public void setAgafada(boolean agafada) {
		this.agafada = agafada;
	}
	public Map<String, PersonaDto> getPersonesMap() {
		return personesMap;
	}
	public void setPersonesMap(Map<String, PersonaDto> personesMap) {
		this.personesMap = personesMap;
	}
	


	public String getTitolLimitat() {
		if (titol.length() > 100)
			return titol.substring(0, 100) + " (...)";
		else
			return titol;
	}



	public int compareTo(TascaLlistatDto aThat) {
	    if (this == aThat) return 0;
    	return this.getDataCreacio().compareTo(aThat.getDataCreacio());
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
		TascaLlistatDto other = (TascaLlistatDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
