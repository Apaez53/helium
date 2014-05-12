<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head>
	<title><fmt:message key="index.inici" /></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.ca.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
<script>
$(document).ready(function() {
	$("#taulaDades").heliumDataTable({
		ajaxSourceUrl: "<c:url value="/v3/tasca/datatable"/>",
		localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
		alertesRefreshUrl: "<c:url value="/nodecorar/v3/missatges"/>"
	});
	$("#tascaConsultaCommand button[value='netejar']").click(function() {
		$('#tascaConsultaCommand')[0].reset();
	});
	$("#mostrarTasquesPersonalsCheck").click(function() {
		$("input[name=mostrarTasquesPersonals]").val(!$("#mostrarTasquesPersonalsCheck").hasClass('active'));
		$('#tascaConsultaCommand').submit();
	});
	$("#mostrarTasquesGrupCheck").click(function() {
		$("input[name=mostrarTasquesGrup]").val(!$("#mostrarTasquesGrupCheck").hasClass('active'));
		$('#tascaConsultaCommand').submit();
	});
	$('#filtresCollapsable').on('hide', function () {
		$('#filtresCollapse i').attr("class", "icon-chevron-down");
		$("input[name=filtreDesplegat]").val("false");
	});
	$('#filtresCollapsable').on('show', function () {
		$('#filtresCollapse i').attr("class", "icon-chevron-up");
		$("input[name=filtreDesplegat]").val("true");
	});
	$('.datepicker').datepicker({language: 'ca', autoclose: true});
});

