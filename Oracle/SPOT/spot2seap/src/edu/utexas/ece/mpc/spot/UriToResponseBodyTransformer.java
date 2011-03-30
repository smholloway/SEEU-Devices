/*
 * UriToResponseBodyTransformer.java
 *
 * Created on February 19, 2008, 12:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.utexas.ece.mpc.spot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * A Thread to open a connection to a remote http server.  The return status and body of the http request are stored for
 * future recovery.
 *
 * @author <a href="mailto:sethh@mail.utexas.edu">Seth Holloway</a>
 * @author <a href="mailto:jlara-garduno@mail.utexas.edu">Jorge Lara-Garduno</a>
 * @author <a href="mailto:dstovall@mail.utexas.edu">Drew Stovall</a>
 */
public class UriToResponseBodyTransformer {

    // ----- Public Interface ------------------------------------------------------------------------------------------

    public synchronized Object transform(Object argument) {
        if (argument == null || !(argument instanceof String)) {
            throw new IllegalArgumentException("This method requires a String argument.");
        }

        String uri = (String) argument;

        HttpConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        OutputStream stringStream = null;

        try {
            // Open the outbound connection...
            System.out.println("Opening http connection ["+uri+"]");
            connection = (HttpConnection) Connector.open(uri);
            connection.setRequestProperty("Connection", "close");

            // Read the return code
            int returnCode = connection.getResponseCode();
            inputStream = connection.openInputStream();
            outputStream = connection.openOutputStream();

            // Read the returned response
            if (returnCode == 200) {
                stringStream = new ByteArrayOutputStream();
                copyStream(inputStream, stringStream);
                return stringStream.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
            closeQuietly(stringStream);
            closeQuietly(connection);
        }

        // If anything fails along the way, just return null
        return null;
    }

    // ----- Private Utility -------------------------------------------------------------------------------------------

    private void copyStream(InputStream connectionStream, OutputStream stringStream) throws IOException {
        byte[] buf = new byte[64];
        int numBytes;
        while ((numBytes = connectionStream.read(buf)) >= 0) {
            stringStream.write(buf, 0, numBytes);
        }
    }

    private void closeQuietly(HttpConnection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    private void closeQuietly(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    private void closeQuietly(OutputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
}