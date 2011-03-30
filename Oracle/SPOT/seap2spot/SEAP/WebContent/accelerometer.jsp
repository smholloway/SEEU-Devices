<jsp:useBean scope="application" id="tags" class="java.util.TreeMap"/>
<jsp:useBean scope="application" id="spot342e" class="edu.utexas.ece.mpc.seap.SunSPOT"/>
<jsp:useBean scope="application" id="spot47dd" class="edu.utexas.ece.mpc.seap.SunSPOT"/>
<jsp:setProperty name="spot47dd" property="x"/>
<jsp:setProperty name="spot47dd" property="y"/>
<jsp:setProperty name="spot47dd" property="z"/>
<html>
<head>
    <title>Accelerometer Data for Sun SPOT 0000.47dd</title>
</head>
<body>
<form action="accelerometer.jsp" method="post">
    X: <input name="x" value="<%= spot47dd.getX() %>"/><br>
    Y: <input name="y" value="<%= spot47dd.getY() %>"/><br>
    Z: <input name="z" value="<%= spot47dd.getZ() %>"/><br>
    <input type="submit"/>
</form>
</body>
</html>