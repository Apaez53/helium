package net.conselldemallorca.helium.webapp.v3.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Validador per al manteniment de tipus d'expedients:
 * - Comprova que el codi no estigui duplicat
 * - Si és amb info pròpia:
 * 		- Si és heretable no pot heretar
 * 		- Si hereta no pot ser heretable
 * 		- Si es desmarca heretable i té expedients tipus que hereden llavors no deixa desmarcar-ho.
 * - Si la opció de seqüència manual d'anys està activada:
 * 	- Comprova que no hi hagi valors nuls.
 * 	- Comprova que no hi hagi anys repetits.
 */
public class ExpedientTipusValidator implements ConstraintValidator<ExpedientTipus, ExpedientTipusCommand>{

	private String codiMissatge;
	@Autowired
	private ExpedientTipusService expedientTipusService;
	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(ExpedientTipus anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha un tipus d'expedient amb el mateix codi
		if (command.getCodi() != null) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			ExpedientTipusDto repetit = expedientTipusService.findAmbCodiPerValidarRepeticio(
					entornActual.getId(),
					command.getCodi());
			if(repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		// Si té informació pròpia llavors només pot heretar o ser heretable, però no les dues a la vegada
		if (command.isAmbInfoPropia()) {
			if (command.isHeretable() && command.getHeretatId() != null) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".heretable.i.heretat", null))
						.addNode("ambInfoPropia")
						.addConstraintViolation();	
				valid = false;
			}
		} else {
			// Si no té info pròpia llavors no pot heretar ni ser heretable
			if (command.isHeretable()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".heretable.sense.info.propia", null))
						.addNode("heretable")
						.addConstraintViolation();	
				valid = false;
			}
			if (command.getHeretatId() != null) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".heretat.sense.info.propia", null))
						.addNode("heretatId")
						.addConstraintViolation();	
				valid = false;
			}
		}
		// Si hi ha tipus que hereden llavors no es pot demarcar la opció d'heretable
		if (command.getId() != null && !command.isHeretable()) {
			List<ExpedientTipusDto> heretats = expedientTipusService.findHeretats(command.getId());
			if (heretats.size() > 0) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(
								this.codiMissatge + ".no.heretable.amb.heretats", 
								new Object[] {heretats.size()}))
						.addNode("heretable")
						.addConstraintViolation();	
				valid = false;
			}
		}
		// Si la opció de seqüència manual d'anys està activada:
		if (command.isReiniciarCadaAny()) {
			Set<Integer> anys = new HashSet<Integer>();
			// Comprova que no hi hagi anys nuls o mal formatats
			for (String anyStr : command.getSequenciesAny()) {
				try {
					Integer any = Integer.parseInt(anyStr);
					// Comprova que no hi hagi anys repetits.
					if (anys.contains(any)) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(this.codiMissatge + ".seq.any.repetit", null))
								.addNode("reiniciarCadaAny")
								.addConstraintViolation();	
						valid = false;
					} else {
						anys.add(any);
					}
				} catch (NumberFormatException ex) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage(this.codiMissatge + ".seq.any", null))
							.addNode("reiniciarCadaAny")
							.addConstraintViolation();	
					valid = false;
				}
			}
			// Comprova que no hi hagi valors nuls o mal formatats
			for (String valorStr : command.getSequenciesValor()) {
				try {
					Long.parseLong(valorStr);
				} catch (NumberFormatException ex) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage(this.codiMissatge + ".seq.valor", null))
							.addNode("reiniciarCadaAny")
							.addConstraintViolation();	
					valid = false;
				}
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();

		
		return valid;
	}

}
