package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.jmc.*;
import com.injoit.examplechat.jmc.connection.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.containers.*;
import com.injoit.examplechat.jmc.media.*;
import com.injoit.examplechat.util.*;
import com.injoit.examplechat.utils.*;
import com.injoit.examplechat.threads.*;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.presence.*;
import com.injoit.examplechat.jabber.subscription.*;

import java.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.decor.*;

public class ConversationScreen extends MainScreen implements FieldChangeListener, InsertSmileListener
{
    private ConversationScreen me;
    private ChatManager chat_manager;
    private final String SYM = "Ú";
    private final char CHR = 'Ú';
   
    private static final int HPADDING = Display.getWidth() <= 320 ? 6 : 8;
    private TextField textBar;
    private VerticalScrollManager vman;
    private HorizontalFieldManager hman;
    private VerticalFieldManager messages_man;
    private VerticalFieldManager participants_man;
    private InputTextManager vfm;
    private TitleBarManager title;
    private Font font = Font.getDefault().derive(Font.PLAIN, 8, Ui.UNITS_pt); 
    private Font fontb = Font.getDefault().derive(Font.BOLD, 8, Ui.UNITS_pt); 
    private final int CHAT_COLOR_MY = 0x000000;
    private final int CHAT_COLOR_CONTACT = 0x0000ff;
    private final int CHAT_COLOR_TEXT = 0x666666;
    private Bitmap borderFieldLeft = Bitmap.getBitmapResource("left_message_bg.png");
    private Bitmap borderFieldRight = Bitmap.getBitmapResource("right_message_bg.png");
    private Bitmap imgAvaliable = Bitmap.getBitmapResource("avaliable.png");
    private Bitmap imgBusy = Bitmap.getBitmapResource("busy.png");
    
    private ButtonField inv;
    
    protected class KeyboardListener implements KeyListener
    {
        public boolean isESCPressed;
        
        private ConversationScreen screen;
        
        public KeyboardListener(ConversationScreen screen)
        {
            this.screen = screen;
        }

        public boolean keyChar(char key, int status, int time)
        {
            //Invoked when a sequence of zero or more keyDowns generates a character. 
            return false;
        }

        public boolean keyDown(int keycode, int time)
        {
            //Invoked when a key has been pressed.
            if (Keypad.key(keycode) == Keypad.KEY_MIDDLE || Keypad.key(keycode) == Keypad.KEY_ENTER)
            {
                Field f = screen.getLeafFieldWithFocus();
                if(f != null)
                {
                    if(f == textBar)
                    {
                         screen.SendMessage();
                         return true;
                    }
                }
                return false;
            }
            else
            {
                return false;
            }
        }

        public boolean keyRepeat(int keycode, int time)
        {
            // Invoked when a key has been repeated.  
            return false;
        }

        public boolean keyStatus(int keycode, int time)
        {
            //Invoked when the ALT or SHIFT status has changed.  
            return false;
        }

        public boolean keyUp(int keycode, int time)
        {
            //Invoked when a key has been released.  
            return false;
        }
    };
    
    public ConversationScreen(ChatManager _chat_manager)
    {
        this(_chat_manager, true);
    }
    
