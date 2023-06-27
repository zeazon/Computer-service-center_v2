<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="form" uri="/WEB-INF/tld/spring-form.tld"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>

<table style="width:100%">
	<tr>
		<td>
			<form:form commandName="searchForm" id="searchForm" class="jqtransform" method="post" onsubmit="load(); return false;">
				<table>
					<tr>
						<td><label><fmt:message key="serviceOrderID" />:</label></td>
						<td style="padding-right:70px;"><div class="rowElem"><form:input path="serviceOrderID" type="text" id="serviceOrderID" class="textboxMockup" /></div></td>
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

	jQuery().ready(function(){
		
		$("form.jqtransform").jqTransform();
		

		
		jQuery("#list").jqGrid({
//			url:"searchServiceOrder.html",
			datatype: "json",
			height: "100%",
			autowidth: true,
			colNames:['<fmt:message key="serviceOrderID" />','<fmt:message key="date" />','<fmt:message key="name" />','<fmt:message key="tel" />','<fmt:message key="mobileTel" />','<fmt:message key="type" />','<fmt:message key="brand" />','<fmt:message key="model" />','<fmt:message key="serialNo" />','<fmt:message key="serviceOrder_empFix" />','<fmt:message key="status" />','<fmt:message key="cannotMakeContact" />'],
			colModel:[
				{name:'serviceOrderID',index:'serviceOrderID', width:'200'},
				{name:'serviceOrderDate', index:'serviceOrderDate', align:'center', sorttype:'date',formatter:'date', formatoptions: {srcformat:'d/m/Y',newformat:'d/m/Y'}, width:'100', firstSortOrder:'desc'},
				{name:'name',index:'name'},				
				{name:'tel',index:'tel', sortable:false},
				{name:'mobileTel',index:'mobileTel', sortable:false, width:'170'},
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
		//	pager: '#pager',
			toppager: false,
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
		;
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
	
	
	function load(){
		var serviceOrderID = jQuery("#serviceOrderID").val();
		
		jQuery("#list").jqGrid('setGridParam',{url:"searchServiceOrder.html?serviceOrderID="+serviceOrderID,page:1}).trigger("reloadGrid");
	}

</script>