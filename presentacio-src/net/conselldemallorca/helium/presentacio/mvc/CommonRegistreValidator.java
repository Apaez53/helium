/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.model.service.DissenyService;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validador per als formularis dels registres
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CommonRegistreValidator implements Validator {
	private DissenyService dissenyService;
	public CommonRegistreValidator(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(Object.class);
	}
	public void validate(Object command, Errors errors) {
		try {
			Long registreId = (Long)PropertyUtils.getSimpleProperty(command, "registreId");
			Camp camp = dissenyService.getCampById(registreId);
			for (CampRegistre campRegistre: camp.getRegistreMembres()) {
				if (campRegistre.isObligatori())
					ValidationUtils.rejectIfEmpty(errors, campRegistre.getMembre().getCodi(), "not.blank");
			}
		} catch (Exception ex) {
			errors.reject("error.validator");
		}
	}

}
