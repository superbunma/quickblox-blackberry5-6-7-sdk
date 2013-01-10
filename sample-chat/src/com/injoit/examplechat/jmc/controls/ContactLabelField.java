package com.injoit.examplechat.jmc.controls;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.container.*;
import java.util.*;
import net.rim.device.api.util.*;
import net.rim.device.api.i18n.SimpleDateFormat;

public class ContactLabelField extends LabelField 
{ 
    private int color = 0x000000; 
    private int time_color = 0x666666; 
    private Font tfont = Font.getDefault().derive(Font.PLAIN, 5, Ui.UNITS_pt); 
    private Font font = Font.getDefault().derive(Font.PLAIN, 8, Ui.UNITS_pt); 
    private String time = "";
    private Vector label = new Vector();
    
    public ContactLabelField(Object text)
    {
        super(text);
        setEditable(true);
    }
    
    public ContactLabelField(Object text, int _color)
    {
        super(text);
        setEditable(true);
        color = _color;
    }
    
    public ContactLabelField(Object text, Object _time, int _color, Font _font)
    {
        super(text);
        setEditable(true);
        color = _color;
        font = _font;
        
        label = wrap((String)text, Display.getWidth()- this.tfont.getAdvance(time + "  ") - 5, font);
        time = (String) _time;
        setEditable(false);
    }
    
    public ContactLabelField(Object text, int _color, Font _font)
    {
        super(text);
        setEditable(true);
        color = _color;
        font = _font;
    }
    
    public ContactLabelField(Object text, int offset, int lenght, long style)
    {
        super(text, offset, lenght, style);
        setEditable(true);
    }
    
    public ContactLabelField(Object text, long style)
    {
        super(text, style);
        setEditable(true);
    }
    
    public ContactLabelField()
    {
        super();
        setEditable(true);
    }
    
    public void invalidate()
    {
        super.invalidate();
    }
    
    public void paint(Graphics g)
    {
         g.setFont(getLabelFont());
         g.setColor(color);
         String out = (String) label.elementAt(0);
         if(label.size()>1) out +="...";
         g.drawText(out , 0 , 0);
         
         g.setFont(tfont);
         g.setColor(time_color);
         g.drawText(time, Display.getWidth() - this.tfont.getAdvance(time + "  ") - 5 , 0);
    }
    
    public void setColor(int _value)
    {
        this.color = _value;
    }
    
    public int getColor()
    {
        return this.color;
    }
    
    public void setLabelFont(Font _value)
    {
        this.font = _value;
    }
    
    public Font getLabelFont()
    {
        return this.font;
    }
    
    public void layout( int width, int height )
    { 
        setExtent(width, this.font.getHeight());
    } 
    
    public boolean isFocusable()
    {
        return isEditable();
    }
    
    public Vector wrap (String text, int width, Font font) 
    {
        Vector result = new Vector ();
        String remaining = text;
        while (remaining.length()>=0)
        {
            int index = getSplitIndex(remaining, width, font);
            if (index == -1)  break;
            result.addElement(remaining.substring(0,index));
            remaining = remaining.substring(index);
            if (index == 0) break;
            if(remaining.length()==0) break;
        }
        return result;
    }  
      
    private int getSplitIndex(String bigString, int width, Font font)
    {
        int index = -1;
        int lastSpace = -1;
        String smallString="";
        boolean spaceEncountered = false;
        boolean maxWidthFound = false;
    
        for (int i=0; i<bigString.length(); i++)
        {
            char current = bigString.charAt(i);
            smallString += current; 
            if (current == ' ')
            {
                lastSpace = i;
                    spaceEncountered = true;
            }
            int linewidth = font.getAdvance(smallString,0,  smallString.length()); 
            if(linewidth>width)
            {
                if (spaceEncountered) 
                {
                    index = lastSpace+1;
                }
                else 
                {
                    index = i;
                }
                maxWidthFound = true;
                break;
            }    
        }
        if (!maxWidthFound) index = bigString.length();
        return index;
    }
}
