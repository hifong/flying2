<!DOCTYPE html>
<html>
<head>
    <title>Schema变更报表</title>
</head> 
<body>
	<center><H1>Schema变更报表</H1></center>
    {widget serviceId="ChangeLogService:findSummariesByDays" template="statics/report/change_logs_part.tpl" params="days=30"}
    
</body>
</html>