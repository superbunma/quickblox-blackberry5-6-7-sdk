package com.injoit.examplenews.news;

import java.util.Vector;
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

import com.injoit.examplenews.*;
import com.injoit.examplenews.utils.*;
import com.injoit.examplenews.utils.containers.*;
import com.injoit.examplenews.utils.constants.*;
import com.injoit.examplenews.utils.controls.*;
import com.injoit.examplenews.news.controls.*;
import com.injoit.examplenews.utils.datastorages.*;

public class NewsScreen extends MainScreen 
{
    private VerticalScrollManager manager;
    private ExampleNewsApp app;
    
    private Vector records = new Vector();
    private Vector fields = new Vector();
    
    private FetchThread fetchThread = new FetchThread();
    private UISyncThread syncThread = new UISyncThread();
    private ImageLoaderThread imageLoaderThread = new ImageLoaderThread();

    public NewsScreen(ExampleNewsApp _app) 
    {
        super(NO_VERTICAL_SCROLL);
        app = _app;

        getMainManager().setBackground(BackgroundFactory.createSolidBackground(Constants.MAIN_BACKGROUND_COLOR));
        
        TitleBarManager titleMan = new TitleBarManager(Constants.NEWS_SCREEN_TITLE);
        add(titleMan);
        
        manager = new VerticalScrollManager(USE_ALL_WIDTH|VERTICAL_SCROLL);
        
        add(manager);
        receiveNews();
    }

    private void receiveNews()
    {
        try
        {
            synchronized (Application.getEventLock()) 
            {
                fetchThread.start();
                syncThread.start();
            }
        }
        catch(Exception ex)
        {
            DebugStorage.getInstance().Log(0, "<NewsScreen.receiveNews> ", ex);
        }
    }
    
    private class FetchThread extends Thread {
        private final int total = 30;
                public void run() {
                        synchronized (Application.getEventLock()) {
                                manager.deleteAll();
                        }
                        ContentProvider.getInstance().fetchNews(new FetchItemListener() {
                                public boolean itemFetched(final Object newsItem,
                                    final int index) {
                                        records.addElement(newsItem);
                                        return index == total;
                                }
                        });
                        imageLoaderThread.start();
                }
    }
    
        private class ImageLoaderThread extends Thread {
                
                private boolean running = true;
                
                public void stop() {
                        running = false;
                }
                
                public void run() {
                        ContentProvider provider = ContentProvider.getInstance();
                        while (running) {
                                try {
                                        sleep(1000);
                                        while(fields.size() > 0) {
                                                NewsItemButton button = (NewsItemButton) fields.elementAt(0);
                                                String url = TextUtils.replaceAll(button.getItem().getImageUrl(), " ", "%20");
                                                
                                                byte[] image = provider.getBytes(url);
                                                button.setItemImage(image);
                                                fields.removeElement(button);
                                        }
                                        stop();
                                } catch (InterruptedException e) {
                                        stop();
                                }
                        }
                }
        }
        
        
        private class UISyncThread extends Thread {
                private boolean running = true;
                private int addedCount = 0;
                
                public void run() {
                        
                        while (running) {
                                try {
                                        if (!fetchThread.isAlive()) {
                                                running = false;
                                        }
                                        
                                        sleep(1000);
                                        while (records.size() > addedCount) {
                                                NewsItem item = (NewsItem) records.elementAt(addedCount);
                                                synchronized (Application.getEventLock()) {
                                                        NewsItemButton btn = new NewsItemButton(item);
                                                        fields.addElement(btn);
                                                        manager.add(btn);
                                                }
                                                addedCount++;
                                        }
                                } catch (InterruptedException e) {
                                        running = false;
                                }
                        }
                };
        };
        
    public void makeMenu(Menu menu, int instance) 
    {
        menu.add(refresh);
        menu.add(MenuItem.separator(100));        
        super.makeMenu(menu, instance);
    }
    
    private MenuItem refresh = new MenuItem("Refresh", 100, 10) 
    {
        public void run() 
        {
            receiveNews();
        }
    };
}
