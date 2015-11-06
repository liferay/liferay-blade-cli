<!doctype html>

<html class="no-js" lang="">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <title></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">
    </head>
    <body>
    	<table>
    		<tr>
    			<th>Title</th>
    			<th>Type</th>
    			<th>File</th>
    			<th>Line</th>
    		</tr>
<#list problems as problem>
	    	<tr>
	    		<td>${problem.title}</td>
	    		<td>${problem.type}</td>
	    		<td>${problem.file}</td>
	    		<td>${problem.lineNumber}</td>
	    	</tr>
</#list>
    	</table>
    </body>
</html>
