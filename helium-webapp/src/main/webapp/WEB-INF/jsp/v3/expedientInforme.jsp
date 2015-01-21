<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="consulta.form.informe" /></title>
	<meta name="title" content="${consulta.expedientTipus.nom}"/>
	<meta name="subtitle" content="${consulta.nom}"/>	
	<script type="text/javascript" src="<c:url value="/js//jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	<style>
		#filtresCollapsable .controls{ width: 100% !important;}
		#filtresCollapsable {padding-top: 20px;}
		input, select, textarea {width: 100%;}
		form .fila_reducida {padding-top: 0px;margin-bottom: 5px;}
		#expedientInformeCommand .row {margin-bottom: -5px;}
		#taulaDades {
			display: block;
			overflow-x: auto;
			border-left: 0 none;
			border-right: 0 none;
			border-bottom: 0 none;
		}
		.col-xs-13 {
    		margin-left: -5px;
    		margin-right: -15px;
    	}
		.form-group {
			padding-right: 	15px;
			margin-left: 	10px !important;
			margin-bottom:	15px;
		}
		.form-group input, .form-group textarea {
			width: 100%;
		}
		
		.form-group li > .select2-container {
			width: 100%;
			padding-right: 20px;
		}
		
		.form-group .select2-container {
			width: calc(100% + 14px);
		}
		.condensed {
			margin-bottom: 0px;
		}
		.form-group.registre {
			padding-right: 1px;
		}
		.col-xs-9 {width: 82.5%}
		.registre table .colEliminarFila {
			width: 1px;
		}
		.registre table .opciones {
			text-align: center;
			padding: 4px;
		}
		p.help-block {
			padding-top: 0;	
			margin-top: 4px !important;
		}
		.clear {
			clear: both;
		}
		.clearForm {
			clear: both;
			margin-bottom: 10px;
			border-bottom: solid 1px #EAEAEA;
		}
		.input-append {
			width: calc(100% - 27px);
		}
		.eliminarFila {
			padding: 4px 6px;
		}
		.tercpre {
			padding-left: 0px !important;
			padding-right: 8px !important;
		}
		.tercmig {
			padding-left: 4px !important;
			padding-right: 4px !important;
		}
		.tercpost {
			padding-left: 8px !important;
			padding-right: 0px !important;
		}
		thead {
/* 			display: inline-table; */
		}
		tbody {
/* 			display: inline-table; */
		}
		.pagination {margin : 0px !important;}
		#btn_exportar {padding-right : 10px;}
		.row {padding-bottom: 5px;}
		.col-xs-3 {width: 17.5%;}
		.control-label.col-xs-4 {width: auto !important;}
		.col-xs-5 {padding-left: 0px !important;margin-right: -55px;}
		.controls.form-group.col-xs-9 {margin-bottom: 0px !important;padding-bottom: 0px !important;}
		.col-xs-9 .form-group {margin-bottom: 0px !important;padding-bottom: 0px !important;}
		body .container .panel.panel-default {}
		body .container .panel.panel-default .panel-body .panel.panel-default {padding: 0px;margin-bottom: 0px;}
		#canviar_consulta {margin-top: 5px;}
	</style>
