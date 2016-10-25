package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

public interface AccioRepository extends JpaRepository<Accio, Long> {

	@Query("select a "
			+ "from Accio a "
			+ "where a.definicioProces = :definicioProces "
			+ "and a.oculta = false "
			+ "order by a.nom")
	public List<Accio> findAmbDefinicioProcesAndOcultaFalse(
			@Param("definicioProces") DefinicioProces definicioProces);

	@Query("select a "
			+ "from Accio a "
			+ "where a.expedientTipus = :expedientTipus "
			+ "and a.oculta = false "
			+ "order by a.nom")
	public List<Accio> findAmbExpedientTipusAndOcultaFalse(
			@Param("expedientTipus") ExpedientTipus expedientTipus);
		
	Accio findByDefinicioProcesIdAndCodi(Long definicioProcesId, String codi);

	Accio findByExpedientTipusIdAndCodi(Long expedientTipusId, String codi);
	
	@Query(	"from Accio a " +
			"where " +
			"   (a.expedientTipus.id = :expedientTipusId or a.expedientTipus.id is null) " +
			"   and (a.definicioProces.id = :definicioProcesId or a.definicioProces.id is null) " +
			"	and (:esNullFiltre = true or lower(a.codi) like lower('%'||:filtre||'%') or lower(a.nom) like lower('%'||:filtre||'%')) ")
	Page<Accio> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("definicioProcesId") Long definicioProcesId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	@Query("select a "
			+ "from Accio a "
			+ "where a.definicioProces.id = :definicioProcesId "
			+ "order by a.codi")
	public List<Accio> findAmbDefinicioProces(@Param("definicioProcesId")Long definicioProcesId);	
	
	@Query("select a "
			+ "from Accio a "
			+ "where a.expedientTipus.id = :expedientTipusId "
			+ "order by a.codi")
	public List<Accio> findAmbExpedientTipus(@Param("expedientTipusId")Long expedientTipusId);	

}
