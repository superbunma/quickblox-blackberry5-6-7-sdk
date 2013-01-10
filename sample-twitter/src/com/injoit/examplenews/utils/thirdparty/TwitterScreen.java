//  @ Project : DSTv TV Guide Application
//  @ File Name : TwitterScreen.java
//  @ Date : 20/09/2012
//  @ Author : Gorban Dmitriy
//
package com.injoit.examplenews.utils.thirdparty;

import net.rim.device.api.ui.*;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.decor.*;
import net.rim.device.api.ui.component.*;

import impl.rim.com.twitterapime.xauth.ui.BrowserContentManagerOAuthDialogWrapper;
import impl.rim.com.twitterapime.xauth.ui.BrowserFieldOAuthDialogWrapper;

import java.io.IOException;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;

import com.twitterapime.rest.Credential;
import com.twitterapime.rest.TweetER;
import com.twitterapime.rest.UserAccountManager;
import com.twitterapime.search.LimitExceededException;
import com.twitterapime.search.Tweet;
import com.twitterapime.xauth.Token;
import com.twitterapime.xauth.ui.OAuthDialogListener;
import com.twitterapime.xauth.ui.OAuthDialogWrapper;

import com.injoit.examplenews.utils.thirdparty.models.TwitterTokenObject;
import com.injoit.examplenews.utils.constants.Constants;
import com.injoit.examplenews.utils.datastorages.*;

public class TwitterScreen extends MainScreen implements OAuthDialogListener, Constants
{
    private TwitterScreen me;
    private VerticalFieldManager vman = new VerticalFieldManager(VERTICAL_SCROLL|USE_ALL_WIDTH);
    private final String CONSUMER_KEY = "hFv99MrITk6qoQ2IeBt7cw";
    private final String CONSUMER_SECRET  = "89pmjwYLH5H57yPv6KSmem15e2KDslZx1ITg4ZFxo";
    private final String CALLBACK_URL  = "https://api.quickblox.com/auth/twitter/callback";
    private Token ttrToken = null;
    private TwitterTokenObject retrievedToken = null;
    private Credential ttrCred = null;
    private UserAccountManager userAccMan = null;
    boolean twitterUserAuthorized;
    boolean twittPostedSuccessfully;
    private String publishText;
    private LabelField loadingLabel;
    
    public TwitterScreen(String _publishText) 
    {
        super(NO_HORIZONTAL_SCROLL);
        me = this;
        publishText = _publishText;
        getMainManager().setBackground(BackgroundFactory.createSolidBackground(Constants.WHITE_BACKGROUND_COLOR));  
        
        try
        {
            retrievedToken = UserSettingsStorage.getInstance().getUserSettings().getTwitterToken();
        }
        catch (Exception ex)
        {
        };
        
        if (retrievedToken != null) 
        {
            ttrToken = getTwitterToken(retrievedToken);
        }
        else
        {
            loginToTwitter();
        };
        
        if (ttrToken != null)
        {
            ttrCred = getTwitterCredential(ttrToken);
        }
        
        if (ttrCred != null)
        {
            userAccMan = getTwitterUserAccountManager(ttrCred);
        }
        
        if (userAccMan != null)
        {
            twitterUserAuthorized = isTwitterUserAuthorized(userAccMan);
        }
        
        if (twitterUserAuthorized == true)
        {
            twittPostedSuccessfully = postTwitt(userAccMan);
            if (twittPostedSuccessfully == true)
            {
                Status.show("Message successfully posted to Twitter!");
                UiApplication.getUiApplication().popScreen(me);
            }
            else
            {
                Status.show("This message already posted to Twitter!");
                UiApplication.getUiApplication().popScreen(me);
            }
        }
        else
        {
            loginToTwitter();
        };
    };
    
    public void onAuthorize(Token _token)
    {
        try
        {
            TwitterTokenObject tTO = new TwitterTokenObject(_token.getToken(),_token.getSecret(),_token.getUserId(),_token.getUsername());
            UserSettingsStorage.getInstance().setTwitterToken(tTO);
        }
        catch (Exception ex)
        {
            System.out.println("<tTO> Exception" + ex);
        }
        if (_token != null)
        {
            ttrCred = getTwitterCredential(_token);
        }
        
        if (ttrCred != null)
        {
            userAccMan = getTwitterUserAccountManager(ttrCred);
        }
        
        if (userAccMan != null)
        {
            twitterUserAuthorized = isTwitterUserAuthorized(userAccMan);
        }
        
        if (twitterUserAuthorized == true)
        {
            twittPostedSuccessfully = postTwitt(userAccMan);
            if (twittPostedSuccessfully == true)
            {
                Status.show("Message successfully posted to Twitter!");
                UiApplication.getUiApplication().popScreen(me);
            }
            else
            {
                Status.show("This message already posted to Twitter!");
                UiApplication.getUiApplication().popScreen(me);
            }
        }
        else
        {
            Status.show("Twitter user not authorized!");
            UiApplication.getUiApplication().popScreen(me);
        };
    };

    public void onAccessDenied(String message)
    {   
        Status.show("Twitter access denied");
        UiApplication.getUiApplication().popScreen(me);
    };

    public void onFail(String error, String message)
    {
        Status.show("Twitter error" + message);
        UiApplication.getUiApplication().popScreen(me);
    };
    
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
    
    private boolean postTwitt(UserAccountManager _userAccMan)
    {
        try
        {
            Tweet uT = new Tweet(publishText);
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
    
    private void loginToTwitter()
    {
        BrowserField myBrowserField = new BrowserField();
        add(myBrowserField);       
        OAuthDialogWrapper loginWrapper = new BrowserFieldOAuthDialogWrapper( myBrowserField,CONSUMER_KEY, CONSUMER_SECRET,CALLBACK_URL,me);
        loginWrapper.login();
    };  
}
