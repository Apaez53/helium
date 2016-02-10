<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="document" value="${dada}"/>
<td id="cela-${expedientId}-${document.id}">									
	<c:choose>
		<c:when test="${not empty document.error}">
			<span class="fa fa-warning fa-2x" title="${document.error}"></span>
		</c:when>
		<c:otherwise>
			<table id="document_${document.id}" class="table-condensed marTop6 tableDocuments">
				<thead>
					<tr>
						<td class="left">
							<a href="<c:url value="/v3/expedient/${expedientId}/document/${document.id}/descarregar"/>">
								<span class="fa fa-file fa-4x" title="Descarregar document"></span>
								<c:if test="${document.adjunt}">
									<span class="adjuntIcon icon fa fa-paperclip fa-2x"></span>
								</c:if>
								<span class="extensionIcon">
									${fn:toUpperCase(document.arxiuExtensio)}
								</span>
							</a>
						</td>
						<td class="right">
							<c:if test="${not empty document.id}">
								<table class="marTop6 tableDocuments">
									<thead>
										<tr>
											<td class="tableDocumentsTd">
												<c:if test="${!document.signat}">
													<a 	href="../../v3/expedient/${expedientId}/document/${document.processInstanceId}/${document.id}/modificar"
														data-rdt-link-modal="true" 
														data-rdt-link-modal-min-height="190" 
														data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
														class="icon modificar" >
															<span class="fa fa-2x fa-pencil" title="<spring:message code='expedient.document.modificar' />"></span>
													</a>
												</c:if>
												<c:if test="${document.signat}">	
												
													<c:choose>
														<c:when test="${not empty document.signaturaUrlVerificacio}">
															<a 	class="icon signature"
															   	data-rdt-link-modal="true" 
															   	data-rdt-link-modal-min-height="400" 
															   	href="${document.signaturaUrlVerificacio}">
																<span class="fa fa-2x fa-certificate" title="<spring:message code='expedient.document.signat' />"></span>
															</a>
														</c:when>
														<c:otherwise>																			
															<a 	data-rdt-link-modal="true"
																class="icon signature" 
																href="<c:url value='../../v3/expedient/${expedientId}/verificarSignatura/${document.processInstanceId}/${document.id}/${document.documentCodi}'/>?urlVerificacioCustodia=${document.signaturaUrlVerificacio}">
																<span class="fa fa-2x fa-certificate" title="<spring:message code='expedient.document.signat' />"></span>
															</a>
														</c:otherwise>
													</c:choose>
													
													<a 	class="icon signature fa-stack fa-2x" 
														data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_signatures' />"
														data-rdt-link-ajax=true
														href='<c:url value="../../v3/expedient/${expedientId}/document/${document.id}/signaturaEsborrar"/>' 
														data-rdt-link-callback="esborrarSignatura(${document.id});"
														title="<spring:message code='expedient.document.esborrar.signatures' />">
														<i class="fa fa-certificate fa-stack-1x"></i>
													  	<i class="fa fa-ban fa-stack-2x text-danger"></i>
													</a>
												</c:if>
												<c:if test="${document.registrat}">
													<a 	data-rdt-link-modal="true" 
														class="icon registre" 
														href="<c:url value='../../v3/expedient/${expedientId}/verificarRegistre/${document.processInstanceId}/${document.id}/${document.documentCodi}'/>">
														<span class="fa fa-book fa-2x" title="<spring:message code='expedient.document.registrat' />"></span>
													</a>
												</c:if>
												<a 	class="icon fa fa-trash-o fa-2x" 
													data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_proces' />"
													data-rdt-link-ajax=true
													href='<c:url value="../../v3/expedient/${expedientId}/document/${document.processInstanceId}/${document.id}/esborrar"/>' 
													data-rdt-link-callback="recargarPanel(${document.processInstanceId});"
													title="<spring:message code='expedient.document.esborrar'/>">
												</a>																				
												<%--
												<c:if test="${not empty psignaPendentActual}">
													<c:choose>
														<c:when test="${psignaPendentActual.error}"><img src="<c:url value="/img/exclamation.png"/>" alt="<fmt:message key="expedient.document.pendent.psigna.error"/>" title="<fmt:message key="expedient.document.pendent.psigna.error"/>" border="0" style="cursor:pointer" onclick="infoPsigna(${documentActual.id})"/></c:when>
														<c:otherwise><img src="<c:url value="/img/clock_red.png"/>" alt="<fmt:message key="expedient.document.pendent.psigna"/>" title="<fmt:message key="expedient.document.pendent.psigna"/>" border="0" style="cursor:pointer" onclick="infoPsigna(${documentActual.id})"/></c:otherwise>
													</c:choose>
													<div id="psigna_${documentActual.id}" style="display:none">
														<dl class="form-info">
															<dt><fmt:message key="common.icones.doc.psigna.id"/></dt><dd>${psignaPendentActual.documentId}&nbsp;</dd>
															<dt><fmt:message key="common.icones.doc.psigna.data.enviat"/></dt><dd><fmt:formatDate value="${psignaPendentActual.dataEnviat}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
															<dt><fmt:message key="common.icones.doc.psigna.estat"/></dt><dd>${psignaPendentActual.estat}&nbsp;</dd>
															<c:if test="${not empty psignaPendentActual.dataProcesPrimer}">
																<dt><fmt:message key="common.icones.doc.psigna.data.proces.primer"/></dt><dd><fmt:formatDate value="${psignaPendentActual.dataProcesPrimer}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
															</c:if>
															<c:if test="${not empty psignaPendentActual.dataProcesDarrer}">
																<dt><fmt:message key="common.icones.doc.psigna.data.proces.darrer"/></dt><dd><fmt:formatDate value="${psignaPendentActual.dataProcesDarrer}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
															</c:if>
															<c:if test="${psignaPendentActual.error}">
																<dt><fmt:message key="common.icones.doc.psigna.error.processant"/></dt><dd>${psignaPendentActual.errorProcessant}&nbsp;</dd>
																<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
																	<form action="<c:url value="/expedient/documentPsignaReintentar.html"/>">
																		<input type="hidden" name="id" value="${instanciaProces.id}"/>
																		<input type="hidden" name="psignaId" value="${psignaPendentActual.documentId}"/>
																		<button class="submitButtonImage" type="submit">
																			<span class="nova-variable"></span><fmt:message key="common.icones.doc.psigna.reintentar"/>
																		</button>
																	</form>
																</security:accesscontrollist>
															</c:if>
														</dl>
													</div>
												</c:if>
												 --%>
											</td>
										</tr>
										<tr>
											<td>
												<spring:message code='expedient.document.data' /> <fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy"/>
											</td>
										</tr>
										<c:if test="${not empty document.dataCreacio}">
											<tr>
												<td>
													<spring:message code='expedient.document.adjuntat' /> <fmt:formatDate value="${document.dataCreacio}" pattern="dd/MM/yyyy hh:mm"/>
												</td>
											</tr>
										</c:if>
									</thead>
								</table>
							</c:if>
						</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="2">
							<strong class="nom_document">
								<c:choose>
									<c:when test="${not document.adjunt}">${document.documentNom}</c:when>
									<c:otherwise>${document.adjuntTitol}</c:otherwise>
								</c:choose>
							</strong><br/>
						</td>
					</tr>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>
</td>