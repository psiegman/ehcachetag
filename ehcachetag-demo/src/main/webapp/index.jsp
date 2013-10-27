<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %>

<html>
<head>
<title>EHCacheTag test</title>
<style>
	pre.code {
		border: 1px solid #c0c0c0;
		background-color: #f0f0f0;
		margin-left: 40px;
		padding: 1em;
	}
</style>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap-theme.min.css">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">

</head>
<body>
<div class="container">
<c:set var="now" value="<%=new java.util.Date()%>" />

<h2>Simple test tag</h2>
Stores the content with the key 'test'.
<h3>JSP</h3>
<pre class="code">
&lt;%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %&gt;

&lt;ect:cache key="test"&gt;
	Content generated on &lt;fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="\${now}"/&gt;
&lt;/ect:cache&gt;
</pre>
<h3>Result</h3>
Note how the text only once per minute:<br/>
<ect:cache key="test">
	Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
</ect:cache>
<hr/>
<h2>Using a simple keyFactory</h2>
This uses the username keyFactory. This keyFactory uses the 'p' request parameter as part of the cacheKey.<br/>
<h3>Java source</h3>
<pre class="code">
public class RequestParameterCacheKeyFactory extends AbstractPageCacheKeyFactory {

	private String parameter;
	
	@Override
	public Object createCacheKey(Object tagCacheKey, PageContext pageContext) {
		Object parameterValue = ((HttpServletRequest) pageContext.getRequest()).getParameter(parameter);
		if (parameterValue == null) {
			return null;
		}
		return new CompositeCacheKey(tagCacheKey, parameterValue);
	}

	...
}
</pre>
<h3>web.xml</h3>
<pre class="code">
&lt;context-param&gt;
	&lt;param-name&gt;ehcachetag.cacheKeyMetaFactoryConfig&lt;/param-name&gt;
	&lt;param-value&gt;
		pparam=nl.siegmann.ehcachetag.cachekeyfactories.RequestParameterCacheKeyFactory?parameter=p
	&lt;/param-value&gt;
&lt;/context-param&gt;
</pre>
<h3>JSP</h3>
<pre class="code">
&lt;%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %&gt;

&lt;ect:cache key="test" keyFactory="pparam"&gt;
	Content generated on &lt;fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="\${now}"/&gt;
&lt;/ect:cache&gt;
</pre>
<h3>Result</h3>
<ect:cache key="test" keyFactory="pparam">
	Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
</ect:cache>
</div>
</body>
</html>
