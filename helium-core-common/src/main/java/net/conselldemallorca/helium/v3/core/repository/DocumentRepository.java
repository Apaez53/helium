/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un document que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {

	@Query(	"select d from " +
			"    Document d " +
			"where " +
			"    d.definicioProces.id = :definicioProcesId " +
			"order by codi asc")
	List<Document> findByDefinicioProcesId(@Param("definicioProcesId") Long definicioProcesId);

	@Query(	"select d from " +
			"    Document d " +
			"where " +
			"    d.expedientTipus.id = :expedientTipusId " +
			"order by codi asc")
	List<Document> findByExpedientTipusId(@Param("expedientTipusId") Long expedientTipusId);

	
	@Query(	"select d from " +
			"    Document d " +
			"		inner join d.expedientTipus et with et.ambInfoPropia " +
			"		left  join et.expedientTipusPare etp " +
			"where " +
			"    d.expedientTipus.id = :expedientTipusId " +
			"    d.expedientTipus.id = etp.id " +
			"order by codi asc")
	List<Document> findByExpedientTipusAmbHerencia(@Param("expedientTipusId") Long expedientTipus);
	
	Document findByDefinicioProcesAndCodi(DefinicioProces definicioProces, String codi);
	
	Document findByExpedientTipusAndCodi(ExpedientTipus expedientTipus, String codi);
	
	@Query(	"select " +
			"    dt.document, " +
			"    dt.required, " +
			"    dt.readOnly, " +
			"    dt.order " +
			"from " +
			"    DocumentTasca dt " +
			"where " +
			"    dt.tasca.definicioProces.id=:definicioProcesId " +
			"and dt.tasca.jbpmName=:jbpmName " +
			"order by " +
			"    dt.order")
	public List<Object[]> findAmbDefinicioProcesITascaJbpmNameOrdenats(
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("jbpmName") String jbpmName);
	
	
	@Query(	"from Document d " +
			"where " +
			"	d.id not in (:exclude) " +
			"   and (d.expedientTipus.id = :expedientTipusId or d.expedientTipus.id = :expedientTipusPareId or d.expedientTipus.id is null) " +
			"   and (d.definicioProces.id = :definicioProcesId or d.definicioProces.id is null) " +
			"	and (:esNullFiltre = true or lower(d.codi) like lower('%'||:filtre||'%') or lower(d.nom) like lower('%'||:filtre||'%')) ")
	public Page<Document> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("expedientTipusPareId") Long expedientTipusPareId,
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			@Param("exclude") Set<Long> exclude, 
			Pageable pageable);
			
	@Query( "select ds " +
			"from Document d " +
			"	join d.expedientTipus et with et.id = :expedientTipusId, " +
			"	Document ds " +
			"where " +
			"	ds.codi = d.codi " +
			" 	and ds.expedientTipus.id = et.expedientTipusPare.id ")
	public List<Document> findSobreescrits(@Param("expedientTipusId") Long expedientTipusId);
}
