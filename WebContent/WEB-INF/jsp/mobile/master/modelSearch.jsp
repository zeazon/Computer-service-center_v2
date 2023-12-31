<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="form" uri="/WEB-INF/tld/spring-form.tld"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>

<table width="100%">
	<tr>
		<td>
			<form:form commandName="searchForm" id="searchForm" class="jqtransform" method="post" onsubmit="gridReload(); return false;">
				<table>
					<tr>
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
						<td><label><fmt:message key="name" />:</label></td>
						<td><div class="rowElem"><form:input path="name" type="text" id="name" class="textboxMockup" size="25" /></div></td>
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
						
						<form id="editForm" action="model.html?do=preEdit" method="post">
							<input type="hidden" name="modelID"/>
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

<c:url var="findBrandURL" value="/brand.html?do=getBrandByType" />

<script type="text/javascript">	
	jQuery().ready(function (){
		//$('#type').combobox({showBlankValue:true});
		//$('#brand').combobox({showBlankValue:true});
		
		//$('#type_autoComplete').val($("#type :selected").text());
		//$('#brand_autoComplete').val($("#brand :selected").text());
		
		jQuery("#list").jqGrid({
			url:"searchModel.html",
			datatype: "json",
			height: "100%",
			autowidth: true,
			colNames:['<fmt:message key="type" />','<fmt:message key="brand" />','<fmt:message key="modelID" />','<fmt:message key="name" />'],
			colModel:[
			    {name:'typeName',index:'typeName'},
			    {name:'brandName',index:'brandName'},
				{name:'modelID',index:'modelID', sorttype:"int"},
				{name:'name',index:'name'}],
			multiselect: false,
			rownumbers: true,
			rowNum:10,
			rowList:[10,20,30,40,50],
			viewrecords: true,
			jsonReader:{
				repeatitems: false,
				id: "modelID"
			},
			pager: '#pager',
			toppager: true
		}).navGrid("#pager",{edit:false,add:false,del:false,search:false,refresh:false,cloneToTop:true})
		.navButtonAdd('#list_toppager',
		{
			caption:"<fmt:message key='button.add' />",
			title:"<fmt:message key='button.add' />",
			buttonicon:"ui-icon-plus",
			onClickButton: function(){ 
				window.location = '<c:url value="/model.html?do=preAdd" />';
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
						url: 'model.html?do=delete&modelID='+gr}); 
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
		
		$(function() {
			//find all form with class jqtransform and apply the plugin
			$("form.jqtransform").jqTransform();
			
			//$("button#searchButton").click(function() {
			//	gridReload();
			//});
			
		});
		
		
		/*$( "#type_autoComplete" ).bind( "autocompletechange", function(event, ui) {
		//	  $( "#type_autoComplete" ).checkVal(ui);
			 // console.info(this.checkVal(ui));
			 
			 //alert($(this).val());
			 
			var select = $("#type");
			var selected = select.children( ":selected" );
			if ( !ui.item ) {
				var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $(this).val() ) + "$", "i" ),
					valid = false;
				select.children( "option" ).each(function() {
					if ( $( this ).text().match( matcher ) ) {
						this.selected = valid = true;
						return false;
					}
				});
			 	if ( !valid ){
					// remove invalid value, as it didn't match anything
					$(this).val( "" );
					select.val( "" );
					//this.data( "autocomplete" ).term = "";
					$( "#type_autoComplete" ).data( "autocomplete" ).term = "";
					return false;
				}
			 }else{
				 $.getJSON('${findBrandURL}', {
					typeID : select.val(),
					ajax : 'true'
				}, function(data) {
					var html = '';
					var len = data.length;
					if(len > 0){
						html += '<option value="">-</option>';
						for ( var i = 0; i < len; i++) {
							html += '<option value="' + data[i].brandID + '">'
									+ data[i].name + '</option>';
						}
						html += '</option>';
					}else{
						html += '<option value="">-</option>';
					}
					
					$('#brand').html(html);
					
					$('#brand_autoComplete').width($('#brand').width());
					$('#brand_autoComplete').val($("#brand :selected").text());
					
					// set change select list dynamic, ref http://www.code-pal.com/the-select-problem-after-using-jqtransform-and-its-solution/ 
					
					/*var sty = $("#brandRow div.jqTransformSelectWrapper").attr("style");
					//alert($("#brandRow div.jqTransformSelectWrapper").attr("style"));
					
					var sels = $("#brand").removeClass("jqTransformHidden");
					var $par = $("#brand");
					$par.parent().replaceWith($par);
					sels.jqTransSelect();
					//alert(sty);
					//$("#brandRow div.jqTransformSelectWrapper").attr("style", sty);
					//$("#brandRow div.jqTransformSelectWrapper").attr("style", "z-index:9;");
					
					// trigger event change of district select list
					$("#brand").change();
					$("#brandRow div.jqTransformSelectWrapper").css("z-index", 9);*/
					
/*					$("#brandRow").css("z-index", 9);
				});
			 }
			 
		});*/
		
		$( "#type_autoComplete" ).autocomplete({
			change: function(event, ui) {
				var select = $("#type");
				var selected = select.children( ":selected" );
				if ( !ui.item ) {
					var matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( $(this).val() ) + "$", "i" ),
						valid = false;
					select.children( "option" ).each(function() {
						if ( $( this ).text().match( matcher ) ) {
							this.selected = valid = true;
							return false;
						}
					});
				 	if ( !valid ){
						// remove invalid value, as it didn't match anything
						$(this).val( "" );
						select.val( "" );
						//this.data( "autocomplete" ).term = "";
						$( "#type_autoComplete" ).data( "autocomplete" ).term = "";
						// get text from blank value option
						$( this ).val(select.children( ":selected" ).text());
						
						// set brand to empty
						html += '<option value="">All</option>';
						$('#brand').html(html);
						$('#brand_autoComplete').width($('#brand').width());
						$('#brand_autoComplete').val($("#brand :selected").text());
						
						$("#brandRow").css("z-index", 9);
						return false;
					}
				 }else{
					 $.getJSON('${findBrandURL}', {
						typeID : select.val(),
						ajax : 'true'
					}, function(data) {
						var html = '';
						var len = data.length;
						html += '<option value="">All</option>';
						if(len > 0){
							for ( var i = 0; i < len; i++) {
								html += '<option value="' + data[i].brandID + '">'
										+ data[i].name + '</option>';
							}
							html += '</option>';
						}
						
						$('#brand').html(html);
						
						$('#brand_autoComplete').width($('#brand').width());
						$('#brand_autoComplete').val($("#brand :selected").text());
						
						$("#brandRow").css("z-index", 9);
					});
				 }
			}
		});
		
		/*$("#type").change(
			function() {
				$.getJSON('${findBrandURL}', {
					typeID : $(this).val(),
					ajax : 'true'
				}, function(data) {
					var html = '';
					var len = data.length;
					if(len > 0){
						html += '<option value="">-</option>';
						for ( var i = 0; i < len; i++) {
							html += '<option value="' + data[i].brandID + '">'
									+ data[i].name + '</option>';
						}
						html += '</option>';
					}else{
						html += '<option value="">-</option>';
					}
					
					$('#brand').html(html);
					
					$('#brand_autoComplete').width($('#brand').width());
					$('#brand_autoComplete').val($("#brand :selected").text());
					
					// set change select list dynamic, ref http://www.code-pal.com/the-select-problem-after-using-jqtransform-and-its-solution/ 
					
					var sty = $("#brandRow div.jqTransformSelectWrapper").attr("style");
					//alert($("#brandRow div.jqTransformSelectWrapper").attr("style"));
					
					var sels = $("#brand").removeClass("jqTransformHidden");
					var $par = $("#brand");
					$par.parent().replaceWith($par);
					sels.jqTransSelect();
					//alert(sty);
					//$("#brandRow div.jqTransformSelectWrapper").attr("style", sty);
					//$("#brandRow div.jqTransformSelectWrapper").attr("style", "z-index:9;");
					
					// trigger event change of district select list
					$("#brand").change();
					$("#brandRow div.jqTransformSelectWrapper").css("z-index", 9);
				});
			}
		);*/
		
		
	});
	
	function gridReload(){
		var name = jQuery("#name").val();
		var typeID = jQuery("#type").val();
		var brandID = jQuery("#brand").val();
		jQuery("#list").jqGrid('setGridParam',{url:"searchModel.html?name="+name+"&typeID="+typeID+"&brandID="+brandID,page:1}).trigger("reloadGrid");
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