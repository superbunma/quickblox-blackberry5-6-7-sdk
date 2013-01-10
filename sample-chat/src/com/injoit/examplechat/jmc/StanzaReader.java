/**
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
package com.injoit.examplechat.jmc;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import javax.microedition.lcdui.StringItem;
import java.util.Vector;

import com.injoit.examplechat.xmlstreamparser.*;
import com.injoit.examplechat.util.Datas;
import com.injoit.examplechat.util.Util;
import com.injoit.examplechat.util.Base64;
import com.injoit.examplechat.util.ExceptionListener;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.JabberListener;
import com.injoit.examplechat.jabber.roster.Jid;
import com.injoit.examplechat.jabber.roster.Jud;
import com.injoit.examplechat.jabber.presence.*;
import com.injoit.examplechat.jabber.subscription.*;
import com.injoit.examplechat.jabber.conversation.Configuration.*;
import com.injoit.examplechat.util.Contents;
import com.twmacinta.util.MD5;
import com.injoit.examplechat.utils.*;

/**
 * Class for reading incoming stanzas
 */
public class StanzaReader 
{
    private ExceptionListener exceptionListener;
    private JabberListener jabberListener;
    
    
    // common attributes for stanzas
    protected String stanzaId;
    protected String stanzaType;
    protected String stanzaFrom;
    protected String stanzaTo;
    
    protected int internalstate;
    protected final int WAIT_LOGIN_PARAMS = 0;
    protected final int WAIT_LOGIN_RESULT = 1;
    protected final int WAIT_SESSION = 2;
    protected final int WAIT_ROSTER = 3;
    protected final int CONNECTION_COMPLETED = 4;
    protected final int REGISTRATION = 5;
        
