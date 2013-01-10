/*
 * OptionsScreen.java
 * @created 21.11.2012
 * @author Fedunets Sergey
 */

package com.injoit.examplenews.share;

import java.util.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.decor.*;
import net.rim.device.api.ui.image.*;

import com.injoit.examplenews.*;
import com.injoit.examplenews.news.*;
import com.injoit.examplenews.utils.thirdparty.*;
import com.injoit.examplenews.utils.datastorages.OptionStorage;
import com.injoit.examplenews.utils.controls.*;
import com.injoit.examplenews.utils.constants.*;
import com.injoit.examplenews.utils.models.*;

public class OptionsScreen extends MainScreen  implements FieldChangeListener//, Constants
{
    private OptionsScreen me;
    private NewsDetailScreen app;
    private ObjectChoiceField tchSelector;
    private Option tchOption;
    
    private boolean authomaticChangeDefaultChoise = false;
    private boolean authomaticChangeDefaultFBChoise = false;
    
    public OptionsScreen(NewsDetailScreen _app)
    {
        super(NO_VERTICAL_SCROLL);
        app = _app;
        me = this;
             
        getMainManager().setBackground(BackgroundFactory.createSolidBackground(0x2f2f2f));
        TitleBarManager titleMan = new TitleBarManager("Options");
        add(titleMan);
        
        //Twitter Login/ Logout Options
        String[] TWITTER_SETTINGS = new String[]{"Login", "Logout" };
        tchSelector = new ObjectChoiceField("Twitter:", TWITTER_SETTINGS, TWITTER_SETTINGS)
        {
            public void paint(Graphics g)
            {
                g.setColor(0xffffff);
                super.paint(g);
            }
        };
        getTwitterStatus();
        tchSelector.setMargin(0,5,0,5);
        tchSelector.setChangeListener(this);
        add(tchSelector);
    }
    
    public boolean onSavePrompt() 
    {
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        
        if(field == tchSelector)
        {
            if (tchSelector.getSelectedIndex() == 0)//login
            {
                TwitterQBAuth tqba = new TwitterQBAuth();
                tqba.getTwitterAuth();
            }
            else//logout
            {
                TwitterPoster tp = new TwitterPoster();
                tp.removeTwitterCredentials();
            }
        }
    }
    
    public boolean onClose()
    {
        close();
        return true;
    };
    
    private void getTwitterStatus()
    {
        TwitterPoster tp = new TwitterPoster();
        if(tp.isAuthorized() == true)
        {
            tchSelector.setSelectedIndex(0);//login
        }
        else
        {
            tchSelector.setSelectedIndex(1);//login
        }
    };
}

