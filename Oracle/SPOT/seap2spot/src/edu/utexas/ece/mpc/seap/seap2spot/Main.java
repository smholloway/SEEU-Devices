package edu.utexas.ece.mpc.seap.seap2spot;

import java.io.IOException;
import java.util.Timer;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import edu.utexas.ece.mpc.spot.SimpleIndicator;
import edu.utexas.ece.mpc.spot.UriToResponseBodyTransformer;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ISwitchListener;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.util.BootloaderListener;
import com.sun.spot.util.IEEEAddress;

/**
 * SEAP-style actuator for displying a number and color on the LEDs of the sunspot.  This code is a first draft all in
 * one method, and ready to be refactored into several objects as time permits.
 *
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 * @author <a href="mailto:sethh@mail.utexas.edu">Seth Holloway</a>
 */
public class Main extends MIDlet {

    private final UriToResponseBodyTransformer transformer = new UriToResponseBodyTransformer();

    private final Object stopMonitor = new Object();
    private boolean stopRequested = false;

    protected void run() throws IOException {
        System.out.println("Seap 2 Spot starting...");
        IEEEAddress ieeeAddress = new IEEEAddress(Spot.getInstance().getRadioPolicyManager().getIEEEAddress());
        String shortAddress = ieeeAddress.asDottedHex().substring(10);

        // ----- Update the Display Support -----

        SimpleIndicator display = new SimpleIndicator(EDemoBoard.getInstance().getLEDs(), 0, 5, LEDColor.BLUE);
        display.setValue(255);
        display.start();

        SimpleIndicator numberCntr = new SimpleIndicator(EDemoBoard.getInstance().getLEDs(), 5, 6, LEDColor.YELLOW);
        numberCntr.setValue(255);
        numberCntr.start();

        SimpleIndicator colorCntr = new SimpleIndicator(EDemoBoard.getInstance().getLEDs(), 6, 7, LEDColor.YELLOW);
        colorCntr.setValue(255);
        colorCntr.start();

        UpdateNumberTask numberTask = new UpdateNumberTask();
        numberTask.setDisplay(display);
        numberTask.setCounter(numberCntr);

        UpdateColorTask colorTask = new UpdateColorTask();
        colorTask.setDisplay(display);
        colorTask.setCounter(colorCntr);

        Timer timer = new Timer();
        timer.schedule(numberTask, 1000, 2000);
        timer.schedule(colorTask,  2000, 2000);

        // ----- Periodic Update of Configuration Support -----

        SimpleIndicator configUpdateCntr = new SimpleIndicator(EDemoBoard.getInstance().getLEDs(), 7, 8, LEDColor.YELLOW);
        configUpdateCntr.setValue(255);
        configUpdateCntr.start();

        UpdateConfigTask configTask = new UpdateConfigTask();
        configTask.setColorUriUri("http://localhost:8080/seap-server/data/sun-spots/" + shortAddress + "/colorUri");
        configTask.setColorTask(colorTask);
        configTask.setNumberUriUri("http://localhost:8080/seap-server/data/sun-spots/" + shortAddress + "/numberUri");
        configTask.setNumberTask(numberTask);
        configTask.setCounter(configUpdateCntr);

        Timer configTimer = new Timer();
//        configTimer.schedule(configTask, 10, 1000*1);
        configTimer.schedule(configTask, 10, 1000*10);

        // ----- Wait until button pressed -----------------------------------------------------------------------------

        waitForSwitchPress();

        // ----- Clean up and shutdown ---------------------------------------------------------------------------------

        timer.cancel();
        configUpdateCntr.requestStop();
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

    // ----- SunSPOT Support -------------------------------------------------------------------------------------------

    protected void startApp() throws MIDletStateChangeException {
        new BootloaderListener().start();       // Listen for downloads/commands over USB connection
        try {
            run();
        } catch (IOException ex) {
            //A problem in reading the sensors.
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
