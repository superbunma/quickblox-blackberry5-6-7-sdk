package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.connection.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.media.*;
import com.injoit.examplechat.util.*;
import com.injoit.examplechat.threads.*;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.presence.*;
import com.injoit.examplechat.jabber.subscription.*;
import com.injoit.examplechat.me.regexp.RE;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.decor.*;


public class ContactEditScreen extends MainScreen implements FieldChangeListener
{
    private ContactEditScreen me;
    private ChatManager chat_manager;   
    private EditField nick;
    private EditField group;
    private EditField jid;
    private ButtonField bOk;
    private ButtonField bCancel;
    
    String name = "Edit Contact";
    
    public ContactEditScreen(ChatManager _chat_manager)
    {
        super(NO_VERTICAL_SCROLL);
        me = this;
        this.getMainManager().setBackground(
            BackgroundFactory.createLinearGradientBackground(0x0099CCFF,
            0x0099CCFF,0x00336699,0x00336699)
        );

        chat_manager = _chat_manager;
        
        VerticalFieldManager vman = new VerticalFieldManager();
        
        String group_Jid = "";
        String jnick = "";
        String jname = "";
        
        if (chat_manager.currentjid != null) 
        {
            name = chat_manager.currentjid.getUsername();
            jname = chat_manager.currentjid.getFullJid();
            group_Jid = chat_manager.currentjid.group;
            jnick = chat_manager.currentjid.getNick();
        }
        
        jid =new EditField("JID: ", jname, 64, 0);
        jid.setEditable(false);
        
        if(jnick.length()>0)
        {
            nick = new EditField("Nick: ", jnick, 64, 0);
        }
        else
        {
            nick = new EditField("Nick: ", name, 64, 0);
        }
        
        group = new EditField("Group: ", group_Jid, 32, 0);
        
        bOk = new ButtonField("Accept", Field.FIELD_HCENTER|DrawStyle.HCENTER);
        bOk.setChangeListener(this);
        bOk.setEditable(true);
        
        bCancel = new ButtonField("Cancel", Field.FIELD_HCENTER|DrawStyle.HCENTER);
        bCancel.setChangeListener(this);
        bCancel.setEditable(true);
        
        vman.add(jid);
        vman.add(nick);
        vman.add(group);
        
        vman.add(bOk);
        vman.add(bCancel);
                                        
        add(vman);
        bOk.setFocus();
    }
    
   protected void makeMenu(Menu menu, int instance)
    {
        if (instance == Menu.INSTANCE_DEFAULT)
        {
            menu.add(_acceptItem);
            menu.add(_closeItem);
        }
    }
    
    private MenuItem _acceptItem = new MenuItem("Accept", 250, 250) 
    {
        public void run() 
        {
            accept();
            onClose();
        }
    }; 
    
    private MenuItem _closeItem = new MenuItem("Close", 260, 260) 
    {
        public void run() 
        {
            onClose();
        }
    }; 
    
    public boolean onSavePrompt() 
    {
        return true;
    }
    
    public boolean onClose()
    {
        close();
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        if(field == bOk)
        {
            accept();
        }
        else if(field == bCancel)
        {
            onClose();
        }
    }
    
    private void accept()
    {
        if(nick.getText().length()>0)
        {
            chat_manager.currentjid.setNick(nick.getText());
        }
        else
        {
            chat_manager.currentjid.setNick(chat_manager.currentjid.getNickName());
        }
        chat_manager.internal_state = chat_manager.ONLINE;
        
        Subscribe.renameRosterItem(chat_manager.currentjid);

        chat_manager.getGuiOnlineMenu();
        onClose();
    }
}
