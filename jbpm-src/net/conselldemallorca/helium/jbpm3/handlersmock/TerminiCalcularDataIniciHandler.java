/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlersmock;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per calcular la data d'inici d'un termini.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiCalcularDataIniciHandler implements ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setTerminiCodi(String terminiCodi) {}
	public void setVarTerminiCodi(String varTerminiCodi) {}
	public void setVarData(String varData) {}
	public void setSumarUnDia(String sumarUnDia) {}
	public void setVarTermini(String varTermini) {}
	public void setVarDataInici(String varDataInici) {}

}