    public ConversationScreen(ChatManager _chat_manager, boolean printMessages)
    {
        super(NO_VERTICAL_SCROLL);//NO_VERTICAL_SCROLL
        me = this;
        chat_manager = _chat_manager;
        
        chat_manager.internal_state = chat_manager.CONVERSATION;
        
        KeyboardListener kt_listener = new KeyboardListener(this);
        this.addKeyListener(kt_listener);
        String out = chat_manager.getNickByJid(chat_manager.currentConversation.name);
        title = new TitleBarManager(out);
        add(title);
        //setTitle(chat_manager.currentConversation.name);
        
        ///////////////////////////////////////////////////////
        final Bitmap borderBitmap = Bitmap.getBitmapResource("border.png");
        vfm = new InputTextManager(Manager.NO_VERTICAL_SCROLL)
        {
            public void sublayout(int w, int h) 
            {
                int height = 0;
                if(getFieldCount()>0)
                {
                    height = getField(0).getHeight();
                    if(textBar.getText().length()>0)
                    {
                        height += textBar.getFont().getHeight();
                    }
                    
                    if(font.getHeight()>height)
                    {
                        height = font.getHeight();
                    }
                }
                super.sublayout(w, height); 
                
                setExtent(w, height); 
            }
        };
        vfm.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12,12,12,12), borderBitmap));
        
        textBar = new TextField()
        {
            public void paint(Graphics g)
            {
                Vector v = wrap(getText(), getWidth() , getFont());
                int ypos = 0;
                for(int i=0; i<v.size(); i++)
                {
                    String row = Util.processSmilesForDevice(v.elementAt(i).toString());
                    if(row.length()>0)
                    {
                        if(row.lastIndexOf(CHR)!=-1)
                        {
                            if(row.lastIndexOf(CHR)==row.length()-2)
                            {
                                row = row.substring(0, row.length()-2);
                                v.removeElementAt(i);
                                v.insertElementAt(row, i);
                                String newValue = "";
                                for(int j=0; j<v.size(); j++)
                                {
                                    newValue += v.elementAt(j).toString();
                                }
                                setText(newValue);
                                invalidate();
                            }
                        }
                    }
                    String next = row;
                    int p = row.indexOf(SYM);
                    if(p!=-1) 
                    {
                        int xOff = 0;
                        while(p>=0)
                        {
                            String part = next.substring(0, p);
                            g.drawText(part, xOff, ypos);
                            xOff+=getFont().getAdvance(part);
                            Bitmap smile = Contents.displayBitmap(next.substring(p, p+3));
                            if(smile != null)
                            {
                                int pad = (getFont().getAdvance(SYM+"00") - smile.getWidth())/2;
                                if(pad<0)pad = pad*(-1);
                                g.drawBitmap( xOff+pad, ypos, smile.getWidth(), smile.getHeight(), smile, 0, 0 );
                                xOff+= getFont().getAdvance(SYM + "00");
                            }
                            else
                            {
                                g.drawText(next.substring(p, p+3), xOff, ypos);
                            }
                            next = next.substring(p+3, next.length());
                            p = next.indexOf(SYM);
                        }
                        if(next.length()>0)
                        {
                             g.drawText(next, xOff, ypos);
                        }
                        ypos+=getFont().getHeight();
                    }
                    else
                    {
                        g.drawText(row, 0, ypos);
                        ypos+=getFont().getHeight();
                    }
                }
            }
        };
        textBar.setChangeListener(this);
        vfm.add(textBar);
        
        final int mHeight = Display.getHeight() - title.getPreferredHeight()  - textBar.getPreferredHeight() - vfm.getPreferredHeight() - 24;
        hman = new HorizontalFieldManager(Manager.USE_ALL_WIDTH)
        {
            protected void sublayout(int w, int h) 
            {
                if(textBar.getText().length()==0)
                {
                    super.sublayout(w, mHeight); 
                    setExtent(w, mHeight);
                }
                else
                {
                    int ad=0;
                    if(font.getAdvance(textBar.getText())>(Display.getWidth()-24) && textBar.getHeight()>=textBar.getFont().getHeight())
                    {
                        ad += textBar.getFont().getHeight();
                    }
                    int _h = Display.getHeight() - title.getPreferredHeight()  - textBar.getHeight() - vfm.getPreferredHeight() - 24 - ad;
                    super.sublayout(w, _h); 
                    setExtent(w, _h);
                }
                
                if(vman.getFieldCount()>0)
                {
                    int scroll = vman.getVirtualHeight() - vman.getVisibleHeight();
                    if(scroll>0)
                    {
                        vman.setVerticalScroll(scroll);
                    }
                }
            }

        };
        vman = new VerticalScrollManager(); 
        messages_man = new VerticalFieldManager();
        //messages_man.
        this.getMainManager().setBackground(
            BackgroundFactory.createLinearGradientBackground(0x0099CCFF,
            0x0099CCFF,0x00336699,0x00336699)
        );
        participants_man = new VerticalFieldManager();
        
        hman.add(vman);
        add(hman);
        add(vfm);

        updateScreen(printMessages);
    }
    
    public void updateScreen()
    {
        updateScreen(true);
    }
    
    public void updateScreen(final boolean printMessages)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                 try 
                {
                    String out = chat_manager.getNickByJid(chat_manager.currentConversation.name);
                    if (out.indexOf("@") != -1) {
                        title.setText(Jid.getUsername(out) + ", [" + Datas.GetRoomInfo(out) + "]");
                    } else {
                        title.setText(Jid.getNickUser(out) + ", [" + Datas.GetRoomInfo(out) + "]");
                    }
                     
                    if(vman.getFieldCount()>0)
                    {
                        vman.deleteAll();
                        messages_man.deleteAll(); 
                        participants_man.deleteAll();
                    }
                    if (chat_manager.currentConversation.isMulti) 
                    {
                        vman.add(new SeparatorField());
                        GroupChat chat = (GroupChat)Datas.multichat.get(chat_manager.currentConversation.name);
                        vman.add(new LabelField("Room Members:"));
                        
                        if(chat != null)
                        {      
                              for (int j=0; j<chat.jids.size(); j++)
                            {
                                String temp = (String)chat.jids.elementAt(j);                             
                                if (temp.equals(chat_manager.currentConversation.name))
                                {
                                    continue;
                                }
                                else
                                {
                                    temp = chat_manager.getNickByJid(temp);
                                    if (temp.indexOf('@') != -1)
                                    {
                                        participants_man.add(new MessageLabelField(Jid.getNickUser(temp.substring(0, temp.indexOf('@'))), 0x696969));
                                    }
                                    else
                                    {
                                        participants_man.add(new MessageLabelField(Jid.getNickUser(temp), 0x696969));
                                    }
                                }
                            }
                        }
                        
                        vman.add(participants_man);
                        vman.add(new SeparatorField());
                        
                        inv = new ButtonField("Invite a contact");
                        inv.setChangeListener(me);
                        vman.add(inv);   
                    }
                    
                    
                    Vector msgs = chat_manager.currentConversation.messages;
                    Message msg;
                    if(msgs != null && printMessages==true)
                    {
                        if(msgs.size()>0)
                        {
                            int  maxText = msgs.size();//all texts
                            
                            int lin = 0; //links count
                            for (int i=0; i<maxText; i++) 
                            {
                                msg = (Message) msgs.elementAt(i);
                                int j,p = -1;
                
                                String m;
                                if (chat_manager.currentConversation.isMulti)
                                {
                                    m = msg.getTextNick();
                                }
                                else
                                {
                                    m = msg.getText();
                                }
                                
                                String name = m.substring(0, m.indexOf(">"));
                                if(name.length()>0)
                                {
                                    int color = CHAT_COLOR_CONTACT;
                                    if(name.equals(Datas.jid.getUsername())==true)
                                    {
                                        color = CHAT_COLOR_MY;
                                        name = "Me:";
                                    }
                                    else
                                    {
                                        System.out.println(name);
                                        int firstLine =  name.indexOf("-");
                                        if(firstLine !=-1)
                                        {
                                            int secLine = name.indexOf('-', firstLine+1);
                                            if(secLine!=-1)
                                            {
                                                name = name.substring(secLine+1) + ":";
                                            }
                                        }
                                        if(name.length()==0)
                                        {
                                            name = m.substring(0, m.indexOf(">"));
                                        }
                                    }
                                    
                                    ContactLabelField txtName = new ContactLabelField(name, msg.getStamp(), color, font);
                            
                                    messages_man.add(txtName);
                                    m = m.substring(m.indexOf(">")+1, m.length()); 
                                      
                                    String next = Util.processSmilesForDevice(m);
                                    p = m.indexOf(SYM);
                                    if(p!=-1) 
                                    {
                                          FlowFieldManager fm = new FlowFieldManager();
                                        int xOff = 0;
                                        while(p>=0)
                                        {
                                            String part = next.substring(0, p);
                                            fm.add(new MessageLabelField(part, CHAT_COLOR_TEXT));
                                            //////////////////////////////////////////////////////////////////////////             
                                            BitmapField smile = new BitmapField(Contents.displayBitmap(next.substring(p, p+3)));
                                            smile.setPadding(0,2,0,0);
                                            fm.add(smile);
                                        
                                            next = next.substring(p+3, next.length());
                                            p = next.indexOf(SYM);
                                        }
                                        if(next.length()>0)
                                        {
                                            fm.add(new MessageLabelField(next, CHAT_COLOR_TEXT));
                                        }
                                        messages_man.add(fm); 
                                    }
                                    //////////////////////////////////
                                    else if ((j = m.indexOf("+url+")) != -1) 
                                    { //check links
                                            vman.add(txtName);
                                            String name_link = "link";
                                            if (lin == 0) 
                                            {
                                                    lin++;
                                            }
                                            else 
                                            {
                                                    name_link += lin;
                                                    lin++;
                                            }
                                            int k = m.indexOf("-url-");
                                            messages_man.add(new MessageLabelField(m.substring(0, j), CHAT_COLOR_TEXT)); 
                                            j = j + 5;
                                            
                                            String link = new String(m.substring(j, k));
                                            chat_manager.infopool.put(name_link, link);

                                            messages_man.add(new MessageLabelField(link));
                                            k = k + 5;
                                            messages_man.add(new MessageLabelField(m.substring(k, m.length()), CHAT_COLOR_TEXT));
                                    }
                                    else if (j == -1 && p == -1) 
                                    {
                                            MessageLabelField message = new MessageLabelField(m, CHAT_COLOR_TEXT );
                                            messages_man.add(message);
                                    } 
                                }
                            }
                        }
                    }
                    vman.add(messages_man);
                                    
                    if(vman.getFieldCount()>0)
                    {
                        int scroll = vman.getVirtualHeight() - vman.getVisibleHeight();
                        if(scroll>0)
                        {
                            vman.setVerticalScroll(scroll);
                        }
                    }
                }
                catch(Exception ex) 
                {
                    DebugStorage.getInstance().Log(0, "<ConversationScreen> updateScreen ", ex);
                    Datas.multichat.clear();
                    Datas.conversations.removeAllElements();
                    Datas.conversations.trimToSize();
                    Datas.server_services.removeAllElements();
                    Datas.conversations.trimToSize();
                    Datas.readRoster = false;
                    
                    chat_manager.cm.terminateStream();
                                       
                    chat_manager.onlineScreen = null;
                    chat_manager.waitScreen = null;
                    chat_manager.multichatScreen = null;
                    chat_manager.conversationScreen = null;
                    
                    chat_manager.internal_state = chat_manager.OFFLINE;
                    chat_manager.getGuiOfflineMenu();
                    me.close();
                } 
            }
        });
    }
    
    public void updateScreenMessage(final Message msg)
    {
        UiApplication.getUiApplication().invokeAndWait(new Runnable() 
        {
            public void run() 
            {
                try 
                {
                    participants_man.deleteAll();
                    GroupChat chat = (GroupChat)Datas.multichat.get(chat_manager.currentConversation.name);
                    
                    if(chat != null)
                    {
                        for (int j=0; j<chat.jids.size(); j++)
                        {
                            String temp = (String)chat.jids.elementAt(j);
                            if (temp.equals(chat_manager.currentConversation.name))
                            {
                                continue;
                            }
                            else
                            {
                                temp = chat_manager.getNickByJid(temp);
                                if (temp.indexOf('@') != -1)
                                {
                                     participants_man.add(new MessageLabelField(Jid.getNickUser(temp.substring(0, temp.indexOf('@'))), 0x696969));
                                }
                                else
                                {
                                    participants_man.add(new MessageLabelField(Jid.getNickUser(temp), 0x696969));  
                                }
                            }
                        }
                    }
                    
                    
                    int lin = 0; //links count
                    int j,p = -1;
    
                    String m;
                    if (chat_manager.currentConversation.isMulti)
                    {
                        m = msg.getTextNick();
                    }
                    else
                    {
                        m = msg.getText();
                    }
                    
                    String name = m.substring(0, m.indexOf(">"));
                    if(name.length()>0)
                    {
                        int color = CHAT_COLOR_CONTACT;
                        if(name.equals(Datas.jid.getUsername())==true)
                        {
                            color = CHAT_COLOR_MY;
                            name = "Me:";
                        }
                        else
                        {
                            System.out.println(name);
                            int firstLine =  name.indexOf("-");
                            if(firstLine !=-1)
                            {
                                int secLine = name.indexOf('-', firstLine+1);
                                if(secLine!=-1)
                                {
                                    name = name.substring(secLine+1) + ":";
                                }
                            }
                            if(name.length()==0)
                            {
                                name = m.substring(0, m.indexOf(">"));
                            }
                        }
                         ContactLabelField txtName = new ContactLabelField(name, msg.getStamp(), color, font);
                        messages_man.add(txtName);
                        m = m.substring(m.indexOf(">")+1, m.length());
                        String next = Util.processSmilesForDevice(m);
                        p = m.indexOf(SYM);
                        if(p!=-1) 
                        {
                            FlowFieldManager fm = new FlowFieldManager();
                            int xOff = 0;
                            while(p>=0)
                            {
                                String part = next.substring(0, p);
                                fm.add(new MessageLabelField(part, CHAT_COLOR_TEXT));
                                BitmapField smile = new BitmapField(Contents.displayBitmap(next.substring(p, p+3)));
                                smile.setPadding(0,2,0,0);
                                if (name.equals("Me:")) {
                                   fm.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderFieldLeft));
                                } else {
                                    fm.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderFieldRight));
                                }
                                fm.add(smile);
                            
                                next = next.substring(p+3, next.length());
                                p = next.indexOf(SYM);
                            }
                            if(next.length()>0)
                            {
                                MessageLabelField message = new MessageLabelField(next, CHAT_COLOR_TEXT);
                                if (name.equals("Me:")) {
                                   fm.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderFieldLeft));
                                } else {
                                    fm.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderFieldRight));
                                }
                                fm.add(message);
                            }
                            messages_man.add(fm); 
                        }
                        //////////////////////////////////
                        else if ((j = m.indexOf("+url+")) != -1) 
                        { //check links
                                vman.add(txtName);
                                
                                String name_link = "link";
                                if (lin == 0) 
                                {
                                        lin++;
                                }
                                else 
                                {
                                        name_link += lin;
                                        lin++;
                                }
                                int k = m.indexOf("-url-");
                                ////
                                MessageLabelField mess = new MessageLabelField(m.substring(0, j), CHAT_COLOR_TEXT);
                                    ////
                                messages_man.add(mess); 
                                j = j + 5;
                                
                                String link = new String(m.substring(j, k));
                                chat_manager.infopool.put(name_link, link);

                                messages_man.add(new MessageLabelField(link)); 
                                k = k + 5;
                                messages_man.add(new MessageLabelField(m.substring(k, m.length()), CHAT_COLOR_TEXT));
                        } 
                        else if (j == -1 && p == -1) 
                        {
                                MessageLabelField message = new MessageLabelField(m, CHAT_COLOR_TEXT );
                                if (name.equals("Me:")) {
                                    message.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderFieldLeft));
                                } else {
                                    message.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderFieldRight));
                                }
                                messages_man.add(message); 
                        }

                        if(vman.getFieldCount()>0)
                        {
                            int scroll = vman.getVirtualHeight() - vman.getVisibleHeight();
                            if(scroll>0)
                            {
                                vman.setVerticalScroll(scroll);
                            }
                        }
                    }
                }
                catch(Exception ex) 
                {
                    DebugStorage.getInstance().Log(0, "<ConversationScreen> updateScreen ", ex);
                    Datas.multichat.clear();
                    Datas.conversations.removeAllElements();
                    Datas.conversations.trimToSize();
                    Datas.server_services.removeAllElements();
                    Datas.conversations.trimToSize();
                    Datas.readRoster = false;
                    
                    chat_manager.cm.terminateStream();
                                       
                    chat_manager.onlineScreen = null;
                    chat_manager.waitScreen = null;
                    chat_manager.multichatScreen = null;
                    chat_manager.conversationScreen = null;
                    
                    chat_manager.internal_state = chat_manager.OFFLINE;
                    chat_manager.getGuiOfflineMenu();
                    me.close();
                } 
            }
        });
    }
    
    public void makeMenu(Menu menu, int instance) 
    {
        if (chat_manager.currentConversation.isMulti) 
        {
            menu.add(inviteItem);
        }
        menu.add(smileItem);
        if (chat_manager.currentConversation.isMulti) 
        {
            menu.add(exitItem);
        }
        
        if(chat_manager.currentConversation.isMulti == true)
        {
            menu.add(addContactItem);
            String af = new String(((GroupChat)chat_manager.currentConversation).userAffiliation);
            if(af.equals("owner"))
            {
                menu.add(deleteRoomItem);
                menu.add(configItem);
            }
        }
        super.makeMenu(menu, instance);
    }
    
    private MenuItem subscribeMeItem = new MenuItem("Send subscribe request", 140, 140) 
    {
        public void run() 
        {
            chat_manager.internal_state = chat_manager.CONVERSATION;
            Subscribe.requestSubscription(chat_manager.currentjid);
        }
    };
    
    private MenuItem deleteRoomItem = new MenuItem("Delete room", 360, 360) 
    {
        public void run() 
        {
            ChatHelper.deleteRoom(Datas.jid.getUsername(),  chat_manager.currentConversation.name);
            onClose();
        }
    };
    
    private MenuItem exitItem = new MenuItem("Exit from chat", 350, 350) 
    {
        public void run() 
        {
            ChatHelper.groupChatExit(Datas.jid.getUsername(),  chat_manager.currentConversation.name);
            onClose();
        }
    };
    
    private MenuItem smileItem = new MenuItem("Add Smile", 250, 250) 
    {
        public void run() 
        {
            SmilesScreen mScreen = new SmilesScreen(me);
        }
    }; 
    
    private MenuItem inviteItem = new MenuItem("Invite a contact", 260, 260) 
    {
        public void run() 
        {
            GroupChat chat = (GroupChat)Datas.multichat.get(chat_manager.currentConversation.name);
            if(chat != null)
            {
                InviteContactScreen iscr = new InviteContactScreen(chat_manager, chat.jids);
                UiApplication.getUiApplication().pushScreen(iscr);
            }
        }
    };
    
    private MenuItem addContactItem = new MenuItem("Add Room Member to contacts", 265, 265) 
    {
        public void run() 
        {
            GroupChat chat = (GroupChat)Datas.multichat.get(chat_manager.currentConversation.name);
            if(chat != null)
            {      
                AddRoomMemberToContactsScreen scr = new AddRoomMemberToContactsScreen(chat_manager, chat.jids);
                UiApplication.getUiApplication().pushScreen(scr);
            }
        }
    };
    
    private MenuItem configItem = new MenuItem("Configure room", 270, 270) 
    {
        public void run() 
        {
            ChatHelper.configRoom(Datas.jid.getUsername(),  chat_manager.currentConversation.name);
        }
    };
    
    public boolean onSavePrompt() 
    {
        return true;
    }
    
    public boolean onClose()
    {
        if(chat_manager.currentConversation instanceof GroupChat)
        {
            int cnt = ((GroupChat)chat_manager.currentConversation).jids.size();
            Datas.UpdateInfo(new RoomInfo(chat_manager.currentConversation.name, cnt));
        }
            
        chat_manager.internal_state = chat_manager.ONLINE;
        chat_manager.getGuiOnlineMenu();
        chat_manager.conversationScreen = null;
        chat_manager.multichatScreen = null;
        chat_manager.getGuiRoomList();
        close();
        return true;
    }
    
   public void fieldChanged(Field field, int context)
   {                
        if(field == inv)
        {
            GroupChat chat = (GroupChat)Datas.multichat.get(chat_manager.currentConversation.name);
            if(chat != null)
            {
                InviteContactScreen iscr = new InviteContactScreen(chat_manager, chat.jids);
                UiApplication.getUiApplication().pushScreen(iscr);
            }
        }
    }
    
    public void SendMessage()
    {
        Field f  = me.getLeafFieldWithFocus();
        if(f!=null)
        {
            if(f == textBar)
            {
                String text = textBar.getText().trim();
                if (!text.equals("")) 
                {
                    //process smileys before sending
                    String textToServer = Util.processSmilesForServer(text);
                    Message msg = new Message("", textToServer);
                    ((Chat) chat_manager.currentConversation).appendFromMe(msg);
                    
                    if(chat_manager.currentConversation.isMulti == false)
                    {
                            
                        ContactLabelField txtName = new ContactLabelField("Me:", msg.getStamp(),  CHAT_COLOR_MY, fontb);
                        messages_man.add(txtName);
                        
                        String m = Util.processSmilesForDevice(text);
                        
                        String next = m;
                        int pt = m.indexOf(SYM);
                        if(pt!=-1) 
                        {
                            FlowFieldManager fm = new FlowFieldManager();
                            int xOff = 0;
                            while(pt>=0)
                            {
                                String part = next.substring(0, pt);
                                fm.add(new MessageLabelField(part, CHAT_COLOR_TEXT));
                    
                                BitmapField smile = new BitmapField(Contents.displayBitmap(next.substring(pt, pt+3)));
                                smile.setPadding(0,2,0,0);
                                fm.add(smile);
                                
                                next = next.substring(pt+3, next.length());
                                pt = next.indexOf(SYM);
                            }
                            if(next.length()>0)
                            {
                                MessageLabelField mess = new MessageLabelField(next, CHAT_COLOR_TEXT);
                                fm.add(new MessageLabelField(next, CHAT_COLOR_TEXT));
                            }
                            messages_man.add(fm); 
                        }
                        else
                        {
                            MessageLabelField message = new MessageLabelField(m, CHAT_COLOR_TEXT);
                            message.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderFieldLeft));
                            messages_man.add(message);
                        }
                        
                        //scroll
                        if(vman.getFieldCount()>0)
                        {
                            int scroll = vman.getVirtualHeight() - vman.getVisibleHeight();
                            if(scroll>0)
                            {
                                vman.setVerticalScroll(scroll);
                            }
                        }
                        textBar.setText("");
                        vfm.update(Display.getWidth() - 24, textBar.getPreferredHeight());
                    }
                    else
                    {
                        textBar.setText("");
                    }
                }
            }
        }
    }
    
    private Vector wrap (String text, int width, Font font) 
    {
        Vector result = new Vector ();
        String remaining = text;
        while (remaining.length()>=0)
        {
            int index = getSplitIndex(remaining, width, font);
            if (index == -1)  break;
            result.addElement(remaining.substring(0,index));
            remaining = remaining.substring(index);
            if (index == 0) break;
        }
        return result;
    }  
      
    private int getSplitIndex(String bigString, int width, Font font)
    {
        int index = -1;
        int lastSpace = -1;
        String smallString="";
        boolean spaceEncountered = false;
        boolean maxWidthFound = false;
    
        for (int i=0; i<bigString.length(); i++)
        {
            char current = bigString.charAt(i);
            smallString += current; 
            if (current == ' ')
            {
                lastSpace = i;
                    spaceEncountered = true;
            }
            int linewidth = font.getAdvance(smallString,0,  smallString.length()); 
            if(linewidth>width)
            {
                if (spaceEncountered) 
                {
                    index = lastSpace+1;
                }
                else 
                {
                    index = i;
                }
                maxWidthFound = true;
                break;
            }    
        }
        if (!maxWidthFound) index = bigString.length();
        return index;
    }
    
    public void smileSelected(final String _value )
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                String message = textBar.getText();
                message += _value;
                textBar.setText(message);
            }
        });
    }
}

