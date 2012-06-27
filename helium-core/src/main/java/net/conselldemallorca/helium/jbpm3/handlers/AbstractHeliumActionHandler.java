/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.conselldemallorca.helium.core.model.dao.DaoProxy;
import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dao.PluginRegistreDao;
import net.conselldemallorca.helium.core.model.dto.ArxiuDto;
import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.service.AlertaService;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.model.service.DocumentService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.model.service.ServiceProxy;
import net.conselldemallorca.helium.core.model.service.TerminiService;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentInfo;

import org.jbpm.JbpmException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler base amb accés a la funcionalitat de Helium
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
abstract class AbstractHeliumActionHandler implements ActionHandler {

	public abstract void execute(ExecutionContext executionContext)
			throws Exception;

	ExpedientDto getExpedient(ExecutionContext executionContext) {
		return getExpedientService().findExpedientAmbProcessInstanceId(
				getProcessInstanceId(executionContext));
	}

	DefinicioProcesDto getDefinicioProces(ExecutionContext executionContext) {
		return getDissenyService().findDefinicioProcesAmbProcessInstanceId(
				getProcessInstanceId(executionContext));
	}

	Estat getEstatPerExpedientTipusICodi(Long expedientTipusId, String codi) {
		return ServiceProxy.getInstance().getDissenyService()
				.findEstatAmbExpedientTipusICodi(expedientTipusId, codi);
	}

	PersonaDto getPersonaAmbCodi(String codi) {
		return ServiceProxy.getInstance().getPluginService()
				.findPersonaAmbCodi(codi);
	}

	Termini getTerminiAmbCodi(ExecutionContext executionContext, String codi) {
		return getDissenyService().findTerminiAmbDefinicioProcesICodi(
				getDefinicioProces(executionContext).getId(), codi);
	}

	TerminiIniciat getTerminiIniciatAmbCodi(ExecutionContext executionContext,
			String codi) {
		Termini termini = getTerminiAmbCodi(executionContext, codi);
		if (termini != null)
			return getTerminiService()
					.findIniciatAmbTerminiIdIProcessInstanceId(termini.getId(),
							getProcessInstanceId(executionContext));
		return null;
	}

	DocumentInfo getDocumentInfo(ExecutionContext executionContext,
			String documentCodi) {
		String varCodi = DocumentHelper.PREFIX_VAR_DOCUMENT + documentCodi;
		Object valor = executionContext.getVariable(varCodi);
		if (valor == null)
			return null;
		if (valor instanceof Long) {
			Long id = (Long) valor;
			DocumentDto document = getDocumentService().documentInfo(id);
			if (document == null)
				return null;
			DocumentInfo resposta = new DocumentInfo();
			resposta.setId(id);
			if (document.isAdjunt()) {
				resposta.setTitol(document.getAdjuntTitol());
			} else {
				resposta.setTitol(document.getDocumentNom());
			}
			resposta.setDataCreacio(document.getDataCreacio());
			resposta.setDataDocument(document.getDataDocument());
			resposta.setSignat(document.isSignat());
			if (document.isRegistrat()) {
				resposta.setRegistrat(true);
				resposta.setRegistreNumero(document.getRegistreNumero());
				resposta.setRegistreData(document.getRegistreData());
				resposta.setRegistreOficinaCodi(document.getRegistreOficinaCodi());
				resposta.setRegistreOficinaNom(document.getRegistreOficinaNom());
				resposta.setRegistreEntrada(document.isRegistreEntrada());
			}
			ArxiuDto arxiu = getDocumentService().arxiuDocumentPerMostrar(id);
			resposta.setArxiuNom(arxiu.getNom());
			resposta.setArxiuContingut(arxiu.getContingut());
			return resposta;
		} else {
			throw new JbpmException("La referencia al document \""
					+ documentCodi + "\" no es del tipus correcte");
		}
	}

	ExpedientService getExpedientService() {
		return ServiceProxy.getInstance().getExpedientService();
	}

	DocumentService getDocumentService() {
		return ServiceProxy.getInstance().getDocumentService();
	}

	PluginService getPluginService() {
		return ServiceProxy.getInstance().getPluginService();
	}

	DissenyService getDissenyService() {
		return ServiceProxy.getInstance().getDissenyService();
	}

	TerminiService getTerminiService() {
		return ServiceProxy.getInstance().getTerminiService();
	}

	AlertaService getAlertaService() {
		return ServiceProxy.getInstance().getAlertaService();
	}

	PluginRegistreDao getPluginRegistreDao() {
		return DaoProxy.getInstance().getPluginRegistreDao();
	}

	DocumentStoreDao getDocumentStoreDao() {
		return DaoProxy.getInstance().getDocumentStoreDao();
	}

	protected String getProcessInstanceId(ExecutionContext executionContext) {
		return new Long(executionContext.getProcessInstance().getId())
				.toString();
	}

	protected Date getVariableComData(ExecutionContext executionContext,
			String var) {
		Object obj = executionContext.getVariable(var);
		if (obj instanceof Date)
			return (Date) obj;
		throw new JbpmException("La variable amb el codi '" + var
				+ "' no és de tipus Date");
	}

	protected Object getValorOVariable(ExecutionContext executionContext,
			Object value, String var) {
		if (value != null)
			return value;
		if (var != null && var.length() > 0)
			return executionContext.getVariable(var);
		return null;
	}

	protected Date getValorOVariableData(ExecutionContext executionContext,
			Object value, String var) {
		if (value != null) {
			if (value instanceof Date) {
				return (Date) value;
			} else {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					return sdf.parse(value.toString());
				} catch (Exception ignored) {
				}
			}
		}
		if (var != null && var.length() > 0) {
			Object valor = executionContext.getVariable(var);
			if (valor != null) {
				if (valor instanceof Date) {
					return (Date) valor;
				} else {
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						return sdf.parse(valor.toString());
					} catch (Exception ignored) {
					}
				}
			}
		}
		return null;
	}

	protected Integer getValorOVariableInteger(
			ExecutionContext executionContext, Object value, String var) {
		if (value != null) {
			if (value instanceof Integer) {
				return (Integer) value;
			} else {
				return new Integer(value.toString());
			}
		}
		if (var != null && var.length() > 0) {
			Object valor = executionContext.getVariable(var);
			if (valor != null) {
				if (valor instanceof Integer) {
					return (Integer) valor;
				} else {
					return new Integer(valor.toString());
				}
			}
		}
		return null;
	}

}
