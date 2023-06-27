<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="form" uri="/WEB-INF/tld/spring-form.tld"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>

<table style="width:100%">
	<tr>
		<td>
			<c:if test="${errMsg != null}">
				<div style="align:center; width: 99%; margin: auto;" class="ui-state-error ui-corner-all">
					<c:out value='${errMsg}' escapeXml="false" />
				</div>
			</c:if>
			<form:form commandName="form" id="form" class="jqtransform" action="sentOutsiteService.html?do=save">
				<table style="width:100%">
					<tr>
						<td style="width:19ch;"><label><fmt:message key="outsiteServiceID" />:</label></td>
						<td><div class="rowElem">${form.outsiteServiceID}<form:hidden path="outsiteServiceID" /></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="date" />:</label></td>
						<td><div class="rowElem">${form.outsiteServiceDate}</div></td>
					</tr>
					<tr>
						<td><div class="rowElem"><label><fmt:message key="outsiteServiceServiceType" />:</label></div></td>
						<td>
							<div class="rowElem">
								<label style="float:left; margin-top:0px;">
									<c:if test="${form.serviceType == 'warranty'}">
										<fmt:message key="outsiteServiceServiceType_inWarranty" />
									</c:if>
									<c:if test="${form.serviceType == 'repair'}">
										<fmt:message key="outsiteServiceServiceType_outWarranty" />
									</c:if>
								</label>
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="serviceOrderID" />:</label></td>
						<td><div class="rowElem">${form.serviceOrderID}</div></td>
					</tr>
					<tr>
						<td colspan="2">
							<fieldset style="background-color:#eeeeee; margin: 0 10px 0 10px; ">
								<legend class="ui-widget-header ui-corner-all">&nbsp;<fmt:message key="serviceOrder_data" />&nbsp;</legend>
								<table style="width:100%">
									<tr>
										<td style="width:13ch;"><label><fmt:message key="serviceOrderDate" />:</label></td>
										<td><div class="rowElem"><span id="serviceOrderDate">${form.serviceOrder.serviceOrderDate}&nbsp;</span></div></td>
									</tr>
									<tr>
										<td><label><fmt:message key="serviceOrderType" />:</label></td>
										<td>
											<div class="rowElem">
												<span id="serviceOrderType">
													<c:if test="${form.serviceOrder.serviceType == '1'}">
														<fmt:message key="serviceOrderType_guarantee" />&nbsp;<fmt:message key="guarantee_No" />&nbsp;<c:out value="${form.serviceOrder.guaranteeNo}"/>
													</c:if>
													<c:if test="${form.serviceOrder.serviceType == '2'}">
														<fmt:message key="serviceOrderType_repair" />
													</c:if>
													<c:if test="${form.serviceOrder.serviceType == '3'}">
														<fmt:message key="serviceOrderType_claim" />
													</c:if>
													<c:if test="${form.serviceOrder.serviceType == '4'}">
														<fmt:message key="serviceOrderType_outsiteService" />&nbsp;<fmt:message key="reference" />&nbsp;<c:out value="${form.serviceOrder.refJobID}"/>
													</c:if>
													<c:if test="${form.serviceOrder.serviceType == '5'}">
														<fmt:message key="serviceOrderType_refix" />&nbsp;<fmt:message key="reference" />&nbsp;<c:out value="${form.serviceOrder.refServiceOrder}"/>
													</c:if>
													&nbsp;
												</span>
											</div>
										</td>
									</tr>
									<tr>
										<td><label><fmt:message key="appointmentDate"/>:</label></td>
										<td><div class="rowElem"><span id="appointmentDate">${form.serviceOrder.appointmentDate}&nbsp;</span></div></td>
									</tr>
								</table>
							</fieldset>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<div class="rowElem"><br>&nbsp;&nbsp;&nbsp;<b><u><fmt:message key="customerDetail" /></u></b></div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="contactName" />:</label></td>
						<td><div class="rowElem"><span id="customerName">${form.customerName}</span></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="tel" />:</label></td>
						<td><div class="rowElem"><span id="tel">${form.tel}</span></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="mobileTel" />:</label></td>
						<td><div class="rowElem"><span id="mobileTel">${form.mobileTel}</span></div></td>
					</tr>
					<tr>
						<td colspan="2">
							<div class="rowElem"><br>&nbsp;&nbsp;&nbsp;<b><u><fmt:message key="productDetail" /></u></b></div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="type" />:</label></td>
						<td>
							<div class="rowElem">
								<span id="type">${form.typeName}</span>
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="brand" />:</label></td>
						<td>
							<div class="rowElem">
								<span id="brand">${form.brandName}</span>
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="model" />:</label></td>
						<td>
							<div class="rowElem">
								<span id="model">${form.modelName}</span>
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="serialNo" />:</label></td>
						<td><div class="rowElem"><span id="serialNo">${form.serialNo}</span></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="outsiteService_accessories" />:</label></td>
						<td><div class="rowElem">${form.accessories}</div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="serviceOrder_problem" />:</label></td>
						<td align="left"><div class="rowElem"><span id="os_problem" >${form.problem}&nbsp;</span></div></td>
					</tr>
					<tr>
						<td><div class="rowElem"><label><fmt:message key="outsiteService_outsiteCompany" />:</label></div></td>
						<td>
							<div class="rowElem">
								${form.outsiteCompanyName }
							</div>
						</td>
					</tr>
					<tr>
						<td><div class="rowElem"><label><fmt:message key="transportCompany" />:</label></div></td>
						<td>
							<div class="rowElem">
								${form.transportCompanyName }
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="outsiteService_sentDate" />:<font color="red">*</font></label></td>
						<td><div class="rowElem"><form:input class="required textboxMockup" id="sentDate" path="sentDate" readonly="readonly" size="10"/></div></td>
					</tr>
					<tr>	
						<td><label><fmt:message key="outsiteService_sentTransportNo" />:<font color="red">*</font></label></td>
						<td><div class="rowElem"><form:input class="required textboxMockup" path="sentTransportNo" maxlength="20"/></div></td>
					</tr>
					<tr>
						<td colspan="2" align="center"><div class="rowElem"><input type="submit" value="<fmt:message key='button.ok' />" /></div></td>
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
			serviceOrderID: "required"
		}
	});
	
	//find all form with class jqtransform and apply the plugin
	$("form.jqtransform").jqTransform();
	
	//$('#sentDate').calendarsPicker($.extend({calendar: $.calendars.instance('thai','th')}));
	$('#sentDate').calendarsPicker($.extend({calendar: $.calendars.instance('gregorian','th')}));
	
});



</script>