/**
 * Class responsible for communication with the Jabber server.
 *
 *
 * MicroJabber, jabber for light java devices. Copyright (C) 2004, Gregoire Athanase
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with 
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, 
 * Suite 330, Boston, MA 02111-1307 USA.
 */
package com.injoit.examplechat.jmc.connection;

import com.injoit.examplechat.util.*;
import com.injoit.examplechat.utils.*;
import com.injoit.examplechat.xmlstreamparser.*;
import com.injoit.examplechat.jmc.*;
import javax.microedition.io.*;
import java.io.*;
import com.injoit.examplechat.threads.*;

/**
 * Connector Manager class
 * Modified by Gabriele Bianchi 04/01/2006
 * Modified by Vladimir Slatvinskiy
 */
public class CommunicationManager implements ParserListener, ExceptionListener/*, ConnectionListener */ 
{
        private boolean keepAlive = true;
        // fields
        ChatEventListener midlet;
        ScreenManager display;
        
        // connections & streams: save them for closing in "disconnect"
        StreamConnection inConn;  
        
        InputStream is;

        OutputStream os;

        meConnector cinit; 
        CharacterReaderThread readerThread;
        IWriterThread writerThread;
        Thread heartbeat;
        StanzaReader stanzaReader; // send him the stanzas
        int type_of_connection;

        
        public CommunicationManager( ChatEventListener _midlet) 
        {
                midlet = _midlet;
        }
        
        public void setHandler(ScreenManager _man)
        {
            display = _man;
        }
        
        /**
         * Makes connection
         * Modified by Gabriele Bianchi 04/01/2006  
         * Modified by Vladimir Slatvinskiy    
         */
        public void connect(int state) 
        {
                // *** should ensure it is disconnected...
                cinit = new meConnector(Datas.server_name, Datas.port, this);
                type_of_connection = state;
                
                cinit.start();
        }
        
        public void httpConnect() 
        {
                //Parser parser = new Parser(this, this);
                stanzaReader = new StanzaReader(this, midlet, type_of_connection);
                writerThread = new HttpBindThread(stanzaReader, midlet);

                Datas.writerThread = writerThread;
        }
        
        /**
         * Notifies connection to application
         * Modified by Gabriele Bianchi 05/01/2006
         * Modified by Vladimir Slatvinskiy
         */
        public void notifyConnect(StreamConnection _inConn, InputStream _is, OutputStream _os) 
        {
            keepAlive = true;
            runHeartBeatThread();
            
            inConn = _inConn;
            //outConn = _inConn;
            is = _is;
            os = _os;
            System.out.println("Connessione effettuata");
            try {
                    
                    
                    Parser parser = new Parser(this, this);
                    
                    readerThread = new CharacterReaderThread(is, parser, this);
                            
                    
                    writerThread = new WriterThread(os, this);
                    writerThread.start();
                    Datas.writerThread = writerThread; // Datas is already initialized

                    // stream start
                    writerThread.write("<?xml version='1.0'?>" +
                                    "<stream:stream to=" + "'" + Datas.hostname + "'" +
                                    " xmlns='jabber:client'" +
                    " xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>");          
                    stanzaReader = new StanzaReader(this, midlet, type_of_connection);
                    readerThread.start();
    
            }
            catch (Exception ex) 
            {
                DebugStorage.getInstance().Log(0, "<CommunicationManager> Stream Init Error : ", ex);
            }
        }
        
        public void runHeartBeatThread()
        {
            heartbeat = new Thread() 
            {
                public void run() 
                {
                    try
                    {
                        while(keepAlive == true)
                        {
                            blank();
                            Thread.sleep(30000);
                        }
                    }
                    catch(Exception ex)
                    {
                        keepAlive = false;
                        DebugStorage.getInstance().Log(0, "<CommunicationManager> HeartBeathread error: ", ex);
                    }
                }
            };
            heartbeat.start();
        }
        
        public void terminateStream() 
        {
                writerThread.write("</stream:stream>");
                disconnect();
                midlet.disconnectedEvent();
                keepAlive = false;
        }
        
