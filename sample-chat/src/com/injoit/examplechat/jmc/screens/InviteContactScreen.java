package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.connection.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.containers.*;
import com.injoit.examplechat.jmc.media.*;
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

public class InviteContactScreen extends MainScreen implements FieldChangeListener
{
    private InviteContactScreen me;
    private ChatManager chat_manager;
    
    private VerticalFieldManager vfm;
    private VerticalFieldManager man;
    private VerticalFieldManager cman;
    
    private EditField nick;
    private String filterString = "";
    private Vector members; 
    
    public InviteContactScreen(ChatManager _chat_manager, Vector _members)
    {
        super(NO_VERTICAL_SCROLL);
     
        me = this;
        chat_manager = _chat_manager;
        members = _members;
        
        this.getMainManager().setBackground(
            BackgroundFactory.createLinearGradientBackground(0x0099CCFF,
            0x0099CCFF,0x00336699,0x00336699)
        );
        
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
                    
                    Vector roster = Datas.createRosterVector(true);  
                    Vector filteredRoster = new Vector();
                    for (int i=0;i< roster.size(); i++) 
                    {                            
                        Jid temp = (Jid)roster.elementAt(i);
                        String username = temp.getUsername().replace('%', '@');
                        if(username.toLowerCase().indexOf(str) !=-1)
                        {
                            //check of contact already in room
                            boolean found = false;
                            for(int m=0; m<members.size(); m++)
                            {
                                String member = (String) members.elementAt(m);
                                if(member.indexOf("@") != -1)
                                {
                                    member = member.substring(0, member.indexOf("@"));
                                }
                                if(username.toLowerCase().equals(member.toLowerCase()))
                                {
                                    found = true;
                                    break;
                                }
                            }
                            ///////////////////////////////
                            if(found == false)
                            {
                                filteredRoster.addElement(roster.elementAt(i));
                            }
                        }
                    }
                    
                    
                    if (filteredRoster.size() > 0) 
                    {
                        cman.add(new LabelField("Your contact list:"));
                        for (int i=0;i< filteredRoster.size(); i++) 
                        {                            
                            Jid temp = (Jid)filteredRoster.elementAt(i);
                            final  ContactButtonField _cnt = new ContactButtonField(temp);
                            _cnt.setCookie(new Integer(i));
                            _cnt.setChangeListener(me);
                            cman.add(_cnt);
                        }
                    }
                }
                catch(Exception ex) 
                {

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
                        invite(nick.getText());
                    }
                    else
                    {
                        Status.show("You have enter JID!");
                    }
                }
                else if(f instanceof ContactButtonField)
                {
                    ContactButtonField b = (ContactButtonField) f;
                    invite(b.getJid());
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
            invite(b.getJid());
        }
    }
    
    public void invite(Jid jid)
    {
        ChatHelper.inviteContact(jid.getUsername()+ "@" + Datas.server_name, chat_manager.currentConversation.name);
        chat_manager.getGuiConversation();
        onClose();
        
        Status.show("Invite sent!");
    }
    
     public void invite(String text)
    {
        if (text.indexOf("@") != -1)
        {
            ChatHelper.inviteContact(text, chat_manager.currentConversation.name);
            chat_manager.getGuiConversation();
            onClose();
        }
        else 
        {
            Status.show("JID not correct");
        }
    }
}

