package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.connection.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.containers.*;
import com.injoit.examplechat.jmc.media.*;

import com.injoit.examplechat.util.*;
import com.injoit.examplechat.utils.*;
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
import net.rim.device.api.collection.util.*;

public class MultiChatScreen extends MainScreen implements FieldChangeListener
{
    private MultiChatScreen me;
    private ChatManager chat_manager;
    private VerticalScrollManager vman;
    private TitleBarManager title;
    
    private String filterString = "";
    
    private static final int HPADDING = Display.getWidth() <= 320 ? 6 : 8;
    
    private boolean showAllContacts = true; //false - show just online contacts 
    
    public MultiChatScreen() {
    }
    
    public MultiChatScreen(ChatManager _chat_manager)
    {
        super(NO_VERTICAL_SCROLL);
       
        me = this;
        chat_manager = _chat_manager;
        
        this.getMainManager().setBackground(
            BackgroundFactory.createLinearGradientBackground(0x0099CCFF,
            0x0099CCFF,0x00336699,0x00336699)
        );
        vman = new VerticalScrollManager();
        title = new TitleBarManager(OfflineScreen.getJidName());
        add(title);
        add(vman);
        ChatHelper.serviceRequest( "muc." + Datas.hostname);
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
                     
                    //Open rooms
                    Enumeration chats = Datas.multichat.keys();
                      
                    BigVector sorted_chats = new BigVector();
                    while (chats.hasMoreElements())
                    {
                        sorted_chats.addElement(chats.nextElement());
                    }
                    sorted_chats.sort(new ChatNameComparator());                        
                    if(chats.hasMoreElements()==true)
                    {
                        vman.add(new LabelField("Your Active Chats:"));
                    }
                            
                        if(vman.getFieldCount()>0)
                        {
                            LabelField label = new LabelField("Your Active Chats:")
                            {
                                public void paint(Graphics g)
                                {
                                    g.setColor(0xffffff);
                                    super.paint(g);
                                }
                            };
                            label.setMargin(HPADDING,0,0,HPADDING);
                            vman.insert(label, 0);
                        }
                                  
                    //Existing rooms
                    if (Datas.rooms != null)
                    {
                        vman.add(new LabelField("Existing Rooms:"));

                        for(int k=0;k<Datas.rooms.size();k++)
                        {
                            String temp = (String)Datas.rooms.elementAt(k);
                            String result;
                            if(temp.lastIndexOf('-')!=-1)
                            {
                                temp = temp.substring(temp.lastIndexOf('-')+1, temp.length());
                            }
                            //result = normalizationName(temp);
                            RoomButtonField b = new RoomButtonField(temp, Contents.displayBitmap("chat"));
                            b.setChangeListener(me);
                            b.setUsersCount(Datas.GetRoomInfo(temp));
                            b.setEditable(true);   
                            vman.add(b);
                             }
                    }
                    else
                    {
                        vman.add(new LabelField("Loading..."));
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
        menu.add(createItem);
        
        Field field = vman.getLeafFieldWithFocus();
        if(field instanceof RoomButtonField)
        {
            menu.add(joinItem);
        }
        
        menu.add(MenuItem.separator(250));        
        super.makeMenu(menu, instance);
    }    
   
    private MenuItem createItem = new MenuItem("Create new chat", 200, 200) 
    {
        public void run() 
        {
            CreateRoomScreen scr = new CreateRoomScreen(chat_manager);
        }
    };
    
    private MenuItem joinItem = new MenuItem("Join chat", 210, 210) 
    {
        public void run() 
        {
            Field field = vman.getLeafFieldWithFocus();
            if(field instanceof RoomButtonField)
            {
                RoomButtonField b = (RoomButtonField) field;
                
                String cname = b.getName();
                chat_manager.currentConversation = (Conversation)Datas.multichat.get(cname);
                if(chat_manager.currentConversation!=null)
                {
                    chat_manager.currentConversation.isMulti = true;
                    chat_manager.internal_state = chat_manager.CONVERSATION;
                    chat_manager.getGuiConversation();
                }
                else
                {
                    ChatHelper.groupExistingChatJoin(Datas.jid.getUsername(), cname.substring(0,cname.indexOf("@")), cname.substring(cname.indexOf("@")+1));
                }
            }
        }
    };
    
    public boolean onSavePrompt() 
    {
        return true;
    }
    
    public boolean onClose()
    {
        chat_manager.internal_state = chat_manager.ONLINE;
        chat_manager.getGuiOnlineMenu();
        chat_manager.multichatScreen = null;
        close();
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        if(field instanceof RoomButtonField)
        {
            RoomButtonField b = (RoomButtonField) field;
            
            String cname = b.getName();
            chat_manager.currentConversation = (Conversation)Datas.multichat.get(cname);
            if(chat_manager.currentConversation!=null)
            {
                chat_manager.currentConversation.isMulti = true;
                chat_manager.internal_state = chat_manager.CONVERSATION;
                chat_manager.getGuiConversation();
            }
            else
            {
                if(cname.indexOf("@") !=-1)
                {
                    ChatHelper.groupExistingChatJoin(Datas.jid.getUsername(), cname.substring(0,cname.indexOf("@")), cname.substring(cname.indexOf("@")+1));
                }
                else
                {
                    Status.show("Incorrect room name!");
                }
            }
        }
    }
    class ChatNameComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            int result = 0;
            if(o1 != null && o2!=null)
            {
                try
                {
                    String n1 = (String) o1;
                    String n2 = (String) o2;
                    
                    n1 = n1.substring(0, n1.indexOf("@"));
                    n2 = n2.substring(0, n2.indexOf("@"));
                    
                    int name1 = Integer.parseInt(n1);
                    int name2 = Integer.parseInt(n2);
                    
                    if(name1 > name2)
                    {
                        result = 1;
                    }
                    else if(name1 < name2)
                    {
                        result = -1;
                    }
                }
                catch(Exception ex)
                {
                    result = 0;
                }
            }
            return result;
        }
        
        public boolean equals(Object obj)
        {
            return true;
        }
    }
        
    public void UpdateRoomMembersCount(final String roomName, final int membersCount)
    {
        Datas.UpdateInfo(new RoomInfo(roomName, membersCount));
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                for(int i=0; i<vman.getFieldCount(); i++)
                {
                    Field f = vman.getField(i);
                    if(f instanceof RoomButtonField)
                    {
                        RoomButtonField chbf = (RoomButtonField) f;
                        if(chbf.getName().equals(roomName))
                        {
                            chbf.setUsersCount(membersCount);
                            me.invalidate();
                        }
                    }
                }
            }
        });
    }
}



