package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.util.*;
import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.containers.*;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.presence.*;
import com.injoit.examplechat.jmc.screens.MultiChatScreen;

import net.rim.device.api.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.Graphics;

public class CreateRoomScreen extends PopupScreen implements FieldChangeListener
{
    private CreateRoomScreen me;
    private ChatManager chat_manager;
    
    private EditField nick;
    private EditField room;
    private EditField server;
    private ButtonField bCreate;
    private ButtonField bCancel;
    
    public CreateRoomScreen(ChatManager _chat_manager)
    {
        super(new VerticalFieldManager(Manager.VERTICAL_SCROLL), DEFAULT_MENU|DEFAULT_CLOSE);
         
        me = this;
        chat_manager = _chat_manager;
        
        try
        {
            VerticalFieldManager man = new VerticalFieldManager(); 
            
            man.add(new LabelField("Create new chat"));
            man.add(new SeparatorField());
            
            nick = new EditField("Your Nick: ", Datas.jid.getUsername(), 32, 0);
            room = new EditField("Chat Room: ", "", 32, 0);
            server = new EditField("Chat Server: ", "muc." + Datas.hostname, 32, 0);
    
            bCreate = new ButtonField("Create", Field.FIELD_HCENTER|DrawStyle.HCENTER);
            bCreate.setChangeListener(this);
            bCreate.setEditable(true);
            
            bCancel = new ButtonField("Cancel", Field.FIELD_HCENTER|DrawStyle.HCENTER);
            bCancel.setChangeListener(this);
            bCancel.setEditable(true);
            
            man.add(nick);
            man.add(room);
            man.add(server);
            man.add(bCreate);
            man.add(bCancel);
                        
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
            menu.add(_createItem);
            menu.add(_cancelItem);
        }
    }
    
    private MenuItem _createItem = new MenuItem("Create", 250, 250) 
    {
        public void run() 
        {
            create();
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
           UiApplication.getUiApplication().popScreen(this);
        }
        catch(Exception ex)
        {
        }
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        if(field == bCreate)
        {
            create();
        }
        else if (field == bCancel)
        {
            onClose();
        }
    }
    
    public void create()
    {
        if(nick.getText().length()==0)
        {
            Status.show("Nick Name should not be empty!");
        }
        else if(room.getText().length()==0)
        {
            Status.show("Room Name should not be empty!");
        }
        else if(server.getText().length()==0)
        {
            Status.show("Server Name should not be empty!");
        }
        else
        {
            ChatHelper.groupChatCreate(Jid.getNickUser(nick.getText()), room.getText(), server.getText());
            chat_manager.getGuiRoomList();         
            //new MultiChatScreen().updateScreen();  
            onClose();
        }
    }
}

