package com.injoit.examplenews.qblox;

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

import com.injoit.examplenews.qblox.networking.*;

public class QBUtils 
{    
    public static String hmacsha1(String key, String message) throws CryptoTokenException, CryptoUnsupportedOperationException, IOException 
    {
        HMACKey k = new HMACKey(key.getBytes());
        HMAC hmac = new HMAC(k, new SHA1Digest());
        hmac.update(message.getBytes());
        byte[] mac = hmac.getMAC();
        
        return convertToHex(mac);
    }
    
    public static String convertToHex(byte[] data) 
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
}

