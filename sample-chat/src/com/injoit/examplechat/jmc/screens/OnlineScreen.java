package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.connection.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.containers.*;
import com.injoit.examplechat.jmc.media.*;
import com.injoit.examplechat.utils.DebugStorage;
import com.injoit.examplechat.util.*;
import com.injoit.examplechat.threads.*;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.presence.*;
import com.injoit.examplechat.jabber.subscription.*;

import java.util.*;
import java.lang.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.decor.*;

public class OnlineScreen extends MainScreen implements FieldChangeListener
{
    private OnlineScreen me;
    private ChatManager chat_manager;
    private VerticalScrollManager vman;
    private TitleBarManager title;
    
    private boolean showAllContacts = true; //false - show just online contacts 
    
    public OnlineScreen(ChatManager _chat_manager)
    {
        super(NO_VERTICAL_SCROLL);
                     
        me = this;
        chat_manager = _chat_manager;
        
        // Set the linear background.
        this.getMainManager().setBackground(
            BackgroundFactory.createLinearGradientBackground(0x0099CCFF,
            0x0099CCFF,0x00336699,0x00336699)
        );
        
        vman = new VerticalScrollManager();
        title = new TitleBarManager(OfflineScreen.getJidName());
        add(title);
        add(vman);
        
        updateScreen();
    }
    
