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
import com.sun.spot.sensorboard.peripheral.IAccelerometer3D;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.Utils;

import java.io.*;
import java.util.Date;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import java.util.Random;

/**
 * 
 * The MIDlet shown below posts light data to http://localhost:8080
 * every 30 seconds.
 * 
 */
public class TiltXSensor extends MIDlet {
    private static final int INACTIVE = 0;
    private static final int CONNECTING = 1;
    private static final int COMPLETED = 2;
    private static final int IOERROR = 3;
    private static final int PROTOCOLERROR = 4;

    private static int GETstatus = INACTIVE;
    private static int POSTstatus = INACTIVE;


    synchronized public static void postTiltXData(double tiltX) throws IOException {
        HttpConnection conn = null;
        OutputStream out = null;
        InputStream in = null;
        long starttime = 0;
        String resp = null;

        System.out.println("Posting: ");
        try {
            POSTstatus = CONNECTING;
            starttime = System.currentTimeMillis();

            conn = (HttpConnection) Connector.open("http://localhost:3000/sensors/3/readings?reading[data]=" + tiltX);
            conn.setRequestMethod(HttpConnection.POST);
            conn.setRequestProperty("Connection", "close");

            out = conn.openOutputStream();

            out.flush();

            in = conn.openInputStream();
            resp = conn.getResponseMessage();
            if (resp.equals("OK")) {
                POSTstatus = COMPLETED;
            } else if (resp.trim().equals("Found")) {
                POSTstatus = COMPLETED;
            } else {
                POSTstatus = PROTOCOLERROR;
            }
        } catch (IOException ex) {
            POSTstatus = IOERROR;
            ex.printStackTrace();
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
            if (conn != null) conn.close();
        }

        if (POSTstatus != COMPLETED)
            System.out.println("Posting failed: " + resp + ".");

        System.out.flush();
    }


    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Starting app ...");
        new com.sun.spot.util.BootloaderListener().start();       // Listen for downloads/commands over USB connection
        new TiltXMonitor().start();
    }

    // This will never be called by the Squawk VM
    protected void pauseApp() {
    }

    // Only called if startApp throws any exception other than 
    // MIDletStateChangeException
    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
    }
}


class TiltXMonitor extends Thread {
    IAccelerometer3D tiltX;
    EDemoBoard demoBoard;

    public TiltXMonitor() {
      demoBoard = EDemoBoard.getInstance();
    }

    public void run() {
      double lastTiltX = 0;
      System.out.println("Monitoring Tilt on X axis...");

      while (true) {
        try {
          System.out.println("about to post");
          lastTiltX = demoBoard.getAccelerometer().getTiltX();
          System.out.println(" tiltX is " + lastTiltX);
          TiltXSensor.postTiltXData(lastTiltX);
          Utils.sleep(10000);
        }

        catch (Exception e) {
          System.out.println("Caught " + e.getMessage() +
                              " in TiltXMonitor()");
        }
      }
    }
}