function confirmarSuspendre(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu suspendre aquesta tasca?");
}
function confirmarReprendre(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu reprendre aquesta tasca?");
}
function confirmarCancelar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu cancel·lar aquesta tasca? Aquesta acció no es podrà desfer.");
}
function confirmarAlliberar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu alliberar aquesta tasca?");
}
</script>
</head>
<body>

	<div id="tasca-reasignar-modal"></div>
	<div id="tasca-tramitacio-modal"></div>
	
	<input type="hidden" id="netejar" value="false"/>
	<form:form action="" method="post" cssClass="well formbox" commandName="tascaConsultaCommand">
		<div class="page-header">
			Consulta d'tasques
			<form:hidden path="filtreDesplegat"/>
		</div>
		<div id="filtresCollapsable" class="collapse<c:if test="${true or tascaConsultaCommand.filtreDesplegat}"> in</c:if>">
			<div class="row-fluid">
				<div class="span2">
					<c:set var="campPath" value="tasca"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<spring:bind path="${campPath}">
							<input type="text" id="${campPath}" name="${campPath}" placeholder="Tasca"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span12">
						</spring:bind>
					</div>
				</div>
				<div class="span4">
					<c:set var="campPath" value="expedient"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="control-group<c:if test="${not empty campErrors}"> error</c:if>">
						<spring:bind path="${campPath}">
							<input type="text" id="${campPath}" name="${campPath}" placeholder="Expedient"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span12">
						</spring:bind>
					</div>
				</div>
				<div class="span3">
					<c:set var="campPath" value="prioritat"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<form:select path="${campPath}" cssClass="span12">
						<option value="">Prioritat</option>
						<form:options items="${prioritats}" itemLabel="codi" itemValue="valor"/>
					</form:select>
				</div>
				<div class="span3">
					<c:set var="campPath" value="expedientTipusId"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<c:choose>
						<c:when test="${not empty expedientTipusActual}">
							<input type="hidden" id="${campPath}" name="${campPath}" value="${expedientTipusActual.id}"/>
							<input type="text" name="${campPath}_actual" value="${expedientTipusActual.nom}" class="span12" disabled="disabled"/>
						</c:when>
						<c:otherwise>
							<form:select path="${campPath}" cssClass="span12">
								<option value="">Tipus d'expedient</option>
								<form:options items="${expedientTipusAccessibles}" itemLabel="nom" itemValue="id"/>
							</form:select>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span4">
					<label>Data creacio</label>
					<div class="row-fluid">
						<div class="span5 input-append date datepicker">
							<c:set var="campPath" value="dataCreacioInicial"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<spring:bind path="${campPath}">
								<input type="text" id="${campPath}" name="${campPath}" placeholder="dd/mm/yyyy"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
							</spring:bind>
							<span class="add-on"><i class="icon-calendar"></i></span>
						</div>
						<script>$("#${campPath}").mask("99/99/9999");</script>
						<div class="span5 offset1 input-append date datepicker">
							<c:set var="campPath" value="dataCreacioFinal"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<spring:bind path="${campPath}">
								<input type="text" id="${campPath}" name="${campPath}" placeholder="dd/mm/yyyy"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
							</spring:bind>
							<span class="add-on"><i class="icon-calendar"></i></span>
						</div>
						<script>$("#${campPath}").mask("99/99/9999");</script>
					</div>
				</div>
				<div class="span4">
					<label>Data limit</label>
					<div class="row-fluid">
						<div class="span5 input-append date datepicker">
							<c:set var="campPath" value="dataLimitInicial"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<spring:bind path="${campPath}">
								<input type="text" id="${campPath}" name="${campPath}" placeholder="dd/mm/yyyy"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span10">
							</spring:bind>
							<span class="add-on"><i class="icon-calendar"></i></span>
						</div>
						<script>$("#${campPath}").mask("99/99/9999");</script>
						<div class="span5 offset1 input-append date datepicker">
							<c:set var="campPath" value="dataLimitFinal"/>
							<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
							<spring:bind path="${campPath}">
								<input type="text" id="${campPath}" name="${campPath}" placeholder="dd/mm/yyyy"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span10">
							</spring:bind>
							<span class="add-on"><i class="icon-calendar"></i></span>
						</div>
						<script>$("#${campPath}").mask("99/99/9999");</script>
					</div>
				</div>
			</div>
			<hr/>
		</div>
		<div class="row-fluid">
			<div class="span6">
				<form:hidden path="mostrarTasquesPersonals"/>
				<form:hidden path="mostrarTasquesGrup"/>
				<div class="btn-group">
					<a id="mostrarTasquesPersonalsCheck" href="javascript:void(0)" title="Mostrar tareas personales" class="btn<c:if test="${tascaConsultaCommand.mostrarTasquesPersonals}"> active</c:if>" data-toggle="button"><i class="icon-user"></i></a>
					<a id="mostrarTasquesGrupCheck" href="javascript:void(0)" title="Mostrar tareas de grupo" class="btn<c:if test="${tascaConsultaCommand.mostrarTasquesGrup}"> active</c:if>" data-toggle="button"><i class="icon-group"></i></a>
				</div>
			</div>
			<div class="span6">
				<input type="hidden" name="consultaRealitzada" value="true"/>
				<button type="submit" name="accio" value="consultar" class="btn btn-primary pull-right">Consultar</button>
				<button type="submit" name="accio" value="netejar" class="btn pull-right" style="margin-right:.6em">Netejar</button>
			</div>
		</div>
	</form:form>

	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-filtre-form-id="tascaConsultaCommand" data-rdt-seleccionable="false" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr>
				<th data-rdt-property="titol" data-rdt-template="cellPersonalGroupTemplate" data-rdt-visible="true" >
					Tasca
					<script id="cellPersonalGroupTemplate" type="text/x-jsrender">
						{{if cancelada}}
							<i class="btn-small btn-danger pull-right" style="margin-right: .3em">CA</i>
						{{/if}}
						{{if suspesa}}
							<i class="btn-small btn-info pull-right" style="margin-right: .3em">SU</i>
						{{/if}}
						{{if oberta}}
							<i class="btn-small btn-warning pull-right" style="margin-right: .3em">OB</i>
						{{/if}}
						{{if completed}}
							<i class="btn-small btn-succes pull-right" style="margin-right: .3em">FI</i>
						{{/if}}
						{{if agafada}}
							<i class="btn-small btn-inverse pull-right" style="margin-right: .3em">AG</i>
						{{/if}}
						{{if responsables != null && !agafada}}
							<i class="icon-group" style="margin-right: .3em" />
						{{/if}}
						{{:titol}}
					</script>
				</th>
				<th data-rdt-property="expedientIdentificador" data-rdt-visible="true">Expedient</th>
				<th data-rdt-property="dataCreacio" data-rdt-type="datetime" data-rdt-sorting="desc" data-rdt-visible="true">Iniciat el</th>
				<th data-rdt-property="expedientTipusNom" data-rdt-visible="true">Tipus expedient</th>
				<th data-rdt-property="prioritat" data-rdt-visible="true">Prioritat</th>
				<th data-rdt-property="dataLimit" data-rdt-visible="true">Data límit</th>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
 						<div class="dropdown"> 
 							<button class="btn btn-success" data-toggle="dropdown"><i class="icon-cog icon-white"></i>&nbsp;Accions&nbsp;<span class="caret"></span></button> 
							<ul class="dropdown-menu"> 
								<li><a rdt-link-modal="true" data-reasignar-modal="true" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/reassignar"/>"><i class="icon-share"></i> Reasignar</a></li>
								{{if responsables != null && !agafada && oberta && !suspesa}}
 									<li><a href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/tascaAgafar"/>"><i class="icon-signin"></i> Agafar</a></li>
								{{/if}}
								{{if agafada && oberta && !suspesa}}
 									<li><a rdt-link-modal="true" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/tramitar"/>" data-tramitar-modal="true"><i class="icon-folder-open"></i> Tramitar</a></li>
									<li><a href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/delegar"/>"><i class="icon-hand-right"></i> Delegar</a></li>
								{{/if}}
								{{if oberta && !suspesa}}
									<li><a onclick="return confirmarSuspendre(event)" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/suspendre"/>"><i class="icon-pause"></i> Suspendre</a></li>
								{{/if}}
								{{if suspesa}}
									<li><a onclick="return confirmarReprendre(event)" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/reprendre"/>"><i class="icon-play"></i> Reprendre</a></li>
								{{/if}}
								{{if !cancelada}}
									<li><a onclick="return confirmarCancelar(event)" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/cancelar"/>"><i class="icon-remove"></i> Cancelar</a></li>
								{{/if}}
								{{if agafada && oberta}}
									<li><a onclick="return confirmarAlliberar(event)" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/tascaAlliberar"/>"><i class="icon-leaf"></i> <spring:message code="tasca.pllistat.alliberar"/></a></li>
								{{/if}} 
 							</ul> 
 						</div>
					</script> 
				</th>
				<th data-rdt-property="agafada" data-rdt-visible="false"></th>
				<th data-rdt-property="cancelada" data-rdt-visible="false"></th>
				<th data-rdt-property="suspesa" data-rdt-visible="false"></th>
				<th data-rdt-property="oberta" data-rdt-visible="false"></th>
				<th data-rdt-property="completed" data-rdt-visible="false"></th>
				<th data-rdt-property="expedientId" data-rdt-visible="false"></th>
				<th data-rdt-property="responsables" data-rdt-visible="false"></th>
			</tr>
		</thead>
	</table>
<script type="text/javascript">
$('#taulaDades a').click(function() {
	if ($(this).data('tramitar-modal')) {
		$('#tasca-tramitacio-modal').heliumModal({
			modalUrl: $(this).attr('href'),
			refrescarTaula: false,
			refrescarAlertes: false,
			refrescarPagina: false,
			adjustWidth: false,
			adjustHeight: true,
			maximize: true,
			alertesRefreshUrl: "<c:url value="/nodecorar/v3/missatges"/>",
			valignTop: true,
			buttonContainerId: 'formFinalitzar',
		});
		return false;
	} else if ($(this).data('reasignar-modal')) {
		$('#tasca-reasignar-modal').heliumModal({
			modalUrl: $(this).attr('href'),
			refrescarTaula: false,
			refrescarAlertes: true,
			refrescarPagina: false,
			adjustWidth: false,
			adjustHeight: true,
			maximize: true,
			alertesRefreshUrl: "<c:url value="/nodecorar/v3/missatges"/>",
			valignTop: true,
			buttonContainerId: 'formReasignar'
		});
		return false;
	} else {
		return true;
	}
});
</script>
</body>
</html>