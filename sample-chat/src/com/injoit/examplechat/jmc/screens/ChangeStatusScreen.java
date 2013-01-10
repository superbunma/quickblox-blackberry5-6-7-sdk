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


public class ChangeStatusScreen extends PopupScreen implements FieldChangeListener
{
    private ChangeStatusScreen me;
    private BitmapTextButton bOnline;
    private BitmapTextButton bAway;
    private BitmapTextButton bBusy;
    private BitmapTextButton bOffline;
    private ChatManager chat_manager;
    
    public ChangeStatusScreen(ChatManager _chat_manager)
    {
        super(new VerticalFieldManager(Manager.VERTICAL_SCROLL), DEFAULT_MENU|DEFAULT_CLOSE);
        me = this;
        chat_manager = _chat_manager;
        
        try
        {
            VerticalFieldManager man = new VerticalFieldManager(); 

            bOnline  = new BitmapTextButton("online", Contents.displayBitmap("online"));
            bAway    = new BitmapTextButton("away", Contents.displayBitmap("away"));
            bBusy    = new BitmapTextButton("busy", Contents.displayBitmap("dnd"));
            bOffline = new BitmapTextButton("offline", Contents.displayBitmap("offline"));
            
            bOnline.setChangeListener(this);
            bAway.setChangeListener(this);
            bBusy.setChangeListener(this);
            bOffline.setChangeListener(this);
            
            bOnline.setPadding(0,3,0,3);
            bAway.setPadding(0,3,0,3);
            bBusy.setPadding(0,3,0,3);
            bOffline.setPadding(0,3,0,3);
            
            man.add(bOnline);
            man.add(bAway);
            man.add(bBusy);
            man.add(bOffline);
            
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
            menu.add(_selectItem);
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
    
    private MenuItem _selectItem = new MenuItem("Select", 250, 250) 
    {
        public void run() 
        {
            Field f = me.getLeafFieldWithFocus();
            if(f!=null)
            {
                select(f);
            }
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
        select(field);
    }
    
    public void select(Field field)
    {
        if(field == bOnline)
        {
            if (chat_manager.internal_state != chat_manager.OFFLINE && chat_manager.internal_state != chat_manager.WAIT_CONNECT && Datas.jid != null)
            {
                Presence.changePresence(Presence.getPresence("online"), Datas.jid.status_message);
                Datas.jid.setPresence(Presence.getPresence("online"), Datas.jid.status_message);
                chat_manager.getGuiOnlineMenu();
            }
        }
        else if(field == bAway)
        {
            if (chat_manager.internal_state != chat_manager.OFFLINE && chat_manager.internal_state != chat_manager.WAIT_CONNECT && Datas.jid != null)
            {
                Presence.changePresence(Presence.getPresence("away"), Datas.jid.status_message);
                Datas.jid.setPresence(Presence.getPresence("away"),  Datas.jid.status_message);
                chat_manager.getGuiOnlineMenu();
            }
        }
        else if(field == bBusy)
        {
            if (chat_manager.internal_state != chat_manager.OFFLINE && chat_manager.internal_state != chat_manager.WAIT_CONNECT && Datas.jid != null)
            {
                Presence.changePresence(Presence.getPresence("dnd"), Datas.jid.status_message);
                Datas.jid.setPresence(Presence.getPresence("dnd"), Datas.jid.status_message);
                chat_manager.getGuiOnlineMenu();
            }
        }
        else if(field == bOffline)
        {
            chat_manager.cm.disconnect(); 
            chat_manager.getGuiOfflineMenu();
            chat_manager.internal_state = chat_manager.OFFLINE;
            chat_manager.onlineScreen = null;
        }
        onClose();
    }
}

