package edu.utexas.ece.mpc.seap.seap2spot;

import java.util.TimerTask;

import edu.utexas.ece.mpc.spot.Counter;
import edu.utexas.ece.mpc.spot.SimpleIndicator;
import edu.utexas.ece.mpc.spot.UriToResponseBodyTransformer;

/**
 * ...
 *
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 */
public class UpdateNumberTask extends TimerTask {

    private final UriToResponseBodyTransformer uriTransformer = new UriToResponseBodyTransformer();

    private String uri;
    private SimpleIndicator display;
    private Counter counter;

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
        String parseableNumber;
        int numberToDisplay = -1;
        try {
            parseableNumber = (String) uriTransformer.transform(uri);
            if (parseableNumber != null) {
                numberToDisplay = Integer.parseInt(parseableNumber.trim());
            }
            if (numberToDisplay >= 0) {
                display.setValue(numberToDisplay);
            }
            if (counter != null) {
                counter.increment();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
