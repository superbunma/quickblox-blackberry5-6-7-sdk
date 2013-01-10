/*
  Copyright (c) 2000, Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.

  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.injoit.examplechat.jmc.connection;

import com.injoit.examplechat.util.Datas;
import com.injoit.examplechat.utils.*;
import javax.microedition.io.*;
import java.io.*;
import net.rim.blackberry.api.browser.*;
import net.rim.device.api.io.*;
import net.rim.device.api.io.transport.*;
import net.rim.device.api.util.Arrays;

public class meConnector extends Thread{
  /**
   * The connection to the jabber server
   */
  private StreamConnection connection = null;
  private CommunicationManager _cm = null;
  private int _port;
  private String _hostname;
  
  static boolean isTransportManaged = false; 
  static int[] availableTransportTypes = {  TransportInfo.TRANSPORT_TCP_WIFI,
                                            TransportInfo.TRANSPORT_BIS_B, 
                                            TransportInfo.TRANSPORT_MDS,
                                            TransportInfo.TRANSPORT_WAP2,
                                            TransportInfo.TRANSPORT_TCP_CELLULAR };
  /**
   * Constructor
   * @author Gabriele Bianchi 
   * Modified  04/01/2006
   * @param hostname The host to connect to
   * @param port The port to connect to
   */

  public meConnector( String hostname, int port, CommunicationManager cm ) 
  {
    _hostname = hostname;
    _cm = cm;
    _port = port;
  }
  
   public static StreamConnection getConnectionForRequest(String url) 
   {
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        
        // Remove any transports that are not currently available.
        if(isTransportManaged == false)
        {
            for (int i = 0; i < availableTransportTypes.length; i++) 
            {
                int transport = availableTransportTypes[i];
                if (!TransportInfo.isTransportTypeAvailable(transport) || !TransportInfo.hasSufficientCoverage(transport)) 
                {
                    Arrays.removeAt(availableTransportTypes, i);
                }
            }
            isTransportManaged = true;
        }
                
        connectionFactory.setPreferredTransportTypes(availableTransportTypes);
        
        final ConnectionDescriptor connectionDescriptor = connectionFactory.getConnection(url);
        StreamConnection connection = null;
        if (connectionDescriptor != null) 
        {
            // connection suceeded
            int transportUsed = connectionDescriptor.getTransportDescriptor().getTransportType();
            System.out.println("Transport type used: " + Integer.toString(transportUsed));
            connection = (StreamConnection) connectionDescriptor.getConnection();
        }
        return connection;
    }
  
  /**
   * Modified by Gabriele Bianchi 04/01/2006
   */
  public void run() 
  {
    StringBuffer connectorStringBuffer;
    if (Datas.isSSL)
    {
        connectorStringBuffer = new StringBuffer( "ssl://");
    }
    else
    {
        connectorStringBuffer = new StringBuffer( "socket://");
    }
    connectorStringBuffer.append( _hostname );
    connectorStringBuffer.append( ":" );
    connectorStringBuffer.append( _port );
    connectorStringBuffer.append( "" );

    String connectorString = connectorStringBuffer.toString().trim();
    System.out.println(connectorString);
   try 
   {
        if (Datas.isSSL)
        {
            connection = (SecureConnection) Connector.open( connectorString );
        }
        else
        {
            connection = (StreamConnection) /*Connector.open*/getConnectionForRequest( connectorString );
        }
        if(connection!=null)
        {
            SocketConnection sc = (SocketConnection) connection;
            sc.setSocketOption(SocketConnection.KEEPALIVE, 2);
        }
   
        _cm.notifyConnect(connection, this.openInputStream(), this.openOutputStream());
    }
    catch (Exception e)
    {
        e.printStackTrace();
        System.out.println("Connessione non riuscita:"+ e.getMessage());
        _cm.notifyNoConnectionOn("I can't connect, server is unreachable");
        DebugStorage.getInstance().Log(0, "Can't connect, server is unreachable", e);
    }
    
    return;
  }

  /**
   * Method to return the input stream of the connection
   *
   * @return InputStream
   */

  public InputStream openInputStream() throws IOException
  {
    return connection.openInputStream();
  }

  /**
   * Method to return the output stream of the connection
   *
   * @return OutputStream
   */

  public OutputStream openOutputStream()  throws IOException
  {
    return connection.openOutputStream();
  }
  
  /** 
   *Method to return  the connection
   *@return StreamConnection
   */
  
 
  public StreamConnection getConnection () {
    return connection;
  }
}
