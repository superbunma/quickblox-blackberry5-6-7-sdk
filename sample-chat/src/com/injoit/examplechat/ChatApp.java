package com.injoit.examplechat;

import com.injoit.examplechat.jmc.*;
import net.rim.device.api.ui.*;

/**
 * The start point of the program
 */
public class ChatApp extends UiApplication
{
    public static void main(String[] args) {
        ChatApp theApp = new ChatApp();       
        theApp.enterEventDispatcher();
    }
    
    public ChatApp() {        
        ChatManager man = new ChatManager();
        man.StartChat();
    }    
}  
