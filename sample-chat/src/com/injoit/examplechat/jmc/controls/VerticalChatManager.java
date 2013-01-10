package com.injoit.examplechat.jmc.controls;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;

public class VerticalChatManager extends VerticalFieldManager 
{
    private String text;
    
    public VerticalChatManager()
    {
        super();
    }
    
    public VerticalChatManager(String _text)
    {
        super();
        text = _text;
        LabelField title = new LabelField(text);
        add(title);
    }
    
    public VerticalChatManager(String _text, long style)
    {
        super(style);
        text = _text;
        
        LabelField title = new LabelField(text);
        add(title);
    }

}
