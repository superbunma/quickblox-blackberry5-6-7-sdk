package com.injoit.examplechat.utils;

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

public class ImageStorage
{
    public static final long STORE_KEY = 0xaeb43beb211295d7L;
    private static final long GUID = 0xaa34d61c12004c17L;
    
    private static ImageStorage instance;
    private static BigVector records = new BigVector();
    private static PersistentObject  persist;
    
    static
    {
        persist = PersistentStore.getPersistentObject( STORE_KEY );
        if( persist.getContents() == null )
        {
            persist.setContents(new BigVector());
            persist.commit();
        } 
        records = (BigVector)persist.getContents();
    }

    public static synchronized ImageStorage getInstance()
    {
        if(instance == null)
        {
            instance = (ImageStorage)RuntimeStore.getRuntimeStore().get(GUID);
            if(instance == null)
            {
                ImageStorage newInstance = new ImageStorage();
                RuntimeStore.getRuntimeStore().put(GUID, newInstance);
                instance = newInstance;
            }
        }      
        return instance;
    }

    public void addImage(ImageObject image) throws Exception
    {
        if(image != null)
        {
            synchronized(records)
            {
                records.addElement(image);
                persist.commit();
            }
        }
    }
    
    public ImageObject getImage(String id) throws Exception
    {
        ImageObject image = null;
        synchronized(records)
        {
            for(int i=0; i<records.size(); i++)
            {
                image = (ImageObject) records.elementAt(i);
                if(image.getFilename().equals(id)==true)
                {
                    break;
                }
                image = null;
            }
            persist.commit();
        }
        return image;
    }
    
    public void deleteImage(ImageObject _country) throws Exception
    {
        synchronized(records)
        {
            for(int i=0; i<records.size(); i++)
            {
                ImageObject country = (ImageObject) records.elementAt(i);
                if(country.getFilename().equals(_country.getFilename()))
                {
                    records.removeElementAt(i);
                    break;
                }
            }
            persist.commit();
        }
    }
    
    public void deleteAll() throws Exception
    {
        synchronized(records)
        {
            records.removeAll();
            persist.commit();
        }
    }
    
    public  BigVector getImages() throws Exception
    {
        Enumeration e = null;
        BigVector list = new BigVector();
        
        synchronized(records)
        {
            for(int i=0; i<records.size(); i++)
            {
                ImageObject storedId = (ImageObject) records.elementAt(i);
                list.addElement(storedId);
            }
        }
        
        return list;
    }
    
    public  boolean Exists(ImageObject _image)  throws Exception
    {
        boolean result = false;  
        synchronized(records)
        {
            for(int i=0; i<records.size(); i++)
            {
                ImageObject image = (ImageObject) records.elementAt(i);
                if(image.getFilename().equals(_image.getFilename()))
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
} 


