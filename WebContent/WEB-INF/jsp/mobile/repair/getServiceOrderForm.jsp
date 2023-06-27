<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="form" uri="/WEB-INF/tld/spring-form.tld"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="fn" uri="/WEB-INF/tld/fn.tld"%>

<table style="width:100%">
	<tr>
		<td>
			<c:if test="${errMsg != null}">
				<div style="align:center; width: 99%; margin: auto;" class="ui-state-error ui-corner-all">
					<c:out value='${errMsg}' escapeXml="false" />
				</div>
			</c:if>
			<form:form commandName="form" id="form" class="jqtransform" action="getServiceOrder.html?do=accept">
				<input type="hidden" name="mode" value="${mode}"/>
				<form:hidden path="status"/>
				<table style="width:98vw;">
					<tr>
						<td style="width:19ch;"><label><fmt:message key="serviceOrderDate" />:</label></td>
						<td align="left">
							<div class="rowElem">${form.serviceOrderDate}</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="serviceOrderID" />:</label></td>
						<td><div class="rowElem">${form.serviceOrderID}<form:hidden path="serviceOrderID"/></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="serviceOrderType" />:</label></td>
						<td>
							<div class="rowElem">
								<form:radiobutton path="serviceType" value="1" cssStyle="margin-top:4px" id="serviceType_guarantee" disabled="true" /><label style="float:left; margin-top:4px"><fmt:message key="serviceOrderType_guarantee" /></label><form:select path="guaranteeNo" id="guaranteeNo" class="disabled"><c:forEach var="i" begin="1" end="7" step="1"><form:option value="${i}" /></c:forEach></form:select><br/><br/>
								<form:radiobutton path="serviceType" value="2" cssStyle="margin-top:4px;" id="serviceType_repair" disabled="true" /><label style="float:left; margin-top:4px"><fmt:message key="serviceOrderType_repair" /></label><br/><br/>
								<form:radiobutton path="serviceType" value="3" cssStyle="margin-top:4px" id="serviceType_claim" disabled="true" /><label style="float:left; margin-top:4px"><fmt:message key="serviceOrderType_claim" /></label><br/><br/>
								<form:radiobutton path="serviceType" value="4" cssStyle="margin-top:4px" id="serviceType_outsiteService" disabled="true" /><label style="float:left; margin-top:4px"><fmt:message key="serviceOrderType_outsiteService" /></label> <form:input path="refJobID" class="textboxMockup" id="refJobID" maxlength="30" readonly="true" /><br/><br/><br/>
								<form:radiobutton path="serviceType" value="5" cssStyle="margin-top:4px" id="serviceType_refix" disabled="true" /><label style="float:left; margin-top:4px;"><fmt:message key="serviceOrderType_refix" /></label><form:input path="refServiceOrder" class="textboxMockup" id="refServiceOrder" maxlength="20" readonly="true" />
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="appointmentDate"/>:</label></td>
						<td>
							<div class="rowElem" style="z-index:200">${form.appointmentDate}</div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<table style="width:100%" cellpadding="0" cellspacing="0">
								<col style="width:19ch;">
								<col>
								<col style="width:5ch;">
								<col>
								<tr align="left">
								<tr>
									<td colspan="4">
										<div class="rowElem"><br>&nbsp;&nbsp;&nbsp;<b><u><fmt:message key="productDetail" /></u></b></div>
									</td>
								</tr>
								<tr>
									<td><label><fmt:message key="productID" />:<c:if test="${mode == 'add'}"><font color="red">*</font></c:if></label></td>
									<td colspan="3">
										<div class="rowElem">${form.productID}</div>
									</td>
								</tr>
								<tr>
									<td><label><fmt:message key="type" />:</label></td>
									<td>
										<div class="rowElem">
											<span id="typeTxt">${product.type.name}&nbsp;</span>
										</div>
									</td>
									<td><label><fmt:message key="brand" />:</label></td>
									<td>
										<div class="rowElem">
											<span id="brandTxt">${product.brand.name}&nbsp;</span>
										</div>
									</td>
								</tr>
								<tr>
									<td><label><fmt:message key="model" />:</label></td>
									<td colspan="3"><div class="rowElem"><span id="modelTxt">${product.model.name}</span></span></div></td>
								</tr>
								<tr>
									<td><label><fmt:message key="serialNo" />:</label></td>
									<td colspan="3"><div class="rowElem"><span id="serialNoTxt">${product.serialNo}</span></div></td>
								</tr>
								<tr>
									<td><label><fmt:message key="accessories" />:</label></td>
									<td colspan="3">
										<div class="rowElem">${form.accessories}</div>
									</td>
								</tr>
								<tr>
									<td><label><fmt:message key="serviceOrder_desc" />:</label></td>
									<td colspan="3">
										<div class="rowElem">${form.desc}</div>
									</td>
								</tr>		
							</table>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="serviceOrder_problem" />:</label></td>
						<td align="left">
							<div class="rowElem">${form.problem}</div>
						</td>
					</tr>
					<tr align="left">
						<td colspan="2">
							<div class="rowElem"><br>&nbsp;&nbsp;&nbsp;<b><u><fmt:message key="customerDetail" /></u></b></div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="customerID" />:</label></td>					
						<td align="left">
							<div class="rowElem">${form.customerID}</div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<table style="width:100%" cellpadding="0" cellspacing="0">
								<col style="width:19ch;">
								<col>
								<tr>
									<td><label><fmt:message key="contactName" />:</label></td>
									<td><div class="rowElem"><span id="contactName">${customer.name}&nbsp;</span></div></td>
								</tr>
								<tr>
									<td><label><fmt:message key="email" />:</label></td>
									<td><div class="rowElem"><span id="email">${customer.email}&nbsp;</span></div></td>
								</tr>
								<tr>	
									<td><label><fmt:message key="tel" />:</label></td>
									<td><div class="rowElem"><span id="tel">${customer.tel}&nbsp;</span></div></td>
								</tr>
								<tr>
									<td><label><fmt:message key="mobileTel"/>:</label></td>
									<td><div class="rowElem"><span id="mobileTel">${customer.mobileTel}&nbsp;</span></div></td>
								</tr>
								<tr>
									<td><label><fmt:message key="address" />:</label></td>
									<td><div class="rowElem"><span id="address">${fullAddr}&nbsp;</span></div></td>
								</tr>
								<tr>
									<td><label><fmt:message key="deliveryCustomer" />:</label></td>
									<td>
										<div class="rowElem">${form.deliveryCustomer}</div>
									</td>
								</tr>
								<tr>
									<td><label><fmt:message key="email" />:</label></td>
									<td>
										<div class="rowElem">${form.deliveryEmail}</div>
									</td>
								</tr>
								<tr>
									<td><label><fmt:message key="tel" />:</label></td>
									<td>
										<div class="rowElem">${form.deliveryTel}</div>
									</td>
								</tr>
								<tr>
									<td><label><fmt:message key="mobileTel"/>:</label></td>
									<td>
										<div class="rowElem">${form.deliveryMobileTel}</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					
					<tr align="center">
						<td colspan="2"><div class="rowElem"><input type="submit" value="<fmt:message key='button.ok' />" /></div></td>
					</tr>					
				</table>
			</form:form>
		</td>
	</tr>
</table>

<div id="dialog" title="Feature not supported" style="display:none">
	<p>That feature is not supported.</p>
</div>

<script type="text/javascript">


$(document).ready(function(){
	
	$("#guaranteeNo_autoComplete").css('width','20px');
	
	//find all form with class jqtransform and apply the plugin
	$("form.jqtransform").jqTransform();		
	
});

</script>