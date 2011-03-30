package edu.utexas.ece.mpc.spot;

import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.sensorboard.peripheral.LEDColor;

/**
 * A base class for displaying numbers to the SunSPOT leds.  Periodicaly, the subclasses 'getValue()' method is queried
 * for a new value.  When a new value is provided, it is displayed in binary on the LEDs provided in the constructor.
 * Note that the number of LEDs provided limits the number of values that can be shown, so only the least significat
 * bits are displayed.
 *
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 */
public abstract class TriColorLedIndicator extends Thread {

    private final ITriColorLED[] leds;

    private boolean stopRequested = false;

    private int lastValue = 0;
    private static final double INTESITY_ON = 0.80;
    private static final double INTESITY_OFF = 0.02;

    public TriColorLedIndicator(ITriColorLED[] leds) {
        this(leds, 0, leds.length);
    }

    public TriColorLedIndicator(ITriColorLED[] leds, int startIndex, int endIndex) {
        this.leds = new ITriColorLED[endIndex - startIndex];
        System.arraycopy(leds, startIndex, this.leds, 0, endIndex - startIndex);
    }

    public synchronized void requestStop() {
        stopRequested = true;
        this.notifyAll();
    }

    public synchronized final void run() {
        while (!stopRequested) {
            updateLeds(getValue(), true);
            waitQuietly(this, 1000);
        }

        updateLeds(0, false);
    }

    // ----- Protected Implementation ----------------------------------------------------------------------------------

    protected abstract int getValue();

    protected abstract LEDColor getColor();

    protected synchronized void onChange() {
        this.notifyAll();
    }

    protected synchronized void updateLeds(int value, boolean shadeOffBits) {
        // convert the number to range 0-8
        value = Math.abs(value % 9);
        // convert the number to range 0-127
        value = (int) (Math.pow(2, value) - 1);

        LEDColor ledColor = getColor();
        if (ledColor == null) {
            ledColor = LEDColor.RED;
        }
        for (int i = 0; i < leds.length; i++) {
            if ((value & 1) > 0) {
                leds[i].setRGB((int) (ledColor.red() * INTESITY_ON), (int) (ledColor.green() * INTESITY_ON), (int) (ledColor.blue() * INTESITY_ON));
            } else if (shadeOffBits) {
                leds[i].setRGB((int) (ledColor.red() * INTESITY_OFF), (int) (ledColor.green() * INTESITY_OFF), (int) (ledColor.blue() * INTESITY_OFF));
            }
            leds[i].setOn(true);
            value = value >> 1;
        }

        lastValue = value;
    }

    private void waitQuietly(Object monitor, int millis) {
        try {
            monitor.wait(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }
}
