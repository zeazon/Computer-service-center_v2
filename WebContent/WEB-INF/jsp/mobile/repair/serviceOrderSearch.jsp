<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="form" uri="/WEB-INF/tld/spring-form.tld"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>

<table width="100%">
	<tr>
		<td>
			<form:form commandName="searchForm" id="searchForm" class="jqtransform" method="post" onsubmit="gridReload(); return false;">
				<table>
					<tr>
						<td><label><fmt:message key="serviceOrderID" />:</label></td>
						<td><div class="rowElem"><form:input path="serviceOrderID" type="text" id="serviceOrderID" class="textboxMockup" /></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="name" />:</label></td>
						<td><div class="rowElem"><form:input path="name" type="text" id="name" class="textboxMockup" /></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="mobileTel" />:</label></td>
						<td><div class="rowElem"><form:input path="mobileTel" type="text" id="mobileTel" class="textboxMockup" /></div></td>
					</tr>
					<%--r>
						<td><label><fmt:message key="surname" />:</label></td>
						<td><div class="rowElem"><form:input path="surname" type="text" id="surname" class="textboxMockup" /></div></td>
					</tr--%>
					<!-- tr>
						<td><label><fmt:message key="date" />:</label></td>
						<td><div class="rowElem" style="z-index:200"><form:input path="date" type="text" class="textboxMockup" id="dateInput" size="9"/></div></td>
					</tr-->
					<tr>
						<td><label><fmt:message key="date" />:</label></td>
						<td><div class="rowElem" style="z-index:200"><form:input path="startDate" class="textboxMockup" id="dateInput" size="9"/><span style="float:left">&nbsp;-&nbsp;</span><form:input path="endDate" type="text" class="textboxMockup" id="endDateInput" size="9"/></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="type" />:</label></td>
						<td>
							<div class="rowElem" style="z-index:100">
								<form:select path="type" id="type" cssClass="selectSearch">
									<form:option value="">All</form:option>
									<form:options items="${typeList}" itemValue="typeID" itemLabel="name"/>
								</form:select>
								<%--form:checkboxes path="type" items="${typeList}" itemValue="typeID" itemLabel="name" element="div style='float:left'" /--%>
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="serialNo" />:</label></td>
						<td><div class="rowElem"><form:input path="serialNo" class="textboxMockup" id="serialNo"/></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="serviceOrder_empFix" />:</label></td>
						<td>
							<form:select path="employee" id="employee" cssClass="selectSearch">
								<form:option value="">All</form:option>
								<%--form:options items="${employeeList}" itemValue="employeeID" itemLabel="name"/--%>
								<c:forEach items="${employeeList}" var="employee">
									<form:option value="${employee.employeeID}">${employee.name} ${employee.surname}</form:option>
								</c:forEach>
							</form:select>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="issuePartCode" />:</label></td>
						<td><div class="rowElem"><form:input path="issuePartCode" class="textboxMockup" id="issuePartCode"/></div></td>
					</tr>
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
						
						<form id="editForm" action="serviceOrder.html?do=preEdit" method="post">
							<input type="hidden" name="serviceOrderID"/>
						</form>
						
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
	//$("#dateInput").calendarsPicker($.extend({calendar: $.calendars.instance('thai','th')}));
	//$("#endDateInput").calendarsPicker($.extend({calendar: $.calendars.instance('thai','th')}));
	$("#dateInput").calendarsPicker($.extend({calendar: $.calendars.instance('gregorian','th')}));
	$("#endDateInput").calendarsPicker($.extend({calendar: $.calendars.instance('gregorian','th')}));
	
	jQuery().ready(function (){
		//find all form with class jqtransform and apply the plugin
		$("form.jqtransform").jqTransform();

		jQuery("#list").jqGrid({
			url:"searchServiceOrder.html",
			datatype: "json",
			height: "100%",
			//autowidth: true,
			shrinkToFit:false,
			width: $('.ui-widget-content')[0].clientWidth - 10,
			colNames:['<fmt:message key="serviceOrderID" />','<fmt:message key="date" />','<fmt:message key="name" />','<fmt:message key="tel" />','<fmt:message key="mobileTel" />','<fmt:message key="type" />','<fmt:message key="brand" />','<fmt:message key="model" />','<fmt:message key="serialNo" />','<fmt:message key="serviceOrder_empFix" />','<fmt:message key="status" />','<fmt:message key="cannotMakeContact" />'],
			colModel:[
				{name:'serviceOrderID',index:'serviceOrderID'},
				{name:'serviceOrderDate', index:'serviceOrderDate', align:'center', sorttype:'date',formatter:'date', formatoptions: {srcformat:'d/m/Y',newformat:'d/m/Y'}, width:'100', firstSortOrder:'desc'},
				{name:'name',index:'name'},				
				{name:'tel',index:'tel', sortable:false},
				{name:'mobileTel',index:'mobileTel', sortable:false},
				{name:'type',index:'type', sortable:false},
				{name:'brand',index:'brand', sortable:false},
				{name:'model',index:'model', sortable:false},
				{name:'serialNo',index:'serialNo', sortable:false},
				{name:'empFix',index:'empFix', sortable:true},
				{name:'status',index:'status', formatter:statusFormatter, align:'center'},
				{name:'cannotMakeContact',index:'cannotMakeContact',hidden:true}],
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
			toppager: true,
			loadComplete: function(data){
				$.each(data.rows,function(i,item){
					if(data.rows[i].cannotMakeContact == 1){
						$("#" + data.rows[i].serviceOrderID).find("td").css("color", "red");
						//$("#" + data.rows[i].serviceOrderID).find("td").css("background-color", "#FF1111");
						//$("#" + data.rows[i].serviceOrderID).find("td").css("color", "silver");
					}
		        });
		    }
		}).navGrid("#pager",{edit:false,add:false,del:false,search:false,refresh:false,cloneToTop:true})
		.navButtonAdd('#list_toppager',
		{
			caption:"<fmt:message key='button.add' />",
			title:"<fmt:message key='button.add' />",
			buttonicon:"ui-icon-plus",
			onClickButton: function(){ 
				window.location = '<c:url value="/serviceOrder.html?do=preAdd" />';
			}, 
			position:"last"
		})
		.navButtonAdd('#list_toppager',
		{
			caption:"<fmt:message key='button.edit' />",
			title:"<fmt:message key='button.edit' />",
			buttonicon:"ui-icon-pencil", 
			onClickButton: function(){ 
				var gsr = jQuery("#list").getGridParam('selrow');
				 
				//var rowData = jQuery(this).getRowData(gsr); 
                //var temp= rowData['status'];//replace name with you column
                //if(temp == "<fmt:message key='serviceOrder_status_close' />"){
                //	alert('close already');
                //}

				if(gsr){
					jQuery("#list").GridToForm(gsr,"#editForm");
					$("#editForm").submit();
				} else {
					jQuery("#dialog").text('<fmt:message key='msg.pleaseSelectRow' />');
					jQuery("#dialog").dialog( 
						{
							title: 'Alert',
					      	modal: true,
					     	buttons: {"Ok": function()  {
					     		jQuery(this).dialog("close");} 
					      	}
				    });
				}
			}, 
			position:"last"
		})
		.navButtonAdd('#list_toppager',
		{
			caption:"<fmt:message key='button.delete' />",
			title:"<fmt:message key='button.delete' />",
			buttonicon:"ui-icon-trash",
			onClickButton: function(){
				var gr = jQuery("#list").getGridParam("selrow");
				if( gr != null ) {
					jQuery("#list").delGridRow(gr,{
						beforeShowForm: function(form) {
							jQuery(".delmsg").replaceWith('<span style="white-space: pre;">' +
								'<fmt:message key="msg.deleteSelectedRecord" />' + '</span>');
							//jQuery('#dData').html('Del').removeClass('fm-button-icon-left');
							
						},
						afterSubmit: function(xhr,postdata){
							var result = eval('(' + xhr.responseText + ')');
							var errors = "";
							if (result.success == false) {
								for (var i = 0; i < result.message.length; i++) {
									errors +=  result.message[i] + "";
								}
							}else{
								jQuery("#dialog").text('<fmt:message key="msg.deleteSuccess" />');
								jQuery("#dialog").dialog( 
									{
										title: 'Success',
								      	modal: true,
								     	buttons: {"Ok": function()  {
								     		jQuery(this).dialog("close");} 
								      	}
							    });
							}
							// only used for adding new records
			                var new_id = null;
							return [result.success, errors, new_id];
						}, 
						//url: 'serviceOrderID.html?do=delete&serviceOrderID='+gr}); 
						url: 'serviceOrder.html?do=delete&serviceOrderID='+gr});
				} else { 
					jQuery("#dialog").text('<fmt:message key='msg.pleaseSelectRow' />');
					jQuery("#dialog").dialog( 
						{
							title: 'Alert',
					      	modal: true,
					     	buttons: {"Ok": function()  {
					     		jQuery(this).dialog("close");} 
					      	}
				    });
				};
			},
			position:"last"
		});
		// remove unuse button
		var topPagerDiv = $("#list_toppager")[0];
		$("#list_toppager_center", topPagerDiv).remove();
		$(".ui-paging-info", topPagerDiv).remove();
		
		var bottomPagerDiv = $("div#pager")[0];
		$("#search_list", bottomPagerDiv).remove();
		
	});

	
	function statusFormatter (cellvalue, options, rowObject)
	{	
		if(cellvalue == 'new'){
			return "<fmt:message key='serviceOrder_status_new' />";
		}else if(cellvalue == 'fixing'){
			return "<fmt:message key='serviceOrder_status_fixing' />";
		}else if(cellvalue == 'outsite'){
			return "<fmt:message key='serviceOrder_status_outsite' />";
		}else if(cellvalue == 'sent'){
			return "<fmt:message key='outsiteService_status_sent' />";
		}else if(cellvalue == 'received'){
			return "<fmt:message key='outsiteService_status_received' />";
		}else if(cellvalue == 'fixed'){
			return "<fmt:message key='serviceOrder_status_fixed' />";
		}else if(cellvalue == 'close'){
			return "<fmt:message key='serviceOrder_status_close' />";
		}
		return cellvalue;
	}
	
	function gridReload(){
		var serviceOrderID = jQuery("#serviceOrderID").val();
		var name = jQuery("#name").val();
		var surname = jQuery("#surname").val();
		var mobileTel = jQuery("#mobileTel").val();
		var startDate = jQuery("#dateInput").val();
		var endDate = jQuery("#endDateInput").val();
		var type = jQuery("#type").val();
		var serialNo = jQuery("#serialNo").val();
		var employee = jQuery("#employee").val();
		var issuePartCode = jQuery("#issuePartCode").val();
/*		
		console.log("gridReload:before call Ajax");
		$.ajax({
			type: "POST",
			dataType: "json",
			contentType: "application/json; charset=UTF-8",
			url: "searchAjaxGetServiceOrder.html",
			cache: false,
			headers: { 
			    Accept : "application/json"
			},
//			data: "name="+name+"&surname="+surname+"&startDate="+startDate+"&endDate="+endDate+"&type="+type+"&serialNo="+serialNo+"&employee="+employee,
			data: JSON.stringify({
			    "subject:name":name,
			    "subject:surname":surname,
			    "subject:startDate":startDate,
			    "subject:endDate":endDate,
			    "subject:type":type,
			    "subject:serialNo":serialNo,
			    "subject:employee":employee,
			    "subject:rows":10,
			    "subject:page":1,
			    "subject:sidx":"",
			    "subject:sord":"",
		//	    "sub:tags": ["facebook:work", "facebook:likes"],
			    "sampleSize" : 10//,
		//	    "values": ["science", "machine-learning"]
			}),

			beforeSend: function(msg){
				console.log("ajax before send");
			},
			success: function(msg){
				alert( "successs: Data Call : " + msg);
			},
			error: function(msg){
				alert("searchAjaxGetServiceOrder error : textStatus:" + msg.textStatus + " , errorThrown : "+msg.errorThrown + ", jqXHR : "+msg.jqXHR);
			},
			complete: function(msg){
				console.log("complete : "+msg.textStatus + " jqXHR : "+msg.jqXHR);
			}
		});
*/		
//		jQuery("#list").jqGrid('setGridParam',{url:"searchServiceOrder.html?name="+name+"&surname="+surname+"&startDate="+startDate+"&endDate="+endDate+"&type="+type+"&serialNo="+serialNo+"&employee="+employee,page:1 , ajaxGridOptions: { contentType: 'application/json; charset=utf-8' } }).trigger("reloadGrid");
		jQuery("#list").jqGrid('setGridParam',{url:"searchServiceOrder.html?serviceOrderID="+serviceOrderID+"&name="+name+"&surname="+surname+"&mobileTel="+mobileTel+"&startDate="+startDate+"&endDate="+endDate+"&type="+type+"&serialNo="+serialNo+"&employee="+employee+"&issuePartCode="+issuePartCode,page:1}).trigger("reloadGrid");
	}
	
	jQuery(window).bind('resize', function() {
		// Get width of parent container
		var width = $('.ui-widget-content')[0].clientWidth;
		if (width == null || width < 1){
			// For IE, revert to offsetWidth if necessary
			width = jQuery('.ui-widget-content')[0].offsetWidth;
		}
		// change width - 8 to width - 10
		width = width - 10; // Fudge factor to prevent horizontal scrollbars
		if (width > 0 &&
			// Only resize if new width exceeds a minimal threshold
			// Fixes IE issue with in-place resizing when mousing-over frame bars
			Math.abs(width - jQuery("#list").width()) > 5)
		{
			jQuery("#list").setGridWidth(width);
		}
	}).trigger('resize');
</script>