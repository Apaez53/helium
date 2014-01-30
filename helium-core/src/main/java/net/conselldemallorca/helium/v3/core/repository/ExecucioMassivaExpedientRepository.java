/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Dao pels objectes del tipus ExecucioMassivaExpedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaExpedientRepository extends JpaRepository<ExecucioMassivaExpedient, Long> {

	@Query("select e " +
			"from	ExecucioMassivaExpedient e " +
			"where 	e.expedient.id = :expedientId ")
	public List<ExecucioMassivaExpedient> getExecucioMassivaByExpedient(
			@Param("expedientId") Long expedientId
	);	
}