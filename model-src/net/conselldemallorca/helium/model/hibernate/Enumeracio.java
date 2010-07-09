/**
 * 
 */
package net.conselldemallorca.helium.model.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import net.conselldemallorca.helium.integracio.domini.ParellaCodiValor;

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una enumeració.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Entity
@Table(	name="hel_enumeracio",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "entorn_id"})})
public class Enumeracio implements Serializable {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@MaxLength(1024)
	private String valors;

	@NotNull
	private Entorn entorn;
	private Set<Camp> camps = new HashSet<Camp>();



	public Enumeracio() {}
	public Enumeracio(Entorn entorn, String codi, String nom) {
		this.entorn = entorn;
		this.codi = codi;
		this.nom = nom;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_enumeracio")
	@TableGenerator(name="gen_enumeracio", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=64, nullable=false)
	public String getCodi() {
		return this.codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@Column(name="nom", length=255, nullable=false)
	public String getNom() {
		return this.nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	@Column(name="valors", length=1024)
	public String getValors() {
		return valors;
	}
	public void setValors(String valors) {
		this.valors = valors;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="entorn_id")
	@ForeignKey(name="hel_entorn_enumeracio_fk")
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@OneToMany(mappedBy="enumeracio", cascade={CascadeType.ALL})
	public Set<Camp> getCamps() {
		return this.camps;
	}
	public void setCamps(Set<Camp> camps) {
		this.camps = camps;
	}
	public void addCamp(Camp camp) {
		getCamps().add(camp);
	}
	public void removeCamp(Camp camp) {
		getCamps().remove(camp);
	}

	@Transient
	public List<ParellaCodiValor> getLlistaValors() {
		List<ParellaCodiValor> resposta = new ArrayList<ParellaCodiValor>();
		if (valors != null) {
			String[] parelles = valors.split(",");
			for (int i = 0; i < parelles.length; i++) {
				String[] parts = parelles[i].split(":");
				if (parts.length == 2)
					resposta.add(new ParellaCodiValor(parts[0], parts[1]));
			}
		}
		return resposta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((entorn == null) ? 0 : entorn.hashCode());
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
		Enumeracio other = (Enumeracio) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (entorn == null) {
			if (other.entorn != null)
				return false;
		} else if (!entorn.equals(other.entorn))
			return false;
		return true;
	}

	private static final long serialVersionUID = -4869633305652583392L;

}
