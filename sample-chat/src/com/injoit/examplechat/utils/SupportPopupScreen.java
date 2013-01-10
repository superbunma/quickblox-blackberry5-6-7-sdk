package com.injoit.examplechat.utils;

import com.injoit.examplechat.util.*;

import net.rim.device.api.servicebook.*;
import net.rim.device.api.synchronization.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.Graphics;

public class SupportPopupScreen extends PopupScreen implements FieldChangeListener
{
    private AutoTextEditField addressField;
    private AutoTextEditField ccField; 
    
    public SupportPopupScreen()
    {
        super(new VerticalFieldManager(Manager.VERTICAL_SCROLL), DEFAULT_MENU|DEFAULT_CLOSE);
      
        try
        {
            // Add screen caption  
            LabelField title = new LabelField("Email logs");
            this.add(title);
            this.add(new SeparatorField());
            
            LabelField prompt = new LabelField("Enter email address:")
            {
                protected void paint(Graphics g)
                {  
                    g.setColor(0x2869b1);
                    super.paint(g);
                }
            };
            this.add(prompt);
            
            addressField =  new AutoTextEditField("To:  ", "vladimir.slatvinskiy@injoit.com"); 

            this.add(addressField);
            
            ccField =  new AutoTextEditField("Cc:  ", "");   
            this.add(ccField);
            
            ButtonField bsend = new ButtonField("Send", ButtonField.CONSUME_CLICK|Field.FIELD_HCENTER|DrawStyle.HCENTER);
            this.add(bsend);
            bsend.setChangeListener(this);
        }
        catch(Exception ex)
        {
            DebugStorage.getInstance().Log(0, "<SupportPopupScreen> error ", ex);
        }
        
        // Show popup screen
        UiApplication.getUiApplication().pushModalScreen(this);
    }    
    
    protected void makeMenu(Menu menu, int instance)
    {
        if (instance == Menu.INSTANCE_DEFAULT)
        {
            menu.add(_sendItem);
            menu.addSeparator();
            menu.add(_closeItem);
        }
    }
    
    public MenuItem _closeItem = new MenuItem(null, 0, 200000, 100)
    {
        public void run()
        {
            onClose();
        }

        public String toString()
        {
            return "Close";
        }
    };
    
     public MenuItem _sendItem = new MenuItem(null, 0, 2, 10)
    {
        public void run()
        {
            Send();
        }

        public String toString()
        {
            return "Send logs";
        }
    };
    
    public boolean onClose()
    {
        try
        {

           UiApplication.getUiApplication().popScreen(this);
           
        }
        catch(Exception ex)
        {
            DebugStorage.getInstance().Log(0, "<SupportPopupScreen.onClose()> error ", ex);
        }
        return true;
    }
    
    public boolean Send()
    {
        try
        {
            final String address = addressField.getText();
            final String ccaddress = ccField.getText();
         
            Util.sendSupportEmail(address,ccaddress );   
            onClose();
        }
        catch(Exception ex)
        {
            DebugStorage.getInstance().Log(0, "<SupportPopupScreen.Send()> error ", ex);
        }
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        if(field instanceof ButtonField)
        {
            Send();
        }
    }
}