    public void updateScreen()
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                try 
                {
                    title.invalidete();
                    vman.deleteAll();
                    if(showAllContacts == true)
                    {
                        chat_manager.roster = Datas.createRosterVector(true);
                    }
                    else
                    {
                        chat_manager.roster = Datas.createOnlineRosterVector(true); 
                    }
                    
                    if (chat_manager.roster.size() > 0) 
                    {
                        vman.add(new LabelField("Contact list:"));
                        for (int i=0;i< chat_manager.roster.size(); i++) 
                        {                            
                            Jid temp = (Jid)chat_manager.roster.elementAt(i);
                            final  ContactButtonField _cnt = new ContactButtonField(temp);
                            _cnt.setCookie(new Integer(i));
                            _cnt.setChangeListener(me);
                            vman.add(_cnt);
                        }
                    }
                    else //if (hide.equals(Contents.hide[0]))
                    { 
                        vman.add(new LabelField(Contents.noRoster));
                    }
                    
                    if (Datas.conversations.size() > 0)
                    {
                            vman.add(new SeparatorField());
                            vman.add(new LabelField("Active chats: "));
        
                            Vector chats = Datas.conversations;
                            
                            for (int k = 0; k < chats.size(); k++)
                            {
                                String temp = "";
                                if(chats.elementAt(k) instanceof SingleChat)
                                {
                                    temp = ((SingleChat)chats.elementAt(k)).roster.getNick();
                                }
                                else
                                {
                                     temp = ((Conversation)chats.elementAt(k)).name;
                                }
                                ChatHelper.RequestMembersCount(temp);
                                final  RoomButtonField _cht = new RoomButtonField(Jid.getNickUser(temp), Contents.displayBitmap("chat"));
                                _cht.setCookie(new Integer(k));
                                _cht.setUsersCount(1);
                                _cht.setChangeListener(me);
                                vman.add(_cht);
                            }
                    }
                    else
                    {
                        vman.add(new SeparatorField());
                    }
                }
                catch(Exception ex) 
                {

                } 
            }
        });
    }
    
    public void makeMenu(Menu menu, int instance) 
    {
        Field f = vman.getLeafFieldWithFocus();
        if(f!=null)
        {
            if(f instanceof ContactButtonField)
            {
                ContactButtonField _cnt = (ContactButtonField) f;
                Integer _index = (Integer) _cnt.getCookie();
                int index = _index.intValue();
                chat_manager.currentjid = (Jid)chat_manager.roster.elementAt(index);
                
                if (Presence.getPresence("unsubscribed").equals(chat_manager.currentjid.getPresence()))
                {
                    menu.add(subscribeMeItem);
                }
                
                menu.add(chatContactItem);
                menu.add(changeContactItem);
                menu.add(delContactItem);
                menu.add(MenuItem.separator(195));
            }
        }
        menu.add(jidMyMeItem);
        menu.add(addContactItem);
        menu.add(mchatItem);
        menu.add(MenuItem.separator(245));
        
        if(showAllContacts == true)
        {
            menu.add(showOnlineItem);
        }
        else
        {
            menu.add(showAllItem);
        }            
        menu.add(statusItem);
        menu.add(messageItem);
        
        menu.add(MenuItem.separator(395));
        menu.add(disconnectItem);
        
        super.makeMenu(menu, instance);
    }
    
    private MenuItem subscribeMeItem = new MenuItem("Send subscribe request", 140, 140) 
    {
        public void run() 
        {
            chat_manager.internal_state = chat_manager.CONVERSATION;
            Subscribe.requestSubscription(chat_manager.currentjid);
        }
    };
    
    
    private MenuItem chatContactItem = new MenuItem("Chat", 150, 150) 
    {
        public void run() 
        {
            Field field = vman.getLeafFieldWithFocus();
            if(field!=null)
            {
                if(field instanceof ContactButtonField)
                {
                    ContactButtonField _cnt = (ContactButtonField) field;
                    Integer _index = (Integer) _cnt.getCookie();
                    int index = _index.intValue();
        
                    chat_manager.currentjid = (Jid)chat_manager.roster.elementAt(index);
                    if (Presence.getPresence("unsubscribed").equals(chat_manager.currentjid.getPresence()))
                    {
                        // try to subscribe to the item
                        chat_manager.internal_state = chat_manager.CONVERSATION;
                        Subscribe.requestSubscription(chat_manager.currentjid);
                    }

        
                    boolean found = false;
                    Conversation c1 = null;
                    Vector conversations = Datas.conversations;
                    
                    DebugStorage.getInstance().Log(0, "---------> Searching in existing chats: for " + chat_manager.currentjid.getUsername());
                    //look for an exsisting Conversation
                    for (int i=0; i< conversations.size(); i++) 
                    {
                        c1 = (Conversation)conversations.elementAt(i);
                        
                        DebugStorage.getInstance().Log(0, "existing chat: " + c1.name);
                        
                        if (c1.name.equals(chat_manager.currentjid.getUsername())) 
                        {
                            DebugStorage.getInstance().Log(0, "chat founded! " + c1.name);
                            found = true;
                            break;
                        }
                    }
                    if (found) 
                    {
                        DebugStorage.getInstance().Log(0, "open existing chat");
                        chat_manager.currentConversation = c1;
                    } 
                    else
                    {
                        DebugStorage.getInstance().Log(0, "Chat was not found, open new chat");
                        // sets up a new conversation 
                        chat_manager.currentConversation = new SingleChat(chat_manager.currentjid, "chat", "");                                   
                        conversations.addElement(chat_manager.currentConversation);
                    }
                    chat_manager.getGuiConversation();
                    chat_manager.internal_state = chat_manager.CONVERSATION;
                    DebugStorage.getInstance().Log(0, "---------> end of chat search");
                }
            }
        }
    }; 
    
    private MenuItem changeContactItem = new MenuItem("Change Contact", 160, 160) 
    {
        public void run() 
        {
            chat_manager.getGuiRosterEdit();
            chat_manager.internal_state = chat_manager.ROSTER_DETAILS;
        }
    };
    
    private MenuItem delContactItem = new MenuItem("Delete Contact", 170, 170) 
    {
        public void run() 
        {
            if (Dialog.YES == Dialog.ask(Dialog.D_YES_NO,"Are you sure you want to delete this contact?\n" + chat_manager.currentjid.getLittleJid()))
            {
                Subscribe.removeRosterItem(chat_manager.currentjid);
                Status.show("Contact deleted!");
            }
        }
    };
    
    private MenuItem addContactItem = new MenuItem("Add Contact", 200, 200) 
    {
        public void run() 
        {
            chat_manager.currentjid = null;
            chat_manager.getGuiRosterDetails();
            chat_manager.internal_state = chat_manager.ROSTER_DETAILS;
        }
    }; 
    
    
    private MenuItem mchatItem = new MenuItem("Multi Chat", 240, 240) 
    {
        public void run() 
        {
             chat_manager.getGuiRoomList();
            
        }
    }; 
    
    private MenuItem showOnlineItem = new MenuItem("Hide offline contacts", 340, 340) 
    {
        public void run() 
        {
            showAllContacts = false;
            updateScreen();
        }
    }; 
    
    private MenuItem showAllItem = new MenuItem("Show All Contacts", 340, 340) 
    {
        public void run() 
        {
            showAllContacts = true;
            updateScreen();
        }
    };
    
    private MenuItem statusItem = new MenuItem("Change Status", 350, 350) 
    {
        public void run() 
        {
            ChangeStatusScreen ch = new ChangeStatusScreen(chat_manager); 
        }
    }; 
    
    private MenuItem messageItem = new MenuItem("Change Status Message", 360, 360) 
    {
        public void run() 
        {
            ChangeStatusMessageScreen ch = new ChangeStatusMessageScreen(chat_manager); 
        }
    }; 
    
    private MenuItem disconnectItem = new MenuItem("Disconnect", 400, 400) 
    {
        public void run() 
        {
            Datas.multichat.clear();
            Datas.conversations.removeAllElements();
            Datas.conversations.trimToSize();
            Datas.server_services.removeAllElements();
            Datas.conversations.trimToSize();
            Datas.readRoster = false;
            
            chat_manager.cm.terminateStream(); 
            chat_manager.getGuiOfflineMenu();
            chat_manager.internal_state = chat_manager.OFFLINE;
            chat_manager.onlineScreen = null; 
        }
    }; 
    
    private MenuItem jidMyMeItem = new MenuItem("My JID", 440, 440) 
    {
        public void run() 
        {
            Dialog.alert("Your JID: " + OfflineScreen.getJidFull());
        }
    };
    
    
    public boolean onSavePrompt() 
    {
        return true;
    }
    
    public boolean onClose()
    {
        Object obj = null;
        obj = RuntimeStore.getRuntimeStore().remove(0xaabda11c5d004c17L);//DebugStorage

        close();
        return true;
    }
    
    protected boolean keyChar(char character, int status, int time) 
    {
        if (character == Keypad.KEY_ESCAPE) 
        {
            askClose();
            return true;
        }
        return super.keyChar(character, status, time);
    }
    
    public boolean askClose() 
    {
        int choice = Dialog.ask(Dialog.D_YES_NO, "Do you want to go offline?", Dialog.YES);

        if (choice == Dialog.YES) 
        {
            Datas.multichat.clear();
            Datas.conversations.removeAllElements();
            Datas.conversations.trimToSize();
            Datas.server_services.removeAllElements();
            Datas.conversations.trimToSize();
            Datas.readRoster = false;
            
            chat_manager.cm.terminateStream(); 
            chat_manager.getGuiOfflineMenu();
            chat_manager.internal_state = chat_manager.OFFLINE;
            chat_manager.onlineScreen = null;                 
        }
        else
        {
            //switch to another application's screen without exit
        }   
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        if(field instanceof ContactButtonField)
        {
            ContactButtonField _cnt = (ContactButtonField) field;
            Integer _index = (Integer) _cnt.getCookie();
            int index = _index.intValue();

            chat_manager.currentjid = (Jid)chat_manager.roster.elementAt(index);
            if (Presence.getPresence("unsubscribed").equals(chat_manager.currentjid.getPresence()))
            {
                // try to subscribe to the item
                chat_manager.internal_state = chat_manager.CONVERSATION;
                Subscribe.requestSubscription(chat_manager.currentjid);         
            }    
                boolean found = false;
                Conversation c1 = null;
                Vector conversations = Datas.conversations;
                
                DebugStorage.getInstance().Log(0, "---------> Searching in existing chats: for " + chat_manager.currentjid.getUsername());
                //look for an exsisting Conversation
                for (int i=0; i< conversations.size(); i++) 
                {
                    c1 = (Conversation)conversations.elementAt(i);
                    
                    DebugStorage.getInstance().Log(0, "existing chat: " + c1.name);
                    
                    if (c1.name.equals(chat_manager.currentjid.getUsername())) 
                    {
                        DebugStorage.getInstance().Log(0, "chat founded! " + c1.name);
                        found = true;
                        break;
                    }
                }
                if (found) 
                {
                    DebugStorage.getInstance().Log(0, "open existing chat");
                    chat_manager.currentConversation = c1;
                } 
                else
                {
                    DebugStorage.getInstance().Log(0, "Chat was not found, open new chat");
                    // sets up a new conversation 
                    chat_manager.currentConversation = new SingleChat(chat_manager.currentjid, "chat", "");                                   
                    conversations.addElement(chat_manager.currentConversation);
                }
                chat_manager.getGuiConversation();
                chat_manager.internal_state = chat_manager.CONVERSATION;
                DebugStorage.getInstance().Log(0, "---------> end of chat search");
        }
        else if(field instanceof RoomButtonField)
        {
            //command "Open chat" 
            RoomButtonField _cnt = (RoomButtonField) field;
            Integer _index = (Integer) _cnt.getCookie();
            int index = _index.intValue();

            RoomButtonField b = (RoomButtonField) field;
            String cname = "";
            if(Datas.conversations.elementAt(index) instanceof SingleChat)
            {
                cname = ((SingleChat)Datas.conversations.elementAt(index)).roster.getNickName();
            }
            else
            {            
                cname = b.getName();
            }
            
            chat_manager.currentConversation = (Conversation)Datas.multichat.get(cname);
            if(chat_manager.currentConversation!=null)
            {
                chat_manager.currentConversation.isMulti = true;
                chat_manager.internal_state = chat_manager.CONVERSATION;
                chat_manager.getGuiConversation();
            }
            else
            {
                boolean found = false;
                Conversation c1 = null;
                Vector conversations = Datas.conversations;
                //look for an exsisting Conversation
                for (int i=0; i< conversations.size(); i++) 
                {
                    c1 = (Conversation)conversations.elementAt(i);
                    if (c1.name.equals(cname)) 
                    {
                            found = true;
                            break;
                    }
                }
                if (found) 
                {
                        chat_manager.currentConversation = c1;
                } 
                else
                {
                    // sets up a new conversation 
                    chat_manager.currentConversation = new SingleChat(chat_manager.currentjid, "chat", "");                                   
                    conversations.addElement(chat_manager.currentConversation);
                }
                chat_manager.getGuiConversation();
                chat_manager.internal_state = chat_manager.CONVERSATION;
            }
        }
    }
}



