package com.injoit.examplenews.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.io.transport.options.TcpCellularOptions;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.io.transport.options.*;

public class HttpClient {
        public String getContent(String url) 
        {
            try 
            {
                HttpConnection connection = (HttpConnection) Connector.open(url);

                int status = connection.getResponseCode();
                if (status == 200) 
                {
                    InputStream input = connection.openInputStream();
                    byte[] data = new byte[256];
                    int length = 0;
                    StringBuffer raw = new StringBuffer();

                    while (-1 != (length = input.read(data))) 
                    {
                        raw.append(new String(data, 0, length));
                    }
                    return raw.toString();
                } 
                else 
                {
                    DialogHelper.alert("Status:" + status);
                }
            } 
            catch (IOException e) 
            {
                DialogHelper.alert(e.toString());
            }
            return null;
        }
        
        public byte[] getBytes(String url) 
        {
            InputConnection connection = null;
            try 
            {
                connection = openConnection(url);
                return IOUtilities.streamToBytes(connection.openInputStream());
            } 
            catch (Exception e) 
            {
                    return null;
            } 
            finally
            {
                if (connection != null) 
                {
                    try 
                    {
                        connection.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
        

        public InputConnection openConnection(String url) throws IOException 
        {
                ConnectionFactory factory = new ConnectionFactory();
                // Create a preference-ordered list of transports.
                int[] _intTransports = { TransportInfo.TRANSPORT_TCP_WIFI,
                                         TransportInfo.TRANSPORT_BIS_B, 
                                         TransportInfo.TRANSPORT_MDS,
                                         TransportInfo.TRANSPORT_WAP2,
                                         TransportInfo.TRANSPORT_TCP_CELLULAR };
                                         
                /*int[] _intTransports = { 
                TransportInfo.TRANSPORT_TCP_WIFI,
                TransportInfo.TRANSPORT_WAP2,
                TransportInfo.TRANSPORT_TCP_CELLULAR };*/

                // Remove any transports that are not currently available.
                for (int i = 0; i < _intTransports.length; i++) 
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
                    factory.setPreferredTransportTypes(_intTransports);
                }
                
                //BisBOptions bisOptions = new BisBOptions("mds-public");
                //factory.setTransportTypeOptions(TransportInfo.TRANSPORT_BIS_B, bisOptions);
                
                //factory.setTransportTypeOptions(TransportInfo.TRANSPORT_TCP_CELLULAR, new TcpCellularOptions());
                factory.setAttemptsLimit(5);
                
                ConnectionDescriptor cd = factory.getConnection(url);
                // If connection was successful, fetch and show the content from
                // the web server
                if (cd != null) 
                {
                    Connection connection = cd.getConnection();
                    OutputConnection outputConn = (OutputConnection) connection;
                    OutputStream os = outputConn.openOutputStream();
                    String getCommand = "GET " + "/" + " HTTP/1.0\r\n\r\n";
                    os.write(getCommand.getBytes());
                    os.flush();

                    // Get InputConnection and read the server's response.
                    InputConnection inputConn = (InputConnection) connection;
                    return inputConn;
                }
                throw new UnableOpenConnectionException();
        }
        
        public class UnableOpenConnectionException extends RuntimeException {
                
        }
        
}
