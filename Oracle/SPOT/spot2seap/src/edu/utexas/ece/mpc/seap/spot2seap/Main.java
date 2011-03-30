package edu.utexas.ece.mpc.seap.spot2seap;

import edu.utexas.ece.mpc.spot.UriToResponseBodyTransformer;

import java.io.IOException;
import java.util.Timer;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import edu.utexas.ece.mpc.spot.SimpleIndicator;
import edu.utexas.ece.mpc.spot.UptimeIndicator;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ISwitchListener;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.util.BootloaderListener;
import com.sun.spot.util.IEEEAddress;

/**
 * A simple application to post accelerometer data to a SEAP-style server.
 *
 * @author <a href="mailto:jlara-garduno@mail.utexas.edu">Jorge Lara-Garduno</a>
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 * @author <a href="mailto:sethh@mail.utexas.edu">Seth Holloway</a>
 */
public class Main extends MIDlet {

    private final Object stopMonitor = new Object();
    private boolean stopRequested = false;

    private void run() throws IOException {

        IEEEAddress ieeeAddress = new IEEEAddress(Spot.getInstance().getRadioPolicyManager().getIEEEAddress());
        String shortAddress = ieeeAddress.asDottedHex().substring(10);

        SimpleIndicator connCntIndicator = new SimpleIndicator(EDemoBoard.getInstance().getLEDs(), 0, 4, LEDColor.GREEN);
        connCntIndicator.start();

        SimpleIndicator updateIndicator = new SimpleIndicator(EDemoBoard.getInstance().getLEDs(), 4, 6, LEDColor.YELLOW);
        updateIndicator.start();
        updateIndicator.increment();

        UptimeIndicator uptime = new UptimeIndicator(EDemoBoard.getInstance().getLEDs(), 6, 8, LEDColor.BLUE);
        uptime.start();

        ReportAccelTask accelTask = new ReportAccelTask();
        //accelTask.setBaseUri(null);
        accelTask.setBaseUri("http://localhost:8080/SEAP/accelerometer.jsp");
        accelTask.setCounter(connCntIndicator);

        Timer reportTimer = new Timer();
        reportTimer.schedule(accelTask, 1000, 1000);

        UpdateConfigTask configTask = new UpdateConfigTask();
        //configTask.setBaseUriUri("http://localhost:8080/seap-server/data/sun-spots/" + shortAddress + "/accelerometerUri");
        configTask.setBaseUriUri(null);
        configTask.setAccelTask(accelTask);
        configTask.setCounter(updateIndicator);

        Timer configTimer = new Timer();
        configTimer.schedule(configTask, 1000, 1000 * 10);

        // Wait for a switch press to terminate...

        waitForSwitchPress();

        // Now wait for everyone to stop and re-join...
        System.out.println("Stopping now...");

        reportTimer.cancel();
        configTimer.cancel();

        connCntIndicator.requestStop();
        joinQuietly(connCntIndicator);
        System.out.println("ConnCnt joined...");


        updateIndicator.requestStop();
        joinQuietly(updateIndicator);
        System.out.println("updateIndicator joined...");

        uptime.requestStop();
        joinQuietly(uptime);
        System.out.println("Uptime joined...");
    }

    private void waitForSwitchPress() {
        EDemoBoard.getInstance().getSwitches()[1].addISwitchListener(new ISwitchListener() {
            public void switchPressed(ISwitch iSwitch) {
                // Ignore
            }

            public void switchReleased(ISwitch iSwitch) {
                stopRequested = true;
                synchronized (stopMonitor) {
                    stopMonitor.notifyAll();
                }
            }
        });

        // Wait for someone to request a stop... (ie the SwitchListener)

        synchronized (stopMonitor) {
            while (!stopRequested) {
                waitQuietly(stopMonitor, 1000 * 10);
            }
        }
    }

    private void waitQuietly(Object monitor, int millis) {
        try {
            monitor.wait(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    private void joinQuietly(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    // ----- SunSPOT Support -------------------------------------------------------------------------------------------

    protected void startApp() throws MIDletStateChangeException {
        new BootloaderListener().start();       // Listen for downloads/commands over USB connection
        try {
            run();
        } catch (IOException ex) { //A problem in reading the sensors.
            ex.printStackTrace();
        }
        notifyDestroyed();                      // cause the MIDlet to exit
    }

    protected void pauseApp() {
        // This will never be called by the Squawk VM
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        // Only called if startApp throws any exception other than MIDletStateChangeException
    }

    // ----- Command Line Execution Support ----------------------------------------------------------------------------

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (IOException e) {
            // Ignore
        }
    }
}