    public StanzaReader(ExceptionListener _exceptionListener, JabberListener _jabberListener, int state)
    {
            exceptionListener = _exceptionListener;
            jabberListener = _jabberListener;
            internalstate = state;//registration(4) or login (0) 
    }
    
    
    /**
    * Read the Node objet in argument
    *@param Node
    */     
    public void read(Node _node) 
    {
        // common attributes for stanzas:
        stanzaId = _node.getValue("id");
        stanzaType = _node.getValue("type");
        stanzaFrom = _node.getValue("from");
        stanzaTo = _node.getValue("to");

        if (_node.name.equals("iq")) 
        {
                readIq(_node);
        } 
        else if (_node.name.equals("presence")) 
        {
                readPresence(_node);
        } 
        else if (_node.name.equals("message")) 
        {
                readMessage(_node);
        } 
        else if (_node.name.equals("stream:error")) 
        {
                // unrecoverable error 
                exceptionListener.reportException(new Exception("Stream Error " + _node.text));
        } 
        else if (_node.name.equals("stream:features")) 
        {
            boolean found = false;
            if (_node.getChild("mechanisms") != null) 
            { 
                try 
                {
                    Vector mec =  _node.getChild("mechanisms").getChildren();
                    for (int j=0; j<mec.size(); j++) 
                    {
                        if (((Node)mec.elementAt(j)).text.equals("PLAIN"))
                        {
                            found = true;
                            break;
                        }
                    }
                }
                catch(Exception e) 
                {
                }
            }
            
            if (found && !Datas.hostname.equals("gmail.com")) 
            {
                // PLAIN authorization 
                System.out.println("Using plain authorization");
                String resp = "\0" + Datas.jid.getUsername() + "\0" + Datas.getPassword();
                Datas.writerThread.write("<auth id='sasl2' xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\">" + MD5.toBase64(resp.getBytes()) + "</auth>");
                internalstate = WAIT_LOGIN_RESULT;
            }
            else 
            {
                //try with old auth 
                Datas.writerThread.write( "<iq id='s1' type='get'><query xmlns='jabber:iq:auth'>" + "<username>" + Util.escapeCDATA(Datas.jid.getUsername()) + "</username>" + "</query></iq>");
                internalstate = WAIT_LOGIN_PARAMS;
            }               
        }
        else if (_node.name.equals("success")) 
        {
            Datas.writerThread.write("<iq type=\"set\" id=\"bind3\">" + "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">" + "<resource>" + Datas.jid.getResource() + "</resource></bind></iq>");
            System.out.println("Binding resource");
            internalstate = WAIT_SESSION;
        }
        else if (_node.name.equals("failure")) 
        {
            //perhaps user not registered, try it!
            Datas.writerThread.write("<iq type='set' id='reg1'><query xmlns='jabber:iq:register'><username>" + Util.escapeCDATA(Datas.jid.getUsername()) + "</username><password>" + Datas.getPassword() + "</password><email>" + Datas.jid.getMail() + "</email></query></iq>");
            internalstate = REGISTRATION;
            return;
        }
    }
    
    /**
    * Reads an iq stanza and answers to the server 
    * Modified by Gabriele Bianchi 04/01/2006
    * Modified by Vladimir Slatvinskiy
    * @param _node
    */
        protected void readIq(Node _node) 
        {
            //System.out.println("+readIQ+");
                
            StringBuffer res = new StringBuffer(0);
            String pass = "";
                
            if (stanzaType.equals("error")) 
            {  //error response

                if (stanzaId.equals("discoitem1")) 
                {
                    Node error = _node.getChild("error");
                    showAlert("Discovery error: " + ((Node)error.children.elementAt(0)).name);
                }
                else if (stanzaId.equals("delete1")) 
                {
                    Node error = _node.getChild("error");
                    showAlert("Deleting error: " + ((Node)error.children.elementAt(0)).name);
                }
                else if (internalstate == WAIT_LOGIN_RESULT || internalstate == WAIT_LOGIN_PARAMS) 
                {    
                    //perhaps user not registered, try it!
                    Datas.writerThread.write("<iq type='set' id='reg1'><query xmlns='jabber:iq:register'><username>" + Util.escapeCDATA(Datas.jid.getUsername()) + "</username><password>" + Datas.getPassword() + "</password><email>" + Datas.jid.getMail() + "</email></query></iq>");
                    internalstate = REGISTRATION;
                    return;
                }
                else if (internalstate == REGISTRATION) 
                {
                        Node error = _node.getChild("error");
                        String exception = "";
                        if (error.children.size()>0) 
                        {
                            Node errorType = (Node)error.children.elementAt(0);
                            exception = errorType.name;
                        }
                        else exception = error.text;
                        
                        exceptionListener.reportRegistrationError(new Exception("Registration failed: "+exception), true);
                        return;
                }
                else if (stanzaId.equals("jud_reg"))
                {
                    //jud registration error
                        System.out.print("Error in Jud registration");
                        exceptionListener.reportRegistrationError(new Exception("Jud Registration failed"), false);
                        return;
                }
                else if (stanzaId.equals("regGateway"))
                {
                    //Gateway registration error
                        System.out.print("Error in Gateway registration");
                        exceptionListener.reportRegistrationError(new Exception("Gateway Registration failed"), false);
                        return;
                }
                else if (stanzaId.equals("getNum"))
                {
                        System.out.print("Error in getting phone number");
                        return;
                }
                else if (stanzaId.equals("vc1")) 
                {
                        System.out.print("Error in setting vcard");
                        
                        return;
                }
                else if (stanzaId.equals("vc2")) 
                {
                        System.out.print("Error in getting vcard");
                        
                        return;
                }
                else if (stanzaId.equals("bind3")) 
                {
                        System.out.print("Error in binding");
                        jabberListener.unauthorizedEvent("Cannot bind to the server");
                        return;
                }
                else if (stanzaId.equals("sess_1")) 
                {
                        System.out.print("Error in session");
                        jabberListener.unauthorizedEvent("Cannot open session with the server");
                        return;
                }
                else
                {
                        Node error = _node.getChild("error");
                        String code = Contents.errorCode;
                        if (error != null && error.getChildren() != null && !error.getChildren().isEmpty())
                                code = ((Node)error.getChildren().firstElement()).name + " (error code:" + error.getValue("code") + ")";
                        else if (error != null)
                                code = error.text;
                        jabberListener.notifyPresenceError(code);
                        return;
                }  
            }
            else if (stanzaType.equals("result")) 
            { //ok response
                if (stanzaId.equals("config1")) 
                {
                    Node query = _node.getChild("query");
                    if (query != null) 
                    { 
                        Node x = query.getChild("x");
                        if(x != null)
                        {
                            Configuration conf = new Configuration(x);
                            GroupChat chat = (GroupChat)Datas.multichat.get(stanzaFrom);
                            if(chat!=null)
                            {
                                chat.setConfiguration(conf);
                                this.jabberListener.RoomConfiguration();
                            }
                        }
                    }
                }
                else if (stanzaId.equals("config2")) 
                {
                    UiApplication.getUiApplication().invokeLater(new Runnable() 
                    {
                        public void run() 
                        {
                            Status.show("Room configured succesfully!");
                        }
                    });
                }
                else if (stanzaId.equals("create1")) 
                {
                    String nick = "";
                    Vector up = SavedData.getUserInfo();
                    if (up != null) 
                    {
                        try
                        {
                            nick = (String)up.elementAt(3);
                        }
                        catch(Exception ex)
                        {
                            //do nothing
                        }
                    }
                    else
                    {
                        nick = Datas.jid.getUsername();
                    }
                    ChatHelper.groupExistingChatJoin(Datas.jid.getUsername(), stanzaFrom.substring(0,stanzaFrom.indexOf("@")), stanzaFrom.substring(stanzaFrom.indexOf("@")+1));
                }
                else if (stanzaId.equals("discoitem1")) 
                {
                    Node query = _node.getChild("query");
                    Vector items = query.getChildren();
                    if (query != null && items != null && items.size() > 0) 
                    { //get items
                        for (int i = 0; i < items.size(); i++) 
                        {
                            Node item = (Node)items.elementAt(i);
                            if (item.name.equals("item")) 
                            {
                                if (item.getValue("name") != null)   
                                        Datas.server_services.addElement(new StringItem(item.getValue("name"), item.getValue("jid")));
                                else
                                        Datas.server_services.addElement(new StringItem("", item.getValue("jid")));
                            }
                        }
                    }
                }//rooms discovery
                else if (stanzaId.equals("discoRooms")) 
                {
                    Node query = _node.getChild("query");
                    Vector items = query.getChildren();
                    if (query != null && items != null && items.size() > 0) 
                    { 
                        //get items
                        Datas.rooms = new Vector(2);
                        for (int i = 0; i < items.size() /*&& i < 10*/; i++) 
                        {
                            //display max 12 rooms
                            Node item = (Node)items.elementAt(i);
                            if (item.name.equals("item")) 
                            {
                                Datas.rooms.addElement(item.getValue("jid"));
                            }
                        }
                    
                    }
                    this.jabberListener.DiscoverRooms();
                    RequestRoomsMembers();
                    return;
                }
                else if (stanzaId.equals("members1")) 
                {
                    Vector members = new Vector(2);
                    Node query = _node.getChild("query");
                    Vector items = query.getChildren();
                    if (query != null && items != null && items.size() > 0) 
                    { 
                        //get items
                        for (int i = 0; i < items.size() /*&& i < 10*/; i++) 
                        {
                            //display max 12 rooms
                            Node item = (Node)items.elementAt(i);
                            if (item.name.equals("item")) 
                            {
                                members.addElement(item.getValue("jid"));
                            }
                        }
                        
                    }
                    this.jabberListener.UpdateRoomMembersCount(stanzaFrom , members.size());
                    return;
                }
                else if (stanzaId.equals("reg1"))
                {
                    //TODO:forse va cambiato qui
                    res.append("<iq id='s1' type='get'><query xmlns='jabber:iq:auth' ><username>").append(Util.escapeCDATA(Datas.jid.getUsername())).append("</username></query></iq>");
                    internalstate = WAIT_LOGIN_PARAMS;
                } 
                else if (stanzaId.equals("getNum"))
                {
                    if (_node.getChild("query") != null)
                    {
                        Node n = (Node)_node.getChild("query").getChildren().firstElement();
                        if (n.getChildren().size() == 0)
                        {
                                return;
                        }
                        n = (Node)n.getChildren().firstElement();
                        String jid = n.getValue("user");
                        Jid j = (Jid)Datas.roster.get(Jid.getLittleJid(jid));
                        if (j == null)
                                return;
                        j.phone = n.text;
                    }
                    return;
                }
                else if (stanzaId.equals("setNum"))
                {
                        System.out.println("Phone number saved");
                        return;
                }
                else if (stanzaId.equals("roster_2"))
                {
                        System.out.println("Contact deleted");
                        this.jabberListener.updateScreen();
                        return;
                }
                else if (stanzaId.equals("roster_rename"))
                {
                        System.out.println("Contact renamed");
                        this.jabberListener.updateScreen();
                        return;
                }
                else if (stanzaId.equals("vc1")) 
                {
                        //AVATAR set        
                        return;
                }
                else if (stanzaId.equals("vc2")) 
                {
                    System.out.print("getting vcard");
                    Node vcard =_node.getChild("vCard");
                    Jid user  = (Jid)Datas.roster.get(Jid.getLittleJid(stanzaFrom));
                    if (user != null && vcard != null && vcard.getChild("PHOTO") != null)
                    {
                        Node binval = vcard.getChild("PHOTO").getChild("BINVAL");
                        try 
                        {
                            if (binval != null && binval.text != null) 
                            {
                                byte[] img = Base64.decode(binval.text);
                                if (img != null)
                                {
                                    user.setAvatar(img);
                                }
                            }
                        }
                        catch(Exception e) 
                        {
                            System.out.println("AVATAR error:"+e.getMessage());
                        }            
                    }
                    return;
                }
                else if (internalstate == WAIT_LOGIN_PARAMS)
                {
                    try
                    {
                        if (_node.getChild("query").getChild("digest") == null)
                        {
                                pass = "<password>" + Datas.getPassword() + "</password>";
                        }
                        else pass = "<digest>" + Datas.getDigestPassword() + "</digest>";
                        
                        String resourceNode = "";
                        if (_node.getChild("query").getChild("resource") != null)
                        {
                            resourceNode = "<resource>" + Util.escapeCDATA(Datas.jid.getResource()) + "</resource>";
                        } 
                        // else forget about the resource
    
                        res.append("<iq id='s2' type='set'><query xmlns='jabber:iq:auth'><username>");
    
                        res.append(Util.escapeCDATA(Datas.jid.getUsername())).append("</username>");
                        res.append(resourceNode).append(pass).append("</query></iq>");
                        internalstate = WAIT_LOGIN_RESULT;
                    }
                    catch(Exception ex)
                    {
                    }
                }
                else if (internalstate == WAIT_LOGIN_RESULT)
                {
                    //old auth
                    // we are connected
                    res.append("<iq id='s3' type='get'><query xmlns='jabber:iq:roster'/></iq>");
                    internalstate = WAIT_ROSTER;
                    res.append("<iq type='get' from='").append(Datas.jid.getFullJid()).append("' to='").append(Datas.hostname).append("' id='discoitem1'><query xmlns='http://jabber.org/protocol/disco#items'/></iq>");

                }
                else if (internalstate == WAIT_SESSION)
                {
                        res.append("<iq to=\""  + Datas.hostname
                                        + "\" type=\"set\" id=\"sess_1\">"
                                        + "<session xmlns=\"urn:ietf:params:xml:ns:xmpp-session\"/></iq>");
                        internalstate = WAIT_ROSTER;
                }
                else if (stanzaId.equals("sess_1")) 
                {
                    res.append("<iq id='s3' type='get'><query xmlns='jabber:iq:roster'/></iq>");
                    internalstate = WAIT_ROSTER;
                    res.append("<iq type='get' from='").append(Datas.jid.getFullJid()).append("' to='").append(Datas.hostname).append("' id='discoitem1'><query xmlns='http://jabber.org/protocol/disco#items'/></iq>");
                }
                else if (stanzaId.equals("s3"))
                {
                    internalstate = CONNECTION_COMPLETED;
                    //send AVATAR
                    byte[] img = Datas.jid.getAvatar();
                    Datas.jid.setPresence(Presence.getPresence("online"));
                    // sends the presence
                    if (img != null) 
                    {
                        try 
                        {
                                res.append(Presence.sendFirstVCard(img));
                                res.append(Presence.sendFirstPresence(img));
                        }
                        catch (Exception e) 
                        {
                            res.append("<presence/>");
                        }
                    }
                    else
                            res.append("<presence/>");
                    
                    //Read roster   
                    readRoster(_node);

                }
                else if (stanzaId.equals("jud_reg"))
                { 
                    //jud registration success
                    System.out.println("Success: jud registration");
                    //alert to midlet
                    jabberListener.notifyJudInfo(Contents.jud_success);
                    return;
                }
                else if (stanzaId.equals("jud_search"))
                { 
                    //jud search success
                    //alert to midlet
                    String info = Jud.getJidfromResponse(_node);
                    System.out.println("Success: jud search:" + info);
                    jabberListener.notifyJudInfo(Contents.jud_search + info);
                    return;
                }
                else if (stanzaId.equals("regGateway")) //gateway registration
                {
                    System.out.println("Success: gateway registration");
                }
                else if (stanzaId.equals("unregGateway")) //gateway registration
                {
                    System.out.println("Success: gateway unregistered");
                }
                else if (internalstate == CONNECTION_COMPLETED)
                {
                    
                }
           }
           else if (stanzaType.equals("set"))
           {
                if (_node.getChild("query") != null && _node.getChild("query").getValue("xmlns") != null)
                {
                    if (_node.getChild("query").getValue("xmlns").equals("jabber:iq:roster"))  readRoster(_node);
                }
           }
           
           if (res.length() > 0) 
           {
                Datas.writerThread.write(res.toString());       
           }
        }
        
        public void RequestRoomsMembers()
        {
            Runnable task = new Runnable() 
            {
                public void run() 
                {
                    for(int i=0; i<Datas.rooms.size(); i++)
                    {
                        String roomJid = (String) Datas.rooms.elementAt(i);
                        ChatHelper.RequestMembersCount(roomJid);
                    }
                }
            };
            new Thread(task).run();
        };
        
        /**
         * Reads a presence node and notify to midlet
         * Modified by Gabriele Bianchi 04/01/2006
         * Modified by Vladimir Slatvinskiy
         * @param _node
         */
        protected void readPresence(Node _node)
        {
            Node x;
            Node status;
            // default stanza type if not specified.
            if (stanzaType == null) 
            {

                if(_node.getChild("show") == null)
                {
                    stanzaType = "online";
                }
                else if (_node.getChild("show").text.equals("xa"))
                {
                    stanzaType = "away";
                }
                else
                {
                    stanzaType = _node.getChild("show").text;
                }
                
                //check if is a chat presence..
                if((x = _node.getChild("x", "xmlns", "http://jabber.org/protocol/muc#user")) != null)
                {
                        String role = "";
                        String affiliation = "";
                        stanzaType = "groupchat";
                        Vector children = x.getChildren();
                        Vector partners = new Vector(1);
                        for (int i = 0; i < children.size(); i++)
                        {
                            Node child = (Node)children.elementAt(i);
                            if (child.name.equals("item"))
                            {
                                try
                                {
                                    affiliation = new String(child.getValue("affiliation"));
                                    role = new String(child.getValue("role"));
                                }
                                catch(Exception ex)
                                {
                                    
                                }
                                
                                if (child.getValue("jid") == null)
                                {
                                    partners.addElement(stanzaFrom.substring(stanzaFrom.indexOf("/") + 1, stanzaFrom.length()));
                                    break;
                                }
                                String temp = new String(child.getValue("jid"));
                                partners.addElement(temp);
                            }
                        }
                        
                        String littleFrom = stanzaFrom;
                        String nick = Datas.jid.getLittleJid();//?
                        if (stanzaFrom.indexOf("/") != -1)
                        {
                                littleFrom = stanzaFrom.substring(0, stanzaFrom.indexOf("/"));
                                nick = stanzaFrom.substring(stanzaFrom.indexOf("/") + 1, stanzaFrom.length());
                        }
                        
                        if (Datas.multichat.get(littleFrom) != null)
                        {
                            //already exists
                            GroupChat update = (GroupChat)Datas.multichat.remove(littleFrom);
                            if (!update.jids.contains(partners.firstElement()))     update.jids.addElement(partners.firstElement());
                            
                            update.userRole = role;
                            update.userAffiliation = affiliation;
                            
                            Datas.multichat.put(littleFrom, update);

                           return;
                        }

                        //Conversation conversation = ChatHelper.createChat(partners, littleFrom, nick);
                        Conversation conversation = ChatHelper.createChat(partners, littleFrom, nick, role, affiliation);
                        conversation.isMulti = true;
                        Datas.conversations.addElement(conversation);
                        jabberListener.newConversationEvent(conversation);
                        return;
                }     
            }
            
            
            if (stanzaType.equals("error"))
            {
                // error! 
                Node error = _node.getChild("error");
                String code = Contents.errorCode;
                if (error != null  && error.getChildren() != null && !error.getChildren().isEmpty())
                {
                        code = ((Node)error.getChildren().firstElement()).name +" (error code:"+ error.getValue("code")+")";
                }
                else if (error != null)
                {
                        if (error.getValue("code") != null && error.getValue("code").equals("407"))
                        {
                            code = "Register to the gateway first";
                        }
                        else if (error.getValue("code") != null && error.getValue("code").startsWith("5"))
                        {
                            code = "Jabber Server Error: "+ error.text;
                        }
                        else
                                code = error.text;
                }
                jabberListener.notifyPresenceError(code);
                return;
            } 
            else if (stanzaType.equals("online") || stanzaType.equals("unavailable") || stanzaType.equals("away") || stanzaType.equals("dnd")) 
            {
                        
                if (stanzaFrom.indexOf("@") == -1) //not a user
                        return;
                //check if is my presence
                if (stanzaFrom.equals(Datas.jid.getFullJid()))
                        return; //skip

                //check if is group chat presence signal..                      
                if ((x = _node.getChild("x", "xmlns","http://jabber.org/protocol/muc#user")) != null) 
                {
                    Vector conversations = Datas.conversations;
            
                    // finds out the conversation
                    int i=0;
                    boolean found = false;
                    Conversation conversation = null;
                    while ((i<conversations.size()) && !found) 
                    {
                        conversation = (Conversation) conversations.elementAt(i);
                        found = conversation.match(_node);
                        i++;
                    }
                    
                    if (found) 
                    {
                        Node item = x.getChild("item");
                        String usr;
                        if (item == null || item.getValue("jid") == null && stanzaFrom.indexOf('/') != -1)
                        {
                                usr = stanzaFrom.substring(stanzaFrom.indexOf('/') + 1, stanzaFrom.length());
                        }
                        else
                                usr = item.getValue("jid");

                        conversation.appendToMe(new Message("", usr + " is " + stanzaType, Jid.getLittleJid(stanzaFrom)));
                        if (stanzaType.equals("unavailable"))
                                ChatHelper.deleteMember(stanzaFrom, usr);
                        else
                                ChatHelper.addMember(stanzaFrom, usr);
                        
                        if(stanzaType.equals("unavailable") &&  Datas.jid.getLittleJid().indexOf(usr)!=-1) 
                        {
                            jabberListener.newMessageEvent(null);
                        }
                        else
                        {       
                            jabberListener.newMessageEvent(conversation);
                        }
                        return;
                    }
                }//avatar management
                
                else if ((x = _node.getChild("x", "xmlns","vcard-temp:x:update")) != null) 
                {
                    try 
                    {
                        Node hash = x.getChild("photo"); //AVATAR
                        if (hash != null && hash.text != null && !hash.text.equals("")) 
                        {
                            Jid rost = (Jid)Datas.roster.get(Jid.getLittleJid(stanzaFrom));
                            if (rost != null)
                            {
                                if (rost.avatarHash == null) 
                                {
                                    //No avatar yet
                                    rost.avatarHash = hash.text;
                                    //ask for avatar
                                    Presence.getVCard(rost.getLittleJid());
                                }
                                else if (!rost.avatarHash.equals(hash.text))
                                {
                                    Presence.getVCard(rost.getLittleJid());//update avatar
                                }
                            }
                        }
                    }
                    catch (Exception e) 
                    {
                        System.out.println(e.getMessage());
                    }
                }

                Jid rost = new Jid(stanzaFrom);
                if (_node.getChild("status") != null)
                {
                    status = _node.getChild("status");
                    rost.setPresence(Presence.getPresence(stanzaType), status.text);
                }
                else
                {
                    rost.setPresence(Presence.getPresence(stanzaType));
                }

                jabberListener.notifyPresence(rost, stanzaType);
                        
            } 
            else if (stanzaType.equals("subscribe")) 
            {
                // roster wants to subscribe for my presence events
                Jid rost = new Jid(stanzaFrom);
                if (stanzaFrom.indexOf("@") == -1 || Datas.isGateway(rost.getServername())) //service subscription
                {
                        Subscribe.acceptSubscription(rost);
                        return;
                }
                jabberListener.notifyPresence(rost, stanzaType);     
            } 
            else if (stanzaType.equals("subscribed")) 
            {
                        // roster granted my "subscribe wish"
                        Jid rost = new Jid(stanzaFrom);
                        jabberListener.notifyPresence(rost, stanzaType);
            } 
            else if (stanzaType.equals("unsubscribe")) 
            {
                // roster wants to unsubscribe for my presence events
            } 
            else if (stanzaType.equals("unsubscribed")) 
            {
                // roster unsubscribed me for his presence events
                // or my "subscribe wish" was declined
                Jid rost = new Jid(stanzaFrom);
                jabberListener.notifyPresence(rost, stanzaType);
            } 
            else if (stanzaType.equals("probe")) 
            {
                // roster/server probes my presence
            }
        }
        
        /**
         * Reads a roster stanza, saves info in Datas and notify to midlet
         * @author Gabriele Bianchi 
         * Modified by Vladimir Slatvinskiy
         * @param _node
         */
        protected void readRoster(Node _node) 
        {
            System.out.println("+readRoster+");
            Node root = _node.getChild("query");
            Vector children = root.getChildren();
            for (int i=0; i<children.size();i++) 
            {
                Node child = (Node)children.elementAt(i); 
                
                Jid newjid = new Jid(child.getValue("jid"));
                
                //nickname
                if(child.getValue("name")!=null)
                {
                    newjid.setNick(child.getValue("name"));
                }
                else
                {
                    String jname = child.getValue("jid");
                    if(jname.indexOf("@")!=-1)
                    {
                        newjid.setNick(jname.substring(0, jname.indexOf("@")));
                    }
                    else
                    {
                        newjid.setNick(jname);
                    }
                }
                
                //check group
                if (child.getChild("group") != null)
                {
                    newjid.group = child.getChild("group").text;
                }
                
                if (newjid.getUsername() == newjid.getServername())
                        continue;
                        
                if (Datas.roster.get(newjid.getLittleJid()) != null ) 
                {
                            
                    if (child.getValue("subscription") != null && (child.getValue("subscription").equals("none") || child.getValue("subscription").equals("from")))
                    {
                        ((Jid)Datas.roster.get(newjid.getLittleJid())).setPresence(Presence.getPresence("unsubscribed"));
                    }
                    else if (child.getValue("subscription") != null && child.getValue("subscription").equals("remove"))
                    {
                        Datas.roster.remove(newjid.getLittleJid());
                        Vector conversations = Datas.conversations;
                        for (int k=0; k<conversations.size(); k++) 
                        {
                            Conversation c = (Conversation) conversations.elementAt(k);
                            if (c.name.equals(newjid.getUsername())) 
                            {
                                conversations.removeElementAt(k);
                                break;
                            }
                        }   
                    }
                    return;
                }
                else 
                {
                    if (child.getValue("subscription") != null && (child.getValue("subscription").equals("none") || child.getValue("subscription").equals("from")))
                    {
                        newjid.setPresence(Presence.getPresence("unsubscribed"));
                    }
                    Datas.registerRoster(newjid);
                    
                    jabberListener.updateScreen();
                }
            }
            
            if (!Datas.readRoster)
            {
                Datas.readRoster = true;
                jabberListener.notifyRoster();
            }
        }
        
        
        /**
         * Reads a message stanza and notify to midlet
         * Modified by Gabriele Bianchi 17/01/2006
         * Modified by Vladimir Slatvinskiy
         * @param _node
         */
        protected void readMessage(Node _node) 
        {
            Message message = new Message(_node); // takes the subject&body of the message
            String threadId = "";
            Conversation conversation = null; // conversation corresponding to the message
            Jid rosterFrom;
                  
            if (_node.getChild("thread") != null) 
            {
                    threadId = _node.getChild("thread").text;
            }
            else 
            {
                // _node has no "thread" child: server message?
                if (message.from.equalsIgnoreCase(Datas.hostname) ||  Datas.isGateway(message.from))
                {
                        System.out.println("server message");
                        conversation = new Conversation(message.from);
                        conversation.appendToMe(message);
                        jabberListener.newMessageEvent(conversation);
                        return;
                }  
            } 
                       
            //groupchat invitation management
            if ((stanzaType == null || stanzaType.equals("normal")) || _node.getChild("x", "xmlns", "jabber:x:conference") != null) 
            {
                String jidfrom = "";
                String room = "";
                String reason = "";
                //check if server uses new MUC protocol
                if (_node.getChild("x", "xmlns", "http://jabber.org/protocol/muc#user") != null) 
                {
                    Node invite = _node.getChild("x", "xmlns", "http://jabber.org/protocol/muc#user");
                    if (invite.getChild("invite") != null) {
                            jidfrom = invite.getChild("invite").getValue("from");
                            room = stanzaFrom;
                            
                            Node r = invite.getChild("invite").getChild("reason");
                            if(r!=null)
                            {
                                reason = r.text;
                                jabberListener.newInvitationEvent(jidfrom, room, reason);
                            }
                            else
                            {
                                jabberListener.newInvitationEvent(jidfrom, room);
                            }
                            return;
                    }
                }//check if server uses old protocol
                else if (_node.getChild("body") != null && _node.getChild("body").text.startsWith("You have been invited")) 
                {

                    Node invite = _node.getChild("x", "xmlns", "jabber:x:conference");
                    jidfrom = stanzaFrom;
                    room = invite.getValue("jid");
                    jabberListener.newInvitationEvent(jidfrom, room);
                    return;
                }                       
            }
            
            //Composing management
            else if (stanzaType.equals("chat") && _node.getChild("composing", "xmlns", "http://jabber.org/protocol/chatstates") != null) 
            {
                Vector conversations = Datas.conversations;
                Conversation convers = null;
                // finds out the conversation, if already exists
                
                for(int i=0; i<conversations.size(); i++) 
                {
                    convers = (Conversation) conversations.elementAt(i);
                    if (convers.match(_node)) 
                    {
                        convers.composing = Jid.getUsername(convers.name) + Contents.composing;
                        jabberListener.newComposingEvent(convers);
                        break;
                    }
                }       
                return;
            }
            else if (stanzaType.equals("chat") && _node.getChild("inactive", "xmlns", "http://jabber.org/protocol/chatstates") != null) 
            {
                Vector conversations = Datas.conversations;
                Conversation convers = null;
                // finds out the conversation, if already exists
                
                for(int i=0; i<conversations.size(); i++) 
                {
                    convers = (Conversation) conversations.elementAt(i);
                    if (convers.match(_node)) 
                    {
                        convers.composing = Jid.getUsername(convers.name) + Contents.inactive;
                        jabberListener.newComposingEvent(convers);
                        break;
                    }
                }
                return;
            }
            else if (stanzaType.equals("chat") && _node.getChild("x", "xmlns", "jabber:x:event") != null &&  _node.getChild("body") == null) 
            {
                Node x = _node.getChild("x", "xmlns", "jabber:x:event");
                if (x.getChild("composing") != null) 
                {
                    Vector conversations = Datas.conversations;
                    Conversation convers = null;
            
                    for(int i=0; i<conversations.size(); i++) 
                    {
                        convers = (Conversation) conversations.elementAt(i);
                        if (convers.match(_node)) 
                        {
                            convers.composing = Jid.getUsername(convers.name) + Contents.composing;
                            jabberListener.newComposingEvent(convers);
                            break;
                        }
                            
                    }
                    return;
                }
            }
                
            Vector conversations = Datas.conversations;
            
            // finds out the conversation, if already exists
            int i=0;
            boolean found=false;
            while ((i<conversations.size()) && !found) 
            {
                conversation = (Conversation) conversations.elementAt(i);
                found = conversation.match(_node);
                i++;
            }
            
            // default stanza type if not specified.
            if(stanzaType == null) 
            {
                stanzaType = "normal";
            }
            
            if (found == false) 
            { 
                // no conversation with this roster is running

                if (stanzaType.equals("error")) 
                {
                        message.addError(_node.getChild("error").text);
                        jabberListener.notifyError(null, message);
                }
                else if (stanzaType.equals("normal")  || stanzaType.equals("chat") ) 
                {
                    rosterFrom = (Jid) Datas.roster.get(Jid.getLittleJid(stanzaFrom));

                    if (rosterFrom == null) 
                    {
                            // the roster is not known
                            Jid newjid = new Jid(stanzaFrom, "online");
                            rosterFrom = newjid;
                            Datas.registerRoster(newjid);    
                    }
                    // normal: default message type. reply expected. no history.
                    // chat: peer to peer communication. with history.

                    SingleChat chat = new SingleChat(rosterFrom, stanzaType, threadId);

                    chat.appendToMe(message);
                    // registers this new conversation
                    conversations.addElement(chat);

                    jabberListener.newConversationEvent(chat);
                }
                else if (stanzaType.equals("groupchat")) 
                {
                        System.out.println("My message");
                        return;
                }
                else if (stanzaType.equals("headline")) 
                {
                    rosterFrom = (Jid) Datas.roster.get(stanzaFrom);
                    if (rosterFrom == null) 
                    {
                        // the roster is not known
                        Jid newjid = new Jid(stanzaFrom);
                        rosterFrom = newjid;
                        if (stanzaFrom.indexOf("@") != -1) //is user
                        { 
                            Datas.registerRoster(newjid);      
                        }
                    }
                    // no reply expected. (e.g. news, ads...)
                    conversation = new Conversation(rosterFrom.getUsername()); 
                    conversation.appendToMe(message);
                    // registers this new conversation
                    
                    jabberListener.newMessageEvent(conversation);
                }
                    
            }
            else
            {
                // conversation already exists
                if (stanzaType.equals("error")) 
                {
                    // should find the message in the conversation, append the error to it and
                    // display the error msg ***
                    message.addError(_node.getChild("error").text);
                    jabberListener.notifyError(conversation, message);
                } 
                else 
                {
                    if (message.body.equals("") && message.subject.equals(""))
                    return; //taglia i messaggi vuoti, come ad esempio gli eventi MSN ("composing..")

                    conversation.appendToMe(message);
                    conversation.composing = "";
                    jabberListener.newMessageEvent(conversation);
                }
            }
        }

        public void setRosterState() 
        {
            internalstate = WAIT_ROSTER;
        }
        
        public void showAlert(final String text)
        {
            UiApplication.getUiApplication().invokeLater(new Runnable() 
            {
                public void run() 
                {
                    Status.show(text);
                }
            });
        }
}
