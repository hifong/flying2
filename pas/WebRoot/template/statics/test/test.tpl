<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1"/>
    <title>企业信息管理平台</title>
	<link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/themes/color.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/pas/easyui/css/style.css">
    <script type="text/javascript" src="{$contextPath}/pas/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="{$contextPath}/pas/easyui/jquery.easyui.min.js"></script>
</head>
<body>
	<input type="button" value="Demo1" onclick="serviceDemo1()">
	<input type="button" value="addWorker" onclick="serviceDemo2()">
	<input type="button" value="addWorker" onclick="serviceDemo3()">
	<p>
    <textarea rows="20" cols="140" name="result" id="result"></textarea>
    
    <script type="text/javascript">
    {literal}
    	function serviceDemo1() {
    		var service1={"serviceId": "base_province:findAll","tid": "1"};
    		var service2={"serviceId": "base_city:findByProvinceId","tid": "2","params":{"province_id":"1"}};
    		var services = new Array();
    		services.push(service1);
    		services.push(service2);
    		
    		var request = {"transaction": "false", "count": services.length, "services":services};
    		
    		$.ajax({
				type: 'POST',
				url: 'http://120.76.220.87/decor/multiservice/invoke.do',
				contentType:'application/json;charset=utf-8',
				data: JSON.stringify(request),
				success: function(res){
					document.getElementById("result").innerText = JSON.stringify(res);
				},
				dataType: 'json'
			});
    	}
    	function serviceDemo2() {
    		var request={
				"reg_user_id": "1",
				"worker_phone": "13099994444",
				"worker_email": "13099994444@139.com",
				"urgent_phone": "13588885555",
				"password": "111111",
				"user_code": "worker132.7",
				"worker_name": "李四",
				"provider_id": "132",
				"identity_no": "3601231000999904134",
				"goods_category_services": 
				[
					{
						"goods_category_id": 1,
						"service_type_id": 1
					},
			
					{
						"goods_category_id": 1,
						"service_type_id": 2
					}
				]
			};
    		
    		$.ajax({
				type: 'POST',
				url: 'http://t.shenggutech.com/decor/provider_worker/addWorker.do',
				contentType:'application/json;charset=utf-8',
				data: JSON.stringify(request),
				success: function(res){
					document.getElementById("result").innerText = JSON.stringify(res);
				},
				dataType: 'json'
			});
    	}
    	function serviceDemo3() {
    		var request={
			"msg_title": "通知",
			"push_type": "0",
			"receivers": 
			[
				1,
				2
			],

			"msg_content": "decor正式上线，请做好准备工作",
			"msg_type": "0",
			"send_user": "system"
		};
    		
    		$.ajax({
				type: 'POST',
				url: 'http://t.shenggutech.com/msg/push_msg/pushMsgToUsers.do',
				contentType:'application/json;charset=utf-8',
				data: JSON.stringify(request),
				success: function(res){
					document.getElementById("result").innerText = JSON.stringify(res);
				},
				dataType: 'json'
			});
    	}
    {/literal}
    </script>
</body>
</html>