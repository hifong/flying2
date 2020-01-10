<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1"/>
    <title>企业信息管理平台</title>
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/themes/{$page_theme | default: 'default'}/easyui.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/easyui/css/style.css">
    <link rel="stylesheet" type="text/css" href="{$contextPath}/{$Module.id}/style/my.css">
    
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="{$contextPath}/{$Module.id}/easyui/jquery.easyui.min.js"></script>
    
    <script type="text/javascript" src="{$contextPath}/assets/javascript/my.js"></script>
	<script type="text/javascript" src="{$contextPath}/assets/highcharts/highcharts.js"></script>
	<script type="text/javascript" src="{$contextPath}/assets/highcharts/modules/exporting.js"></script>
	<script type="text/javascript" src="{$contextPath}/assets/highcharts/modules/export-data.js"></script>
</head>
<body style="display:inline-block; width:98%;">
	<div class="easyui-layout" style="width:100%;height:100%;">
	
		<div data-options="region:'center',split:true" style="width:75%;">
	
			<div id="container1" class="easyui-panel" title="利润分析" style="width:100%;height:50%;padding:2px;">
			</div>
	
			<div id="container2" class="easyui-panel" title="成本市值分析" style="width:100%;height:50%;padding:2px;">
			</div>
		
		</div>
		<div data-options="region:'east',split:true" style="width:25%;">
			<div id="p" class="easyui-panel" title="投资有风险，牢记这些原则" style="width:100%;padding:2px;">
				<ul id="exps">
				</ul>
			</div>
			<div id="p" class="easyui-panel" title="操盘提醒" style="width:100%;padding:2px;" data-options="collapsible:true">
				<p style="font-size:14px">昨日美股大涨</p>
				<ul>
					<li>跟跌不跟涨，看.</li>
				</ul>
			</div>
		</div>
	</div>
	
	{literal}
	<script type="text/javascript">
	$.getJSON('/assets/ExperienceService/findAll.do?$value=rows', function(data){
		for(var i=0; i< data.length; i++) {
	        $('#exps').append('<li>'+data[i].content+'</li> ');
		}
	});
	
	$.getJSON('/assets/AssetsPlanService/findSummaryByUser.do?frequency_unit=w&$value=rows', function(data){
		var categories = new Array();
		var series = new Array();
		var profits = new Array();
		var rates = new Array();
		var markets = new Array();
		var costs = new Array();
		
		for(var i=0; i< data.length; i ++) {
			categories[i] 	= data[data.length - 1 - i].summary_date.substring(0, 10);
			profits[i]		= parseInt(data[data.length - 1 - i].totalProfit);
			markets[i]		= parseInt(data[data.length - 1 - i].totalMarketMoney);
			costs[i]		= parseInt(data[data.length - 1 - i].totalCost);
			rates[i] 		= parseInt(data[data.length - 1 - i].totalProfitRate * 10000) / 100.0;
		}
		data = [{name:'利润', type: 'column', yAxis: 0, data:profits},{name:'利润率', type: 'spline', yAxis: 1, color: '#ff9999', data:rates }]
		rendererProfitChart(categories, data, 'container1');
		
		data = [{name:'成本', data:costs},{name:'市值', data:markets}]
		rendererAreaChart(categories, data, 'container2');
	});
	</script>
	{/literal}
</body>
</html>