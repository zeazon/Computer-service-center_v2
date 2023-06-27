<%@page pageEncoding="TIS-620" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="tis-620" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>ระบบจัดการงานซ่อม</title>
<!-- link rel="shortcut icon" href="<c:url value='/resources/image/favicon.ico' />" /-->
<!-- link rel="icon" type="image/png" sizes="32x32" href="<c:url value='/resources/image/favicon-32x32.png' />" >
<link rel="icon" type="image/png" sizes="16x16" href="<c:url value='/resources/image/favicon-16x16.png' />" -->

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
 
 
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/pro_dropdown_2/pro_dropdown_2.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jQuery/jquery-ui-1.8.13.custom.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jqGrid/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jqTransform/jqtransform.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/default.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jQuery-calendar/2bytes.calendars.picker.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jQuery-datetimeentry/jquery.datetimeentry.css">


<link rel="stylesheet" href="http://code.jquery.com/mobile/1.0.1/jquery.mobile-1.0.1.min.css" />
<!-- link rel="stylesheet" href="http://code.jquery.com/mobile/1.1.2/jquery.mobile-1.1.2.min.css" /-->
<!-- link rel="stylesheet" href="http://code.jquery.com/mobile/1.2.1/jquery.mobile-1.2.1.min.css" /-->
<!-- link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.css" /-->

<link href="https://fonts.googleapis.com/css2?family=Lexend+Deca" rel="stylesheet" type="text/css">


<script src="<%=request.getContextPath()%>/script/jQuery/jquery-1.6.1.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jQuery/jquery-ui-1.8.13.custom.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/jqGrid/jquery.jqGrid.src-fixed3.js" type="text/javascript"></script>
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
<script src="<%=request.getContextPath()%>/script/autoComplete.js" type="text/javascript"></script>




<!-- script src="http://code.jquery.com/mobile/1.0.1/jquery.mobile-1.0.1.min.js"></script-->

<!-- script src="http://code.jquery.com/mobile/1.1.2/jquery.mobile-1.1.2.min.js"></script-->
<!-- script src="http://code.jquery.com/mobile/1.2.1/jquery.mobile-1.2.1.min.js"></script-->
<!-- script src="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.js"></script-->

<script src="<%=request.getContextPath()%>/script/mmenu/mmenu.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/mmenu/mmenu.polyfills.js" type="text/javascript"></script>

<style type="text/css">

table {
	font-size:1em;
	border-spacing: 1rem 0rem;
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


.ui-li-divider {
	
}

.ui-li-divider .ui-bar-inherit {
	
}


</style>
 
</head>
<body>
<tiles:insertAttribute name="body" />
</body>
</html>