<jsp:useBean scope="application" id="tags" class="java.util.TreeMap"/>
<jsp:useBean scope="application" id="spot47dd" class="edu.utexas.ece.mpc.seap.SunSPOT"/>
<jsp:useBean scope="application" id="spot342e" class="edu.utexas.ece.mpc.seap.SunSPOT"/>
<%
    // Using the accelerometer data from spot47dd, we change colors from Red to Yellow to Green...

    String color = "YELLOW";

	try {
	    if (spot47dd.getX() < -0.2) {
			color = "RED";
	    }
	    if (spot47dd.getX() > 0.2) {
			color = "GREEN";
	    }
    } catch(Exception e) { 
    	color = "WHITE";
    }
%>
<%= color %>