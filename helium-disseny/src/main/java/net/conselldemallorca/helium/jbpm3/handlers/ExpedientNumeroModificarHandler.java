/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.handlers.ExpedientNumeroModificarHandlerInterface;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar el número d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientNumeroModificarHandler implements ActionHandler, ExpedientNumeroModificarHandlerInterface {

	public void execute(ExecutionContext executionContext) throws Exception {}

	public void setNumero(String numero) {}
	public void setVarNumero(String varNumero) {}

}
