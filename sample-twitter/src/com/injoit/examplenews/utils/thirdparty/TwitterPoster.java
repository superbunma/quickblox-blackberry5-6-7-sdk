//  @ Project : DSTv TV Guide Application
//  @ File Name : TwitterPoster.java
//  @ Date : 20/09/2012
//  @ Author : Gorban Dmitriy
//

package com.injoit.examplenews.utils.thirdparty;

import com.injoit.examplenews.utils.datastorages.UserSettingsStorage;

import impl.rim.com.twitterapime.xauth.ui.BrowserContentManagerOAuthDialogWrapper;
import impl.rim.com.twitterapime.xauth.ui.BrowserFieldOAuthDialogWrapper;

import java.io.IOException;

import com.twitterapime.rest.Credential;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.Tweet;
import com.twitterapime.xauth.Token;
import com.twitterapime.xauth.ui.OAuthDialogListener;
import com.twitterapime.xauth.ui.OAuthDialogWrapper;

import com.injoit.examplenews.utils.json.me.*;
import com.injoit.examplenews.utils.thirdparty.models.TwitterTokenObject;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;

public class TwitterPoster
{
    private final String CONSUMER_KEY = "hFv99MrITk6qoQ2IeBt7cw";
    private final String CONSUMER_SECRET  = "89pmjwYLH5H57yPv6KSmem15e2KDslZx1ITg4ZFxo";
   
    private String TOKEN  = "";
    private String SECRET  = "";
    private String USER_FULL_NAME = "";
    private String USER_TWITTER_ID = "";
    private String messageToPost = "";
    
    private TwitterPoster me;
    private Token ttrToken;
    private TwitterTokenObject retrievedToken;
    private Credential ttrCred;
    private UserAccountManager userAccMan;
    boolean twitterUserAuthorized;
    boolean twittPostedSuccessfully;
    
    public TwitterPoster()
    {
        me = this;
    }

    private Credential getTwitterCredential(Token _ttrToken)
    {
        try
        {
            ttrCred = new Credential(CONSUMER_KEY, CONSUMER_SECRET, _ttrToken);
            return ttrCred;
        }
        catch (IllegalArgumentException iAE)
        {
            return null;
        }
         catch (Exception ex)
        {
            return null;
        }
    };
    
    private Token getTwitterToken(TwitterTokenObject _retrievedToken)
    {
        try
        {
            ttrToken = new Token(_retrievedToken.getToken(),_retrievedToken.getSecret(),_retrievedToken.getUserId(),_retrievedToken.getUsername());
            return ttrToken;
        }
        catch (IllegalArgumentException iAE)
        {
            return null;
        }
         catch (Exception ex)
        {
            return null;
        }
    };
    
    private UserAccountManager getTwitterUserAccountManager (Credential _ttrCred)
    {
        try
        {
            userAccMan = UserAccountManager.getInstance(_ttrCred);
            return userAccMan;
        }
        catch(NullPointerException nPE)
        {
            return null;
        }
        catch(IllegalArgumentException iAE)
        {
            return null;
        }
        catch(Exception ex)
        {
            return null;
        }
    };
    
    private boolean isTwitterUserAuthorized(UserAccountManager _userAccMan)
    {
        try
        {
            twitterUserAuthorized = _userAccMan.verifyCredential();
            if (twitterUserAuthorized)
            {
                return true;
            }
            else 
            {
                return false;
            }
        }
        catch (IOException iOE)
        {
            return false;
        }
        catch (Exception ex)
        {
            return false;
        }
    };
 
    
    public boolean isAuthorized()
    {
        try
        {
            retrievedToken = UserSettingsStorage.getInstance().getUserSettings().getTwitterToken();
        }
        catch (Exception ex)
        {
            return false;
        };
        
        if (retrievedToken != null) 
        {
            ttrToken = getTwitterToken(retrievedToken);
        }
        else
        {
            return false;
        };
        
        if (ttrToken != null)
        {
            ttrCred = getTwitterCredential(ttrToken);
        }
        else
        {
            return false;
        };
        
        if (ttrCred != null)
        {
            userAccMan = getTwitterUserAccountManager(ttrCred);
        }
        else
        {
            return false;
        };
        
        if (userAccMan != null)
        {
            twitterUserAuthorized = isTwitterUserAuthorized(userAccMan);
        }
        else
        {
            return false;
        };
        
        if (twitterUserAuthorized == true)
        {
            return true;
        }
        else
        {
        }
        return false;
    };
    
    private boolean postTwitt(UserAccountManager _userAccMan, String _message)
    {
        try
        {
            Tweet uT = new Tweet(_message);
            TweetER tTR = TweetER.getInstance(_userAccMan);
            tTR.post(uT);
            return true;
        }
        catch(IllegalArgumentException iAE)
        {
            return false;
        }
        catch(SecurityException  sE)
        {
            return false;
        }
        catch(Exception  ex)
        {
            return false;
        }
    };
    
    public void saveToken(String token)
    {
        TOKEN = token;
    }
    
    public void saveSecret(String secret)
    {
        SECRET = secret;
    }
    
    public void parseSuccsessAuthMessage(String message)
    {
        try
        {
            JSONObject jsonResponse = new JSONObject(message);
            JSONObject jsonUserItem = jsonResponse.getJSONObject("user");
            USER_FULL_NAME = jsonUserItem.getString("full_name");
            USER_TWITTER_ID = jsonUserItem.getString("twitter_id");
            saveTwitterCredentials();
        }
        catch(Exception ex)
        {
        }
    };
    
    private void saveTwitterCredentials()
    {
        if( TOKEN.equals("") && SECRET.equals("") && USER_FULL_NAME.equals("") && USER_TWITTER_ID.equals("") )
        {
        }
        else
        {
            TwitterTokenObject newToken = new TwitterTokenObject(TOKEN, SECRET, USER_TWITTER_ID, USER_FULL_NAME);
            try
            {
                UserSettingsStorage.getInstance().setTwitterToken(newToken);
                
                if (messageToPost.equals(""))
                {
                }
                else
                {
                    postMessageToTwitter(messageToPost);
                }
            }
            catch(Exception ex)
            {
            }
        }
    };
    
    public void postMessageToTwitter(String message)
    {
        messageToPost = message;
        
        if(isAuthorized() == true)
        {
            if (postTwitt(userAccMan, messageToPost) == true)
            {
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Status.show("Message successfully posted to Twitter!");
                    }
                });
            }
            else
            {
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Status.show("This message already posted to Twitter!");
                    }
                });
            }
        }
        else
        {
            TwitterQBAuth tqba = new TwitterQBAuth();
            tqba.setCallBackClass(me);
            tqba.getTwitterAuth();
        }
    };
    
    public void removeTwitterCredentials()
    {
        UserSettingsStorage.getInstance().removeTwitterToken();
    }
    
}
