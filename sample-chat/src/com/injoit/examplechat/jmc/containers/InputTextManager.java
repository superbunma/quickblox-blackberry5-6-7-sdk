package com.injoit.examplechat.jmc.containers;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;

public class InputTextManager extends VerticalFieldManager
{
    public InputTextManager(long style)
    {
        super(style);
    }
    
    public void update(int w, int h)
    {
        sublayout(w, h);
    }
}
