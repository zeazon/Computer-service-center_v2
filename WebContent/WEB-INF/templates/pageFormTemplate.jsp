<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt.tld"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ระบบจัดการงานซ่อม</title>
<link rel="shortcut icon" href="././images/favicon.ico" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/pro_dropdown_2/pro_dropdown_2.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jQuery/jquery-ui-1.8.13.custom.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jqGrid/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jqTransform/jqtransform.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/default.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jQuery-calendar/2bytes.calendars.picker.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jQuery-datetimeentry/jquery.datetimeentry.css">
<link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/checkboxtree/jquery.checkboxtree.min.css" />

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
<script src="<%=request.getContextPath()%>/script/checkboxtree/jquery.checkboxtree.min.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/script/autoComplete.js" type="text/javascript"></script>
</head>
<body marginheight="0" marginwidth="0">
<div style="background: url('./images/header_logo.png'); background-repeat:no-repeat; height: 80px;" align="right">
<br>
<br>
<br>
<font size="-1" style="margin-right:10px">
Login as <c:if test="${not empty UserLogin}"><c:out value="${UserLogin.login}"/></c:if>, <a href="logout.html">Logout</a>
</font>
</div>
<nav>
	<c:if test="${menuStr != null}">
		<c:out value='${menuStr}' escapeXml="false"/>
	</c:if>
</nav>

<!-- open div page -->
<div style="margin:20px auto; width:98%">

<div onclick="history.back()" style="

	display: inline-block;
    padding: 3px 6px;
    margin-bottom: 5px;
    font-size: 14px;
    font-weight: 400;
    line-height: 1.42857143;
    text-align: center;
    white-space: nowrap;
    vertical-align: middle;
    -ms-touch-action: manipulation;
    touch-action: manipulation;
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    background-image: none;
    border: 1px solid transparent;
    border-radius: 2px;
    
    color: #333;
    background-color: #fefefe;
    border-color: #e3e3e3;
    border-right-color: #aaaaaa;
    border-bottom-color: #a7a7a7;

">
	<span style="float: left; margin-right: 0.5em; margin-top:2px;"
	    class="ui-icon ui-icon-circle-arrow-w">icon</span>
	<fmt:message key='button.back' />
</div>
<!-- open&end div header -->
<div class="ui-widget-header ui-corner-top ui-helper-clearfix" style="padding-left:2px"><tiles:insertAttribute name="pageName" /></div>
<!-- open div content -->
<div class="ui-widget-content">

<tiles:insertAttribute name="body" />

<!-- end div content -->
</div>
<c:if test="${msg != null}">
	<div class="ui-state-highlight" style="margin-top:5px">
		<c:out value='${msg}' escapeXml="false"/>
	</div>
</c:if>
<!-- end div page -->
</div>
</body>
</html>