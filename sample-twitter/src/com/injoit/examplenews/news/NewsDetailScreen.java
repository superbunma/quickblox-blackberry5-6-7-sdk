package com.injoit.examplenews.news;

import java.util.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*; 
import net.rim.device.api.system.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.decor.*;
import net.rim.device.api.ui.image.*;
import net.rim.device.api.util.*;
import net.rim.device.api.i18n.*;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.collection.util.BigVector;

import com.injoit.examplenews.*;
import com.injoit.examplenews.share.*;
import com.injoit.examplenews.news.controls.*;
import com.injoit.examplenews.news.NewsItem;
import com.injoit.examplenews.utils.constants.*;
import com.injoit.examplenews.utils.controls.*;
import com.injoit.examplenews.utils.*;
import com.injoit.examplenews.utils.datastorages.*;
import com.injoit.examplenews.utils.thirdparty.*;

public class NewsDetailScreen extends MainScreen implements FieldChangeListener, ShareListener {
        private NewsDetailScreen me;
        private NewsScreen parent;
        private NewsItem item ;
        private boolean isPushedFromFavoritesScreen = false;
        
        public NewsDetailScreen(NewsScreen _parent, NewsItem _newsItem, Bitmap newsImage) 
        {
            super(NO_VERTICAL_SCROLL);
            parent = _parent;
            item = _newsItem;
            me = this;
            
            getMainManager().setBackground(BackgroundFactory.createSolidBackground(Constants.MAIN_BACKGROUND_COLOR));  
            
            TitleBarManager titleMan = new TitleBarManager(Constants.NEWS_SCREEN_TITLE);
            add(titleMan);
        
                VerticalFieldManager fieldManager = new VerticalFieldManager(VerticalFieldManager.VERTICAL_SCROLL);
                NewsDetailHeader header = new NewsDetailHeader(item, newsImage);
                header.setChangeListener(me);
                header.setEditable(true);
                fieldManager.add(header);
                
                LabelField nbf = new LabelField(item.getBody())
                {
                    public void paint(Graphics g)
                    {  
                        g.setColor(0xffffff);
                        super.paint(g);
                    } 
                    
                    public boolean isFocusable()
                    {
                        return isEditable();
                    }
                    
                    protected void drawFocus(Graphics g, boolean on )
                    {  
                        paint(g);
                    }   
                };
                
                nbf.setFont(Utils.getFont(6));
                nbf.setPadding(5,5,5,5);
                nbf.setBackground(BackgroundFactory.createSolidBackground(Constants.MAIN_BACKGROUND_COLOR));
                nbf.setEditable(true);
                fieldManager.add(nbf);
                fieldManager.add(new NullField());
                add(fieldManager);
        }
    
    public void makeMenu(Menu menu, int instance) 
    {
        menu.add(shareItem);
        menu.add(optMenuItem);
        super.makeMenu(menu, instance);
    }
   
    private MenuItem shareItem = new MenuItem("Share", 82, 82) 
    {
        public void run() 
        {
            shareProcessing();
        }
    };
   
    private MenuItem optMenuItem = new MenuItem("Twitter options", 550, 550)
    {
        public void run()
        {
                OptionsScreen oScreen = new OptionsScreen(me);
                UiApplication.getUiApplication().pushScreen(oScreen);
        }
    };
    
    private void shareProcessing()
    {
        new SharePopupScreen(me, item);
    }
      
    public void fieldChanged(Field field, int context)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                shareProcessing();
            }
        });
    }
    
    protected boolean keyChar(char character, int status, int time) {
            if (character == Characters.ENTER) {
                    clickButton();
                    return true;
            }
            return super.keyChar(character, status, time);
    };

    protected boolean navigationClick(int status, int time) {
            clickButton();
            return true;
    };

    protected boolean trackwheelClick(int status, int time) {
            clickButton();
            return true;
    };

    protected boolean invokeAction(int action) {
            switch (action) {
            case ACTION_INVOKE: {
                    clickButton();
                    return true;
            }
            }
            return super.invokeAction(action);
    };
    
    public void shareTwitter(Object object)
    {
        TwitterPoster tp = new TwitterPoster();
        tp.postMessageToTwitter(item.getTitle());
    }
    
    public void shareSomething(Object object)
    {
        //
    }

    public void clickButton() {
        shareProcessing();
    };
}