        /**
         * Disconnect
         */
        public void disconnect()
        {
            try 
            {
                keepAlive = false;
                // unlock the WriterThread and finish it
                
                writerThread.terminate();
                // closes the streams
                if (is != null)
                {
                        is.close();
                        os.close();
                }
                
                // closes the connection
                if (inConn != null)  inConn.close();
    
            } 
            catch (IOException e)
            {
            }
        }
        
        public void prologEnd(Node _node) 
        {
                System.out.println("<< PROLOG: " + _node.toString());
        }
        
        /**
         * @param Node
         */
        public void nodeStart(Node _node) 
        {
                if (_node.parent == null) {
                        // document entity (stream) starts...
                        if (_node.name.equals("stream:stream")) {
                                Datas.setSessionId(_node.getValue("id"));
                                // IGNORED attributes: xmlns, xmlns:stream, from
                                if (_node.getValue("version") == null) {
                                        //NO SASL
                                        writerThread.write(
                                                        "<iq id='s1' type='get'><query xmlns='jabber:iq:auth'>" +
                                                        "<username>" + Util.escapeCDATA(Datas.jid.getUsername()) + "</username>" +
                                                "</query></iq>");                                       
                                }
                                 midlet.connectedEvent();
                        }
                        else {
                                disconnect();
                                midlet.disconnectedEvent();
                        }
                }
        }
        /**
         * @param Node
         */
        public void nodeEnd(Node _node) {
                if (_node.parent == null) 
                {
                        // end of stream
                        System.out.println("<< DOCUMENT ENTITY end: " + _node.toString());
                        if (_node.name.equals("stream:stream")) 
                        {
                                disconnect();
                                midlet.disconnectedEvent();
                        }
                        else 
                        {
                        }
                }
                else if (_node.parent.parent == null) 
                {
                        // it's a stanza
                        System.out.println("<< stanza: " + _node.toString());                       
                        stanzaReader.read(_node);
                }
                
        }
        /**
         * Sends out a keep alive space character.
         */
        public void blank() 
        {
            try
            {
                System.out.println("----------------blank--------------------");
                
                if(stanzaReader == null)
                {
                    try
                    {
                        stanzaReader = new StanzaReader(this, midlet, type_of_connection);
                    }
                    catch(Exception ex)
                    {
                        DebugStorage.getInstance().Log(0, "<CommunicationManager> blank issue with stanzaReader: ", ex);
                    }
                }
                
                if(writerThread == null)
                {
                    try
                    {
                        writerThread = new HttpBindThread(stanzaReader, midlet);
                        Datas.writerThread = writerThread;
                    }
                    catch(Exception ex)
                    {
                        DebugStorage.getInstance().Log(0, "<CommunicationManager> blank issue with writerThread: ", ex);
                    }
                }
                
                writerThread.write(" ");
            }
            catch(Exception ex)
            {
                 DebugStorage.getInstance().Log(0, "<CommunicationManager> blank error: ", ex);
            }
        }
        
        
        /**
         * Exceptions handling
         * @param Exception
         */
        public void reportException(Thread _t, Exception _e) 
        {
        }
        /**
         * Exceptions handling
         * Modified by Gabriele Bianchi 05/01/2006
         * Modified by Vladimir Slatvinskiy
         * @param Exception
         */
        public void reportException(Exception _e) 
        {
            terminateStream();
            midlet.unauthorizedEvent("Error: " + _e.getMessage());
        }
        /**
         * @param Exception
         * @author Gabriele Bianchi 
         */
        public void reportRegistrationError(Exception _e, boolean disconnect) 
        {
            DebugStorage.getInstance().Log(0, "<CommunicationManager> reportRegistrationError() ", _e);
            if (disconnect)
            {
                terminateStream();
            }
            midlet.registrationFailure(_e, disconnect);    
        }
        
        public void notifyNoConnectionOn(String reason)
        {
                midlet.unauthorizedEvent(reason);
        }
}
