/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.FormulariExternDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.ws.formext.IniciFormulari;
import net.conselldemallorca.helium.v3.core.ws.formext.ParellaCodiValor;
import net.conselldemallorca.helium.v3.core.ws.formext.RespostaIniciFormulari;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Helper per a accedir a la funcionalitat dels plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class FormulariExternHelper {

	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private TascaHelper tascaHelper;

	public FormulariExternDto iniciar(
			String taskId,
			Map<String, Object> valors) {
		try {
			ExpedientTipus expedientTipus = expedientTipusHelper.findAmbTaskId(
					taskId);
			String url;
			String username = null;
			String password = null;
			if (expedientTipus.getFormextUrl() != null) {
				url = expedientTipus.getFormextUrl();
				if (expedientTipus.getFormextUsuari() != null)
					username = expedientTipus.getFormextUsuari();
				if (expedientTipus.getFormextContrasenya() != null)
					password = expedientTipus.getFormextContrasenya();
			} else {
				url = GlobalProperties.getInstance().getProperty(
						"app.forms.service.url");
				username = GlobalProperties.getInstance().getProperty(
						"app.forms.service.username");
				password = GlobalProperties.getInstance().getProperty(
						"app.forms.service.password");
			}
			IniciFormulari service = getIniciFormulariService(
					url,
					username,
					password);
			List<ParellaCodiValor> varsFormext = new ArrayList<ParellaCodiValor>();
			for (String var: valors.keySet()) {
				varsFormext.add(
						new ParellaCodiValor(
								var,
								valors.get(var)));
			}
			Tasca tasca = tascaHelper.findTascaByJbpmTaskId(taskId);
			RespostaIniciFormulari resposta = service.iniciFormulari(
					tasca.getFormExtern(),
					taskId,
					varsFormext);
			FormulariExternDto dto = new FormulariExternDto();
			dto.setFormulariId(resposta.getFormulariId());
			dto.setUrl(resposta.getUrl());
			dto.setWidth(resposta.getWidth());
			dto.setHeight(resposta.getHeight());
			return dto;
		} catch (Exception ex) {
			logger.error("Error al iniciar formulariExtern", ex);
			throw new SistemaExternException("Error al iniciar formulariExtern", ex);
		}
	}



	private IniciFormulari getIniciFormulariService(
			String serviceUrl,
			String username,
			String password) throws Exception {
		Service service = Service.create(
				new URL(serviceUrl),
				new QName(
						"http://caib.es/emiserv/backoffice",
						"EmiservBackofficeService"));
		IniciFormulari port = (IniciFormulari)service.getPort(
				new QName(
						"http://caib.es/emiserv/backoffice",
						"EmiservBackofficePort"),
				IniciFormulari.class);
		if (username != null && !username.isEmpty()) {
			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		}
		return port;
	}

	private static final Log logger = LogFactory.getLog(FormulariExternHelper.class);

}