<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<p>Hello World</p>

	<form action="/hadoop-cloud/hdfsDownToLocal.do" method = "POST">
			用户文件夹: <input type="text" name="userDir"> <br />
			需要下载的文件: <input type="text" name="downloadFile" /> 
			<input type="submit" value="Submit" />


		</form>
</body>
</html>