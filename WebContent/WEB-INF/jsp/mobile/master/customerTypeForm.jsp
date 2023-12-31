<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="form" uri="/WEB-INF/tld/spring-form.tld"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>

<table width="100%">
	<tr>
		<td>
			<c:if test="${errMsg != null}">
				<div style="align:center; width: 99%; margin: auto;" class="ui-state-error ui-corner-all">
					<c:out value='${errMsg}' escapeXml="false" />
				</div>
			</c:if>
			<form:form commandName="form" id="form" class="jqtransform" action="customerType.html?do=save">
				<input type="hidden" name="mode" value="${mode}" />
				<table width="100%">
					<tr>
						<td width="40%"><label><fmt:message key="customerTypeID" />:<font style="color:red">*</font></label></td>
						<td>
							<div class="rowElem">
								<c:choose>
									<c:when test="${mode == 'add'}">
										<form:input path="customerTypeID" maxlength="2" class="textboxMockup" /> <label class="error" for="typeID" generated="true" style="display: none; padding-left:10px"></label>	
									</c:when>
									<c:otherwise>
										<form:input path="customerTypeID" maxlength="2" class="textboxMockup" readonly="true" />
									</c:otherwise>
								</c:choose>
								
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="name" />:<font style="color:red">*</font></label></td>
						<td><div class="rowElem"><form:input type="text" path="name" maxlength="255" class="textboxMockup" /> <label class="error" for="name" generated="true" style="display: none; padding-left:10px"></label></div></td>
					</tr>
					<tr align="center">
						<td colspan="2"><div class="rowElem"><input type="submit" value="<fmt:message key='button.ok' />" /></div></td>
					</tr>
				</table>
			</form:form>
		</td>
	</tr>
</table>

<script type="text/javascript">

$(document).ready(function(){
	$("#form").validate({
		rules: {
			customerTypeID: "required",
			name: "required"
		}
	});
	
	//find all form with class jqtransform and apply the plugin
	$("form.jqtransform").jqTransform();
	
	//$('#backButton', this).jqTransInputButton();
	$('#backButton', this).button({
        icons: {
            primary: "ui-icon-circle-arrow-w"
         },
         text:false
    });
});

</script>