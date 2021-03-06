/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per copiar un swimlane del procés pare cap al
 * procés fill.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SwimlaneCopiarPareHandlerInterface extends ActionHandler {

	public void execute(ExecutionContext executionContext) throws Exception;

	public void setSwimlaneNom(String swimlaneNom);

}
