//  @ Project : DSTv TV Guide Application
//  @ File Name : UserSettings.java
//  @ Date : 28/06/2012
//  @ Author : Vladimir Slatvinskyi
//

package com.injoit.examplenews.utils.models;

import net.rim.device.api.util.Persistable;

import com.injoit.examplenews.utils.thirdparty.models.*;

public class UserSettings implements Persistable
{
    private TwitterTokenObject twitterToken = null;
    private String chatChannelName = "";
    private boolean isFacebookUsed = false;

    public  UserSettings()
    {
        
    }
    
    public void setTwitterToken(TwitterTokenObject _twitterToken)
    {
        this.twitterToken = _twitterToken;
    }
    
    public TwitterTokenObject getTwitterToken()
    {
        return twitterToken;
    }
    
    public void removeTwitterToken()
    {
        this.twitterToken = null;
    }
    
    public String getChatChannelName()
    {
        return chatChannelName;
    }
    
    public void setChatChannelName(String _chatChannelName)
    {
        this.chatChannelName = _chatChannelName;
    }
    
    public boolean getFacebookUsedStatus()
    {
        return isFacebookUsed;
    }
    
    public void setFacebookUsedStatus(boolean _isFacebookUsed)
    {
        this.isFacebookUsed = _isFacebookUsed;
    }
    
    
}
