package com.injoit.examplechat.networking;

import com.injoit.examplechat.utils.*;
import net.rim.device.api.system.*;
import java.io.*;
import javax.microedition.io.*;
import java.util.*;
import net.rim.device.api.collection.util.*;
import net.rim.blackberry.api.browser.*;

public class HTTPOut
{
    private HTTPAnswerListener listener;
    private int handler;
    private int type;
    private BigVector postData;
    
    public HTTPOut(int _type, BigVector _postData, Connection conn, final int _handler, final HTTPAnswerListener _listener)
    {
        listener = _listener;
        handler = _handler;
        postData = _postData;
        ContentReaderThread t = new ContentReaderThread(_type, postData, conn, handler, _listener);
        t.start();
    }

    private final class ContentReaderThread extends Thread 
    {
        private Connection _connection;
        private HTTPAnswerListener listener;
        private int handler;
        private int type;
        private BigVector postData;
        private final int GET  = 0;
        private final int POST = 1;
        
        private final int REQUEST_SESSION = 0; // registration: get token
        private final int REQUEST_USERS   = 1; // registration: sent credentials 
        private final int REQUEST_LOGIN   = 2; // registration: login
        
        ContentReaderThread(final int _type, final BigVector _postData, Connection conn, final int _handler, final HTTPAnswerListener _listener) 
        {
            _connection = conn;
            listener = _listener;
            handler = _handler;
            type = _type;
            postData = _postData;
        }

        public void run() 
        {
            long start = System.currentTimeMillis();
            String result = "";
            OutputStream os = null;
            InputStream is = null;

            try 
            {
                //Send HTTP GET to the server.
                HttpConnection httpConnection = (HttpConnection) _connection;
                
                byte[] outData = new byte[0];
                switch(type)
                {
                    case GET:
                    {
                        DebugStorage.getInstance().Log(0, "HTTPOut: request method GET");
                        String command = "GET " + "/" + " HTTP/1.0\r\n\r\n";
                        outData = command.getBytes();
                        break;
                    }
                    case POST:
                    {
                        DebugStorage.getInstance().Log(0, "HTTPOut: request method POST");
                        switch(handler)
                        {
                            case REQUEST_SESSION:
                            {
                                httpConnection.setRequestMethod(HttpConnection.POST);
                                 
                                String data = (String) postData.elementAt(0);
                                outData = data.getBytes();
                                
                                httpConnection.setRequestProperty("Content-Type", "application/json");
                                httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                                
                                DebugStorage.getInstance().Log(0, "HTTPOut: type REQUEST_SESSION");
                                break;
                            }
                            case REQUEST_USERS:
                            {
                                httpConnection.setRequestMethod(HttpConnection.POST);
                                 
                                String data = (String) postData.elementAt(1);
                                outData = data.getBytes();

                                httpConnection.setRequestProperty("Content-Type", "application/json");
                                httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                                httpConnection.setRequestProperty("QB-Token", (String) postData.elementAt(0));
                                
                                DebugStorage.getInstance().Log(0, "HTTPOut: type REQUEST_USERS");
                                break;
                            }
                            case REQUEST_LOGIN:
                            {
                                httpConnection.setRequestMethod(HttpConnection.POST);                           
                                String data = (String) postData.elementAt(1);
                                outData = data.getBytes();                                
                                httpConnection.setRequestProperty("Content-Type", "application/json");
                                httpConnection.setRequestProperty("QuickBlox-REST-API-Version", "0.1.0");
                                httpConnection.setRequestProperty("QB-Token", (String) postData.elementAt(0));
                                
                                DebugStorage.getInstance().Log(0, "HTTPOut: type REQUEST_LOGIN");
                                break;
                            }
                            default: break;
                        }
                        
                        break;
                    }
                    default: break;
                }
                
                DebugStorage.getInstance().Log(0, "HTTPOut: data: " + outData );
                
                os = httpConnection.openOutputStream();
                os.write(outData);
                os.flush();
                
                //HttpConnection httpConnection = (HttpConnection) _connection;
                int state= httpConnection.getResponseCode();
                DebugStorage.getInstance().Log(0, "HTTPOut: response code =  " + state );
                
                if(state == HttpsConnection.HTTP_OK || state == HttpsConnection.HTTP_CREATED || state == HttpsConnection.HTTP_ACCEPTED)
                {
                    // Get InputConnection and read the server's response.
                    InputConnection inputConn = (InputConnection) _connection;
                    is = inputConn.openInputStream();
                    
                    byte[] data = net.rim.device.api.io.IOUtilities.streamToBytes(is);
                    result = new String(data);
                    
                    DebugStorage.getInstance().Log(0, "HTTPOut: answer =  " + result );
                    
                    listener.ProcessAnswer(handler, result);
                } 
                else if( state == HttpConnection.HTTP_TEMP_REDIRECT
                        || state == HttpConnection.HTTP_MOVED_TEMP
                        || state == HttpConnection.HTTP_MOVED_PERM ) 
                {
                    String location = httpConnection.getHeaderField("location").trim();
                    new HTTPConnManager(type, location, postData, handler, listener);
                }
                else
                {
                    final String message = httpConnection.getResponseMessage();
                    
                    DebugStorage.getInstance().Log(0, "HTTPOut: error answer =  " + message );
                    
                    listener.ProcessError(handler, message);
                }
            } 
            catch (Exception ex) 
            {
                listener.ProcessError(handler, ex.getMessage());
            } 
            finally 
            {
                // Close OutputStream
                if (os != null) 
                {
                    try 
                    {
                        os.close();
                    }
                    catch (IOException e) 
                    {
                        
                    }
                }

                // Close InputStream.
                if (is != null) 
                {
                    try 
                    {
                        is.close();
                    }
                    catch (IOException e) {
                        
                    }
                }

                // Close Connection.
                try 
                {
                    _connection.close();
                } 
                catch (IOException ioe)
                {
                }
            }
        }
    }
}
