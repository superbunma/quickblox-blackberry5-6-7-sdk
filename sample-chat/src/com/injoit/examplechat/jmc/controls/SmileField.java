package com.injoit.examplechat.jmc.controls;

import com.injoit.examplechat.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;

public class SmileField extends BitmapField 
{ 
    private String text = "";
    public SmileField(String _text)
    {
        super();
        text = _text;
        Bitmap bitmap = Contents.displayBitmap(text);
        setBitmap(bitmap);
    }
    
    public String toString()
    {
        return text;
    }
}
