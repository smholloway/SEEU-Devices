package edu.utexas.ece.mpc.seap.seap2spot;

import java.util.TimerTask;

import edu.utexas.ece.mpc.spot.Counter;
import edu.utexas.ece.mpc.spot.UriToResponseBodyTransformer;

/**
 * ...
 *
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 */
public class UpdateConfigTask extends TimerTask {

    private final UriToResponseBodyTransformer connection = new UriToResponseBodyTransformer();

    private String colorUriUri;
    private String numberUriUri;
    private UpdateColorTask colorTask;
    private UpdateNumberTask numberTask;
    private Counter counter;

    public String getColorUriUri() {
        return colorUriUri;
    }

    public void setColorUriUri(String colorUriUri) {
        this.colorUriUri = colorUriUri;
    }

    public String getNumberUriUri() {
        return numberUriUri;
    }

    public void setNumberUriUri(String numberUriUri) {
        this.numberUriUri = numberUriUri;
    }

    public UpdateColorTask getColorTask() {
        return colorTask;
    }

    public void setColorTask(UpdateColorTask colorTask) {
        this.colorTask = colorTask;
    }

    public UpdateNumberTask getNumberTask() {
        return numberTask;
    }

    public void setNumberTask(UpdateNumberTask numberTask) {
        this.numberTask = numberTask;
    }

    public Counter getCounter() {
        return counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    public void run() {
        try {
            String colorUri = (String) connection.transform(colorUriUri);
            if (colorUri != null && !"null".equals(colorUri) && colorTask != null) {
                colorTask.setUri(colorUri.trim());
            }

            String numberUri = (String) connection.transform(numberUriUri);
            if (numberUri != null && !"null".equals(numberUri) && numberTask != null) {
                numberTask.setUri(numberUri.trim());
            }

            if (counter != null) {
                counter.increment();
            }
        } catch (Throwable e) {
            // Ignore
        }
    }
}