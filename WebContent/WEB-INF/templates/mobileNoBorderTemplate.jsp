<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>ระบบจัดการงานซ่อม</title>
<link rel="shortcut icon" href="<c:url value='/images/favicon.ico' />" />

<!-- script src="<c:url value="/resources/script/jquery-1.11.2.min.js" />" type="text/javascript"></script>
<script src="<c:url value="/resources/css/jquery/jquery-ui.min.js" />" type="text/javascript"></script>
<script src="<c:url value="/resources/script/amcharts/amcharts.js" />" type="text/javascript"></script>
<script src="<c:url value="/resources/script/amcharts/serial.js" />" type="text/javascript"></script>
<script src="<c:url value="/resources/script/amcharts/plugins/responsive/responsive.min.js" />" type="text/javascript"></script>
<script src="<c:url value="/resources/script/jquery.mobile/jquery.mobile-1.4.5.min.js" />" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/script/jquery.mobile/jquery.mobile-1.4.5.min.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/mazda.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/mazda_mobile.css" />" />
 
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery_mobile_redmond/jquery.mobile.icons.min.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery_mobile_redmond/redmond.min.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/script/jquery.mobile/jquery.mobile.structure-1.4.5.min.css" />" />
 
<script src="<c:url value="/resources/script/datatables-1.10.10/js/jquery.dataTables.js" />" type="text/javascript"></script>
<script src="<c:url value="/resources/script/datatables-1.10.10/js/dataTables.jqueryui.js" />" type="text/javascript"></script>

<script src="<c:url value="/resources/script/datatables-1.10.10/Responsive/js/dataTables.responsive.js" />" type="text/javascript"></script>
<script src="<c:url value="/resources/script/datatables-1.10.10/Responsive/js/responsive.jqueryui.js" />" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/script/datatables-1.10.10/css/dataTables.jqueryui.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/script/datatables-1.10.10/css/jquery.dataTables.css" />" />

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/script/datatables-1.10.10/Responsive/css/responsive.dataTables.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/script/datatables-1.10.10/Responsive/css/responsive.jqueryui.css" />" />

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery_redmond/jquery-ui.min.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery_redmond/jquery-ui.structure.min.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jquery_redmond/jquery-ui.theme.min.css" />" /-->





<!-- link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/mmenu/mmenu.css"-->

<!-- script src="<%=request.getContextPath()%>/script/mmenu/mmenu.js" type="text/javascript"></script-->





<!-- link rel="stylesheet" href="http://code.jquery.com/mobile/1.0.1/jquery.mobile-1.0.1.min.css" /-->
<!-- link rel="stylesheet" href="https://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css" /-->
<!-- link rel="stylesheet" href="https://code.jquery.com/mobile/1.3.0/jquery.mobile-1.3.0.min.css" /-->




<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jQuery/jquery-ui-1.8.13.custom.css">
<!-- link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jqGrid/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jqTransform/jqtransform.css"-->
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/default.css">
<!-- link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jQuery-calendar/2bytes.calendars.picker.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jQuery-datetimeentry/jquery.datetimeentry.css"-->

<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/mmenu/mmenu.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/mmenu/demo.css">




<script src="<%=request.getContextPath()%>/script/jQuery/jquery-1.6.1.min.js" type="text/javascript"></script>
<!-- script src="<%=request.getContextPath()%>/script/jQuery/jquery-ui-1.8.13.custom.min.js" type="text/javascript"></script-->





<!-- script src="<%=request.getContextPath()%>/script/jqGrid/jquery.jqGrid.src-fixed3.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jqGrid/grid.locale-th.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jqTransform/jquery.jqtransform.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jQueryValidate/jquery.validate.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jQueryValidate/messages_th.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/inputFieldEffect.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jQuery-calendar/jquery.calendars.all.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jQuery-calendar/jquery.calendars.thai.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jQuery-calendar/jquery.calendars.picker-th.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jQuery-calendar/jquery.calendars.lang.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jQuery-calendar/jquery.calendars.thai-th.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jQuery-datetimeentry/jquery.datetimeentry.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/autoComplete.js" type="text/javascript"></script-->


<script src="<%=request.getContextPath()%>/script/mmenu/mmenu.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/mmenu/mmenu.polyfills.js" type="text/javascript"></script>

<!-- Fire the plugin -->
<script>
//    document.addEventListener(
//        "DOMContentLoaded", () => {
//            new Mmenu( "#my-menu" );
//        }
//    );
</script>
 
<style type="text/css">
	.mm-navbar__title{	
		/*background:#FFF0A5;*/
		background:#ef8500;
	}
	.mm-navbar__title>span{	
		color:#403c29;
		font-weight: bold;
		/*font-size: larger;*/
		font-size: 1.4em;
	}
	div.mm-panel{
		/*background:#FFF0A5;*/
		background:#f7b54a;
		font-size: 1.2em;
	}
	a.mm-btn.mm-btn_prev.mm-navbar__btn {
		/*background:#FFF0A5;*/
		background:#f7b54a;
	}




@media all and (orientation: portrait) and (min-width: 261px) {
	
	.criteria {
		font-size:1.188em;
	}

	.vertical {
		
	}


}


@media all and (orientation: portrait) {
	
	.criteria {
		font-size:1.188em;
	}

	.vertical {
		
	}

}

@media all and (orientation: landscape) and (min-width: 561px) {

	.criteria {
		font-size:1.188em;
		text-align: right;
	}
	
	.vertical {
		position: relative;
		top: 17px;
	}

}

@media all and (orientation: landscape) {

	.criteria {
		font-size:1.188em;
		text-align: right;
	}
	
	.vertical {
		@media (min-width: 500px) {
			position: relative;
			top: 17px;
		}
	}

}

@media screen and (min-width:1440px){
	.ui-header .ui-title {
		font-size:48px;
		color:blue;
	}
	
	.ui-icon-bars {
		zoom:3;
	}
	
	.selectClass {font-size:3em;}

	.btnClass {font-size:0.5em;}
}

.tableList{
	width: 98vw ;
}


</style>
 
</head>
<body marginheight="0" marginwidth="0">
<div style="background: url('./images/header_logo.png'); background-repeat:no-repeat; height: 80px;" align="right" style="padding: 25px 50px 75px 100px;">
	<div style="padding-top:60px; padding-right:10px;">
		<font size="-1">
		Login as <c:if test="${not empty UserLogin}"><c:out value="${UserLogin.login}"/></c:if>, <a href="logout.html">Logout</a>
		</font>
	</div>
</div>
<!-- nav-->
<div class="header">
	<a href="#menu"><span></span></a>
	<nav id="menu">
		<c:if test="${menuStr != null}">
			<c:out value='${menuStr}' escapeXml="false"/>
		</c:if>
	</nav>
</div>
<tiles:insertAttribute name="body" />

	<script>
        new Mmenu(document.querySelector('#menu'));

        document.addEventListener('click', function (evnt) {
            var anchor = evnt.target.closest('a[href^="#/"]');
            if (anchor) {
                alert("Thank you for clicking, but that's a demo link.");
                evnt.preventDefault();
            }
        });
    </script>
    
</body>
</html>