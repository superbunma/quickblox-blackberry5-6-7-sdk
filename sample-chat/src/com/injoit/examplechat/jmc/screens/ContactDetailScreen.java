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
import com.injoit.examplechat.me.regexp.RE;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.decor.*;
import java.util.Vector;

public class ContactDetailScreen extends MainScreen implements FieldChangeListener
{
    private ContactDetailScreen me;
    private ChatManager chat_manager;
    
    private EditField jid;
    private EditField group;
    private EditField phone;
    
    private ButtonField bOk;
    private ButtonField bCancel;
    
    String name = "New Contact";
    final String current_Jid = "username@chat.quickblox.com";
    
    public ContactDetailScreen(ChatManager _chat_manager)
    {
        super(NO_VERTICAL_SCROLL);
        me = this;
        chat_manager = _chat_manager;
        this.getMainManager().setBackground(
            BackgroundFactory.createLinearGradientBackground(0x0099CCFF,
            0x0099CCFF,0x00336699,0x00336699)
        );
        
        VerticalFieldManager vman = new VerticalFieldManager();
        
        String group_Jid = "";
        String phone_num = "";
        String jname = "";
        
        if (chat_manager.currentjid != null) 
        {
            name = chat_manager.currentjid.getUsername();
            jname = chat_manager.currentjid.getFullJid();
            group_Jid = chat_manager.currentjid.group;
            phone_num = chat_manager.currentjid.phone;
        }
        else
        {
            jname = current_Jid;
        }
        
        jid = new EditField("JID: ", jname, 64, 0);
        jid.setFocusListener(new FocusChangeListener() 
                            {                  
                               public void focusChanged(Field field, int eventType) 
                               {
                                  if(eventType == FOCUS_GAINED)
                                  {
                                      if(jid.getText().length()>0 && jid.getText().equals(current_Jid)==true)
                                      {
                                          jid.setText(current_Jid.substring(8));
                                         //jid.clear(0);
                                      }
                                  }
                                  else if(eventType == FOCUS_LOST)
                                  {
                                      if(jid.getText().length()==0 || jid.getText().indexOf("@")==0)
                                      {
                                         jid.setText(current_Jid);
                                      }
                                  }
                              }
                            });
        group = new EditField("Group: ", group_Jid, 32, 0);
        phone = new EditField("Phone: ", phone_num, 32, 0);
        
        bOk = new ButtonField("Accept", Field.FIELD_HCENTER|DrawStyle.HCENTER);
        bOk.setChangeListener(this);
        bOk.setEditable(true);
        
        bCancel = new ButtonField("Cancel", Field.FIELD_HCENTER|DrawStyle.HCENTER);
        bCancel.setChangeListener(this);
        bCancel.setEditable(true);
        
        vman.add(jid);
        vman.add(group);
        vman.add(phone);
        vman.add(bOk);
        vman.add(bCancel);
                                        
        add(vman);
        bOk.setFocus();
    }
    
   protected void makeMenu(Menu menu, int instance)
    {
        if (instance == Menu.INSTANCE_DEFAULT)
        {
            menu.add(_acceptItem);
            menu.add(_closeItem);
        }
    }
    
    private MenuItem _acceptItem = new MenuItem("Accept", 250, 250) 
    {
        public void run() 
        {
            accept();
            onClose();
        }
    }; 
    
    private MenuItem _closeItem = new MenuItem("Close", 260, 260) 
    {
        public void run() 
        {
            onClose();
        }
    }; 
    
    public boolean onSavePrompt() 
    {
        return true;
    }
    
    public boolean onClose()
    {
        close();
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        if(field == bOk)
        {
            String pattern = "([0-9]{1,})-" + chat_manager.offlineScreen.getAppId() + "-([a-zA-Z0-9]{1,})" + "@chat.quickblox.com";
            RE re = new RE(pattern);
            re.match(jid.getText());
                        
            if(jid.getText().equals(current_Jid) == true || re.match(jid.getText()) == false)
            {
                Status.show("Incorrect Jid!");
            }
            else
            {
                boolean found = false;
                Vector roster = Datas.createRosterVector(true);  
                for (int i=0; i<roster.size(); i++) 
                {                            
                    Jid temp = (Jid)roster.elementAt(i);
                    if(jid.getText().toLowerCase().equals(temp.getLittleJid())==true)
                    {
                        found = true;
                        break;
                    }
                }
                
                if(found == false)
                {        
                    accept();
                    Status.show("Contact added!");
                }
                else
                {
                    Status.show("Contact already exists!");
                }
            }
        }
        else if(field == bCancel)
        {
            onClose();
        }
    }
    
    private void accept()
    {
        chat_manager.internal_state = chat_manager.ONLINE;
        
        boolean changes = false; // test if there are changes
        boolean onlyphone = false;
        boolean isNew = true;
        if (chat_manager.currentjid == null || !jid.getText().equals(chat_manager.currentjid.getFullJid()) || !group.getText().equals(chat_manager.currentjid.group) || (phone.getText() != null && !phone.getText().equals(chat_manager.currentjid.phone)))
        {
            if (chat_manager.currentjid != null && jid.getText().equals(chat_manager.currentjid.getFullJid()) && group.getText().equals(chat_manager.currentjid.group))
            {
                    onlyphone = true;//only phone number changes
            }
            changes = true; 
        }
        
        if (chat_manager.currentjid != null) 
        {
            Datas.roster.remove(chat_manager.currentjid.getLittleJid());
            isNew = false;
        }
        
        if (onlyphone) 
        {
            chat_manager.currentjid.phone = phone.getText();
            Subscribe.setPhoneNumber(chat_manager.currentjid, chat_manager.currentjid.phone);
            Datas.roster.put(chat_manager.currentjid.getLittleJid(), chat_manager.currentjid);

        }
        else if (changes) 
        {
            Jid newjid = new Jid(jid.getText());
            newjid.group = group.getText();
            newjid.phone = phone.getText();
            
            //notify to the server
            Subscribe.setNewRosterItem(newjid, isNew);
            Datas.registerRoster(newjid);
            
            if (newjid.phone != null && !newjid.phone.equals(""))
            {
                Subscribe.setPhoneNumber(newjid, newjid.phone);
            }
        } 
        
        chat_manager.getGuiOnlineMenu();
        onClose();
    }
}
