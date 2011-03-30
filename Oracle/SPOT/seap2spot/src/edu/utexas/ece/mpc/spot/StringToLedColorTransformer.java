package edu.utexas.ece.mpc.spot;

import java.util.Hashtable;
import java.util.NoSuchElementException;

import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.squawk.util.StringTokenizer;

/**
 * ...
 *
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 */
public class StringToLedColorTransformer {
    private static final Hashtable COLORS = new Hashtable();

    static {
        COLORS.put("RED", LEDColor.RED);
        COLORS.put("GREEN", LEDColor.GREEN);
        COLORS.put("BLUE", LEDColor.BLUE);
        COLORS.put("CYAN", LEDColor.CYAN);
        COLORS.put("MAGENTA", LEDColor.MAGENTA);
        COLORS.put("YELLOW", LEDColor.YELLOW);
        COLORS.put("TURQUOISE", LEDColor.TURQUOISE);
        COLORS.put("PUCE", LEDColor.PUCE);
        COLORS.put("MAUVE", LEDColor.MAUVE);
        COLORS.put("CHARTREUSE", LEDColor.CHARTREUSE);
        COLORS.put("ORANGE", LEDColor.ORANGE);
        COLORS.put("WHITE", LEDColor.WHITE);
    }

    public Object transform(Object argument) {
        if (argument == null || !(argument instanceof String)) {
            throw new IllegalArgumentException("Requires non-null String argument");
        }

        if (COLORS.containsKey(argument)) {
            return COLORS.get(argument);
        }

        String parseable = (String) argument;

        try {
            StringTokenizer tokens = new StringTokenizer(parseable, ",");
            int red = Integer.parseInt(tokens.nextToken());
            int green = Integer.parseInt(tokens.nextToken());
            int blue = Integer.parseInt(tokens.nextToken());

            return new LEDColor(red, green, blue);
        } catch (NoSuchElementException e) {
            // Ignore
        } catch (NumberFormatException e) {
            // Ignore
        }

        return null;
    }
}