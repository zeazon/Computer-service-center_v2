<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="form" uri="/WEB-INF/tld/spring-form.tld"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>

<table style="width:100%">
	<tr>
		<td>
			<form:form commandName="searchForm" id="searchForm" class="jqtransform" method="post" onsubmit="gridReload(); return false;">
				<table>
					<tr>
						<td><label><fmt:message key="name" />:</label></td>
						<td><div class="rowElem"><form:input path="name" type="text" id="name" class="textboxMockup" /></div></td>
					</tr>
					<tr>	
						<td><label><fmt:message key="serialNo" />:</label></td>
						<td><div class="rowElem"><form:input path="serialNo" id="serialNo" class="textboxMockup" /></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="transportCompany" />:</label></td>
						<td>
							<div class="rowElem" style="z-index:100">
								<form:select path="transportCompanyID" id="transportCompanyID" cssClass="selectSearch">
									<form:option value="" style="text-overflow: ellipsis; width:50px;">All</form:option>
									<form:options items="${transportCompanyList}" itemValue="transportCompanyID" itemLabel="name" />
								</form:select>
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="date" />:</label></td>
						<td><div class="rowElem" style="z-index:200"><form:input path="date" type="text" class="textboxMockup" id="dateInput" size="9"/></div></td>
					</tr>
					<tr>	
						<td><label><fmt:message key="refOutsiteJobID" />:</label></td>
						<td><div class="rowElem"><form:input path="refOutsiteJobID" id="refOutsiteJobID" /></div></td>
					</tr>
					<tr>
						<td><label><fmt:message key="status" />:</label></td>
						<td>
							<form:select path="status" id="status" cssClass="selectSearch">
								<form:option value="">All</form:option>
								<form:option value="new"><fmt:message key='outsiteService_status_new' /></form:option>
								<form:option value="sent"><fmt:message key='outsiteService_status_sent' /></form:option>
								<form:option value="close"><fmt:message key='outsiteService_status_close' /></form:option>
							</form:select>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="type" />:</label></td>
						<td>
							<div class="rowElem" style="z-index:100">
								<form:select path="type" id="type" cssClass="selectSearch">
									<form:option value="">All</form:option>
									<form:options items="${typeList}" itemValue="typeID" itemLabel="name"/>
								</form:select>
							</div>
						</td>
					</tr>
					<tr>
						<td><label><fmt:message key="outsiteService_outsiteCompany" />:</label></td>
						<td>
							<div class="rowElem" style="z-index:100">
								<form:select path="outsiteCompanyID" id="outsiteCompanyID" cssClass="selectSearch">
									<form:option value="">All</form:option>
									<form:options items="${outsiteCompanyList}" itemValue="outsiteCompanyID" itemLabel="name"/>
								</form:select>
							</div>
						</td>
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
			<table style="width:99%" style="margin:0px auto 0px auto;">
				<tr>
					<td>
						<table id="list"></table>
						<div id="pager"></div>
						
						<form id="editForm" action="outsiteService.html?do=preEdit" method="post">
							<input type="hidden" name="outsiteServiceID"/>
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
	$("#dateInput").calendarsPicker($.extend({calendar: $.calendars.instance('gregorian','th')}));

	jQuery().ready(function (){
		//find all form with class jqtransform and apply the plugin
		$("form.jqtransform").jqTransform();
	
		$('#type_autoComplete').width($('#type').width());
		$('#outsiteCompanyID_autoComplete').width($('#outsiteCompanyID').width());
		$('#transportCompanyID_autoComplete').width($('#transportCompanyID').width());
		$('#status_autoComplete').width($('#status').width());
		
		jQuery("#list").jqGrid({
			url:"searchOutsiteService.html",
			datatype: "json",
			height: "100%",
			//autowidth: true,
			shrinkToFit:false,
			width: $('.ui-widget-content')[0].clientWidth - 10,
			colNames:['<fmt:message key="outsiteServiceID" />','<fmt:message key="date" />','<fmt:message key="name" />','<fmt:message key="type" />','<fmt:message key="brand" />','<fmt:message key="model" />','<fmt:message key="serialNo" />','<fmt:message key="serviceOrder_problem" />','<fmt:message key="outsiteService_outsiteCompany" />','<fmt:message key="refOutsiteJobID" />','<fmt:message key="transportCompany" />','<fmt:message key="status" />','serviceOrderID'],
			colModel:[
				{name:'outsiteServiceID',index:'outsiteServiceID', width:'110'},
				{name:'outsiteServiceDate', index:'outsiteServiceDate', align:'center', sorttype:'date',formatter:'date', formatoptions: {srcformat:'d/m/Y',newformat:'d/m/Y'}, width:'100', firstSortOrder:'desc'},
				{name:'name',index:'name'},
				{name:'type',index:'type', sortable:false},
				{name:'brand',index:'brand', sortable:false},
				{name:'model',index:'model', sortable:false},
				{name:'serialNo',index:'serialNo', sortable:false},
				{name:'problem',index:'problem', sortable:false},
				{name:'outsiteCompanyName',index:'outsiteCompanyName', sortable:true},
				{name:'refOutsiteJobID',index:'refOutsiteJobID', sortable:true},
				{name:'transportCompanyName',index:'transportCompanyName', sortable:true},
				{name:'status',index:'status', formatter:statusFormatter, align:'center', sortable:false, width:'100'},
				{name:'serviceOrderID',index:'serviceOrderID', hidden:true}],
			multiselect: false,
			rownumbers: true,
			rowNum:10,
			rowList:[10,20,30,40,50],
			viewrecords: true,
			jsonReader:{
				repeatitems: false,
				id: "outsiteServiceID"
			},
			pager: '#pager',
			toppager: true,
			loadComplete: function(data){
				$.each(data.rows,function(i,item){
					if(data.rows[i].status == 'sent'){
						$("#" + data.rows[i].outsiteServiceID).find("td").css("background-color", "#fffaa0");
					}else if(data.rows[i].status == 'new'){
						$("#" + data.rows[i].outsiteServiceID).find("td").css("background-color", "#ceffca");
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
				window.location = '<c:url value="/outsiteService.html?do=preAdd" />';
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
						url: 'outsiteService.html?do=delete&outsiteServiceID='+gr});
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
			return "<fmt:message key='outsiteService_status_new' />";
		}else if(cellvalue == 'sent'){
			return "<fmt:message key='outsiteService_status_sent' />";
		}else if(cellvalue == 'received'){
			return "<fmt:message key='outsiteService_status_received' />";
		}else if(cellvalue == 'close'){
			return "<fmt:message key='outsiteService_status_close' />";
		}
		return cellvalue;
	}
	
	function gridReload(){
		var name = jQuery("#name").val();
		var date = jQuery("#dateInput").val();
		var type = jQuery("#type").val();
		var serialNo = jQuery("#serialNo").val();
		var refOutsiteJobID = jQuery("#refOutsiteJobID").val();
		var outsiteCompanyID = jQuery("#outsiteCompanyID").val();
		var transportCompanyID = jQuery("#transportCompanyID").val();
		var status = jQuery("#status").val();
		jQuery("#list").jqGrid('setGridParam',{url:"searchOutsiteService.html?name="+name+"&date="+date+"&type="+type+"&serialNo="+serialNo+"&refOutsiteJobID="+refOutsiteJobID+"&outsiteCompanyID="+outsiteCompanyID+"&transportCompanyID="+transportCompanyID+"&status="+status,page:1}).trigger("reloadGrid");
	}
	
	jQuery(window).bind('resize', function() {
		// Get width of parent container
		var width = $('.ui-widget-content')[0].clientWidth;
		if (width == null || width < 1){
			// For IE, revert to offsetWidth if necessary
			width = jQuery('.ui-widget-content')[0].offsetWidth;
		}
		// change width - 24 to width - 10
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