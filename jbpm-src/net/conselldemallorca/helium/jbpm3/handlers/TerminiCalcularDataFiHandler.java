/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Calendar;
import java.util.Date;

import net.conselldemallorca.helium.model.hibernate.Termini;

import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per calcular la data de fi d'un termini.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiCalcularDataFiHandler extends AbstractHeliumActionHandler {

	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;
	private String sumarUnDia;
	private String varTermini;
	private String varDataFi;



	public void execute(ExecutionContext executionContext) throws Exception {
		Termini termini = getTerminiAmbCodi(
				executionContext,
				(String)getValorOVariable(executionContext, terminiCodi, varTerminiCodi));
		if (termini != null) {
			Date dataFi;
			if (varTermini != null) {
				Object valorTermini = executionContext.getVariable(varTermini);
				if (valorTermini == null)
					throw new JbpmException("No s'ha pogut llegir el termini de la variable '" + varTermini + "'");
				net.conselldemallorca.helium.jbpm3.integracio.Termini vt = (net.conselldemallorca.helium.jbpm3.integracio.Termini)valorTermini;
				if (varData != null)
					dataFi = getTerminiService().getDataFiTermini(
							getDataVariable(executionContext),
							vt.getAnys(),
							vt.getMesos(),
							vt.getDies(),
							termini.isLaborable());
				else
					dataFi = getTerminiService().getDataFiTermini(
							new Date(),
							vt.getAnys(),
							vt.getMesos(),
							vt.getDies(),
							termini.isLaborable());
			} else {
				if (varData != null)
					dataFi = getTerminiService().getDataFiTermini(
							getDataVariable(executionContext),
							termini.getAnys(),
							termini.getMesos(),
							termini.getDies(),
							termini.isLaborable());
				else
					dataFi = getTerminiService().getDataFiTermini(
							new Date(),
							termini.getAnys(),
							termini.getMesos(),
							termini.getDies(),
							termini.isLaborable());
			}
			executionContext.setVariable(varDataFi, dataFi);
		} else {
			throw new JbpmException("No existeix cap termini amb aquest codi '" + terminiCodi + "'");
		}
	}

	public void setTerminiCodi(String terminiCodi) {
		this.terminiCodi = terminiCodi;
	}
	public void setVarTerminiCodi(String varTerminiCodi) {
		this.varTerminiCodi = varTerminiCodi;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public void setSumarUnDia(String sumarUnDia) {
		this.sumarUnDia = sumarUnDia;
	}
	public void setVarTermini(String varTermini) {
		this.varTermini = varTermini;
	}
	public void setVarDataFi(String varDataFi) {
		this.varDataFi = varDataFi;
	}



	private Date getDataVariable(ExecutionContext executionContext) {
		Date data = getVariableComData(executionContext, varData);
		if (sumarUnDia != null && sumarUnDia.length() > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return cal.getTime();
		} else {
			return data;
		}
	}

}
