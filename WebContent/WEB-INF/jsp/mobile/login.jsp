<%--@taglib  uri="http://www.springframework.org/tags/form" prefix="form" --%>
<%@ taglib prefix="form" uri="/WEB-INF/tld/spring-form.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>

<style type="text/css">

div.rounded-box {
    position:relative;
    width: 345px;
    background-color: #E6E6E6;
    margin: 3px auto;
	border-radius: 15px;
	-moz-border-radius: 15px;
}

/****
 Addition for modify screen display
*****/
.ui-body-c {
	background-image: linear-gradient(#ffffff,#ffffff);
}

.ui-mobile .ui-page-active {
	width: 100%;
}

/*********************
GLOBAL ATTRIBUTES
*********************/
/*div.top-left-corner, div.bottom-left-corner, div.top-right-corner, div.bottom-right-corner
{position:absolute; width:20px; height:20px; background-color:#FFF; overflow:hidden;}


div.top-left-inside, div.bottom-left-inside, div.top-right-inside, div.bottom-right-inside {position:relative; font-size:150px; font-family:arial; color:#E6E6E6; line-height: 40px;}
*/

/*********************
SPECIFIC ATTRIBUTES
*********************/

div.top-left-corner { top:0px; left:0px; }
div.bottom-left-corner {bottom:0px; left:0px;}
div.top-right-corner {top:0px; right:0px;}
div.bottom-right-corner {bottom: 0px; right:0px;}

div.top-left-inside {left:-8px;}
div.bottom-left-inside {left:-8px; top:-15px;}
div.top-right-inside {left:-25px;}
div.bottom-right-inside {left:-25px; top:-15px;}

div.box-contents {
   position: relative; padding: 8px; color:#000; border-radius: 25px;
}

.css-3d-text {
	font-family: 'Lexend Deca', sans-serifpx;
	color: rgba(0,0,0,.6);
	text-shadow: rgba(0,0,0,.2) 2px 6px 5px,rgba(255,255,255,.4) 0 -4px 30px;
	font-size:7.5vw;
}
</style>

<!--div style="border:1px dashed; height:300px;"><img src="././images/login_header.png" align="middle" /></div-->
<div style="margin:10px 10px 0px 10px ;border:1px dashed; height:300px; padding-top:10px;"><table width="100%" cellpadding="0" cellspacing="0"><tr align="center"><td class="css-3d-text"><img src="././images/mobile/pic_program.png" align="middle" /><br/><br/>Service Center System</td></tr></table></div>
<table width="100%" height="80px">
<tr>
	<td></td>
	<td></td>
	<td></td>
</tr>
<tr>
	<td></td>
	<td></td>
	<td></td>
</tr>
<tr>
	<td></td>
	<td></td>
	<td></td>
</tr>
</table>

<form:form commandName="loginForm" class="jqtransform" id="loginForm" method="post" action="login.html">
<!-- div style="text-align: center; background-color: #E6E6E6;"-->
<div>
<div class="rounded-box">
	<div class="top-left-corner"><div class="top-left-inside"></div></div>
	<div class="bottom-left-corner"><div class="bottom-left-inside"></div></div>

	<div class="top-right-corner"><div class="top-right-inside"></div></div>
	<div class="bottom-right-corner"><div class="bottom-right-inside"></div></div>
	<div class="box-contents">
		<table border="0">
			<tr>
				<td colspan="2" align="center"><img src="./images/logo_small.png"></td>
				<td></td>
			</tr>
			<tr>
				<td align="right"><label style="width:67px"><fmt:message key="user" />:</label></td>
				<td><div class="rowElem"><form:input path="login" style="width: 122px;" id="login"/></div></td>
				<td align="left"><label for="login" generated="true" class="error" style="width:71px; margin-left:4px"></label></td>
			</tr>
			<tr>
				<td align="right"><label><fmt:message key="password" />:</label></td>
				<td><div class="rowElem"><form:input path="password" type="password" style="width: 122px" /></div></td>
				<td align="left"><label for="password" generated="true" class="error" style="width:71px; margin-left:4px"></label></td>
			</tr>
			<tr>
				<td colspan="2" align="center"><div class="rowElem"><input type="submit" value='<fmt:message key="button.ok" />'/></div></td>
				<td></td>
			</tr>
		</table>
	</div> <!-- end div.box-contents -->
</div> <!-- end div.rounded-box -->

<c:if test="${errMsg != null}">
	<div style="align:center; width:345px; margin: auto;" class="ui-state-error ui-corner-all">
	
		<c:out value='${errMsg}' escapeXml="false"/>
	
		<%--script>
			alert("<c:out value='${errMsg}'/>");
		</script--%>
	</div>
</c:if>

</div>

</form:form>

<script type="text/javascript">
/*function doAjax() {
    $.ajax({
      url: 'time.html',
      data: ({name : "me"}),
      success: function(data) {
        $('#time').html(data);
      }
    });
  }*/

	$(document).ready(function() {
		
		$("#login").focus();
		
		$("#loginForm").validate({
			rules:{
				login: {
					required: true
				},
				password: {
					required: true
				}		
			}
		});
		
		$("form.jqtransform").jqTransform();


		
		
		$("#loginForm").submit(function() {
			//var account = $(this).serializeObject();
			//$.postJSON("account", account, function(data) {
			//	$("#assignedId").val(data.id);
			//	showPopup();
			//});
			//return false;
		//	alert('submit');
		//	return false;
		});
	});
</script>