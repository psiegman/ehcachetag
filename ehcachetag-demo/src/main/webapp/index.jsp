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


<h2>Using a simple modifier</h2>
This uses the 'pparam' modifier. This modifier uses the value of the 'p' request parameter as part of the cacheKey, and <b>will not cache without it</b><br/>
<h3>Java source</h3>
<pre class="code">
public class RequestParameterCacheTagModifier extends AbstractCacheTagModifier {

	private String parameter;

	/**
	 * Modifies the cacheKey to a combination of the tag's key and the parameter from the parameter request property.
	 * 
	 * Returns null and does not cache if the parameter has no value.
	 */
	@Override
	public void beforeLookup(CacheTag cacheTag, PageContext pageContext) {
		Object parameterValue = ((HttpServletRequest) pageContext.getRequest()).getParameter(parameter);
		Object cacheKey;
		if (parameterValue == null) {
			cacheKey = null;
		} else {
			cacheKey = new CompositeCacheKey(cacheTag.getKey(), parameterValue);
		}
		cacheTag.setKey(cacheKey);
	}
	
	...
}
</pre>
<h3>web.xml</h3>
<pre class="code">
    &lt;listener&gt;
        &lt;listener-class&gt;
			nl.siegmann.ehcachetag.EHCacheTagServletContextListener
        &lt;/listener-class&gt;
    &lt;/listener&gt;

	&lt;context-param&gt;
		&lt;param-name&gt;ehcachetag.cacheTageModifierFactoryConfig&lt;/param-name&gt;
		&lt;param-value&gt;
		pparam=nl.siegmann.ehcachetag.cachetagmodifier.RequestParameterCacheTagModifier?parameter=p
		&lt;/param-value&gt;
	&lt;/context-param&gt;
</pre>
<h3>JSP</h3>
<pre class="code">
&lt;%@ taglib prefix="ect" uri="http://www.siegmann.nl/ehcachetag/taglib" %&gt;

&lt;ect:cache key="test" modifiers="pparam"&gt;
	Content generated on &lt;fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="\${now}"/&gt;
&lt;/ect:cache&gt;
</pre>
<h3>Result</h3>
<ect:cache key="test" modifiers="pparam">
	Content generated on <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss.SSS" value="${now}"/>
</ect:cache>
</div>
</body>
</html>
