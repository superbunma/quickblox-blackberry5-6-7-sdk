/*
 * ExampleNewsApp.java
 *
 * @created : 13/11/2012
 * @author : Fedunets Sergey
 */

package com.injoit.examplenews;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

import com.injoit.examplenews.news.NewsScreen;

public class ExampleNewsApp extends UiApplication{
    private ExampleNewsApp theApp;
    public static void main(String[] args) {
        // Create a new instance of the application.
        ExampleNewsApp theApp = new ExampleNewsApp(); 
        // To make the application enter the event thread and start, processing messages,
        // we invoke the enterEventDispatcher() method.
        theApp.enterEventDispatcher();
    }
    
    /**
     * Public constructor
     */
    public ExampleNewsApp() {
        pushScreen(new NewsScreen(theApp));
    } 
} 
