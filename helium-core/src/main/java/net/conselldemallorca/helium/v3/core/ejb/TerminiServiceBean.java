package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class TerminiServiceBean implements TerminiService {
	
	@Autowired
	TerminiService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void pausar(Long terminiIniciatId, Date data) {
		delegate.pausar(terminiIniciatId, data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void continuar(Long terminiIniciatId, Date data) {
		delegate.continuar(terminiIniciatId, data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void cancelar(Long terminiIniciatId, Date data) {
		delegate.cancelar(terminiIniciatId, data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiIniciatDto> findIniciatsAmbProcessInstanceId(String processInstanceId) {
		return delegate.findIniciatsAmbProcessInstanceId(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TerminiIniciatDto iniciar(Long terminiId, Long expedientId, Date data, boolean esDataFi) {
		return delegate.iniciar(terminiId, expedientId, data, esDataFi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void modificar(Long terminiId, Long expedientId, Date inicio, int anys, int mesos, int dies, boolean equals) {
		delegate.modificar(terminiId, expedientId, inicio, anys, mesos, dies, equals);
	}
}
