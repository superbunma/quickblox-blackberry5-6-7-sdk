package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.connection.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.containers.*;
import com.injoit.examplechat.jmc.media.*;
import com.injoit.examplechat.utils.*;

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

public class AddRoomMemberToContactsScreen extends MainScreen implements FieldChangeListener
{
    private AddRoomMemberToContactsScreen me;
    private ChatManager chat_manager;
    
    private VerticalFieldManager vfm;
    private VerticalFieldManager man;
    private VerticalFieldManager cman;
    
    private EditField nick;
    private String filterString = "";
    private Vector roster;
    
    public AddRoomMemberToContactsScreen(ChatManager _chat_manager, Vector _roster)
    {
        super(NO_VERTICAL_SCROLL);
           me = this;
                this.getMainManager().setBackground(
            BackgroundFactory.createLinearGradientBackground(0x0099CCFF,
            0x0099CCFF,0x00336699,0x00336699)
        );
        chat_manager = _chat_manager;
        roster = _roster;
        
        try
        {
            man = new VerticalFieldManager();
            
            TitleBarManager title = new TitleBarManager("Invite contact");
            
            final Bitmap borderBitmap = Bitmap.getBitmapResource("border.png");
            vfm = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL);
            vfm.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12,12,12,12), borderBitmap));
            
            nick = new EditField(Field.USE_ALL_WIDTH);
            nick.setChangeListener(me);
            
            cman = new VerticalFieldManager();
            
            man.add(title);
            vfm.add(nick);
            man.add(vfm);
            man.add(cman);
            
            add(man);
            
            updateRoster();
        }
        catch(Exception ex)
        {
        }
    }    
    
    public void updateRoster()
    {
        final String str = filterString.toLowerCase();
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                try 
                {
                    cman.deleteAll();
                    
                    Vector filteredRoster = new Vector();
                    for (int i=0; i<roster.size(); i++) 
                    {                            
                        String username = (String)roster.elementAt(i);
                        if(username.toLowerCase().indexOf(str) !=-1)
                        {
                            filteredRoster.addElement(roster.elementAt(i));
                        }
                    }
                    
                    
                    if (filteredRoster.size() > 0) 
                    {
                        cman.add(new LabelField("Room members:"));
                        for (int i=0; i<filteredRoster.size(); i++) 
                        {  
                            String name = (String)filteredRoster.elementAt(i);
                            try
                            {
                                name = name.substring(0, name.indexOf("@"));
                            }
                            catch(Exception ex)
                            {
                            }
                            
                            //check if contact already exists in contact list
                            boolean found = false;
                            for (int c=0; c<chat_manager.roster.size(); c++) 
                            {                            
                                Jid temp = (Jid)chat_manager.roster.elementAt(c);
                                if(temp.getNickName().equals(name) == true)
                                {
                                    found = true;
                                    break;
                                }
                            }
                            
                            if(found == false)
                            {
                                if(Datas.jid.getUsername().equals(name)==false)
                                {
                                    name += "@" + Datas.hostname;
                                    Jid temp = new Jid(name);
                                    final  ContactButtonField _cnt = new ContactButtonField(temp, false);
                                    _cnt.setCookie(new Integer(i));
                                    _cnt.setChangeListener(me);
                                    cman.add(_cnt);
                                }
                            }
                        }
                    }
                }
                catch(Exception ex) 
                {
                    DebugStorage.getInstance().Log(0, "<AddRoomMemberToContactsScreen> updateRoster ", ex);
                } 
            }
        });
    }
    
    protected void makeMenu(Menu menu, int instance)
    {
        menu.add(_inviteItem);
        menu.add(_cancelItem);
        super.makeMenu(menu, instance);
    }
    
    private MenuItem _inviteItem = new MenuItem("Invite", 250, 250) 
    {
        public void run() 
        {
            Field f = man.getLeafFieldWithFocus();
            if(f !=null)
            {
                if(f == nick)
                {
                    if(nick.getText().length()>0)
                    {
                        addToContacts(nick.getText());
                    }
                    else
                    {
                        Status.show("You have enter JID!");
                    }
                }
                else if(f instanceof ContactButtonField)
                {
                    ContactButtonField b = (ContactButtonField) f;
                    addToContacts(b.getJid());
                }
            }
        }
    }; 
    
    private MenuItem _cancelItem = new MenuItem("Cancel", 260, 260) 
    {
        public void run() 
        {
            onClose();
        }
    }; 
    
    public boolean onClose()
    {
        try
        {
           close();
        }
        catch(Exception ex)
        {
        }
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        if(field instanceof EditField)
        {
            EditField f = (EditField) field;
            String old = filterString;
            filterString = f.getText();
            
            if(filterString.equals(old)==false)
            {
                updateRoster();
            }
        }
        else if(field instanceof ContactButtonField)
        {
            ContactButtonField b = (ContactButtonField) field;
            addToContacts(b.getJid());
        }
    }
    
    public void addToContacts(Jid jid)
    {
        //notify to the server
        Subscribe.setNewRosterItem(jid, true);
        Datas.registerRoster(jid);
            
        chat_manager.getGuiConversation();
        onClose();
    }
    
     public void addToContacts(String text)
    {
        if (text.indexOf("@") != -1)
        {
            Jid jid = new Jid(text);
            jid.group = "";
            jid.phone = "";
            
            //notify to the server
            Subscribe.setNewRosterItem(jid, true);
            Datas.registerRoster(jid);
        
            chat_manager.getGuiConversation();
            onClose();
        }
        else 
        {
            Status.show("JID not correct");
        }
    }
}

