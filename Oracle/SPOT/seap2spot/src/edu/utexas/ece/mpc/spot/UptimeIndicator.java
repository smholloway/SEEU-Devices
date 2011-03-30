package edu.utexas.ece.mpc.spot;

import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.sensorboard.peripheral.LEDColor;

/**
 * ...
 *
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 */
public class UptimeIndicator extends TriColorLedIndicator {

    private final long startTime = System.currentTimeMillis();

    private final LEDColor color;

    public UptimeIndicator(ITriColorLED[] leds) {
        super(leds);
        this.color= LEDColor.BLUE;
    }

    public UptimeIndicator(ITriColorLED[] leds, int startIndex, int endIndex) {
        super(leds, startIndex, endIndex);
        this.color = LEDColor.BLUE;
    }

    public UptimeIndicator(ITriColorLED[] leds, int startIndex, int endIndex, LEDColor color) {
        super(leds, startIndex, endIndex);
        this.color = color;
    }

    protected int getValue() {
        return (int) ((System.currentTimeMillis() - startTime) / 1000.0);
    }

    protected LEDColor getColor() {
        return color;
    }
}
