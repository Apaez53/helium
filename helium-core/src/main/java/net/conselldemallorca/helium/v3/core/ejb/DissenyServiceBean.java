/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

/**
 * Servei que proporciona la funcionalitat de disseny d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DissenyServiceBean implements DissenyService {
	@Autowired
	DissenyService delegate;

	/**
	 * Retorna una llista amb els estats donats d'alta a dins un determinat tipus d'expedient.
	 * 
	 * @param expedientTipusId
	 * @return
	 * @throws ExpedientTipusNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EstatDto> findEstatByExpedientTipus(Long expedientTipusId) throws ExpedientTipusNotFoundException {
		return delegate.findEstatByExpedientTipus(expedientTipusId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de lectura.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(Long entornId) throws EntornNotFoundException {
		return delegate.findExpedientTipusAmbPermisReadUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de disseny.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(Long entornId) throws EntornNotFoundException {
		return delegate.findExpedientTipusAmbPermisDissenyUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de gestió.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(Long entornId) throws EntornNotFoundException {
		return delegate.findExpedientTipusAmbPermisGestioUsuariActual(entornId);
	}

	/**
	 * Retorna els tipus d'expedient per als quals l'usuari actual te permisos de creació.
	 * 
	 * @param entornId
	 * @return
	 * @throws EntornNotFoundException
	 */
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(Long entornId) throws EntornNotFoundException {
		return delegate.findExpedientTipusAmbPermisReadUsuariActual(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] getDeploymentResource(Long id, String recursForm) {
		return delegate.getDeploymentResource(id, recursForm);
	}

	public ExpedientTipusDto getExpedientTipusById(Long id) {
		return delegate.getExpedientTipusById(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto getById(Long id, boolean ambTascaInicial) {
		return delegate.getById(id, ambTascaInicial);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId, boolean ambTascaInicial) {
		return delegate.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId, ambTascaInicial);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientTipusDto> findExpedientTipusAmbEntorn(EntornDto entornId) {
		return delegate.findExpedientTipusAmbEntorn(entornId);
	}
}
