package com.injoit.examplechat.networking;

import com.injoit.examplechat.utils.*;

import javax.microedition.io.*;
import net.rim.device.api.io.transport.*;
import net.rim.device.api.io.transport.options.*;
import net.rim.device.api.util.*;
import net.rim.device.api.io.transport.options.*;
import net.rim.device.api.collection.util.*;

public class HTTPConnManager
{
    ConnectionFactory _factory = new ConnectionFactory();
    private HTTPAnswerListener listener;
    private BigVector postData;

    public HTTPConnManager(final int type, final String http, BigVector _postData, final int handler, final HTTPAnswerListener _listener) 
    {
        listener = _listener;
        postData = _postData;
        // Create a preference-ordered list of transports.
        int[] _intTransports = { TransportInfo.TRANSPORT_TCP_WIFI,
                                 TransportInfo.TRANSPORT_BIS_B, 
                                 TransportInfo.TRANSPORT_MDS,
                                 TransportInfo.TRANSPORT_WAP2,
                                 TransportInfo.TRANSPORT_TCP_CELLULAR };

        // Remove any transports that are not currently available.
        for(int i = 0; i < _intTransports.length; i++) 
        {
            int transport = _intTransports[i];
            if (!TransportInfo.isTransportTypeAvailable(transport) || !TransportInfo.hasSufficientCoverage(transport)) 
            {
                Arrays.removeAt(_intTransports, i);
            }
        }

        // Set ConnectionFactory options.
        if (_intTransports.length > 0) 
        {
            _factory.setPreferredTransportTypes(_intTransports);
        }

        _factory.setAttemptsLimit(5); 

        // Open a connection on a new thread.
        Thread t = new Thread(new Runnable() 
        {
            public void run() 
            {
                DebugStorage.getInstance().Log(0, "HTTPConnManager: open connection to " + http);
                ConnectionDescriptor cd = _factory.getConnection(http);
                if (cd != null) {
                    DebugStorage.getInstance().Log(0, "HTTPConnManager: connection created.");
                    Connection c = cd.getConnection();
                    new HTTPOut(type, postData, c, handler, listener);
                }
                else {
                    DebugStorage.getInstance().Log(0, "HTTPConnManager: UNABLE TO CREATE CONNECTION!");
                }
            }
        });
        t.start();
    }
}
