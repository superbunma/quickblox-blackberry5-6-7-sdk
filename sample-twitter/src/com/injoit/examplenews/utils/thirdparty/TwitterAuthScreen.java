//  @ Project : DSTv TV Guide Application
//  @ File Name : TwitterScreen1.java
//  @ Date : 20/11/2012
//  @ Author : Dmytro Horban
//

package com.injoit.examplenews.utils.thirdparty;

import java.util.*;
import java.io.*;
import javax.microedition.io.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.decor.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.browser.field2.*;
import net.rim.device.api.browser.field2.debug.BrowserFieldDebugger;
import net.rim.device.api.io.http.*;
import org.w3c.dom.*;
import org.w3c.dom.html2.*;

import com.injoit.examplenews.utils.*;
import com.injoit.examplenews.utils.constants.*;
import com.injoit.examplenews.utils.thirdparty.TwitterPoster;

public class TwitterAuthScreen extends MainScreen implements Constants
{
    private TwitterAuthScreen me;
    private BrowserField browserField;
    private ProtocolController protocolController;
    private BrowserFieldErrorHandler browserFieldErrorHandler;
    private BrowserFieldConfig browserFieldConfig = new BrowserFieldConfig();
    private String twitterCookie  = "";
    private String twittwerAuthURL  = "";
    private TwitterPoster twitterPoster = null;
    private boolean tokenRetrieved = false;
    private boolean secretRetrieved = false;
    private boolean succsessAuthMessage = false;
    
    public TwitterAuthScreen(String authURL, String browserCookie)
    {
        super(VERTICAL_SCROLL);
        me = this;
        getMainManager().setBackground(BackgroundFactory.createSolidBackground(Constants.MAIN_BACKGROUND_COLOR));
       
        twitterCookie = browserCookie;
        twittwerAuthURL = authURL;
        
        UiApplication.getUiApplication().invokeLater(new Runnable()
        {
            public void run()
            {
                browserField = new BrowserField(browserFieldConfig);
                protocolController = new ProtocolController(browserField)
                {
                    public void handleNavigationRequest(final BrowserFieldRequest request) throws Exception
                    {
                        try
                        {
                            HttpHeaders myHttpHeaders = request.getHeaders();
                            for(int mm=0; mm<myHttpHeaders.size(); ++mm)
                            {
                                String mykey   = myHttpHeaders.getPropertyKey(mm);
                                String myvalue = myHttpHeaders.getPropertyValue(mm);
                            }
                            if(twitterCookie != null && !twitterCookie.equalsIgnoreCase(""))
                            {
                                myHttpHeaders.addProperty("Cookie",twitterCookie);
                            }
                            BrowserFieldRequest mybfr = new BrowserFieldRequest(request.getURL(), request.getPostData(), myHttpHeaders);
                            super.handleNavigationRequest(mybfr);
                        }
                        catch (Throwable e)
                        {
                            ;
                        }
                    }
                };
                browserFieldConfig.setProperty(BrowserFieldConfig.CONTROLLER, protocolController);
                
                browserFieldErrorHandler = new BrowserFieldErrorHandler(browserField)
                {
                    public void displayContentError(String url, InputConnection connection, Throwable t)
                    {
                        InputConnection inputConn = (InputConnection) connection;
                        String dafaultResult = "";
                        try
                        {
                            InputStream is = inputConn.openInputStream();
                            byte[] data = net.rim.device.api.io.IOUtilities.streamToBytes(is);
                            dafaultResult = new String(data);

                            if (! dafaultResult.equals(""))
                            {
                                twitterPoster.parseSuccsessAuthMessage(dafaultResult);
                                succsessAuthMessage = true;
                                checkAllAuthParams();
                            }
                            else
                            {
                                succsessAuthMessage = false;
                                checkAllAuthParams();
                            }
                        }
                        catch (Exception ex)
                        {
                        }
                    }
                };
                browserFieldConfig.setProperty(BrowserFieldConfig.ERROR_HANDLER, browserFieldErrorHandler);
                
                Mybdebug bdebug = new Mybdebug();
                browserField.setDebugger(bdebug);
                browserField.requestContent(twittwerAuthURL);
                add(browserField);                  
            }
        });
    };
    
    public void setCallBackClass(TwitterPoster _twitterPoster)
    {
        twitterPoster = _twitterPoster;
    }
    
    public class Mybdebug extends BrowserFieldDebugger
    {
        public void notifyHttpTraffic(HttpConnection connection, Hashtable requestHeaders)
        {
            if (twitterPoster == null)
            {
                twitterPoster = new TwitterPoster();
            }
            try
            {
                for(int j=0; j<10; ++j)
                {
                    String myHeader = connection.getHeaderFieldKey(j);
                    String myValue  = connection.getHeaderField(j);
                    if(myHeader.equalsIgnoreCase("Set-Cookie"))
                    {
                        twitterCookie = myValue;
                        if(twitterCookie == null && !twitterCookie.equalsIgnoreCase(""))
                        {
                        }
                        else
                        {
                            if(twitterCookie.indexOf("; Path")>0)
                            {
                                twitterCookie = twitterCookie.substring(0,twitterCookie.indexOf("; Path"));
                            }
                        }
                    }
                    
                    if(myHeader.equalsIgnoreCase("Social_Provider_Token"))
                    {
                        final String twitterToken = myValue;
                        if(twitterToken == null && !twitterToken.equalsIgnoreCase(""))
                        {
                            tokenRetrieved = false;
                        }
                        else
                        {
                            tokenRetrieved = true;
                            twitterPoster.saveToken(twitterToken);
                        }
                    }
                    
                    if(myHeader.equalsIgnoreCase("Social_Provider_Secret"))
                    {
                        final String twitterSecret = myValue;
                        if(twitterSecret == null && !twitterSecret.equalsIgnoreCase(""))
                        {
                            secretRetrieved = false;
                        }
                        else
                        {
                            secretRetrieved = true;
                            twitterPoster.saveSecret(twitterSecret);
                        }
                    }
                }
            }
            catch(Exception ex)
            {
            }
        }
    };
   
  public void checkAllAuthParams()
    {
        if ((tokenRetrieved == true) && (secretRetrieved == true) && (succsessAuthMessage == true))
        {
            showStatusMessage("Successfully authorized");
        }
        else
        {
            showStatusMessage("Authoriation failed");
        }
    };
    
    private void showStatusMessage(String statusMessage)
    {
        final String statusMessageText = statusMessage;
        UiApplication.getUiApplication().invokeLater(new Runnable()
        {
            public void run()
            {
                Status.show(statusMessageText);
                UiApplication.getUiApplication().popScreen(me);
            }
        });
    };  
}

