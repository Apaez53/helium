/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un documentStore que està emmagatzemat a dins la 
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentStoreRepository extends JpaRepository<DocumentStore, Long> {

	public List<DocumentStore> findByProcessInstanceId(String processInstanceId);

}
