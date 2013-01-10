package com.injoit.examplechat.jabber.conversation.Configuration;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;
import net.rim.device.api.collection.util.BigVector;
import java.util.Vector;

import com.injoit.examplechat.xmlstreamparser.*;

public class Configuration 
{
    private String title = "";
    private String instruction = "";
    private BigVector fields = new BigVector();
    
    public Configuration(Node _node) 
    {   
        if(_node != null)
        {
            Vector items = _node.getChildren();
            if(items != null && items.size() > 0) 
            { 
                for (int i = 0; i < items.size(); i++) 
                {
                    Node item = (Node)items.elementAt(i);
                    if (item.name.equals("title")) 
                    {
                        this.title = item.text;
                    }
                    else if (item.name.equals("instructions")) 
                    {
                        this.instruction = item.text;
                    }
                    else if (item.name.equals("field")) 
                    {
                        ConfigField f = new ConfigField(item);
                        this.fields.addElement(f);
                    }
                }
            }
        }
    }
    
    public String getTitle()
    {
        return this.title;
    }
    
    public void setTitle(String value)
    {
        this.title = value;
    }
    
    public String getInstruction()
    {
        return this.instruction;
    }
    
    public void setInstruction(String value)
    {
        this.instruction = value;
    }
    
    public BigVector getFields()
    {
        return this.fields;
    }
    
    public void setFields(BigVector value)
    {
        this.fields = value;
    }
} 
