package com.injoit.examplechat.jabber.conversation.Configuration;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;
import net.rim.device.api.collection.util.BigVector;

import com.injoit.examplechat.xmlstreamparser.*;

public class ConfigFieldOption 
{
    private String label = "";
    private String value = "";
    
    public ConfigFieldOption(Node _node) 
    {   
        try
        {
            label = _node.getValue("label");
            value = _node.getChild("value").text;
        }
        catch(Exception ex)
        {
            System.out.println("ConfigFieldOption parsing issue " + ex.getMessage());
        }
    }
    
    public String getLabel()
    {
        return this.label;
    }
    
    public void setLabel(String value)
    {
        this.label = value;
    }
    
    public String getValue()
    {
        return this.value;
    }
    
    public void setValue(String _value)
    {
        this.value = _value;
    }
    
    public String toString()
    {
        return this.label;
    }
} 
