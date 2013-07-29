<html>
<head>
<title>EHCacheTag test</title>
</head>
<body>
<%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %>
<ect:cache key="test">
	<h2>Content generated on <%= new java.util.Date() %> (<%= System.currentTimeMillis() %>)</h2>
</ect:cache>
</body>
</html>