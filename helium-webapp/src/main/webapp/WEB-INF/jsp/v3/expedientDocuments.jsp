<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="numColumnes" value="${3}"/>
<c:choose>
	<c:when test="${not empty documents}">
		<table class="table table-bordered">
			<colgroup>
				<c:forEach begin="0" end="${numColumnes - 1}">
					<col width="${100 / numColumnes}%"/>
				</c:forEach>
			</colgroup>
			<%--thead>
				<tr>
					<th colspan="${numColumnes}">
						Documents <span class="badge">${fn:length(documents)}</span>
					</th>
				</tr>
			</thead--%>
			<tbody>
				<c:set var="index" value="${0}"/>
				<c:set var="posicioOffset" value="${0}"/>
				<c:set var="count" value="${fn:length(documents)}"/>
				<c:forEach var="document" items="${documents}">
					<c:set var="posicioActual" value="${(index + posicioOffset) % numColumnes}"/>
					<c:if test="${posicioActual == 0}"><tr></c:if>
					<td<c:if test="${not empty document.error}"> style="background-color:#f2dede"</c:if>>
						<c:choose>
							<c:when test="${not empty document.error}">
								<i class="icon-warning-sign" title="${document.error}"></i>
							</c:when>
							<c:otherwise>
								<dl class="dl-horizontal">
									<dt><a href="<c:url value="/v3/expedient/${expedientId}/document/${document.id}/descarregar"/>" title="Descarregar document"><i class="icon-file icon-4x"></i><span style="float:left;position:relative;left:2.5em;top:-2.4em;font-size:10px">${fn:toUpperCase(document.arxiuExtensio)}</span></a></dt>
									<dd>
										<strong>${document.documentNom}</strong><br/>
										<fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy"/>
										<c:if test="${document.signat or document.registrat}">
										<br/>
										<c:if test="${document.signat}"><a href="#"><i class="icon-certificate icon-large" title="Document signat (clic per veure detalls)"></i></a></c:if>
										<c:if test="${document.registrat}"><a href="#"><i class="icon-book icon-large" title="Document registrat (clic per veure detalls)"></i></a></c:if>
										</c:if>
									</dd>
								</dl>
							</c:otherwise>
						</c:choose>
					</td>
					<c:if test="${(index == count - 1) and posicioActual != (numColumnes - 1)}"><td colspan="${numColumnes - posicioActual - 1}">&nbsp;</td></c:if>
					<c:if test="${(index == count - 1) or (index != 0 and posicioActual == (numColumnes - 1))}"></tr></c:if>
					<c:set var="index" value="${index + 1}"/>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="well well-small">Aquest expedient no conté cap document</div>
	</c:otherwise>
</c:choose>



<%--c:set var="numColumnes" value="${4}"/>
<c:set var="agrupacioFirst" value="${true}"/>
<c:forEach begin="0" end="${fn:length(agrupacions)}" varStatus="agrupacioStatus">
	<c:set var="agrupacio" value="${agrupacions[agrupacioStatus.index]}"/>
	<c:set var="count" value="${0}"/>
	<c:forEach var="dada" items="${dades}">
		<c:if test="${dada.agrupacioId == agrupacio.id}"><c:set var="count" value="${count + 1}"/></c:if>
	</c:forEach>
	<c:if test="${count > 0}">
		<table class="table table-bordered marTop10">
			<colgroup>
				<c:forEach begin="0" end="${numColumnes - 1}">
					<col width="${100 / numColumnes}%"/>
				</c:forEach>
			</colgroup>
			<c:if test="${not empty agrupacio}">
				<thead>
					<tr>
						<th colspan="${numColumnes}">
							${agrupacio.nom} <span class="badge">${count}</span>
							<div class="pull-right accordion-toggle">
								<c:choose>
									<c:when test="${agrupacioFirst}"><i class="agrupacio-desplegador icon-chevron-up"></i></c:when>
									<c:otherwise><i class="agrupacio-desplegador icon-chevron-down"></i></c:otherwise>
								</c:choose>
							</div>
						</th>
					</tr>
				</thead>
			</c:if>
			<tbody<c:if test="${not agrupacioFirst and not empty agrupacio}"> class="hide"</c:if>>
				<c:set var="index" value="${0}"/>
				<c:set var="posicioOffset" value="${0}"/>
				<c:forEach var="dada" items="${dades}">
					<c:set var="posicioActual" value="${(index + posicioOffset) % numColumnes}"/>
					<c:if test="${dada.agrupacioId == agrupacio.id}">
						<c:if test="${posicioActual == 0}"><tr></c:if>
						<c:if test="${dada.campTipusRegistre}">
							<c:if test="${posicioActual > 0}"><td colspan="${numColumnes - posicioActual}">&nbsp;</td></tr><tr></c:if>
							<c:set var="posicioOffset" value="${posicioOffset + (numColumnes - posicioActual) - 1}"/>
							<c:set var="posicioActual" value="${0}"/>
						</c:if>
						<td<c:if test="${dada.campTipusRegistre}"> colspan="${numColumnes}"</c:if><c:if test="${not empty dada.error}"> style="background-color:#f2dede"</c:if>>
							<address>
								${dada.campEtiqueta}<br/>
								<c:if test="${not empty dada.varValor}">
									<c:choose>
										<c:when test="${dada.campTipusRegistre}">
											<c:set var="registreFilesTaula" value="${dada.dadesRegistrePerTaula}"/>
											<table class="table subtable marTop6">
												<thead>
													<tr>
														<c:forEach var="dadaFila0" items="${registreFilesTaula[0].registreDades}">
															<th>${dadaFila0.campEtiqueta}</th>
														</c:forEach>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="registreFila" items="${registreFilesTaula}">
														<tr>
															<c:forEach var="registreDada" items="${registreFila.registreDades}">
																<td>${registreDada.text}</td>
															</c:forEach>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</c:when>
										<c:otherwise><strong>${dada.text}</strong></c:otherwise>
									</c:choose>
								</c:if>
							</address>
						</td>
						<c:if test="${(index == count - 1) and posicioActual != (numColumnes - 1) and not dada.campTipusRegistre}"><td colspan="${numColumnes - posicioActual - 1}">&nbsp;</td></c:if>
						<c:if test="${(index == count - 1) or dada.campTipusRegistre or (index != 0 and posicioActual == (numColumnes - 1))}"></tr></c:if>
						<c:set var="index" value="${index + 1}"/>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	<c:set var="agrupacioFirst" value="${false}"/>
</c:forEach--%>