package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.connection.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.media.*;
import com.injoit.examplechat.jmc.containers.*;
import com.injoit.examplechat.util.*;
import com.injoit.examplechat.threads.*;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.presence.*;
import com.injoit.examplechat.jabber.subscription.*;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import java.lang.*;


public class InviteScreen extends PopupScreen implements FieldChangeListener
{
    private ChatManager chat_manager;
    private String type = "";
    private ButtonField bAccept;
    private ButtonField bDeny;

    public InviteScreen(String _type, ChatManager _chat_manager) 
    {
        super(new VerticalFieldManager(VerticalFieldManager.VERTICAL_SCROLL | VerticalFieldManager.VERTICAL_SCROLLBAR));
        
        chat_manager = _chat_manager;
        type = _type;
        
        String text = "";
        String label = "";
        String reason = "";
        if (type.equalsIgnoreCase("subscription"))
        {
            label = "Subscription request";
            text = chat_manager.currentjid.getUsername()+ " wants to subscribe your presence!";
        }
        else if (type.equalsIgnoreCase("invitation")) 
        {
            try
            {
                reason = (String)chat_manager.infopool.get("invit_reason");
                if(reason == null) reason = "";
            }
            catch(Exception ex)
            {
                reason = "";
            }
            label = "Chat Invitation";
            text = "You have been invited to " + (String)chat_manager.infopool.get("invit_room") + " from "+(String)chat_manager.infopool.get("invit_from");
            if(reason.length()>0)
            {
                text += "\nMessage: " + reason;
            }
        }
        
        bAccept = new ButtonField("Accept",Field.FIELD_HCENTER|DrawStyle.HCENTER);
        bAccept.setChangeListener(this);
        
        bDeny = new ButtonField("Deny",Field.FIELD_HCENTER|DrawStyle.HCENTER);
        bDeny.setChangeListener(this);
        
        add(new LabelField(label));
        add(new SeparatorField());
        add(new LabelField(text));
        add(bAccept);
        add(bDeny);
    }
    
    protected boolean keyChar(char character, int status, int time) 
    {
        if (character == Keypad.KEY_ESCAPE) 
        {
            if (type.equalsIgnoreCase("subscription"))
            {
                Subscribe.denySubscription(chat_manager.currentjid);
                chat_manager.setCurrentDisplay();
            }
            else if (type.equalsIgnoreCase("invitation")) 
            {
                chat_manager.internal_state = ((Integer)chat_manager.infopool.remove("invit_internal_state")).intValue();
                chat_manager.infopool.remove("invit_from");
                chat_manager.infopool.remove("invit_room");
                chat_manager.infopool.remove("invit_reason");
                chat_manager.setCurrentDisplay();
            }
            
            onClose();
            return true;
        }
        return super.keyChar(character, status, time);
    }
    
    public boolean onClose()
    {
        chat_manager.inviteScreen = null;
        if(chat_manager.onlineScreen != null)
        {
            chat_manager.onlineScreen.updateScreen();
        }
        close();
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        if(field == bAccept)
        {
            if (type.equalsIgnoreCase("subscription"))
            {
                 Subscribe.acceptSubscription(chat_manager.currentjid);   
                 chat_manager.currentjid.setPresence("subscribed");
                 chat_manager.setCurrentDisplay();
            }
            else if (type.equalsIgnoreCase("invitation")) 
            {
                Jid room = new Jid((String)chat_manager.infopool.remove("invit_room"));
                String from = (String) chat_manager.infopool.remove("invit_from");
                ChatHelper.groupExistingChatJoin(Datas.jid.getUsername(), room.getUsername(), room.getServername());
            }
        }
        else if(field == bDeny)
        {
            if (type.equalsIgnoreCase("subscription"))
            {
                Subscribe.denySubscription(chat_manager.currentjid);
                chat_manager.setCurrentDisplay();
            }
            else if (type.equalsIgnoreCase("invitation")) 
            {
                chat_manager.internal_state = ((Integer)chat_manager.infopool.remove("invit_internal_state")).intValue();
                chat_manager.infopool.remove("invit_from");
                chat_manager.infopool.remove("invit_room");
                chat_manager.setCurrentDisplay();
            }
        }
        onClose();
    }
}




