package com.injoit.examplechat.jabber.conversation.Configuration;


import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;
import net.rim.device.api.collection.util.BigVector;
import java.util.Vector;
import com.injoit.examplechat.xmlstreamparser.*;

public class ConfigField 
{
    private String label = "";
    private String type = "";
    private String var = "";
    
    private BigVector values = new BigVector();
    private BigVector options = new BigVector();
    private Object myObject;
    
    public ConfigField(Node _node) 
    {   
        try
        {
            if(_node != null)
            {
                label = _node.getValue("label");
                type = _node.getValue("type");
                var = _node.getValue("var");
                
                Vector items = _node.getChildren();
                if(items != null && items.size() > 0) 
                { 
                    for (int i=0; i<items.size(); i++) 
                    {
                        Node item = (Node)items.elementAt(i);
                        if (item.name.equals("value")) 
                        {
                            String value = item.text;
                            if(value != null)
                            {
                                values.addElement(value);
                            }
                        }
                        else if(item.name.equals("option")) 
                        {
                            ConfigFieldOption option = new ConfigFieldOption(item);
                            options.addElement(option); 
                        }
                    }
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println("ConfigFieldparsingissue " + ex.getMessage());
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
    
    public String getType()
    {
        return this.type;
    }
    
    public void setType(String value)
    {
        this.type = value;
    }
    
    public String getVar()
    {
        return this.var;
    }
    
    public void setVar(String value)
    {
        this.var = value;
    }
    
    public BigVector getOptions()
    {
        return this.options;
    }
    
    public void setOptions(BigVector value)
    {
        this.options = value;
    }
    
    public BigVector getValues()
    {
        return this.values;
    }
    
    public void setValues(BigVector value)
    {
        this.values = value;
    }
    
    public Object getObject()
    {
        if(type.equals("hidden"))
        {
            NullField f = new NullField();
            myObject =  f;
        }
        else if(type.equals("text-single"))
        {
            EditField f = new EditField(this.label + " ", (String)values.elementAt(0));
            myObject =  f;
        }
        else if(type.equals("boolean"))
        {
            String str = (String)values.elementAt(0);
            boolean checked = str.equals("1")?true:false;
            CheckboxField f = new CheckboxField(this.label, checked);
            myObject =  f;
        }
        else if(type.equals("list-single"))
        {
            int idx = 0;
            ConfigFieldOption[] choices= new ConfigFieldOption[options.size()];
            
            for(int i=0;i< options.size(); i++)
            {
                ConfigFieldOption  option = (ConfigFieldOption)options.elementAt(i);
                choices[i] = option;
                String current = (String) values.elementAt(0);
                if(current.equals(option.getValue())==true)
                {
                    idx = i;
                }
            }
            
            //String ooo[] = {"111", "222", "333"};
            
            ObjectChoiceField f = new ObjectChoiceField(this.label, choices, idx);
            myObject =  f;
        }
        else if(type.equals("fixed"))
        {
            LabelField f = new LabelField((String)this.values.elementAt(0));
            myObject =  f;
        }
        else if(type.equals("text-private"))
        {
            PasswordEditField f = new PasswordEditField(this.label, (String)values.elementAt(0));
             myObject =  f;
        }
        else if(type.equals("list-multi"))
        {
            NullField f = new NullField();
            myObject =  f;
        }
        else if(type.equals("jid-multi"))
        {
            NullField f = new NullField();
            myObject =  f;
        }
        
        return myObject;
    }
    
    public String GetAnswerXml()
    {
        String out = "";
        if(var.equals("muc#roomconfig_roomadmins")==false)
        {
            out = "<field var='" + var + "'><value>" + (String)values.elementAt(0) + "</value></field>";
        }
        else
        {
            out =  "<field var='" + var + "'>";
            for(int i=0; i<values.size(); i++)
            {
                String val = (String) values.elementAt(i);
                out += ("<value>" + val + "</value>");
            }
            out+="</field>";
        }
        return out;
    }
    
    public void StoreValue()
    {
        if(type.equals("hidden"))
        {

        }
        else if(type.equals("text-single"))
        {
            EditField f = (EditField) myObject;
            values.removeAll();
            values.addElement(f.getText());
        }
        else if(type.equals("boolean"))
        {
            CheckboxField f = (CheckboxField) myObject;
            values.removeAll();
            if(f.getChecked() == true)
            {
                values.addElement("1");
            }
            else
            {
                 values.addElement("0");
            }
        }
        else if(type.equals("list-single"))
        {
            ObjectChoiceField f = (ObjectChoiceField) myObject;
            values.removeAll();
            ConfigFieldOption option = (ConfigFieldOption)f.getChoice(f.getSelectedIndex());
            values.addElement(option.getValue());
        }
        else if(type.equals("fixed"))
        {
            
        }
        else if(type.equals("text-private"))
        {
            PasswordEditField f =(PasswordEditField) myObject;
            values.removeAll();
            values.addElement(f.getText());
        }
        else if(type.equals("list-multi"))
        {
        }
        else if(type.equals("jid-multi"))
        {
        }
    }
} 
