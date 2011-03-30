<jsp:useBean scope="application" id="tags" class="java.util.TreeMap"/>
<jsp:useBean scope="application" id="spot47dd" class="edu.utexas.ece.mpc.seap.SunSPOT"/>
<jsp:useBean scope="application" id="spot342e" class="edu.utexas.ece.mpc.seap.SunSPOT"/>
<%
    // Using the accelerometer data from spot47dd, we show the first 2 digits of the y-axis accelerometer reading
%>
<%= (int)(spot47dd.getY() * 100.0) %>