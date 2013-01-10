/**
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package com.injoit.examplechat.jmc;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.container.*;
import javax.microedition.io.PushRegistry;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.microedition.lcdui.StringItem;

import com.injoit.examplechat.jmc.connection.CommunicationManager;
import com.injoit.examplechat.jmc.media.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.screens.*;
import com.injoit.examplechat.threads.SMSThread;
import com.injoit.examplechat.threads.PushThread;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.Jid;
import com.injoit.examplechat.jabber.roster.Jud;
import com.injoit.examplechat.jabber.presence.Presence;
import com.injoit.examplechat.util.Datas;
import com.injoit.examplechat.util.Contents;
import com.injoit.examplechat.jabber.subscription.*;

public class ChatManager 
{
        // fields
        public CommunicationManager cm;
        private ChatEventListener listener;
        private Font font;
        public ScreenManager display;
        
        // to remember the *displayed* state
        public int internal_state;
        public boolean history = false;
        public final static int OFFLINE         = 0;
        public final static int ONLINE          = 1;
        public final static int ROSTER          = 2;
        public final static int CONVERSATION    = 3;
        public final static int SUBSCRIPTION    = 4;    
        public final static int PARAMS          = 5;
        public final static int WAIT_CONNECT    = 6;
        public final static int WAIT_DISCONNECT = 7;
        public final static int ROSTER_DETAILS  = 8;
        public final static int MULTI_CHAT      = 9;
        public final static int INVITATION      = 10;
        public final static int OPTIONS         = 11;
        public final static int JUD             = 12;
        
        // to remember which roster/conversation is displayed
        public Conversation currentConversation;
        public Jid currentjid;
        
        public Enumeration contacts = null;

        public Manager conversationForm = null;
        
        public Hashtable infopool; // contains highly dynamic data. i.e: TextFields in forms...
        public Vector roster;
        
        
        //SCREENS
        public ConversationScreen conversationScreen;
        public OfflineScreen offlineScreen;
        public WaitScreen waitScreen;
        public SettingsScreen settingsScreen;
        public OnlineScreen onlineScreen;
        public InviteScreen inviteScreen;
        public MultiChatScreen multichatScreen;
        
        private ChatManager me;
        
        
        public  ChatManager() 
        {
            me = this;
            infopool = new Hashtable(5);
            infopool.put("hide", Contents.hide[1]);
            font = Font.getDefault();

            listener = new ChatEventListener(this);
            cm = new CommunicationManager(listener);
        }
        
        public void StartChat()
        {
            if (display == null) 
            { 
                display = new ScreenManager();
                cm.setHandler(display);
                getGuiOfflineMenu(); 
                Datas.load();
                listener.display = display;       
            }
            else 
            {
                if (internal_state != OFFLINE && internal_state != WAIT_CONNECT && Datas.jid != null)
                Datas.jid.setPresence(Presence.getPresence("online")); //change user status
            }
        }
        
        public void pauseChat() 
        {
            if (internal_state != OFFLINE && internal_state != WAIT_CONNECT && Datas.jid != null)
            Datas.jid.setPresence(Presence.getPresence("away")); //change user status
        }
        
        public void destroyChat(boolean unconditional)
        {
        }
        

        public void getGuiOfflineMenu()
        {
  
            int i = UiApplication.getUiApplication().getScreenCount();

            for (int k=0; k<i; k++) 
            {
                synchronized (Application.getEventLock()) 
                {
                    UiApplication.getUiApplication().popScreen();
                }
            }
            
            
            UiApplication.getUiApplication().invokeLater(new Runnable() 
            {
                public void run() 
                {
                    offlineScreen = new OfflineScreen(me);
                    UiApplication.getUiApplication().pushScreen(offlineScreen);
                }
            });   
        }
        
        /**
         * Wait for connecting
         * @return Displayable
         */
        public void getGuiWaitConnect() 
        {
            if(waitScreen == null)
            {
                waitScreen = new WaitScreen("Connecting...", this);
                UiApplication.getUiApplication().pushScreen(waitScreen);
            }
        }
        
        /**
         * Show the form to insert user jid information
         * @return Displayable
         */
        public void getGuiParams() 
        {
            UiApplication.getUiApplication().invokeLater(new Runnable() 
            {
                public void run() 
                {
                    if(settingsScreen == null)
                    {
                        settingsScreen = new SettingsScreen(me);
                        UiApplication.getUiApplication().pushScreen(settingsScreen);
                    }
                    else
                    {
                        UiApplication.getUiApplication().pushScreen(settingsScreen);
                    }
                }
            }); 
        }
        
        /**
         * Show the main menu (online)
         * @return Displayable
         */
        public void getGuiOnlineMenu() 
        {
            if(onlineScreen == null)
            {
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        onlineScreen = new OnlineScreen(me);
                        UiApplication.getUiApplication().pushScreen(onlineScreen);
                    }
                });
            }
            else
            {
                onlineScreen.updateScreen();
            }
            
            if(multichatScreen != null)
            {
                multichatScreen.updateScreen();
            }
            
            if(waitScreen !=null)
            {
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        waitScreen.close();
                        waitScreen = null;
                    }
                });
            }
        }
        
        public void getGuiConversation() 
        {
            if(conversationScreen==null)
            {
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        conversationScreen = new ConversationScreen(me);
                        UiApplication.getUiApplication().pushScreen(conversationScreen);
                    }
                });
            }
            else
            {
                conversationScreen.updateScreen();
            }
        }
        
        public void getGuiConversationWithoutMessages() 
        {
            if(conversationScreen==null)
            {
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        conversationScreen = new ConversationScreen(me, false);
                        UiApplication.getUiApplication().pushScreen(conversationScreen);
                    }
                });
            }
        }
        
        public void getGuiUpdateConversation()
        {
            if(conversationScreen == null)
            {
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        conversationScreen = new ConversationScreen(me);
                        UiApplication.getUiApplication().pushScreen(conversationScreen);
                    }
                });
            }
            else
            {
                conversationScreen.updateScreen();
            }
        }
        
        public void getGuiUpdateConversation(final Message msg)
        {
            UiApplication.getUiApplication().invokeAndWait(new Runnable() 
            {
                public void run() 
                {
                    if(conversationScreen == null)
                    {
                        conversationScreen = new ConversationScreen(me);
                        UiApplication.getUiApplication().pushScreen(conversationScreen);
                    }
                    else
                    {
                        conversationScreen.updateScreenMessage(msg);
                    }
                }
            });
        }
        
        
        public void getGuiChoose(String type) 
        { 
            if(inviteScreen == null)
            {
                inviteScreen = new InviteScreen(type, this);
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        UiApplication.getUiApplication().pushScreen(inviteScreen);
                    }
                });
            }
        }
       
        
        /**
         *
         *Create a new one
         */
        public void getGuiRosterDetails() 
        {
            UiApplication.getUiApplication().invokeLater(new Runnable() 
            {
                public void run() 
                {
                    UiApplication.getUiApplication().pushScreen(new ContactDetailScreen(me));
                }
            });
        }
        
        /**
         *
         *Change a current roster info or create a new one
         */
        public void getGuiRosterEdit() 
        {
            UiApplication.getUiApplication().invokeLater(new Runnable() 
            {
                public void run() 
                {
                    UiApplication.getUiApplication().pushScreen(new ContactEditScreen(me));
                }
            });
        }
        
        
         /**
         *Display existing conversations and the form to join in a new chat room
         * @return Displayable
         */
        public void getGuiRoomList() 
        {
            if(multichatScreen == null)
            {
                multichatScreen = new MultiChatScreen(this);
                UiApplication.getUiApplication().invokeLater(new Runnable() 
                {
                    public void run() 
                    {
                        UiApplication.getUiApplication().pushScreen(multichatScreen);
                    }
                });
            }
            else
            {
                multichatScreen.updateScreen();
            }
        }
        
        
         /**
         *Utility method to set the display
         *
         */
        public void setCurrentDisplay() 
        {
                if (internal_state == ONLINE)
                {
                       getGuiOnlineMenu();
                }
                else if (internal_state == ROSTER || internal_state == ROSTER_DETAILS) 
                {  
                        if (infopool.containsKey("currentjid"))
                        {
                            currentjid = (Jid)infopool.remove("currentjid");
                        }
                                
                        if (internal_state == ROSTER_DETAILS)
                        {
                            getGuiRosterDetails();//possible problems..
                        }
                        else
                        {
                            getGuiRosterItem();
                        }
                }
                else if (internal_state == OPTIONS)
                {
                    getGuiOtherOptions();
                }
                else if (internal_state == JUD)
                {
                    getGuiJudMenu();
                }
                else
                {
                    getGuiOnlineMenu();
                }
        }
        
        public void getConfigurationScreen()
        {
            UiApplication.getUiApplication().invokeLater(new Runnable() 
            {
                public void run() 
                {
                    ConfigurationRoomScreen scr = new ConfigurationRoomScreen(((GroupChat)me.currentConversation).getConfiguration(), me);
                    UiApplication.getUiApplication().pushScreen(scr);
                }
            });
        }
        
        public void showAlert(final String message)
        {
            UiApplication.getUiApplication().invokeLater(new Runnable() 
            {
                public void run() 
                {
                    Status.show(message);
                }
            });
        }
        
        public String getNickByJid(String jidStr)
        {
            String j = "";
            if(jidStr.indexOf("@")!=-1)
            {
                j=jidStr.substring(0, jidStr.indexOf("@"));
            }
            else
            {
                j = jidStr;
            }
            String out = "";
            for (int i=0; i<roster.size(); i++) 
            {                            
                Jid temp = (Jid)roster.elementAt(i);
                if(temp.getNickName().toLowerCase().equals(j.toLowerCase()))
                {
                    out = temp.getNick();
                    break;
                }
            }
            if(out.length()==0)
            {
                out = jidStr;
            }
            return out;
        }
        
        public String getJidByNick(String nick)
        {
            String out = "";
            for (int i=0; i<roster.size(); i++) 
            {                            
                Jid temp = (Jid)roster.elementAt(i);
                if(temp.getNick().toLowerCase().equals(nick.toLowerCase()))
                {
                    out = temp.getNickName();
                    break;
                }
            }
            return out;
        }
        
        public void getGuiRosterItem()
        {
        }
        
        public void getGuiJudMenu()
        {
        }
        
        public void getGuiOtherOptions()
        {
        }
        
        
        /**
        *  Determine if activated due to inbound connection and
        *  if so dispatch a PushProcessor to handle incoming
        *  connection(s). 
          @return true if MIDlet was activated
        *  due to inbound connection, false otherwise
        */
        private boolean handlePushActivation()
        {
                //  Discover if there are pending push inbound
                //  connections and if so, dispatch a
                //  PushProcessor for each one.
                String[] connections = PushRegistry.listConnections(true);
                if (connections != null && connections.length > 0)
                {
                        
                        PushThread pp = new PushThread(connections[0], this);
                        pp.start();
                        
                        return (true);
                }
                return (false);
        }
        
}
