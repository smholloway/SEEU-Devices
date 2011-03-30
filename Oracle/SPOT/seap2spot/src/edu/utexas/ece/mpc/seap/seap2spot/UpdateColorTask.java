package edu.utexas.ece.mpc.seap.seap2spot;

import java.util.TimerTask;

import edu.utexas.ece.mpc.spot.Counter;
import edu.utexas.ece.mpc.spot.SimpleIndicator;
import edu.utexas.ece.mpc.spot.StringToLedColorTransformer;
import edu.utexas.ece.mpc.spot.UriToResponseBodyTransformer;

import com.sun.spot.sensorboard.peripheral.LEDColor;

/**
 * ...
 *
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 */
public class UpdateColorTask extends TimerTask {

    private final UriToResponseBodyTransformer uriTransformer = new UriToResponseBodyTransformer();
    private final StringToLedColorTransformer colorTransformer = new StringToLedColorTransformer();

    private Counter counter;
    private String uri;
    private SimpleIndicator display;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public SimpleIndicator getDisplay() {
        return display;
    }

    public void setDisplay(SimpleIndicator display) {
        this.display = display;
    }

    public Counter getCounter() {
        return counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    public void run() {
        if (uri == null) {
            return;
        }
        String parseableColor = null;
        LEDColor color = null;
        try {
            parseableColor = (String) uriTransformer.transform(uri);
            if (parseableColor != null) {
                color = (LEDColor) colorTransformer.transform(parseableColor.trim());
            }
            if (color != null) {
                display.setColor(color);
            }
            if (counter!= null) {
                counter.increment();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}