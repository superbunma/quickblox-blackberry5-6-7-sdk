//  @ Project : DSTv TV Guide Application
//  @ File Name : SharePopupScreen.java
//  @ Date : 10/06/2012
//  @ Author : Vladimir Slatvinskyi
//

package com.injoit.examplenews.share;

import net.rim.device.api.system.Characters;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.blackberry.api.mail.Message;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

import com.injoit.examplenews.*;
import com.injoit.examplenews.utils.*;
import com.injoit.examplenews.utils.datastorages.DebugStorage;
import com.injoit.examplenews.utils.containers.ListStyleButtonSet;
import com.injoit.examplenews.share.*;

public class SharePopupScreen extends PopupScreen implements FieldChangeListener
{
    private ShareListener listener;
    private Object object;
    private ShareButtonField bTwitter;
    private ShareButtonField bSome;

    private boolean dialogResult = true;
    
    public SharePopupScreen(ShareListener _listener, Object _object)
    {
        super(new VerticalFieldManager(Manager.VERTICAL_SCROLL), DEFAULT_MENU|DEFAULT_CLOSE);
        listener = _listener;
        object = _object;
        
        setBackground(BackgroundFactory.createSolidTransparentBackground(0x000000, 0));
        setBorder(BorderFactory.createSimpleBorder(new XYEdges(), Border.STYLE_TRANSPARENT));
        
        try
        {
            ListStyleButtonSet set = new ListStyleButtonSet();
            ShareButtonField bHeader = new ShareButtonField("Share via" , ShareButtonField.SHARE_HEADER);
            bTwitter = new ShareButtonField("Twitter" , ShareButtonField.SHARE_TWITTER);
            bSome = new ShareButtonField("Something...", ShareButtonField.SHARE_SOMETHING);

            bHeader.setEditable(false);
            bTwitter.setEditable(true);
            bSome.setEditable(true);
            
            bTwitter.setChangeListener(this);
            bSome.setChangeListener(this);
            
            set.add(bHeader);
            set.add(bTwitter);
            set.add(bSome);
            
            add(set);
        }
        catch(Exception ex)
        {
            DebugStorage.getInstance().Log(0, "<BaseSharePopupScreen> error ", ex);
        }
        
        // Show popup screen
        UiApplication.getUiApplication().pushModalScreen(this);
    }    
   
    public boolean onClose()
    {
        try
        {
           dialogResult = false;
           UiApplication.getUiApplication().popScreen(this);
        }
        catch(Exception ex)
        {
            DebugStorage.getInstance().Log(0, "<BaseSharePopupScreen.onClose()> error ", ex);
        }
        return true;
    }
    

    public void fieldChanged(Field field, int context)
    {
        if(field == bTwitter)
        {
            UiApplication.getUiApplication().invokeLater(new Runnable() 
            {
                public void run() 
                {
                    try
                    {
                        close();
                        listener.shareTwitter(object);
                    }
                    catch(Exception ex)
                    {
                        //do nothing here
                    }
                }
            });
        } else if(field == bSome) {
            UiApplication.getUiApplication().invokeLater(new Runnable() 
            {
                public void run() 
                {
                    try
                    {
                        close();
                        listener.shareSomething(object); 
                    }
                    catch(Exception ex)
                    {
                        //do nothing here
                    }
                }
            });
        }
    }
    
    public boolean getResult()
    {
        return this.dialogResult;
    }
} 
