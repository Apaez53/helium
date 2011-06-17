<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
	<head>
		<title>Tipus d'expedient: ${expedientTipus.nom}</title>
		<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
		<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
		<c:import url="../common/formIncludes.jsp"/>
		<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
		
		<script type="text/javascript" language="javascript">
			// <![CDATA[
			function confirmar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
				return confirm("<fmt:message key='enumeracio.valors.confirmacio' />");
			}
			// ]]>
		</script>
	</head>
	
	<body>
		<c:import url="../common/tabsExpedientTipus.jsp">
			<c:param name="tabActiu" value="enum"/>
		</c:import>
	
		<display:table name="llistat" id="registre" requestURI="" class="displaytag">
			<display:column property="codi" titleKey="comuns.codi" sortable="true" />
			<display:column property="nom" titleKey="comuns.titol" sortable="true"/>
			<display:column>
				<a href="<c:url value="/expedientTipus/enumeracioValorsEsborrar.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
			</display:column>
		</display:table>
		<script type="text/javascript">initSelectable();</script>
		
		<form:form action="enumeracioValors.html" cssClass="uniForm" method="post">
			<div class="inlineLabels">
				<input type="hidden" name="enumeracio" value="${enumeracio}" />
				<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
				<c:if test="${not empty command.id}">
					<form:hidden path="id"/>
				</c:if>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="codi"/>
					<c:param name="required" value="true"/>
					<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="nom"/>
					<c:param name="required" value="true"/>
					<c:param name="label"><fmt:message key='comuns.titol' /></c:param>
				</c:import>
			</div>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit,cancel</c:param>
				<c:param name="titles"><fmt:message key='comuns.afegir' />,<fmt:message key='comuns.cancelar' /></c:param>
			</c:import>
		</form:form>
		
		<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>
	</body>
</html>
