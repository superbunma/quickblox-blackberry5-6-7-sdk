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
import com.injoit.examplechat.*;
import com.injoit.examplechat.networking.*;
import com.injoit.examplechat.utils.*;
import com.json.me.*;

import java.util.*;
import java.io.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.decor.*;
import net.rim.device.api.crypto.*;
import net.rim.device.api.collection.util.*;

public class OfflineScreen extends MainScreen implements HTTPAnswerListener, FieldChangeListener
{
    private String TOKEN = "";
    private String USER_ID = ""; 
    private final String PORT = "5222";
    private final String SERVER = "chat.quickblox.com";
    private final String APP_ID = "1069";
    private final String AUTH_KEY = "KEFW72n6nJVjVsM";
    private final String AUTH_SECRET = "eUsXvytqwjak6As";
    private final static String nonce = new Long(new Date().getTime()).toString();
    
    private OfflineScreen me;
    private VerticalFieldManager listManager = new VerticalFieldManager(Manager.USE_ALL_HEIGHT|Manager.USE_ALL_WIDTH|Manager.VERTICAL_SCROLL);

    public EditField usernameField;
    public PasswordEditField passwordField;
    
    public CheckboxField saveField;
    private ButtonField logButton;
    private ButtonField regButton;
    
    private static String jidName;
    private static String jidFull;
    private int PADDING = 6;
    private final int REQUEST_SESSION = 0; //registration: get token
    private final int REQUEST_USERS   = 1; //registration: sent credentials 
    private final int REQUEST_LOGIN   = 2; //login
    
    private final int GET  = 0;
    private final int POST = 1;
    private boolean isRegistration = false;
    private ChatManager chat_manager;
    
    private int width=0;
    private int height=0;
        
