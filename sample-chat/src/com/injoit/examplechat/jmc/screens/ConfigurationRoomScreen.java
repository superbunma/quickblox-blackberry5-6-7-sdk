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
import com.injoit.examplechat.jabber.conversation.Configuration.*;
import com.injoit.examplechat.jmc.containers.*;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.decor.*;

public class ConfigurationRoomScreen extends MainScreen implements FieldChangeListener
{
    private ConfigurationRoomScreen me;
    private Configuration conf;
    private TitleBarManager title;
    
    private ButtonField bOk;
    private ButtonField bCancel;
    private ChatManager chat_manager;
    
    public ConfigurationRoomScreen(Configuration _conf, ChatManager _chat_manager)
    {
        super(NO_VERTICAL_SCROLL);
        me = this;
        conf = _conf;
        chat_manager = _chat_manager;
        
        VerticalScrollManager vman = new VerticalScrollManager();
                                
        title = new TitleBarManager("Configuration");
        add(title);
        
        if(conf!=null)
        {
            for(int i=0; i<conf.getFields().size(); i++)
            {
                ConfigField f = (ConfigField) conf.getFields().elementAt(i);
                Field field = (Field) f.getObject();
                if(field !=null)
                {
                    vman.add(field);
                }
            }
        }
        
        bOk = new ButtonField("Save", Field.FIELD_HCENTER|DrawStyle.HCENTER);
        bOk.setChangeListener(this);
        bOk.setEditable(true);
        
        bCancel = new ButtonField("Cancel", Field.FIELD_HCENTER|DrawStyle.HCENTER);
        bCancel.setChangeListener(this);
        bCancel.setEditable(true);
        
        vman.add(bOk);
        vman.add(bCancel);
        
        add(vman);
    }
    
    protected void makeMenu(Menu menu, int instance)
    {
        if (instance == Menu.INSTANCE_DEFAULT)
        {
            menu.add(_okItem);
            menu.add(_closeItem);
        }
    }
    
    private MenuItem _okItem = new MenuItem("Save", 250, 250) 
    {
        public void run() 
        {
            accept();
        }
    }; 
    
    private MenuItem _closeItem = new MenuItem("Cancel", 260, 260) 
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
        //store values
        if(conf!=null)
        {
            for(int i=0; i<conf.getFields().size(); i++)
            {
                ConfigField f = (ConfigField) conf.getFields().elementAt(i);
                f.StoreValue();
            }
            
            ChatHelper.groupChatSendConfiguration(Datas.jid.getUsername(), chat_manager.currentConversation.name, conf);
        }
        onClose();
    }
}

