package com.injoit.examplechat.jmc.controls;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;

public class MessageLabelField extends LabelField 
{ 
    private int color = 0x000000; 
    private Font font = Font.getDefault().derive(Font.PLAIN, 8, Ui.UNITS_pt); 
    
    public MessageLabelField(Object text)
    {
        super(text);
        setEditable(true);
    }
    
    public MessageLabelField(Object text, int _color)
    {
        super(text);
        setEditable(true);
        color = _color;
    }
    
     public MessageLabelField(Object text, int _color, Font _font)
    {
        super(text);
        setEditable(true);
        color = _color;
        font = _font;
    }
    
    public MessageLabelField(Object text, int offset, int lenght, long style)
    {
        super(text, offset, lenght, style);
        setEditable(true);
    }
    
    public MessageLabelField(Object text, long style)
    {
        super(text, style);
        setEditable(true);
    }
    
    public MessageLabelField()
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
         super.paint(g);
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
        super.layout( width, height);
    } 
    
    public boolean isFocusable()
    {
        return isEditable();
    }
}
