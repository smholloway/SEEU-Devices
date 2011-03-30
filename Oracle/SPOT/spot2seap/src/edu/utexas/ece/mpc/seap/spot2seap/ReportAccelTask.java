package edu.utexas.ece.mpc.seap.spot2seap;

import java.io.IOException;
import java.util.TimerTask;

import edu.utexas.ece.mpc.spot.Counter;
import edu.utexas.ece.mpc.spot.UriToResponseBodyTransformer;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.IAccelerometer3D;

/**
 * ...
 *
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 */
public class ReportAccelTask extends TimerTask {

    private final UriToResponseBodyTransformer connection = new UriToResponseBodyTransformer();

    private String baseUri;
    private Counter counter;

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public Counter getCounter() {
        return counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }


    public void run() {
        try {
            String reportUri = getReportUri();

            if (reportUri != null) {
                connection.transform(reportUri);
            }

            if (counter!= null) {
                counter.increment();
            }
        } catch (Throwable t) {
            // Ignore
        }
    }

    // ----- Protected Implementation ----------------------------------------------------------------------------------

    protected String getReportUri() {
        if (baseUri == null) {
            return null;
        }

        String postAddr = null;

        try {
            IAccelerometer3D accelerometer = EDemoBoard.getInstance().getAccelerometer();
            double x = getNormalizedTilt(accelerometer, IAccelerometer3D.X_AXIS);
            double y = getNormalizedTilt(accelerometer, IAccelerometer3D.Y_AXIS);
            double z = getNormalizedTilt(accelerometer, IAccelerometer3D.Z_AXIS);

            postAddr = baseUri + "?x=" + x + "&y=" + y + "&z=" + z;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return postAddr;
    }

    protected double getNormalizedTilt(IAccelerometer3D accelerometer, int axis) throws IOException {
        double value = accelerometer.getTilt(axis);
        value = radiansToFraction(value);
        value = roundToThree(value);
        return value;
    }

    protected double roundToThree(double value) {
        return (double) ((int) (value * 1000.0)) / 1000.0;
    }

    /**
     * Converts argument in radians to a fraction.   Maps the values in [-PI/2 .. PI/2] into [-1 .. 1].
     * <p/>
     * If the argument is outside the range [-PI/2 .. PI/2], PI is added or substracted from it some number of times
     * until it is in this range.
     *
     * @param tilt
     * @return
     */
    protected double radiansToFraction(double tilt) {
        double piOverTwo = Math.PI / 2.0;
        while (tilt < (-1) * piOverTwo) {
            tilt += Math.PI;
        }
        while (tilt > piOverTwo) {
            tilt -= Math.PI;
        }

        return tilt * (2 / Math.PI);
    }
}