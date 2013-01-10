package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.connection.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.media.*;
import com.injoit.examplechat.jmc.containers.*;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import java.lang.*;

public class WaitScreen extends PopupScreen 
{
    private AnimatedGIFField _ourAnimation = null;
    private LabelField _ourLabelField = null;
    private ChatManager chatManager;

    public WaitScreen(String text, ChatManager _chatManager) 
    {
        super(new VerticalFieldManager(VerticalFieldManager.VERTICAL_SCROLL | VerticalFieldManager.VERTICAL_SCROLLBAR));
        chatManager = _chatManager;
        
        EvenlySpacedHorizontalFieldManager spinners = new EvenlySpacedHorizontalFieldManager(FIELD_VCENTER);
        addSpinner( spinners, new ProgressAnimationField( Bitmap.getBitmapResource("spinner.png" ), 12, Field.FIELD_VCENTER ) ); 
        _ourLabelField = new LabelField(text, Field.FIELD_VCENTER);
        _ourLabelField.setPadding(0,0,0,10);
        addSpinner(spinners,_ourLabelField);
        add( spinners ); 
    }
    
    private void addSpinner( Manager parent, Field spinner )
    {   
        parent.add( spinner );  
    } 
    
    protected boolean keyChar(char character, int status, int time) 
    {
        if (character == Keypad.KEY_ESCAPE) 
        {
            chatManager.cm.disconnect(); 
            chatManager.getGuiOfflineMenu();
            chatManager.internal_state = chatManager.OFFLINE;
            chatManager.waitScreen = null;
            
            onClose();
            return true;
        }
        return super.keyChar(character, status, time);
    }
    
    public boolean onClose()
    {
        close();
        return true;
    }
}





