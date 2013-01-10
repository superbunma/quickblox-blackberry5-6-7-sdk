package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.util.*;
import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.containers.*;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.presence.*;

import net.rim.device.api.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.Graphics;

public class ChangeStatusMessageScreen extends PopupScreen implements FieldChangeListener
{
    private ChangeStatusMessageScreen me;
    private ChatManager chat_manager;
    private EditField textBar;
    private ButtonField bOk;
    
    public ChangeStatusMessageScreen(ChatManager _chat_manager)
    {
        super(new VerticalFieldManager(Manager.VERTICAL_SCROLL), DEFAULT_MENU|DEFAULT_CLOSE);
        me = this;
        chat_manager = _chat_manager;
        
        try
        {
            VerticalFieldManager man = new VerticalFieldManager(); 
            LabelField title = new LabelField("Status message:");
            
            textBar = new EditField("", Datas.jid.status_message, 64, 0);
            bOk = new ButtonField("Ok", Field.FIELD_HCENTER|DrawStyle.HCENTER);
            bOk.setChangeListener(this);
            bOk.setEditable(true);
            
            man.add(title);
            man.add(new SeparatorField());
            man.add(textBar);
            man.add(bOk);
            add(man);
        }
        catch(Exception ex)
        {
        }
        
        // Show popup screen
        UiApplication.getUiApplication().pushModalScreen(this);
    }    
    
    protected void makeMenu(Menu menu, int instance)
    {
        if (instance == Menu.INSTANCE_DEFAULT)
        {
            menu.add(_changeItem);
            menu.add(_closeItem);
        }
    }
    
    private MenuItem _closeItem = new MenuItem("Close", 260, 260) 
    {
        public void run() 
        {
            onClose();
        }
    }; 
    
    private MenuItem _changeItem = new MenuItem("Change", 250, 250) 
    {
        public void run() 
        {
            change();
            onClose();
        }
    }; 
    
    public boolean onClose()
    {
        try
        {
           UiApplication.getUiApplication().popScreen(this);
        }
        catch(Exception ex)
        {
        }
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        change();
        onClose();
    }
    
    private void change()
    {
        String message = textBar.getText();
        Presence.changePresence(Datas.jid.getPresence(), message);            
        Datas.jid.setPresence(Datas.jid.getPresence(), message);
        chat_manager.getGuiOnlineMenu();
    }
}

