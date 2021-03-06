/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.core.extern.formulari.IniciFormulari;
import net.conselldemallorca.helium.core.extern.formulari.ParellaCodiValor;
import net.conselldemallorca.helium.core.extern.formulari.RespostaIniciFormulari;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FormulariExtern;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 * Dao pels objectes de tipus FormulariExtern
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class FormulariExternDao extends HibernateGenericDao<FormulariExtern, Long> {

	public FormulariExternDao() {
		super(FormulariExtern.class);
	}

	public List<FormulariExtern> findAmbTaskId(String taskId) {
		return findByCriteria(Restrictions.eq("taskId", taskId));
	}
	public FormulariExtern findAmbTaskIdActiu(String taskId) {
		List<FormulariExtern> formularis = findByCriteria(
				Restrictions.eq("taskId", taskId),
				Restrictions.isNull("dataFi"));
		if (formularis.size() > 0)
			return formularis.get(0);
		return null;
	}
	public FormulariExtern findAmbFormulariId(String formulariId) {
		List<FormulariExtern> formularis = findByCriteria(
				Restrictions.eq("formulariId", formulariId));
		if (formularis.size() > 0)
			return formularis.get(0);
		return null;
	}

	public FormulariExtern iniciarFormulariExtern(
			ExpedientTipus expedientTipus,
			String taskId,
			String codiFormulari,
			Map<String, Object> vars) {
		List<ParellaCodiValor> parelles = new ArrayList<ParellaCodiValor>();
		if (vars != null) {
			for (String key: vars.keySet()) {
				if (!key.startsWith(TascaService.VAR_PREFIX))
					parelles.add(new ParellaCodiValor(key, vars.get(key)));
			}
		}
		RespostaIniciFormulari resposta = iniciFormulariExtern(
				expedientTipus,
				codiFormulari,
				taskId,
				parelles);
		FormulariExtern fext = findAmbFormulariId(resposta.getFormulariId());
		if (fext == null) {
			fext = new FormulariExtern(
					taskId,
					resposta.getFormulariId(),
					resposta.getUrl());
			saveOrUpdate(fext);
		} else {
			fext.setUrl(resposta.getUrl());
			fext.setDataDarreraPeticio(new Date());
			if (resposta.getWidth() != -1)
				fext.setFormWidth(resposta.getWidth());
			if (resposta.getHeight() != -1)
				fext.setFormHeight(resposta.getHeight());
		}
		return fext;
	}



	private RespostaIniciFormulari iniciFormulariExtern(
			ExpedientTipus expedientTipus,
			String codiFormulari,
			String taskId,
			List<ParellaCodiValor> valors) {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IniciFormulari.class);
		if (expedientTipus.getFormextUrl() != null) {
			factory.setAddress(expedientTipus.getFormextUrl());
			if (expedientTipus.getFormextUsuari() != null)
				factory.setUsername(expedientTipus.getFormextUsuari());
			if (expedientTipus.getFormextContrasenya() != null)
				factory.setPassword(expedientTipus.getFormextContrasenya());
		} else {
			String url = GlobalProperties.getInstance().getProperty(
					"app.forms.service.url");
			factory.setAddress(url);
			String username = GlobalProperties.getInstance().getProperty(
					"app.forms.service.username");
			String password = GlobalProperties.getInstance().getProperty(
					"app.forms.service.password");
			if (username != null && !"".equals(username) && password != null && !"".equals(password)) {
				factory.setUsername(username);
				factory.setPassword(password);
			}
		}
		return ((IniciFormulari)factory.create()).iniciFormulari(codiFormulari, taskId, valors);
	}

}
