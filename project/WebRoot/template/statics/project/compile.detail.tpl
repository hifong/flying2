<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>企业信息管理平台</title>
</head> 
<body onpageshow="resetPagination('dg')">
    {widget template="statics/project/compile.info.tpl"  moduleId="pas" serviceId="ProjectCompileLog:findById" params="log_id=`$Request.log_id`" }
</body>
</html>