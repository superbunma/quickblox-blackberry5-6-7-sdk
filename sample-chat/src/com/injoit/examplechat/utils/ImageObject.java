package com.injoit.examplechat.utils;

import net.rim.device.api.system.*;
import java.io.*;
import java.util.*;
import net.rim.device.api.util.*;
import net.rim.device.api.io.http.*;
import net.rim.device.api.i18n.*;
import net.rim.device.api.util.*;
import net.rim.device.api.collection.util.*;

public class ImageObject implements Persistable
{
    String filePath = "";
    String filename = "";
    private String imageURL;
    private byte[] photoArray = new byte[1];
    
    public ImageObject(String _filename) 
    {
        this.filename = _filename;
    }
    
    public String getFilePath()
    {
        return filePath;
    }
    
    public void setFilePath(String data)
    {
        filePath = data;
    }
    
    public String getFilename()
    {
        return filename;
    }
    
    public void setFilename(String data)
    {
        filename = data;
    }
    
    public Bitmap getImage()
    {
        Bitmap bitmap = Bitmap.createBitmapFromBytes( this.photoArray, 0, -1, 1 );
        return bitmap;
    }
    
    public Bitmap getImage(int scale)
    {
        Bitmap bitmap = Bitmap.createBitmapFromBytes( this.photoArray, 0, -1, scale );
        return bitmap;
    }
    
    public String getImageURL() {
        return imageURL;
    }
    
    public void setImageURL(String _imageURL) {
        this.imageURL = _imageURL;
    }
    
    public void setImage(Bitmap bmp)
    {
        try 
        {
            int height=bmp.getHeight();
            int width=bmp.getWidth();
            int[] rgbdata = new int[width*height];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            bmp.getARGB(rgbdata,0,width,0,0,width,height);

            for (int i = 0; i < rgbdata.length ; i++) 
            {
                if (rgbdata[i] != -1)
                {
                    dos.writeInt(rgbdata[i]);
                    dos.flush();
                }
            } 
            bos.flush();
            this.photoArray = bos.toByteArray(); 
        } 
        catch (Exception ex)
        {
            this.photoArray = new byte[0];
        } 
    }
    
    public void setImage(byte[] _photoArray)
    {
        this.photoArray = _photoArray;
    }
}
