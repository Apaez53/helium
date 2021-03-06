/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.jsp.jstl.core.LoopTagStatus;

import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ValidacioDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springmodules.validation.bean.BeanValidator;
import org.springmodules.validation.bean.conf.DefaultBeanValidationConfiguration;
import org.springmodules.validation.bean.conf.loader.SimpleBeanValidationConfigurationLoader;
import org.springmodules.validation.bean.rule.ExpressionValidationRule;
import org.springmodules.validation.util.cel.valang.ValangConditionExpressionParser;

/**
 * Validador per als formularis de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaFormValidatorHelper implements Validator {
	private static final int STRING_MAX_LENGTH = 2048;
	@Resource(name = "tascaServiceV3")
	private TascaService tascaService;
	@Resource(name = "expedientServiceV3")
	private ExpedientService expedientService;

	private List<TascaDadaDto> tascaDades;
	boolean inicial;
	boolean validarObligatoris;
	boolean validarExpresions;



	public TascaFormValidatorHelper(
			TascaService tascaService,
			List<TascaDadaDto> tascaDades) {
		this.tascaService = tascaService;
		this.tascaDades = tascaDades;
		this.inicial = false;
		// Si validam els obligatoris també validarem les expresions
		this.validarObligatoris = false;
		this.validarExpresions = validarObligatoris;
	}

	public TascaFormValidatorHelper(
			ExpedientService expedientService,
			List<TascaDadaDto> tascaDades) {
		this.expedientService = expedientService;
		this.tascaDades = tascaDades;
		this.inicial = true;
		this.validarObligatoris = false;
		this.validarExpresions = true;
	}

	public void setValidarObligatoris(boolean validarObligatoris) {
		this.validarObligatoris = validarObligatoris;
	}
	public void setValidarExpresions(boolean validarExpresions) {
		this.validarExpresions = validarExpresions;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(Object.class);
	}

	public void validate(Object command, Errors errors) {
		try {
			List<TascaDadaDto> tascaDades = getTascaDades(command);
			for (TascaDadaDto camp : tascaDades) {
				if (validarObligatoris && camp.isRequired()) {
					if (camp.getCampTipus().equals(CampTipusDto.REGISTRE)) {
						Object valorRegistre = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valorRegistre == null || registreEmpty(camp, valorRegistre, errors))
							errors.rejectValue(camp.getVarCodi(), "not.blank");
						else {
							Object[] registres = null;
							List<TascaDadaDto> registreDades = null;
							if (valorRegistre != null) {
								if (camp.isCampMultiple()) {
									registreDades = camp.getMultipleDades().get(0).getRegistreDades();
									registres = (Object[])valorRegistre;
								} else {
									registreDades = camp.getRegistreDades();
									registres = new Object[] {valorRegistre};
								}
								int i = 0;
								for (Object reg: registres) {
									boolean emptyReg = true;
									for (TascaDadaDto campRegistre : registreDades) {
										if (campRegistre.isRequired()) {
											boolean emptyVal = true;
											Object oValor = PropertyUtils.getProperty(reg, campRegistre.getVarCodi());
											if (oValor != null) {
												if (oValor instanceof String[]) {
													String[] oValor_arr = (String[])oValor;
													emptyVal = (oValor_arr.length < 3 || (oValor_arr[0].equalsIgnoreCase("0") && oValor_arr[1].equalsIgnoreCase("0") && (oValor_arr[2].equalsIgnoreCase("0") || oValor_arr[2] == null)));
												} else if (oValor instanceof String && "".equals(oValor)) {
													emptyVal = true;
												} else {
													emptyVal = false;
												}
											}
											if (emptyVal) {
												if (campRegistre.isRequired()) {
													errors.rejectValue(camp.getVarCodi() + (camp.isCampMultiple() ? "[" + i + "]." : ".") + campRegistre.getVarCodi(), "not.blank");
												}
											} else {
												emptyReg = false;
											}
										} else {
											emptyReg = false;
										}
									}
									if (emptyReg && (i > 0 || camp.isRequired()))
										errors.rejectValue(camp.getVarCodi() + (camp.isCampMultiple() ? "[" + i + "]" : ""), "fila.not.blank");
									i++;
								}
							}
						}
					} else if (!camp.isCampMultiple()) {
						if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
							Object termini = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
							String[] termini_arr = (String[])termini;
							if (termini == null || termini_arr.length < 3 || (termini_arr[0].equalsIgnoreCase("0") && termini_arr[1].equalsIgnoreCase("0") && (termini_arr[2].equalsIgnoreCase("") || termini_arr[2] == null)))
								errors.rejectValue(camp.getVarCodi(), "not.blank");
						} else {
							ValidationUtils.rejectIfEmpty(errors, camp.getVarCodi(), "not.blank");
						}
					} else {
						Object valors = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valors == null) {
							errors.rejectValue(camp.getVarCodi(), "not.blank");
						} else {
							for (int i = 0; i < Array.getLength(valors); i++) {
								Object valor = Array.get(valors, i);
								if (camp.getCampTipus().equals(CampTipusDto.TERMINI)) {
									String[] valor_arr = (String[])valor;
									if (valor == null || (valor_arr).length < 3 || (valor_arr[0].equalsIgnoreCase("0") && valor_arr[1].equalsIgnoreCase("0") && (valor_arr[2].equalsIgnoreCase("0") || valor_arr[2] == null)))
										errors.rejectValue(camp.getVarCodi() + "[" + i + "]", "not.blank");
								} else {
									if ((valor instanceof String && "".equals(valor)) || (!(valor instanceof String) && valor == null)) {
										errors.rejectValue(camp.getVarCodi() + "[" + i + "]", "not.blank");
									}
								}
							}
						}
					}
				}
				comprovaCamp(camp, command, errors);
			}
			// Només valida amb expressions si no hi ha errors previs
			if (validarExpresions && !errors.hasErrors()) {
				getValidatorPerExpressions(tascaDades, command).validate(
						getCommandPerValidadorExpressions(command),
						errors);
			}
			logger.debug(errors.toString());
		} catch (Exception ex) {
			logger.error("Error en el validator", ex);
			errors.reject("error.validator");
		}
		logger.debug("Errors de validació en el formulari de la tasca: " + errors.toString());
	}

	public static String getErrorField(Errors errors, TascaDadaDto dada, LoopTagStatus index) {
		try {
			FieldError fieldError = errors.getFieldError(dada.getVarCodi() + "[" + index.getIndex()  + "]");
			return fieldError.getCode();
		} catch (Exception ex) {
			return null;
		}
	}
	private static boolean registreEmpty(TascaDadaDto camp, Object registre, Errors errors) throws Exception {
		boolean empty = true;
		// Registre
		if (registre != null) {
			if  (camp.isCampMultiple()) {
				List<TascaDadaDto> registreDades = camp.getMultipleDades().get(0).getRegistreDades();
				Object[] registres = (Object[])registre;
				for (Object reg: registres) {
					for (TascaDadaDto campRegistre : registreDades) {
						Object oValor = PropertyUtils.getProperty(reg, campRegistre.getVarCodi());
						if (oValor != null) {
							if (oValor instanceof String[]) {
								String[] oValor_arr = (String[])oValor;
								empty = (oValor_arr.length < 3 || (oValor_arr[0].equalsIgnoreCase("0") && oValor_arr[1].equalsIgnoreCase("0") && (oValor_arr[2].equalsIgnoreCase("0") || oValor_arr[2] == null)));
							} else {
								empty = false;
							}
							if (!empty) return false;
						}
					}
				}
			} else {
				List<TascaDadaDto> registreDades = camp.getRegistreDades();
				for (TascaDadaDto campRegistre : registreDades) {
					Object oValor = PropertyUtils.getProperty(registre, campRegistre.getVarCodi());
					if (oValor != null) {
						if (oValor instanceof String[]) {
							String[] oValor_arr = (String[])oValor;
							empty = (oValor_arr.length < 3 || (oValor_arr[0].equalsIgnoreCase("0") && oValor_arr[1].equalsIgnoreCase("0") && (oValor_arr[2].equalsIgnoreCase("0") || oValor_arr[2] == null)));
						} else {
							empty = false;
						}
						if (!empty) break;
					}
				}
			}
		}
		return empty;
	}

	private void comprovaCamp(TascaDadaDto camp, Object command, Errors errors) throws Exception {
		if (camp != null && camp.getCampTipus() != null) {
			if (camp.getCampTipus().equals(CampTipusDto.STRING)) {
				try {
					if (camp.isCampMultiple()) {
						String[] valors = (String[]) PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valors != null)
							for (String valor : valors) {
								if (valor != null && valor.length() > STRING_MAX_LENGTH)
									errors.rejectValue(camp.getVarCodi(), "max.length");
							}
					} else {
						String valor = (String) PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
						if (valor != null && valor.length() > STRING_MAX_LENGTH)
							errors.rejectValue(camp.getVarCodi(), "max.length");
					}
				} catch (NoSuchMethodException ex) {
					logger.error("No s'ha pogut trobar la propietat '" + camp.getVarCodi() + "' con campId " + camp.getCampId());
				}
			} else if (camp.getCampTipus().equals(CampTipusDto.DATE) && camp.getText() != null && !camp.getText().isEmpty()) {
				try {
					PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
					String valor = camp.getText(); 
					if (valor != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						sdf.setLenient(false);
						sdf.parse(valor);
					}
				} catch (NoSuchMethodException ex) {
					logger.error("No s'ha pogut trobar la propietat '" + camp.getVarCodi() + "' con campId " + camp.getCampId());
				} catch (ParseException  ex) {
					errors.rejectValue(camp.getVarCodi(), "error.camp.dada.valida");
				}
			}
		}
	}

	private List<TascaDadaDto> getTascaDades(Object command) throws Exception {
		if (tascaDades != null) {
			return tascaDades;
		} else {
			String id = (String)PropertyUtils.getSimpleProperty(command, "id");
			return tascaService.findDades(id);
		}
	}

	private Validator getValidatorPerExpressions(
			List<TascaDadaDto> tascaDadas,
			Object command) {
		SimpleBeanValidationConfigurationLoader validationConfigurationLoader = new SimpleBeanValidationConfigurationLoader();
		DefaultBeanValidationConfiguration beanValidationConfiguration = new DefaultBeanValidationConfiguration();
		for (TascaDadaDto camp: tascaDadas) {
			if (camp.getValidacions() != null) {
				for (ValidacioDto validacio: camp.getValidacions()) {
					if (camp.isCampMultiple()) {
						try {
							Object valors = PropertyUtils.getSimpleProperty(command, camp.getVarCodi());
							if (valors != null) {
								String expressio = validacio.getExpressio();
								if (expressio.indexOf("sum(" + camp.getVarCodi() + ")") != -1) {
									if (camp.getCampTipus().equals(CampTipusDto.INTEGER)) {
										Long suma = 0L;
										for (Long valor: (Long[])valors) {
											suma += (valor == null ? 0L : valor); 
										}
										expressio = expressio.replace("sum(" + camp.getVarCodi() + ")", suma.toString());
									} else if (camp.getCampTipus().equals(CampTipusDto.FLOAT)) {
										Double suma = 0.0;
										for (Double valor: (Double[])valors) {
											suma += (valor == null ? 0.0 : valor); 
										}
										expressio = expressio.replace("sum(" + camp.getVarCodi() + ")", suma.toString());
									} else if (camp.getCampTipus().equals(CampTipusDto.PRICE)) {
										BigDecimal suma = new BigDecimal(0);
										for (BigDecimal valor: (BigDecimal[])valors) {
											if (valor == null) valor = new BigDecimal(0);
											suma = suma.add(valor);
										}
										expressio = expressio.replace("sum(" + camp.getVarCodi() + ")", suma.toString());
									}
									afegirExpressioValidacio(
											camp.getVarCodi(),
											expressio,
											camp.getCampEtiqueta() + ": " + validacio.getMissatge(),
											"error.camp." + camp.getVarCodi(),
											beanValidationConfiguration);
								} else {
									for (int i = 0; i < Array.getLength(valors); i++) {
										String expressioFill = expressio.replaceAll(camp.getVarCodi() + "[^\\[]" , camp.getVarCodi() + "[" + i + "]");
										afegirExpressioValidacio(
												camp.getVarCodi() + "[" + i + "]",
												expressioFill,
												camp.getCampEtiqueta() + ": " + validacio.getMissatge(),
												"error.camp." + camp.getVarCodi(),
												beanValidationConfiguration);
									}
								}
							}
						} catch (Exception ex) {
							logger.error("No s'ha pogut generar la validació de l'expressió definida per a la variable '" + camp.getVarCodi() + "' amb campId " + camp.getCampId());
						}
					} else {
						afegirExpressioValidacio(
								camp.getVarCodi(),
								validacio.getExpressio(),
								validacio.getMissatge(),
								"error.camp." + camp.getVarCodi(),
								beanValidationConfiguration);
					}
				}
			}
		}
		validationConfigurationLoader.setClassValidation(
				Object.class,
				beanValidationConfiguration);
		return new BeanValidator(validationConfigurationLoader);
	}

	private void afegirExpressioValidacio(
			String varCodi,
			String validacioExpressio,
			String validacioMissatge,
			String errorCodi,
			DefaultBeanValidationConfiguration beanValidationConfiguration) {
		ExpressionValidationRule validationRule = new ExpressionValidationRule(
				new ValangConditionExpressionParser(),
				validacioExpressio);
		logger.debug("Afegint expressió VALANG al validador (" +
				"camp=" + varCodi + ", " +
				"expressió=" + validacioExpressio + ", " +
				"missatge=" + validacioMissatge + ")");
		validationRule.setDefaultErrorMessage(validacioMissatge);
		validationRule.setErrorCode(errorCodi);
		beanValidationConfiguration.addPropertyRule(
				varCodi,
				validationRule);
	}

	private Object getCommandPerValidadorExpressions(
			Object commandOriginal) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// Crea una còpia del command amb els strings que sigin NULL amb valor buit
		// per evitar excepcions en les expressions VALANG
		BeanGenerator bg = new BeanGenerator();
		for (PropertyDescriptor descriptor: PropertyUtils.getPropertyDescriptors(commandOriginal)) {
			if (!"class".equals(descriptor.getName())) {
				bg.addProperty(descriptor.getName(), descriptor.getPropertyType());
			}
		}
		Object command = bg.create();
		for (PropertyDescriptor descriptor: PropertyUtils.getPropertyDescriptors(commandOriginal)) {
			if (!"class".equals(descriptor.getName())) {
				Object valor = PropertyUtils.getProperty(commandOriginal, descriptor.getName());
				if (String.class.equals(descriptor.getPropertyType()) && valor == null)
					valor = "";
				PropertyUtils.setProperty(
						command,
						descriptor.getName(),
						valor);
			}
		}
		return command;
	}

	private static final Log logger = LogFactory.getLog(TascaFormValidatorHelper.class);

}
