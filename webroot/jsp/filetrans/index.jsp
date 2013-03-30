<%@ page language="java" pageEncoding="UTF-8"%><%@include file="/jsp/common/base.jsp"%><%@ page import="com.david4.console.TaskInfo"%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>文件传输</title>
<script type="text/javascript" src="${path}/js/common/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
	function startTask(){
		if(!window.confirm("确定要执行该任务吗？")){
			return;
		}
		$("#startButton").attr('disabled',"true");
		var choose = document.getElementById("taskId");
		var taskId = choose.options[choose.selectedIndex].value;
		$.ajax({
			url: '${path}'+"/console/runtask.jhtml?taskId="+taskId+"&t="+new Date().getTime(),
            type: "get",
            success: function(data) {
            	alert(formatData(data));
            	$('#startButton').removeAttr("disabled"); 
            },
            error: function(data) {
            	alert("执行失败");
            	$('#startButton').removeAttr("disabled");
            }
		});
	}
	var runFlag = false;
	function getStateAndRunInfo(){
		//避免上一次还没有执行完，下一次又开始执行
		if(runFlag==false){
			runFlag = true;
			getState();
			getRunInfo();
		}
	}
	//获取系统当前状态
	function getState(){
		$.get('${path}'+"/console/getState.jhtml?t="+new Date().getTime(),{}, function (data, textStatus){
			var msg = "";
			if(data==null || data==""){
				msg = "<li>系统空闲中</li>"
			}else{
				var msgArr = data.split(",");
				for(var i=0;i<msgArr.length;i++){
					var taskId = msgArr[i];
					if(taskId!=null && taskId!=""){
						msg = msg+"<li>"+getTextById(taskId)+" 运行中</li>"
					}
				}
			}
			$("#stateul").html(msg);
		});
	}
	
	var lastSize = 0;
	function getRunInfo(){
		//var group = 0;
		var choose = document.getElementById("taskId");
		var taskId = choose.options[choose.selectedIndex].value;
		$.ajax({
			url: '${path}'+"/console/getRunInfo.jhtml?lastSize="+lastSize+"&taskId="+taskId+"&t="+new Date().getTime(),
            type: "get",
            success: function(data) {
            	if(data!=null && data!=""){
    				var msgArr = data.split('<%=TaskInfo.FEN_GE%>');
    				lastSize = msgArr[0];
    				for(var i=1;i<msgArr.length;i++){
    					var msg = msgArr[i];
    					if(msg!=null && msg!=""){
    						append(formatData(msg));
    					}
    				}
    			}
    			runFlag = false;
            },
            error: function(data) {
            	$("#stateul").html("<li>系统当前异常，可能在重启</li>");
            	runFlag = false;
            }
		});
	}
	//刷新配置文件
	function refresh(){
		$.get('${path}'+"/refresh.jhtml?t="+new Date().getTime(),{}, function (data, textStatus){
			alert(data);
		});
	}
	function getTextById(taskId){
		var choose = document.getElementById("taskId");
		for(var i=0;i<choose.options.length;i++){
			if(taskId==choose.options[i].value){
				return choose.options[i].text;
			}
		}
		return "TASKID "+taskId;
	}
	//向控制台追加信息
	function append(msg){
		$("#consoleText").val($("#consoleText").val()+msg+"\n");
		 var scrollTop = $("#consoleText")[0].scrollHeight;  
         $("#consoleText").scrollTop(scrollTop); 
	}
	//清空控制台
	function clearConsole(){
		$("#consoleText").val("");
	}

	$(document).ready(function() {
		clearConsole();
		getStateAndRunInfo();
		//3秒刷新一次
		setInterval(getStateAndRunInfo, 3000);  
	});
	
	function formatData(data){
		var patrn=/#(\d+)#/; 
		var result = data.match(patrn);
		if(result==null){
			return data;
		}			  
		return data.replace(result[0],getTextById(result[1])+" ");
	}
	var i =0;
	function appendtest(){
		append(i);
		i++;
	}
	
	//文件预览
	function listFile(){
		var choose = document.getElementById("taskId");
		var taskId = choose.options[choose.selectedIndex].value;
		$.get('${path}'+"/console/listfile.jhtml?taskId="+taskId+"&t="+new Date().getTime(),{}, function (data, textStatus){
			if(data!=null && data!=""){
				var msgArr = data.split('<%=TaskInfo.FEN_GE%>');
				for(var i=0;i<msgArr.length;i++){
					var msg = msgArr[i];
					if(msg!=null && msg!=""){
						append(msg);
					}
				}
			}
		});
	}
	
	function selectChange(){
		clearConsole();
		lastSize = 0;
	}
	function logout(){
		window.location.href="${path}/logout.jhtml";
	}
</script>
</head>
<body>
	<table width="100%" align="center" style="margin-top: 30px">
		<tr align="center">
			<td colspan="2">
				<select name="taskId" id="taskId" onchange="selectChange()">
					<c:forEach items="${taskModelList}" var="task">
						<option value="${task.id}">${task.desc}</option>
					</c:forEach>
				</select>
				<input type="button" id="startButton" value="启动" onclick="startTask();" />
				<input type="button" value="文件预览" onclick="listFile();" />
				<input type="button" value="清空控制台" onclick="clearConsole();" />
				
				<input type="button" value="退出" onclick="logout();" />
			</td>
		</tr>
		<tr align="center">
			<td align="right" style="text-align: right;"><span>系统当前状态：</span></td>
		    <td align="left" valign="middle"><ul id="stateul" style="list-style-type: none;color: red;"><li>&nbsp;</li></ul></td>
		</tr>
		<tr align="center">
			<td colspan="2" ><textarea id="consoleText" rows="25" cols="120" readonly="readonly"></textarea></td>
		</tr>
	</table>
</body>
</html>