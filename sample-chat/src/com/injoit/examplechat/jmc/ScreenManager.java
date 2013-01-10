package com.injoit.examplechat.jmc;

import com.injoit.examplechat.jmc.screens.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class ScreenManager 
{
    private UiApplication app;
    private MainScreen current;
    
    public ScreenManager() 
    {   
    }  
    public void setCurrent(final MainScreen _screen)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                try
                {
                    UiApplication.getUiApplication().pushScreen(_screen);
                }
                catch(Exception ex)
                {                  
                    System.out.println("setCurrent ############################# ERROR: " + ex.getMessage());
                }
            }
        });
    }
    
    public void setCurrent(final String message, final MainScreen _screen)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                try
                {
                    UiApplication.getUiApplication().pushScreen(_screen);
                }
                catch(Exception ex)
                {
                    System.out.println("setCurrent (t, s)############################# ERROR: " + ex.getMessage());
                }
            }
        });
    }
    
    public void showAlert(final String message)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                Status.show(message);
            }
        });
    }
} 
