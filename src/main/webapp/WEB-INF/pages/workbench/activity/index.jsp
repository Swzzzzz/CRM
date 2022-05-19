<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" +
request.getServerName() + ":" + request.getServerPort() +
request.getContextPath() + "/";
%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<link rel="stylesheet" type="text/css" href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css">
<script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

<script type="text/javascript">

	$(function(){

		$("#createActivity").click(function () {

			datetimepickerFun();

			$("#createActivityForm")[0].reset();
			$("#createActivityModal").modal("show");
		})

		$("#saveActivity").click(function () {
			var owner=$("#create-marketActivityOwner").val();
			var name=$.trim($("#create-marketActivityName").val());
			var startDate=$("#create-startTime").val();
			var endDate=$("#create-endTime").val();
			var cost=$.trim($("#create-cost").val());
			var description=$.trim($("#create-description").val());

			if (owner==""||name==""){
				alert("拥有者或名称不能为空！");
				return;
			}
			if (startDate!=""&&endDate!=""){
				if (startDate>endDate){
					alert("您输入的日期不和法！请重新输入")
					return;
				}
			}
			if (cost!=""){
				var proPex=/^(([1-9]\d*)|0)$/;
				if (!proPex.test(cost)){
					alert("成本只能是非负正数！");
					return;
				}
			}
			$.ajax({
				url:"workbench/activity/insertActivity.do",
				data:{
					owner:owner,
					name:name,
					startDate:startDate,
					endDate:endDate,
					cost:cost,
					description:description
				},
				dataType:"json",
				type:"post",
				success:function (data) {
					if (data.code==1){
						$("#createActivityModal").modal("hide");
						//返回活动页并刷新
						queryActivityFun(1 ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

					}else {
						alert(data.message);
						$("#createActivityModal").modal("show");
					}
				}
			})
		})

		queryActivityFun(1,10);

		$("#queryActivityBtn").click(function () {
			queryActivityFun(1 ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
		});

		//给全选按钮添加函数
		$("#checkedAll").click(function () {
			$("#tbody input[type='checkbox']").prop("checked",this.checked)
		})

		//jquery中普通的click函数只能为固态元素加函数，动态需要on函数来添加
		$("#tbody").on("click","input[type='checkbox']",function () {
			if ($("#tbody input[type='checkbox']").size() == $("#tbody input[type='checkbox']:checked").size()){
				$("#checkedAll").prop("checked",true)
			}else{
				$("#checkedAll").prop("checked",false)
			}
		});

		$("#deleteActivity").click(function () {
			var checkkedIds = $("#tbody input[type='checkbox']:checked");
			if (checkkedIds.size()==0){
				alert("请选择您需要删除的数据！");
				return;
			}
			if(window.confirm("您确定删除吗？")){
				var ids = "";
				$.each(checkkedIds,function (i,n) {
					ids += "id="+n.id+"&";
				})
				ids = ids.substr(0,ids.length-1)
				$.ajax({
					url:"workbench/activity/deleteActivityByIds.do",
					data:ids,
					dataType:"json",
					type:"post",
					success:function (data) {
						if (data.code=="1"){
							queryActivityFun(1 ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						}else{
							alert(data.message)
						}
					}
					/*error:function (data) {
						if (data.data=="1"){
							queryActivityFun(1 ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						}else{
							alert(data.message)
						}
					}*/
				})
			}
		})

		//修改
		$("#editActivity").click(function () {

			datetimepickerFun();

			var checkkedIds = $("#tbody input[type='checkbox']:checked");
			if (checkkedIds.size()==0){
				alert("请选择您需要修改的市场活动");
				return;
			}else if(checkkedIds.size()>1){
				alert("单次只可以修改一条数据")
				return;
			}
			var id = checkkedIds[0].id;
			$.ajax({
				url:"workbench/activity/queryActivityById.do",
				data:{
					id:id
				},
				dataType:"json",
				type:"post",
				success:function (data) {
					$("#hid-edit-id").val(data.id);
					$("#edit-marketActivityOwner").val(data.owner);
					$("#edit-marketActivityName").val(data.name);
					$("#edit-startTime").val(data.startDate);
					$("#edit-endTime").val(data.endDate);
					$("#edit-cost").val(data.cost);
					$("#edit-describe").val(data.description);

					$("#editActivityModal").modal("show");
				}
			})
		})

		//绑定更新按钮
		$("#updateBtn").click(function () {
			var id = $("#hid-edit-id").val();
			var owner=$("#edit-marketActivityOwner").val();
			var name=$("#edit-marketActivityName").val();
			var startDate=$("#edit-startTime").val();
			var endDate=$("#edit-endTime").val();
			var cost=$("#edit-cost").val();
			var description=$("#edit-describe").val();

			if (owner==""||name==""){
				alert("拥有者或名称不能为空！");
				return;
			}
			if (startDate!=""&&endDate!=""){
				if (startDate>endDate){
					alert("您输入的日期不和法！请重新输入")
					return;
				}
			}
			if (cost!=""){
				var proPex=/^(([1-9]\d*)|0)$/;
				if (!proPex.test(cost)){
					alert("成本只能是非负正数！");
					return;
				}
			}
			$.ajax({
				url:"workbench/activity/updateByPrimaryKeySelective.do",
				data:{
					id:id,
					owner:owner,
					name:name,
					startDate:startDate,
					endDate:endDate,
					cost:cost,
					description:description
				},
				dataType:"json",
				type:"post",
				success:function (data) {
					if (data.code==1){
						$("#editActivityModal").modal("hide");
						alert(data.message);
						//返回活动页并刷新
						queryActivityFun($("#activityPage").bs_pagination('getOption', 'currentPage') ,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

					}else {
						alert(data.message);
						$("#editActivityModal").modal("show");
					}
				}
			})
		})

		$("#exportActivityAllBtn").click(function () {
			window.location.href="workbench/activity/exportAllActivities.do";
		})

		$("#exportActivityXzBtn").click(function () {

			var checkkedIds = $("#tbody input[type='checkbox']:checked");
			alert(checkkedIds.size())
			if (checkkedIds.size()==0){
				alert("请选择您需要导出的数据！");
				return;
			}

				var ids = "";
				$.each(checkkedIds,function (i,n) {
					ids += "id="+n.id+"&";
				})
				ids = ids.substr(0,ids.length-1)

			window.location.href="workbench/activity/exportAllActivitiesById.do?"+ids;


			/*$.ajax({
				url:"workbench/activity/exportAllActivitiesById.do",
				data:ids,
				dataType:"json",
				type:"post",
				success:function (data) {
					alert("导出成功")
				}
			})*/
		})

		$("#importActivityBtn").click(function () {
			var activityFileName = $("#activityFile").val();
			var subStr = activityFileName.substr(activityFileName.lastIndexOf(".")+1).toLocaleLowerCase();
			if (subStr!="xls"){
				alert("仅支持xls文件");
				return;
			}
			var activityFile = $("#activityFile")[0].files[0];
			if (activityFile.size > 5*1024*1024){
				alert("文件大小不超过5兆");
				return;
			}
			var formData = new FormData();
			formData.append("activityFile",activityFile)
			$.ajax({
				url:"workbench/activity/insertActivityByList.do",
				data:formData,
				processData:false,
				contentType:false,
				dataType:"json",
				type:"post",
				success:function (data) {
					if (data.code=="1"){
						alert("成功导入"+data.retData+"条数据");
						$("#importActivityModal").modal("hide");
						queryActivityFun(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
					}else{
						alert(data.message);
						$("#importActivityModal").modal("show");
					}
				}
			})
		})

	});

	function queryActivityFun(pageNo,pageSize) {
		var name = $("#query-name").val();
		var owner = $("#query-owner").val();
		var endDate = $("#query-endTime").val();
		var startDate = $("#query-startTime").val();
		$.ajax({
			url:"workbench/activity/queryActivityByConditionForPage.do",
			data: {
				name:name,
				owner:owner,
				endDate:endDate,
				startDate:startDate,
				pageNo:pageNo,
				pageSize:pageSize
			},
			dataType: "json",
			type: "post",
			success:function (data) {
				$("#countAllData").text(data.count);
				var str = "";
				$.each(data.activityList,function (i,n) {
					str+="<tr class=\"active\">";
					str+="<td><input type=\"checkbox\" id="+n.id+" /></td>"
					str+="<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/activity/queryActivityRemarkById.do?id="+n.id+"'\">"+n.name+"</a></td>"
					str+="<td>"+n.owner+"</td>";
					str+="<td>"+n.startDate+"</td>";
					str+="<td>"+n.endDate+"</td>";
					str+="</tr>";

				})
				$("#tbody").html(str);

				$("#checkedAll").prop("checked",false)

				var totalPages = data.count%pageSize==0 ? data.count/pageSize :parseInt(data.count/pageSize)+1;

				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage : function(event, data){
						queryActivityFun(data.currentPage , data.rowsPerPage);
					}
				});

			}
		})
	}

	function datetimepickerFun() {
		$(".time").datetimepicker({
			language:  "zh-CN",
			format: "yyyy-mm-dd",//显示格式
			minView: "hour",//设置只显示到月份
			initialDate: new Date(),//初始化当前日期
			autoclose: true,//选中自动关闭
			todayBtn: true, //显示今日按钮
			clearBtn : true,
			pickerPosition: "bottom-left"
		});

		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});
	}

</script>
</head>
<body>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="createActivityForm">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">
								  <c:forEach items="${userList}" var="users">
									  <option value="${users.id}">${users.name}</option>
								  </c:forEach>
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label ">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startTime" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endTime" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveActivity">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<input type="hidden" id="hid-edit-id">
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">
									<c:forEach items="${userList}" var="users">
										<option value="${users.id}">${users.name}</option>
									</c:forEach>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName" >
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startTime"  readonly>
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endTime"  readonly>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" >
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" id="updateBtn" class="btn btn-primary" data-dismiss="modal">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 导入市场活动的模态窗口 -->
    <div class="modal fade" id="importActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
                </div>
                <div class="modal-body" style="height: 350px;">
                    <div style="position: relative;top: 20px; left: 50px;">
                        请选择要上传的文件：<small style="color: gray;">[仅支持.xls]</small>
                    </div>
                    <div style="position: relative;top: 40px; left: 50px;">
                        <input type="file" id="activityFile">
                    </div>
                    <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;" >
                        <h3>重要提示</h3>
                        <ul>
                            <li>操作仅针对Excel，仅支持后缀名为XLS的文件。</li>
                            <li>给定文件的第一行将视为字段名。</li>
                            <li>请确认您的文件大小不超过5MB。</li>
                            <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                            <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                            <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                            <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
                </div>
            </div>
        </div>
    </div>
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="query-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="query-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="query-startTime" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="query-endTime">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="queryActivityBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="createActivity"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editActivity"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteActivity"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				<div class="btn-group" style="position: relative; top: 18%;">
                    <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal" ><span class="glyphicon glyphicon-import"></span> 上传列表数据（导入）</button>
                    <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（批量导出）</button>
                    <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（选择导出）</button>
                </div>
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="checkedAll" /></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="tbody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			<div id="activityPage"></div>
			<div style="height: 50px; position: relative;top: 30px;">
				<%--<div>
					<button type="button" class="btn btn-default" style="cursor: default;">共<b id="countAllData"></b>条记录</button>
				</div>
				<div class="btn-group" style="position: relative;top: -34px; left: 110px;">
					<button type="button" class="btn btn-default" style="cursor: default;">显示</button>
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
							10
							<span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu">
							<li><a href="#">20</a></li>
							<li><a href="#">30</a></li>
						</ul>
					</div>
					<button type="button" class="btn btn-default" style="cursor: default;">条/页</button>
				</div>

				<div style="position: relative;top: -88px; left: 285px;">
					<nav>
						<ul class="pagination">
							<li class="disabled"><a href="#">首页</a></li>
							<li class="disabled"><a href="#">上一页</a></li>
							<li class="active"><a href="#">1</a></li>
							<li><a href="#">2</a></li>
							<li><a href="#">3</a></li>
							<li><a href="#">4</a></li>
							<li><a href="#">5</a></li>
							<li><a href="#">下一页</a></li>
							<li class="disabled"><a href="#">末页</a></li>
						</ul>
					</nav>
				</div>
			</div>--%>
		</div>

	</div>
</body>
</html>