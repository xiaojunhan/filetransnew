<%@ page language="java" pageEncoding="UTF-8"%><%@include file="/jsp/common/base.jsp"%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录</title>
<script type="text/javascript" src="${path}/js/common/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
	function login(){
		$("#loginFrom").submit();
	}
</script>
</head>
<body>
	<form id="loginFrom" action="${path}/login.jhtml" method="post">
		<table width="100%" align="center" style="margin-top: 30px">
			<tr align="center">
				<td>用户名：<input type="text" name="name" maxlength="30"/></td>
			</tr>
			<tr align="center">
				<td>密&nbsp;&nbsp;码：<input type="password" name="password" maxlength="30"/></td>
			</tr>
			<tr align="center">
				<td><input type="button" value="登录" onclick="login()"/></td>
			</tr>
			<tr align="center">
				<td>${message}</td>
			</tr>
		</table>
	</form>
</body>
</html>