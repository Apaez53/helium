/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaDefaultAny;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaAnyDto;
import net.conselldemallorca.helium.v3.core.api.dto.SequenciaDefaultAnyDto;

import org.springframework.stereotype.Component;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelper {

	private MapperFactory mapperFactory;

	public ConversioTipusHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<Document, DocumentDto>() {
					@Override
					public DocumentDto convert(Document source, Type<? extends DocumentDto> destinationClass) {
						DocumentDto target = new DocumentDto();
						target.setId(source.getId());
						target.setCodi(source.getCodi());
						target.setArxiuNom(source.getArxiuNom());
						target.setDocumentNom(source.getNom());
						target.setPlantilla(source.isPlantilla());
						return target;
					}
		});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<Camp, CampDto>() {
					@Override
					public CampDto convert(Camp source, Type<? extends CampDto> destinationClass) {
						CampDto target = new CampDto();
						target.setId(source.getId());
						target.setCodi(source.getCodi());
						target.setEtiqueta(source.getEtiqueta());
						target.setObservacions(source.getObservacions());
						target.setTipus(
								CampTipusDto.valueOf(
										source.getTipus().toString()));						
						return target;
					}
		});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<CampTasca, CampTascaDto>() {
					public CampTascaDto convert(CampTasca source, Type<? extends CampTascaDto> destinationClass) {
						CampTascaDto target = new CampTascaDto();
						target.setId(source.getId());
						target.setReadFrom(source.isReadFrom());
						target.setWriteTo(source.isWriteTo());
						target.setRequired(source.isRequired());
						target.setReadOnly(source.isReadOnly());
						target.setOrder(source.getOrder());
						if (source.getCamp() != null) {
							target.setCamp(convertir(source.getCamp(), CampDto.class));
						}
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<Consulta, ConsultaDto>() {
					@Override
					public ConsultaDto convert(Consulta source, Type<? extends ConsultaDto> destinationType) {
						ConsultaDto target = new ConsultaDto();
						target.setCodi(source.getCodi());
						target.setDescripcio(source.getDescripcio());
						target.setExpedientTipus(convertir(source.getExpedientTipus(), ExpedientTipusDto.class));
						target.setExportarActiu(source.isExportarActiu());
						target.setFormatExport(source.getFormatExport());
						target.setGenerica(source.isGenerica());
						target.setId(source.getId());
						target.setInformeContingut(source.getInformeContingut());
						target.setInformeNom(source.getInformeNom());
						target.setNom(source.getNom());
						target.setOcultarActiu(source.isOcultarActiu());
						target.setOrdre(source.getOrdre());
						target.setValorsPredefinits(source.getValorsPredefinits());
						return target;
					}				
			});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientTipus, ExpedientTipusDto>() {
					@Override
					public ExpedientTipusDto convert(ExpedientTipus source, Type<? extends ExpedientTipusDto> destinationType) {
						ExpedientTipusDto target = new ExpedientTipusDto();
						target.setAnyActual(source.getAnyActual());
						target.setCodi(source.getCodi());
						target.setDemanaNumero(source.getDemanaNumero());
						target.setDemanaTitol(source.getDemanaTitol());
						target.setEntorn(convertir(source.getEntorn(), EntornDto.class));
						if (source.getEstats() != null)
							target.setEstats(convertirList(source.getEstats(), EstatDto.class));
						target.setExpressioNumero(source.getExpressioNumero());
						target.setId(source.getId());
						for (Consulta consulta : source.getConsultes()) {
							if (!consulta.isOcultarActiu()) {
								ConsultaDto consulte = new ConsultaDto();
								consulte.setId(consulta.getId());
								consulte.setNom(consulta.getNom());
								target.getConsultes().add(consulte);
							}
						}
						target.setJbpmProcessDefinitionKey(source.getJbpmProcessDefinitionKey());
						target.setNom(source.getNom());
						target.setReiniciarCadaAny(source.isReiniciarCadaAny());
						target.setResponsableDefecteCodi(source.getResponsableDefecteCodi());
						target.setRestringirPerGrup(source.isRestringirPerGrup());
						target.setSeleccionarAny(source.isSeleccionarAny());
						target.setAmbRetroaccio(source.isAmbRetroaccio());
						target.setReindexacioAsincrona(source.isReindexacioAsincrona());
						target.setSequencia(source.getSequencia());
						target.setSequenciaDefault(source.getSequenciaDefault());
						target.setTeNumero(source.getTeNumero());
						target.setTeTitol(source.getTeTitol());
						target.setTramitacioMassiva(source.isTramitacioMassiva());						
						Map<Integer,SequenciaAnyDto> sequenciaAnyMap = new HashMap<Integer, SequenciaAnyDto>();
						for (Entry<Integer, SequenciaAny> entry : source.getSequenciaAny().entrySet()) {
							SequenciaAny value = entry.getValue();
							SequenciaAnyDto valueDto = new SequenciaAnyDto();
							valueDto.setAny(value.getAny());
							valueDto.setId(value.getId());
							valueDto.setSequencia(value.getSequencia());
							sequenciaAnyMap.put(entry.getKey(), valueDto);
						}					
						target.setSequenciaAny(sequenciaAnyMap);
						Map<Integer,SequenciaDefaultAnyDto> sequenciaAnyDefaultMap = new HashMap<Integer, SequenciaDefaultAnyDto>();
						for (Entry<Integer, SequenciaDefaultAny> entry : source.getSequenciaDefaultAny().entrySet()) {
							SequenciaDefaultAny value = entry.getValue();
							SequenciaDefaultAnyDto valueDto = new SequenciaDefaultAnyDto();
							valueDto.setAny(value.getAny());
							valueDto.setId(value.getId());
							valueDto.setSequenciaDefault(value.getSequenciaDefault());							
							sequenciaAnyDefaultMap.put(entry.getKey(), valueDto);
						}					    
						target.setSequenciaDefaultAny(sequenciaAnyDefaultMap);
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ExpedientTipusDto, ExpedientTipus>() {
					@Override
					public ExpedientTipus convert(ExpedientTipusDto source, Type<? extends ExpedientTipus> destinationType) {
						ExpedientTipus target = new ExpedientTipus();
						target.setAnyActual(source.getAnyActual());
						target.setCodi(source.getCodi());
						target.setDemanaNumero(source.isDemanaNumero());
						target.setDemanaTitol(source.isDemanaTitol());
						target.setEntorn(convertir(source.getEntorn(), Entorn.class));
						target.setEstats(convertirList(source.getEstats(), Estat.class));
						target.setExpressioNumero(source.getExpressioNumero());
						target.setId(source.getId());
						target.setJbpmProcessDefinitionKey(source.getJbpmProcessDefinitionKey());
						target.setNom(source.getNom());
						target.setReiniciarCadaAny(source.isReiniciarCadaAny());
						target.setResponsableDefecteCodi(source.getResponsableDefecteCodi());
						target.setRestringirPerGrup(source.isRestringirPerGrup());
						target.setSeleccionarAny(source.isSeleccionarAny());
						target.setAmbRetroaccio(source.isAmbRetroaccio());
						target.setReindexacioAsincrona(source.isReindexacioAsincrona());
						target.setSequencia(source.getSequencia());
						target.setSequenciaDefault(source.getSequenciaDefault());
						target.setTeNumero(source.isTeNumero());
						target.setTeTitol(source.isTeTitol());
						target.setTramitacioMassiva(source.isTramitacioMassiva());						
						SortedMap<Integer, SequenciaAny> sequenciaAnySorted = new TreeMap<Integer, SequenciaAny>();
						for (Entry<Integer, SequenciaAnyDto> entry : source.getSequenciaAny().entrySet()) {
							SequenciaAnyDto valueDto = entry.getValue();
							SequenciaAny value = new SequenciaAny();
							value.setAny(valueDto.getAny());
							value.setId(valueDto.getId());
							value.setSequencia(valueDto.getSequencia());
							sequenciaAnySorted.put(entry.getKey(), value);
						}					
						target.setSequenciaAny(sequenciaAnySorted);
						SortedMap<Integer, SequenciaDefaultAny> sequenciaAnyDefaultSorted = new TreeMap<Integer, SequenciaDefaultAny>();
						for (Entry<Integer, SequenciaDefaultAnyDto> entry : source.getSequenciaDefaultAny().entrySet()) {
							SequenciaDefaultAnyDto valueDto = entry.getValue();
							SequenciaDefaultAny value = new SequenciaDefaultAny();
							value.setAny(valueDto.getAny());
							value.setId(valueDto.getId());
							value.setSequenciaDefault(valueDto.getSequenciaDefault());
							sequenciaAnyDefaultSorted.put(entry.getKey(), value);
						}					
						target.setSequenciaDefaultAny(sequenciaAnyDefaultSorted);
						return target;
					}
				});
	}

	public <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}

	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
