package edu.utexas.ece.mpc.spot;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;

/**
 * ...
 *
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 */
public class SimpleIndicator extends TriColorLedIndicator implements Counter {

    private int value = 0;
    private LEDColor color = LEDColor.RED;

    public SimpleIndicator(ITriColorLED[] leds) {
        super(leds);
    }

    public SimpleIndicator(ITriColorLED[] leds, int startIndex, int endIndex) {
        super(leds, startIndex, endIndex);
    }

    public SimpleIndicator(ITriColorLED[] leds, int startIndex, int endIndex, LEDColor color) {
        super(leds, startIndex, endIndex);
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public synchronized void setValue(int value) {
        this.value = value;
        onChange();
    }

    public LEDColor getColor() {
        return color;
    }

    public synchronized void setColor(LEDColor color) {
        this.color = color;
        onChange();
    }

    public synchronized void increment() {
        value++;
        onChange();
    }

    public synchronized void decrement() {
        value--;
        onChange();
    }
}