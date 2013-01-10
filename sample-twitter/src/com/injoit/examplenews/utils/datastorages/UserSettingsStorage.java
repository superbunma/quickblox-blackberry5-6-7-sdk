//  @ Project : DSTv TV Guide Application
//  @ File Name : UserSettingsStorage.java
//  @ Date : 28.06.2012
//  @ Author : Vladimir Slatvinskyi
//

package com.injoit.examplenews.utils.datastorages;

import java.util.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.database.*;
import net.rim.device.api.io.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.collection.util.*;

import com.injoit.examplenews.utils.datastorages.UserSettingsStorage;
import com.injoit.examplenews.utils.models.*;
import com.injoit.examplenews.utils.thirdparty.models.*;

public class UserSettingsStorage
{
    public static final long STORE_KEY = 0xa1b11beb21aa95d7L;
    private static final long GUID = 0xa111d61c5d004c17L;
    
    private static UserSettingsStorage instance;
    private static UserSettings record = new UserSettings();
    private static PersistentObject  persist;
    private BigVector handlers = new BigVector();
    

    static
    {
        persist = PersistentStore.getPersistentObject( STORE_KEY );
        if( persist.getContents() == null )
        {
            persist.setContents(new UserSettings());
            persist.commit();
        } 
        record = (UserSettings)persist.getContents();
    }

    public static synchronized UserSettingsStorage getInstance()
    {
        if(instance == null)
        {
            instance = (UserSettingsStorage)RuntimeStore.getRuntimeStore().get(GUID);
            if(instance == null)
            {
                UserSettingsStorage newInstance = new UserSettingsStorage();
                RuntimeStore.getRuntimeStore().put(GUID, newInstance);
                instance = newInstance;
            }
        }      
        return instance;
    }

    public UserSettings getUserSettings() throws Exception
    {
        if(record != null)
        {
            return record;
        }
        return null;
    }
    
    
    public void setTwitterToken(TwitterTokenObject _twitterToken) throws Exception
    {
        synchronized(record)
        {
            record.setTwitterToken(_twitterToken);
            persist.commit();
        }
    }
    
    public void removeTwitterToken()
    {
        synchronized(record)
        {
            record.removeTwitterToken();
            persist.commit();
        }
    }
    
    public void  Save() throws Exception
    {
        synchronized(record)
        {
            persist.commit();
        }
    }
} 

class HandlerComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        int result = 1;
        if(o1 != null && o2 != null)
        {
            if(o1 == o2)
            {
                result = 0;
            }
        }
        return result;
    }
    
    public boolean equals(Object obj)
    {
        return true;
    }
}


