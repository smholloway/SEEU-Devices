SEEU Fan Actuator

Adapted from Sun SPOT HTTP Demo - Version 1.0 by Vipul Gupta 

--------
OVERVIEW
--------

Using the Sun SPOT LEDs, blink at the fan speed.

States: OFF, LOW, MEDIUM, HIGH
OFF state is indicated by all LEDs being off
LOW state is indicated by all LEDs blinking once every 6 seconds 
MEDIUM state is indicated by all LEDs blinking once every 3 seconds 
HIGH state is indicated by all LEDs blinking once every second 

This demo application uses the Sun SPOT's built-in HTTP networking 
to post temperature data to the local web app, SEEU

The application uses HTTP POSTs to post a brief message aka
a "microblog", e.g. "SPOT 0014.4F01.0000.0123 sez yay!", when 
the SPOT is shaken vigorously (as measured by its accelerometer). 
The SPOT blinks an LED (5th from the left) thrice when it detects
a shake vigorous enough to trigger an HTTP POST. Posted microblogs
can be viewed by pointing any web browser to:

   http://twitter.com/sunspotdemo

NOTE:
=====

---------------------------------------
STEPS FOR BUILDING AND RUNNING THE DEMO
---------------------------------------
To build and run the demo, follow these steps.

1. Compiling and loading the application on a SPOT:

   [Follow the instructions in 1(a) if you have real SPOTs. Otherwise,
   follow the instructions in 1(b) if you are using emulated SPOTs.]

   (a) In the directory containing this README file, execute

       ant deploy

       This will compile the application and load it. Disconnect
       the SPOT. *DO NOT* reset it just yet.

    (b) In the directory containing this README file, execute

        ant solarium

        to start up the Solarium tool which also includes the SPOT
        emulator. 
          i.  Create a virtual SPOT by choosing Emulator -> New Virtual
              SPOT.
         ii.  Right click on the virtual SPOT, choose "Deploy a MIDlet 
              bundle ..." from the menu. A file chooser window will pop 
              up. Navigate to the HTTPDemo subdirectory and select
              the "build.xml" file. This will automatically cause the 
              application to be compiled and loaded into the virtual SPOT.
        iii.  Right click on the virtual SPOT, choose "Display application
              output" -> "in Internal Frame" to open a window where
              output from the application will be displayed.
         iv.  Right click on the virtual SPOT, choose "Display sensor
              panel" to open up a multi-tabbed panel that allows you
              to manipulate various sensor readings experienced by the
              virtual SPOT.

2. Starting a socket proxy:

   [Skip this step if you are using emulated SPOTs. Emulated SPOTs run
    on a host computer and can establish direct HTTP connections to
    Internet hosts without the need for a proxy.]
 
   HTTP networking requires the use of a basestation and a
   special host application called the socket-proxy that
   acts as a relay between a TCP/IP connection (e.g., with
   Twitter.com in this case) and a radiostream connection
   to a SPOT.

   Attach a basestation to your host with a USB cable and execute

   ant socket-proxy-gui

   Wait for a GUI window to open, and check the box labelled
   "I/O". This will enable logging of the actual data sent
   over the HTTP connection.

   Press the start button (in the GUI window) to start the socket-proxy.

3. Starting the HTTPDemo application:

   [Follow the instructions in 3(a) if you have real SPOTs. Otherwise,
   follow the instructions in 3(b) if you are using emulated SPOTs.]

   (a) Now reset the SPOT you used in Step 1 and monitor the "Proxy Log"
       in the GUI window from Step 2.
   
       You should see the SPOT retrieving the last microblog
       posted by "sunspotdemo". This is done via an HTTP GET request
       and the SPOT blinks its left most LED green when a GET request is
       in progress. After the message has been downloaded,
       it can be displayed by gently moving the SPOT back and forth
       in a direction perpendicular to the LED array. You can verify
       that the correct message is being displayed by pointing your
       browser to http://twitter.com/sunspotdemo.

       Next, shake the SPOT along the Z-axis, i.e. holding it flat
       and moving it up and down with force (be careful not to let go
       or you might damage your SPOT :)). If you do this vigorously 
       enough for the SPOT to quickly blink its the 5th LED (from the 
       left) thrice, the SPOT will post a microblog. The last part of 
       the text is chosen randomly from amongst a set of pre-configured
       strings. The message is posted using an HTTP POST request and 
       the SPOT blinks its rightmost LED green when a POST request is 
       in progress.
       
       Again, you can verify that by pointing your browser to
      
       http://twitter.com/sunspotdemo

       and matching the address in the last post against that of your
       own SPOT.


   (b) Now start the application by right clicking on the virtual SPOT and
       selecting "Run MIDlet" -> "HTTPDemo".
 
       You should see the SPOT retrieving the last microblog posted by
       "sunspotdemo". This is done via an HTTP GET request
       and the SPOT blinks its left most LED green when a GET request is
       in progress. After the message has been downloaded, it is
       displayed in the application output (in the window that was created
       in Step 1(b)iii). You can verify that the correct message is being
       displayed by pointing your browser to 
                     http://twitter.com/sunspotdemo.

       Next, select the "Accel" tab in the sensor panel and move the 
       Z-axis slider all the way down and up again until the 5th LED 
       (from the left) blinks white. This indicates that the SPOT has
       experienced a shake vigorous enough to warrant a new post. 
       The last part of the posted microblog is chosen randomly from 
       amongst a set of pre-configured strings. The message is printed
       in the application output and posted using an HTTP POST request. 
       The SPOT blinks its rightmost LED green when a POST request is 
       in progress.
       
       Again, you can verify that by pointing your browser to
      
       http://twitter.com/sunspotdemo

       and matching the address in the last post against that of your
       own SPOT.

---------------
TROUBLESHOOTING
---------------

  - This demo requires a working Internet connection. Make sure
    your host is connected to the Internet and can access Twitter.com
    directly *without* going through a proxy.

    If you happen to be behind a firewall and need to use a proxy
    to reach Twitter.com, add the following line to 
    resources/META-INF/MANIFEST.MF (see the section on "Configuring
    the http protocol" inside the Sun SPOT developer's Guide)

    com.sun.squawk.io.j2me.http.Protocol-HttpProxy: <proxyaddress>:<port>


  - If you have a working Internet connection but are seeing errors like
    "Attempt to open connection twice for ...", try restarting the demo.
    You should never see this problem on an emulator. Here is the exact
    sequence of steps you should use:

    a. turn off the SPOT running the HTTPDemo
    b. terminate the socket proxy host application
    c. restart the socket proxy host application (i.e. repeat Step 2)
       and wait until it is running again
    d. turn on the SPOT to start the HTTPDemo

  - If you are still encountering issues, please post them to the
    forums at http://www.sunspotworld.com/forums
