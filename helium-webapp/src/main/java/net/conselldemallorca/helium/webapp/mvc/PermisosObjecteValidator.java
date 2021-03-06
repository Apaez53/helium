/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validador per gestionar permisos per als objectes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosObjecteValidator {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(PermisosObjecteCommand.class);
	}

	public void validate(Object command, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "nom", "not.blank");
		ValidationUtils.rejectIfEmpty(errors, "permisos", "not.blank");
	}

}
