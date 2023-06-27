<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="form" uri="/WEB-INF/tld/spring-form.tld"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>

<table width="100%">
	<tr>
		<td>
			<form:form commandName="searchForm" id="searchForm" class="jqtransform" onsubmit="gridReload(); return false;" method="post">
				<table>
					<tr>
						<td><label><fmt:message key="date" />:</label></td>
						<td><div class="rowElem" style="z-index:200"><form:input path="startDate" class="textboxMockup" id="dateInput" size="9"/><span style="float:left">&nbsp;-&nbsp;</span><form:input path="endDate" type="text" class="textboxMockup" id="endDateInput" size="9"/></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="partID" />:</label></label></td>
						<td>
							<div class="rowElem">
								<form:select path="code" id="code" cssClass="selectSearch">
									<form:options items="${codeList}" />
								</form:select>
							</div>
						</td>
					</tr>
					<%--tr>
						<td><label><fmt:message key="type" />:</label></td>
						<td>
							<div class="rowElem">
								<form:select path="typeID" id="type" cssClass="selectSearch">
									<form:option value="" label="All"/>
									<form:options items="${typeList}" itemValue="typeID" itemLabel="name"/>
								</form:select>
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="brand" />:</label></td>
						<td id="brandRow">
							<div class="rowElem" style="z-index:100">
								<form:select path="brandID" id="brand" cssClass="selectSearch">
									<form:option value="" label="All"/>
									<form:options items="${brandList}" itemValue="brandID" itemLabel="name"/>
								</form:select>
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="model" />:</label></td>
						<td id="modelRow">
							<div class="rowElem">
								<form:select path="modelID" id="model" cssClass="selectSearch">
									<form:option value="" label="All"/>
								</form:select>
							</div>
						</td>
					</tr--%>
					<%-- tr>
						<td><label><fmt:message key="employee" />:</label></td>
						<td>
							<div class="rowElem" style="z-index:100">
								<form:select path="employeeID" id="employeeID" cssClass="selectSearch">
									<form:option value="">All</form:option>
									<c:forEach items="${employeeList}" var="employee">
										<form:option value="${employee.employeeID}">${employee.name} ${employee.surname}</form:option>
									</c:forEach>
								</form:select>
							</div>
						</td>
					</tr--%>
					<tr>
						<td colspan="2"><div class="rowElem"><input type="submit" id="searchButton" value="<fmt:message key='button.search' />" /></div></td>
					</tr>
				</table>
			</form:form>
		</td>
	</tr>
	<tr>
		<td>
			<table width="99%" style="margin:0px auto 0px auto;">
				<tr>
					<td>
						<table id="list"></table>
						<div id="pager"></div>
						
						<div id="dialog" title="Feature not supported" style="display:none">
							<p>That feature is not supported.</p>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<script type="text/javascript">
	$("#dateInput").calendarsPicker($.extend({calendar: $.calendars.instance('gregorian','th')}));
	$("#endDateInput").calendarsPicker($.extend({calendar: $.calendars.instance('gregorian','th')}));

	//jQuery().ready(function (){
		
	
	
	jQuery().ready(function (){
		
		jQuery("#list").jqGrid({
			url:"searchIssuePart.html?code="+jQuery("#code").val(),
			datatype: "json",
			height: "100%",
			autowidth: true,
			colNames:['<fmt:message key="serviceOrderDate" />','<fmt:message key="serviceOrderID" />','<fmt:message key="serviceOrder_empFix" />','<fmt:message key="returnDate" />','<fmt:message key="amount" />','<fmt:message key="price" />'],
			colModel:[
				{name:'serviceOrderDate',index:'serviceOrderDate', align:'center'},
				{name:'serviceOrderID',index:'serviceOrderID', align:'center'},
				{name:'fixEmp_name',index:'fixEmp_name'},
				{name:'returnDate',index:'returnDate', align:'center'},
				{name:'quantity',index:'quantity', align:'right'},
				{name:'totalPrice',index:'totalPrice', align:'right'}],
			multiselect: false,
			rownumbers: true,
			rowNum:10,
			rowList:[10,20,30,40,50],
			viewrecords: true,
			jsonReader:{
				repeatitems: false,
				id: "serviceOrderID"
			},
			pager: '#pager',
			toppager: true
		}).navGrid("#pager",{edit:false,add:false,del:false,search:false,refresh:false,cloneToTop:true});
		// remove unuse button
		var topPagerDiv = $("#list_toppager")[0];
		$("#list_toppager_center", topPagerDiv).remove();
		$(".ui-paging-info", topPagerDiv).remove();
		
		var bottomPagerDiv = $("div#pager")[0];
		$("#search_list", bottomPagerDiv).remove();	
		
		
		
	
		//find all form with class jqtransform and apply the plugin
		$("form.jqtransform").jqTransform();
		
		$("#searchForm").validate({
			rules: {
				//startDate: "required"
			}
		});
	});
	
	function gridReload(){
		var startDate = jQuery("#dateInput").val();
		var endDate = jQuery("#endDateInput").val();
		var modelID = jQuery("#model").val();
		var code = jQuery("#code").val();
		jQuery("#list").jqGrid('setGridParam',{url:"searchIssuePart.html?code="+code+"&startDate="+startDate+"&endDate="+endDate,page:1}).trigger("reloadGrid");
	}
	
	jQuery(window).bind('resize', function() {
		// Get width of parent container
		var width = $('.ui-widget-content')[0].clientWidth;
		if (width == null || width < 1){
			// For IE, revert to offsetWidth if necessary
			width = jQuery('.ui-widget-content')[0].offsetWidth;
		}
		width = width - 24; // Fudge factor to prevent horizontal scrollbars
		if (width > 0 &&
			// Only resize if new width exceeds a minimal threshold
			// Fixes IE issue with in-place resizing when mousing-over frame bars
			Math.abs(width - jQuery("#list").width()) > 5)
		{
			jQuery("#list").setGridWidth(width);
		}
	}).trigger('resize');
</script>