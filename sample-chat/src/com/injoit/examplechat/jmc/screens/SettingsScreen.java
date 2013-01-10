package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.connection.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.media.*;
import com.injoit.examplechat.util.*;
import com.injoit.examplechat.threads.*;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.presence.*;
import com.injoit.examplechat.jabber.subscription.*;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.decor.*;

public class SettingsScreen extends MainScreen implements FieldChangeListener
{
    private SettingsScreen me;
    private ChatManager chat_manager;
    
    private String subdomain = "";
    private EditField _jid; 
    private EditField _server;
    private PasswordEditField _password;
    private EditField _mail;
    private EditField _port;
    private ObjectChoiceField _avatar_list;
    private ObjectChoiceField _ssl_list;
    
    
    public SettingsScreen(ChatManager _chat_manager)
    {
        super(NO_VERTICAL_SCROLL);
        me = this;
        chat_manager = _chat_manager;
        
        VerticalFieldManager vman = new VerticalFieldManager();
        
        
        if (Datas.subdomain != null) subdomain = "@" + Datas.subdomain;
                
        //SSL
        _ssl_list = new ObjectChoiceField("Connection:", Contents.sslChoices);
        if (Datas.isSSL)
        {
            _ssl_list.setSelectedIndex(1);
        }
        else if (Datas.isHTTP)
        {
            _ssl_list.setSelectedIndex(2);
        }
        
        
        //AVATAR
        _avatar_list = new ObjectChoiceField("Choose your avatar: ", Contents.avatarChoices);
        if (Datas.avatarFile != null && Datas.avatarFile.indexOf("icon") == -1)  
        {
            _avatar_list.setSelectedIndex(1);
        }
        
        //Credentials
        _jid      = new EditField("Username: ", Datas.jid.getUsername() + subdomain, 64, 0);
        _server   = new EditField("Server: ", Datas.server_name, 32, 0);
        _password = new PasswordEditField("Password: ", Datas.getPassword(), 32, 0);
        _mail     = new EditField("Mail: ", Datas.jid.getMail(), 32, 0);
        _port     = new EditField("Custom Port: ", Datas.customPort, 4, 0); 
        
        vman.add(_jid);
        vman.add(_password);
        vman.add(_server);
        vman.add(_mail);
        vman.add(_ssl_list);
        vman.add(_port);
        vman.add(_avatar_list);
                                
        add(vman);
    }
    
    public void makeMenu(Menu menu, int instance) 
    {
        super.makeMenu(menu, instance);
    }
    
    public boolean onSavePrompt() 
    {
        return true;
    }
    
    public boolean onClose()
    {
        String jid = _jid.getText();
        String password = _password.getText();
        String mail = _mail.getText();
        String server =  _server.getText();
        String customPort =  _port.getText();
       
                
        if (jid.equals("") || server.equals(""))
        {
             Dialog.alert(Contents.jid_sintax_error);
             return true;
        }
        else 
        {
            if (jid.indexOf('@') != -1)
            {//subdomain exists
                    Datas.subdomain = jid.substring(jid.indexOf('@') + 1, jid.length());
                    jid = jid.substring(0, jid.indexOf('@'));
                    Datas.hostname = Datas.subdomain;
                    
            }
            else
            {
                    Datas.hostname = server;
                    Datas.subdomain = null;
            }
            Datas.jid = new Jid(jid + "@" + Datas.hostname);
            if (Datas.jid.getResource() == null) Datas.jid.setResource("JabberMix");
            Datas.setPassword(password);
            Datas.server_name = server;
            
            Datas.jid.setMail(mail);
            if (_ssl_list.getSelectedIndex() == 1) 
            {
                    Datas.isSSL = true;
                    Datas.isHTTP = false;
            }
            else if (_ssl_list.getSelectedIndex() == 2)
            {
                    Datas.isSSL = false;
                    Datas.isHTTP = true;
            }
            else 
            {
                Datas.isSSL = false;
                Datas.isHTTP = false;
            }
            
            if (customPort != null && !customPort.equals("")) 
            {
                    Datas.customPort = customPort;
                    Datas.port = Integer.parseInt(customPort);
            }
            
            if (_avatar_list.getSelectedIndex() == 0) { //AVATAR
                    Datas.avatarFile = Contents.getImage("icon");
                    
            }
            else if (_avatar_list.getSelectedIndex() == 1){
                    Datas.avatarFile = Contents.getImage("jmcAvatar");
            }
            Datas.setJidAvatar(); 
            Datas.saveRecord();
 
            //empty roster, future retrieve offline
            Datas.roster.clear();
        }
                
        close();
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        
    }
}