<script>
$(document).ready(function() {	
	$("#taulaDades").heliumDataTable({
		ajaxSourceUrl: "<c:url value="/v3/informe/consulta/${expedientInformeCommand.consultaId}/datatable"/>",
		localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
		alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
		rowClickCallback: function(row) {
// 			$('a.obrir-expedient', $(row))[0].click();
			$.ajax({
				"url": "<c:url value="/nodeco/v3/expedient/"/>" + $(row).find(".rdt-seleccio").val() + "/tasquesPendents",
				"beforeSend": function( xhr ) {	
					$('.fa-chevron-up').addClass('fa-chevron-down').removeClass('fa-chevron-up');
					$(row).find('.icona-collapse').removeClass('fa-chevron-down').addClass('fa-circle-o-notch fa-spin');
					$(".table-pendents").find('td').wrapInner('<div style="display: block;" />').parent().find('td > div').slideUp(400, function(){
					  	$(this).parent().parent().remove();
					});
				},
				"success": function (data) {
					$(row).find('.icona-collapse').removeClass('fa-circle-o-notch fa-spin').addClass('fa-chevron-up');
					$(row).after(data);
					$(".table-pendents").find('td').wrapInner('<div style="display: none;" />').parent().find('td > div').slideDown(400, function(){
						  var $set = $(this);
						  $set.replaceWith($set.contents());
					});
				},
			  	"error": function(XMLHttpRequest, textStatus, errorThrown) {
					$('.fa-chevron-up').removeClass('fa-chevron-down fa-circle-o-notch fa-spin fa-chevron-up');
					$(".table-pendents").remove();
				}
			});
		},
		seleccioCallback: function(seleccio) {
			$('#tramitacioMassivaCount').html(seleccio.length);
		}
	});
	$("#expedientInformeCommand button[value='netejar']").click(function() {
		$('#expedientInformeCommand')[0].reset();
		$('#expedientInformeCommand').submit();
	});
	$("#nomesPendentsCheck").click(function() {
		$("input[name=nomesPendents]").val(!$("#nomesPendentsCheck").hasClass('active'));
		$('#expedientInformeCommand').submit();
	});
	$("#nomesAlertesCheck").click(function() {
		$("input[name=nomesAlertes]").val(!$("#nomesAlertesCheck").hasClass('active'));
		$('#expedientInformeCommand').submit();
	});
	$("#mostrarAnulatsCheck").click(function() {
		$("input[name=mostrarAnulats]").val(!$("#mostrarAnulatsCheck").hasClass('active'));
		$('#expedientInformeCommand').submit();
	});
});
</script>
</head>
<body>	
	<input type="hidden" id="netejar" value="false"/>
	
	<form:form method="post" cssClass="well form-horizontal form-tasca" commandName="expedientInformeCommand">
		<form:hidden path="consultaId"/>
		<div class="control-group fila_reducida">
			<c:forEach var="camp" items="${campsFiltre}">
				<c:set var="campActual" value="${camp}" scope="request"/>
				<c:set var="readonly" value="${false}" scope="request"/>
				<c:set var="required" value="${false}" scope="request"/>
				<c:import url="campsFiltre.jsp"/>
			</c:forEach>
		</div>
		<div class="row">
			<div class="col-md-6">
				<form:hidden path="nomesPendents"/>
				<form:hidden path="nomesAlertes"/>
				<form:hidden path="mostrarAnulats"/>
				<div class="btn-group">
					<a id="nomesPendentsCheck" href="javascript:void(0)" title="<spring:message code="expedient.llistat.filtre.camp.tasques"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.nomesPendents || preferenciesUsuari.filtroTareasActivas}"> active</c:if>" data-toggle="buttons"><span class="fa fa-user"></span></a>
					<a id="nomesAlertesCheck" href="javascript:void(0)" title="<spring:message code="expedient.llistat.filtre.camp.alertes"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.nomesAlertes}"> active</c:if>" data-toggle="buttons"><span class="fa fa-warning"></span></a>
					<a id="mostrarAnulatsCheck" href="javascript:void(0)" title="<spring:message code="expedient.llistat.filtre.camp.anulats"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.mostrarAnulats}"> active</c:if>" data-toggle="buttons"><span class="fa fa-times"></span></a>
				</div>
			</div>
			<div class="col-md-6">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
				
	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-button-template="tableButtonsTemplate" data-rdt-filtre-form-id="expedientInformeCommand" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr class="panel-heading clicable proces" data-toggle="collapse">
				<th data-rdt-property="expedient.id" width="4%" data-rdt-sortable="false"></th>
				<th data-rdt-property="id" data-rdt-template="cellPendentsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="2%">
					<script id="cellPendentsTemplate" type="text/x-jsrender">
						<div class="pull-left">
							<span class="icona-collapse fa fa-chevron-down"></i>						
						</div>
					</script>
				</th>
				<th data-rdt-property="expedient.identificador" data-rdt-sorting="desc" data-visible=true><spring:message code="expedient.llistat.columna.expedient"/></th>
				<c:forEach var="camp" items="${campsInforme}">
					<th data-rdt-property="dadesExpedient.${camp.varCodi}.valorMostrar" data-visible=true >
					${camp.campEtiqueta}
				</th>
				</c:forEach>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="<c:url value="../../expedient/{{:id}}"/>" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code='comuns.obrir'/></a></li>
									<li><a href="<c:url value="../../expedient/{{:id}}/suspend"/>" data-rdt-link-modal="true"><span class="fa fa-stop"></span>&nbsp;<spring:message code='comuns.aturar'/></a></li>
									<li><a href="<c:url value="../../expedient/{{:id}}/cancel"/>" data-rdt-link-modal="true"><span class="fa fa-times"></span>&nbsp;<spring:message code='comuns.anular'/></a></li>
									<li><a href="<c:url value="../../expedient/{{:id}}/delete"/>" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code='expedient.consulta.confirm.esborrar'/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code='comuns.esborrar'/></a></li>
								</ul>
							</div>
						</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<div class="btn-group pull-right">
			<a class="btn btn-default" href="<c:url value="../../../v3/informe/consulta/${consulta.id}/seleccioTots"/>" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
			<a class="btn btn-default" href="<c:url value="../../../v3/informe/consulta/${consulta.id}/seleccioNetejar"/>" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
			<a class="btn btn-default" href="<c:url value="../../../v3/expedient/massiva?consultaId=${consulta.id}"/>"><spring:message code="expedient.llistat.accio.massiva"/>&nbsp;<span id="tramitacioMassivaCount" class="badge">&nbsp;</span></a>
		</div>
		<div id="btn_exportar" class="btn-toolbar pull-right btn_under_taulaDades">
			<c:if test="${consulta.exportarActiu}">
				<a id="exportar_excel" href="<c:url value="../../informe/consulta/${consulta.id}/exportar_excel"/>" class="btn btn-default">
					<span class="icon-download-alt"></span>&nbsp;<spring:message code="comuns.descarregar"/>
				</a>
			</c:if>
			<c:if test="${not empty consulta.informeNom and not empty campsInformeParams}">
				<a data-rdt-link-modal="true" id="mostrar_informe" href="<c:url value="../../../v3/informe/consulta/${consulta.id}/mostrar_informe_params"/>" class="btn btn-default">
					<span class="icon-download-alt"></span>&nbsp;<spring:message code="expedient.consulta.informe"/>
				</a>
			</c:if>
			<c:if test="${not empty consulta.informeNom and empty campsInformeParams}">
				<a id="mostrar_informe" href="<c:url value="../../../v3/informe/consulta/${consulta.id}/mostrar_informe"/>" class="btn btn-default">
					<span class="icon-download-alt"></span>&nbsp;<spring:message code="expedient.consulta.informe"/>
				</a>
			</c:if>
		</div>			
	</script>
	
	<script type="text/javascript">
		$("#tableButtonsTemplate a").heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: false,
			alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
		});
	</script>
</body>
</html>
