package edu.utexas.ece.mpc.seap.spot2seap;

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

    private String baseUriUri;
    private ReportAccelTask accelTask;
    private Counter counter;

    public String getBaseUriUri() {
        return baseUriUri;
    }

    public void setBaseUriUri(String baseUriUri) {
        this.baseUriUri = baseUriUri;
    }

    public ReportAccelTask getAccelTask() {
        return accelTask;
    }

    public void setAccelTask(ReportAccelTask accelTask) {
        this.accelTask = accelTask;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    public void run() {
        try {
            String baseUri = (String) connection.transform(baseUriUri);

            if (baseUri != null && !"null".equals(baseUri) && accelTask != null) {
                accelTask.setBaseUri(baseUri.trim());
            }
            if (counter != null) {
                counter.increment();
            }
        } catch (Throwable e) {
            // Ignore
        }
    }
}