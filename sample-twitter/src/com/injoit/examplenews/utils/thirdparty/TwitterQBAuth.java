package com.injoit.examplenews.utils.thirdparty;

import com.injoit.examplenews.utils.datastorages.*;

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

import com.injoit.examplenews.qblox.*;
import com.injoit.examplenews.qblox.networking.*;
import com.injoit.examplenews.utils.json.me.*;



public class TwitterQBAuth implements QBHTTPAnswerListener
{
    private String TOKEN = "";
    private String timestamp = "";
    private String BROWSER_SESSION = "";
    private String TWITTER_AUTH_URL = "";

    private final String QB_APP_ID = "341";
    private final String QB_AUTH_KEY = "nhsOHKDNqwvpuDV";
    private final String QB_AUTH_SECRET = "evPuQba3jC3Q6AX";
    private final String QB_SERVER_API = "http://api.stage.quickblox.com/";

    private final static String nonce = new Long(new Date().getTime()).toString();

    private TwitterQBAuth me;
    private TwitterPoster twitterPoster = null;
    private final int REQUEST_SESSION = 0; //get token
    private final int REQUEST_SIGN_UP_USER = 1; //registration: sign up new user
    private final int REQUEST_LOGIN  = 2; //login
    private final int POST = 1;
    private final int SAVE_BROWSER_SESSION = 11;
    private final int SAVE_TWITTER_AUTH_URL = 12;

    public TwitterQBAuth()
    {
        me = this;
        String time = new Long(new Date().getTime()).toString();
        timestamp = time.substring(0, time.length()-3);
    }

    public void setCallBackClass(TwitterPoster _twitterPoster)
    {
        twitterPoster = _twitterPoster;
    }

    public void getTwitterAuth()
    {
        doRequest(REQUEST_SESSION);
    }

    private void doRequest(int type)
    {
        switch(type)
        {
            case REQUEST_SESSION:
            {
                String time = new Long(new Date().getTime()).toString();
                String timestamp = time.substring(0, time.length()-3);
                String postParam = "application_id=" + QB_APP_ID + "&auth_key=" + QB_AUTH_KEY + "&nonce=" + nonce  + "&timestamp=" + timestamp;
                String signature = "";
                try
                {
                    signature = QBUtils.hmacsha1(QB_AUTH_SECRET, postParam);
                }
                catch(Exception ex)
                {
                    break;
                }

                JSONObject postObject = new JSONObject();
                try
                {
                    postObject.put("application_id", QB_APP_ID);
                    postObject.put("auth_key", QB_AUTH_KEY);
                    postObject.put("nonce", nonce);
                    postObject.put("timestamp", timestamp);
                    postObject.put("signature", signature);
                }
                catch(Exception ex)
                {
                }
                BigVector postData = new BigVector();
                postData.addElement(postObject.toString());

                QBHTTPConnManager man = new QBHTTPConnManager(POST, QB_SERVER_API + "session.json", postData, type, this);
                break;
            }

            case REQUEST_LOGIN:
            {
                JSONObject postObject = new JSONObject();
                try
                {
                    postObject.put("provider", "twitter");
                }
                catch(Exception ex)
                {
                }
                BigVector postData = new BigVector();
                postData.addElement(TOKEN);
                postData.addElement(postObject.toString());

                final BigVector out = postData;
                final int outType = type;
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        QBHTTPConnManager man = new QBHTTPConnManager(POST, QB_SERVER_API + "login.json", out, outType, me);
                    }
                });
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
                    doRequest(REQUEST_LOGIN);
                }
                catch(Exception ex)
                {
                }
                break;
            }
            case REQUEST_LOGIN:
            {
                final String authURL = TWITTER_AUTH_URL;
                final String cookies = BROWSER_SESSION;

                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        TwitterAuthScreen tS = new TwitterAuthScreen(authURL, cookies);
                        if(twitterPoster != null)
                        {
                            tS.setCallBackClass(twitterPoster);
                        }
                        UiApplication.getUiApplication().pushScreen(tS);
                    }
                });

                break;
            }
            case SAVE_BROWSER_SESSION:
            {
                BROWSER_SESSION = result;
                break;
            }
            case SAVE_TWITTER_AUTH_URL:
            {
                TWITTER_AUTH_URL = result;
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
                }
                catch (Exception ex)
                {
                }
            }
        });
    };
};

/**
 * Post message to Twitter
 */
private boolean postTwitt(UserAccountManager _userAccMan, String _message) {
    try {
        Tweet uT = new Tweet(_message);
        TweetER tTR = TweetER.getInstance(_userAccMan);
        tTR.post(uT);
        return true;
    } catch (IllegalArgumentException iAE) {
        return false;
    }catch (SecurityException sE) {
        return false;
    }catch (Exception ex) {
        return false;
    }
};