    public OfflineScreen(ChatManager _chat_manager)
    {
        super(NO_VERTICAL_SCROLL);
        me = this;
        chat_manager = _chat_manager;
        
        String username = "";
        String password = "";     
        Vector up = SavedData.getUserInfo();
        boolean isSaved = false;
        if (up != null) 
        {
            isSaved = true;
            username = (String)up.elementAt(0);
            password = (String)up.elementAt(1);
        }
        this.setTitle("QuickBlox chat");
        
        // Set the linear background.
        this.getMainManager().setBackground(
            BackgroundFactory.createLinearGradientBackground(0x0099CCFF,
            0x0099CCFF,0x00336699,0x00336699)
        );
        
        Bitmap borderField = Bitmap.getBitmapResource("border_field.png"); 
        Font font = Font.getDefault().derive(Font.BOLD);
        // Create vertical field manager       
        VerticalFieldManager verticalMan = new VerticalFieldManager(NO_VERTICAL_SCROLL);             
        verticalMan.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderField));
        // Create label window name
        LabelField labelNameWindow = new LabelField("Enter to QuikBlox chat", LabelField.FIELD_HCENTER);
        labelNameWindow.setFont(font);
        verticalMan.add(labelNameWindow);
        // Create label usernameLabel
        LabelField usernameLabel = new LabelField("Username:");
        verticalMan.add(usernameLabel);
        usernameField = new EditField(){
            protected void paint(Graphics g)
            {       
                g.setColor(Color.WHITE);
                super.paint(g);
            }
        };
        usernameField.setFont(font);
        usernameField.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderField));
        usernameField.setText(username);
        verticalMan.add(usernameField);
        verticalMan.add(new LabelField("Password:"));
        // Create PasswordEditField passwordField
        passwordField = new PasswordEditField();
        passwordField = new PasswordEditField(){
            protected void paint(Graphics g)
            {       
                g.setColor(Color.WHITE);
                super.paint(g);
            }
        };
        passwordField.setText(password);
        passwordField.setFont(font);
        passwordField.setBorder(BorderFactory.createBitmapBorder(new XYEdges(12, 12, 12, 12), borderField));
        verticalMan.add(passwordField);
        saveField = new CheckboxField("Save data", isSaved);
        verticalMan.add(saveField); 
        // Create horizontal field manager  
        HorizontalFieldManager horizontalMan = new HorizontalFieldManager(Field.USE_ALL_WIDTH | Field. USE_ALL_HEIGHT);  
        logButton = new ButtonField("Login", ButtonField.FIELD_HCENTER);
        logButton.setChangeListener(me);
        horizontalMan.add(logButton);
        regButton = new ButtonField("Register", ButtonField.FIELD_HCENTER);
        regButton.setChangeListener(me);
        horizontalMan.add(regButton);  
        verticalMan.add(horizontalMan);
        add(verticalMan);
    }
     
    public String getAppId()
    {
        return this.APP_ID;
    }
    
    public void doRequest(int type)
    {
        switch(type)
        {
            case REQUEST_SESSION:
            {
                String time = new Long(new Date().getTime()).toString();
                String timestamp = time.substring(0, time.length()-3);
                String postParam = "application_id=" + APP_ID + "&auth_key=" + AUTH_KEY + "&nonce=" + nonce  + "&timestamp=" + timestamp;
                String signature = "";
                try
                {
                    signature = hmacsha1(AUTH_SECRET, postParam);
                }
                catch(Exception ex)
                {
                    System.out.println("---------> SIGNATURE ERROR");
                    break;
                }
                
                JSONObject postObject = new JSONObject();
                try
                {
                    postObject.put("application_id", APP_ID);
                    postObject.put("auth_key", AUTH_KEY);
                    postObject.put("nonce", nonce);
                    postObject.put("timestamp", timestamp);
                    postObject.put("signature", signature);
                }
                catch(Exception ex)
                {
                }
                BigVector postData = new BigVector();
                postData.addElement(postObject.toString());
                
                DebugStorage.getInstance().Log(0, "Post params: " + postParam);
                DebugStorage.getInstance().Log(0, "Post data: " + postObject.toString());
                
                HTTPConnManager man = new HTTPConnManager(POST, "http://api.quickblox.com/session.json", postData, type, this);
                break;
            }
            case REQUEST_USERS:
            {
                JSONObject credentialsObject = new JSONObject();
                JSONObject postObject = new JSONObject();
                try
                {
                    credentialsObject.put("login", usernameField.getText());
                    credentialsObject.put("password", passwordField.getText());
                    postObject.put("user", credentialsObject);
                }
                catch(Exception ex)
                {
                }
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                postData.addElement(postObject.toString());
                
                DebugStorage.getInstance().Log(0, "Post data: " + postObject.toString());
                
                HTTPConnManager man = new HTTPConnManager(POST, "http://api.quickblox.com/users.json", postData , type, this);
                break;
            }
            case REQUEST_LOGIN:
            {
                String time = new Long(new Date().getTime()).toString();
                String timestamp = time.substring(0, time.length()-3);
                String postParam = "application_id=" + APP_ID + "&auth_key=" + AUTH_KEY + "&nonce=" + nonce  + "&timestamp=" + timestamp;
                String signature = "";
                try
                {
                    signature = hmacsha1(AUTH_SECRET, postParam);
                }
                catch(Exception ex)
                {
                    System.out.println("---------> SIGNATURE ERROR");
                    break;
                }
                
                JSONObject postObject = new JSONObject();
                try
                {
                    postObject.put("application_id", APP_ID);
                    postObject.put("auth_key", AUTH_KEY);
                    postObject.put("nonce", nonce);
                    postObject.put("timestamp", timestamp);
                    postObject.put("signature", signature);
                    postObject.put("login", usernameField.getText());
                    postObject.put("password", passwordField.getText());
                }
                catch(Exception ex)
                {
                }
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                postData.addElement(postObject.toString());
                
                DebugStorage.getInstance().Log(0, "Post params: " + postParam);
                DebugStorage.getInstance().Log(0, "Post data: " + postObject.toString());
                
                HTTPConnManager man = new HTTPConnManager(POST, "http://api.quickblox.com/login.json", postData, type, this);
                break;
            }
            default: break;
        }
    }
    
    public void ProcessAnswer(int type, String result)
    {
        switch(type)
        {
            case REQUEST_SESSION:
            {
                try
                {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject jsonSessionItem = jsonResponse.getJSONObject("session");
                    TOKEN = jsonSessionItem.getString("token");
                    
                    if(isRegistration == true)
                    {
                        doRequest(REQUEST_USERS);
                    }
                    else
                    {
                        doRequest(REQUEST_LOGIN);
                    }
                }
                catch(Exception ex)
                {
                    System.out.println("Exception: " + ex.getMessage());
                }
                break;
            }
            case REQUEST_USERS:
            {
                try
                {
                    doRequest(REQUEST_LOGIN);
                }
                catch(Exception ex)
                {
                    System.out.println("Exception: " + ex.getMessage());
                }
                break;
            }
            case REQUEST_LOGIN:
            {
                try
                {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject jsonSessionItem = jsonResponse.getJSONObject("user");
                    USER_ID = jsonSessionItem.getString("id");
                    System.out.println("USER_ID  = " + USER_ID);
                    
                    //Storing credentials
                    if(saveField.getChecked()) 
                    {
                        SavedData.setUserInfo(usernameField.getText(), passwordField.getText(), USER_ID);
                    } 
                    else 
                    {
                        SavedData.destroyUserInfo();
                    }
                    LoginToChat(usernameField.getText(), passwordField.getText(), USER_ID);
                }
                catch(Exception ex)
                {
                    DebugStorage.getInstance().Log(0, "<OfflineScreen> ProcessAnswer: ", ex);
                    System.out.println("Exception: " + ex.getMessage());
                }
                break;
            }
            default: break;
        }
    }
    
    public void ProcessError(int type, final String message)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                try 
                {
                    Status.show(message);
                    
                     //Storing credentials
                    if(saveField.getChecked()) 
                    {
                        SavedData.setUserInfo(usernameField.getText(), passwordField.getText(), "");
                    } 
                    else 
                    {
                        SavedData.destroyUserInfo();
                    }
                } 
                catch (Exception e) 
                {
                   
                }
            }
        }); 
    }
    
    
    private static String hmacsha1(String key, String message) throws CryptoTokenException, CryptoUnsupportedOperationException, IOException 
    {
        HMACKey k = new HMACKey(key.getBytes());
        HMAC hmac = new HMAC(k, new SHA1Digest());
        hmac.update(message.getBytes());
        byte[] mac = hmac.getMAC();
        
        return convertToHex(mac);
    }
    
    private static String convertToHex(byte[] data) 
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) 
        {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do 
            {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
    
    public void makeMenu(Menu menu, int instance) 
    {
        menu.add(emailItem);
        super.makeMenu(menu, instance);
    }
    

    private MenuItem emailItem = new MenuItem("Email logs", 160, 160) 
    {
        public void run() 
        {
            SupportPopupScreen scr = new SupportPopupScreen();
        }
    };
    
    public void LoginToChat(final String _jid, final String _password, final String _userid)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run() 
            {
                jidName = _jid;
                String jid = "";
                String password = "";
                String server =  SERVER;
                String customPort =  PORT;
                
                Vector up = SavedData.getUserInfo();
                if (up != null) 
                {
                    jid = (String)up.elementAt(2) + "-" + APP_ID + "-" + (String)up.elementAt(0) + "@" + SERVER;
                    jidFull = (String)up.elementAt(2) + "-" + APP_ID + "-" + (String)up.elementAt(0);
                    password = (String)up.elementAt(1);
                }
                else
                {
                    jid = _userid + "-" + APP_ID + "-" + _jid + "@" + SERVER;
                    jidFull = _userid + "-" + APP_ID + "-" + _jid;
                    password = _password;
                }
            
                        
                if (jid.equals("") || server.equals(""))
                {
                    Dialog.alert(Contents.jid_sintax_error);
                    return;
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
                    
                    Datas.jid.setMail("");
                    Datas.isSSL = false;
                    Datas.isHTTP = false;
                    Datas.avatarFile = Contents.getImage("jmcAvatar");
                    Datas.setJidAvatar(); 
                    
                    Datas.saveRecord();
                    
                    //empty roster, future retrieve offline
                    Datas.roster.clear();
                    
                    chat_manager.getGuiWaitConnect();
                    chat_manager.internal_state = chat_manager.WAIT_CONNECT;
                    
                    DebugStorage.getInstance().Log(0, "<OfflineScreen> LoginToChat: ");
                    DebugStorage.getInstance().Log(0, "<OfflineScreen> LOGIN: " + jid);
                    DebugStorage.getInstance().Log(0, "<OfflineScreen> PASSWORD: " + password);
                    
                    if (Datas.isHTTP)
                    {
                        chat_manager.cm.httpConnect(); //HTTP
                    }
                    else
                    {
                        chat_manager.cm.connect(0); //TCP
                    }
                }
                 
            }
        });
    }
    
    public static String getJidName() {
        return jidName;
    }
    
    public static String getJidFull() {
        return jidFull;
    }
    
    public void fieldChanged(Field field, int context)
    {
        if(field == logButton)
        {
            if (usernameField.getText().length() == 0 || passwordField.getText().length() == 0) 
            {
                Dialog.alert("Invalid username/password!");
                return;
            }
            if(passwordField.getText().length()<8)
            {
                Dialog.alert("Password: min 8 characters required!");
                return;
            }
            isRegistration = false;   
            doRequest(REQUEST_SESSION);
        }
        else if(field == regButton)
        {
            if (usernameField.getText().length() == 0 || passwordField.getText().length() == 0) 
            {
                Dialog.alert("Invalid username/password!");
                return;
            }
            if(passwordField.getText().length()<8)
            {
                Dialog.alert("Password: min 8 characters required!");
                return;
            }
            isRegistration = true;         
            doRequest(REQUEST_SESSION);
        }
    }
    
    public boolean onClose()
    {
        Object obj = null;
        obj = RuntimeStore.getRuntimeStore().remove(0xaabda11c5d004c17L);//DebugStorage

        close();
        return true;
    }
};

