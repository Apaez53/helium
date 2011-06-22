/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a reprendre la tramitació d'un expedient aturat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientReprendreHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

}
