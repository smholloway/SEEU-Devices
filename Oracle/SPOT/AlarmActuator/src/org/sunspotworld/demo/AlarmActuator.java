/*
 * HTTPDemo.java
 *
 * Created on Aug 9, 2007 1:08:15 PM;
 *
 * Copyright (c) 2007,2008 Sun Microsystems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.sunspotworld.demo;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ITemperatureInput;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.Utils;

import java.io.*;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import java.util.Random;

/**
 * 
 * The MIDlet shown below retrieves commad from the web application at http://localhost:8080
 * every minute.
 * If command is to blink LEDs, the midlet flashes the onboard LEDs
 * 
 */
public class AlarmActuator extends MIDlet {
    private static final int INACTIVE = 0;
    private static final int CONNECTING = 1;
    private static final int COMPLETED = 2;
    private static final int IOERROR = 3;
    private static final int PROTOCOLERROR = 4;

    private static int GETstatus = INACTIVE;
    private static int POSTstatus = INACTIVE;

    synchronized public static void getCommand() throws IOException {
        String url = "http://localhost:3000/actuators/2/command";
        HttpConnection conn = null;
        OutputStream out = null;
        OutputStream stringStream = null;
        InputStream in = null;
        long starttime = 0;
        String resp = null;
        String result = "off";

        System.out.println("getting command");
        try {
            GETstatus = CONNECTING;
            starttime = System.currentTimeMillis();

            conn = (HttpConnection) Connector.open(url);
            conn.setRequestMethod(HttpConnection.GET);
            conn.setRequestProperty("Connection", "close");

            out = conn.openOutputStream();

            out.flush();

            in = conn.openInputStream();
            resp = conn.getResponseMessage();
            System.out.println("response is....." + resp);

            if (resp.trim().equals("OK")) {
              GETstatus = COMPLETED;

              stringStream = new ByteArrayOutputStream();
              copyStream(in, stringStream);
              result = stringStream.toString().trim().toLowerCase();
              System.out.println("result = " + result);
            }
/*
            if (resp.equals("Not Acceptable")) {
                ITriColorLED[] LED = EDemoBoard.getInstance().getLEDs();
                for (int i=0; i < LED.length; i++) {
                    blink(LED[i], 10, 255, 0, 0, 1);
                }
		        }
            else if (resp.equals("Found ") || resp.equals("OK")) {
                ITriColorLED[] LED = EDemoBoard.getInstance().getLEDs();
                for (int i=0; i < LED.length; i++) {
                    blink(LED[i], 10, 255, 0, 0, 10);
                }
		        }
            else {
                ITriColorLED[] LED = EDemoBoard.getInstance().getLEDs();
                for (int i=0; i < LED.length; i++) {
                    blink(LED[i], 10, 255, 0, 0, 100);
                }
		        }
*/
        } catch (IOException ex) {
            GETstatus = IOERROR;
            ex.printStackTrace();
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
            if (conn != null) conn.close();
        }

        if (GETstatus != COMPLETED)
            System.out.println("Posting failed: " + resp);
        
        applyCommand(result, 10000);


        System.out.println("Total time to post " +
                            "(including connection set up): " +
                            (System.currentTimeMillis() - starttime) + " ms");
        System.out.flush();
    }

    public static void copyStream(InputStream connectionStream, OutputStream stringStream) throws IOException {
        byte[] buf = new byte[64];
        int numBytes;
        while ((numBytes = connectionStream.read(buf)) >= 0) {
            stringStream.write(buf, 0, numBytes);
        }
    }

    public static void applyCommand(String command, int timeToWait) {
        System.out.println("applyCommand command = " + command);

        ITriColorLED[] LEDs = EDemoBoard.getInstance().getLEDs();

        if (command.equals("off") || !command.equals("on") ) {
            setLeds(LEDs, 0, 255, 0);
        } else {
            setLeds(LEDs, 255, 0, 0);
        }
        Utils.sleep(timeToWait);
    }

    public static void setLeds(ITriColorLED[] LEDs, int r, int g, int b) {
        for (int i = 0; i < LEDs.length; i++) {
            LEDs[i].setRGB(r, g, b);
            LEDs[i].setOn();
        }
    }

    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Starting app ...");
        new com.sun.spot.util.BootloaderListener().start();
        new CommandMonitor().start();
    }

    // This will never be called by the Squawk VM
    protected void pauseApp() {
    }

    // Only called if startApp throws any exception other than 
    // MIDletStateChangeException
    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
    }
}


class CommandMonitor extends Thread {

    public CommandMonitor() {
    }

    public void run() {
        System.out.println("Waiting 10s for commands...");

        while (true) {
            try {
                System.out.println("about to retrieve command");
                AlarmActuator.getCommand();
                //AlarmActuator.applyCommand("high", 10000);
                //Utils.sleep(10000);
            } catch (Exception e) {
                System.out.println("Caught " + e.getMessage() +
                                    " in CommandMonitor()");
            }
        }
    }
}
