/**
 * MicroJabber, jabber for light java devices. Copyright (C) 2004, Gregoire Athanase
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with 
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, 
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
 
package com.injoit.examplechat.util;

import com.injoit.examplechat.utils.*;
import javax.microedition.lcdui.Image;
import org.bouncycastle.crypto.digests.SHA1Digest;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.rim.device.api.ui.*;
import net.rim.device.api.system.*;
import net.rim.blackberry.api.homescreen.*;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.Vector;
import net.rim.device.api.notification.*;
import net.rim.device.api.compress.*;
import net.rim.device.api.util.*;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.blackberry.api.mail.*;

public class Util 
{
    public static final String SYM = "Ú";
    public static final char CHR = 'Ú';
    public static String[] smileys =  new String[] {":-)", ":)", ":-(", ":(", ";-)", ";)", ":'-(", ":'(", ">:-(", ">:(", ":-P", ":P", ":-D", ":D", ":yes:", ":no:", ":wait:", "@->--", ":rose:", 
                                                     ":kiss:", ":heart:", ":love:", ":brokenheart:",  
                                                    ":music:", ":beer:", ":coffee:", ":money:", ":moon:", ":sun:", ":star:" };
    public static String[] codes =  new String[] {"01", "01", "02", "02", "03", "03", "04", "04", "05", "05", "06", "06", "07", "07", "08", "09", "10", "11", "11", "12", "13", "13", "14", "15", "16", "17", "18", "19", "20", "21"};
    public static String[] indexes =  new String[] {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"};
    public static String[] description =  new String[] {"smile", "sad", "winking", "crying", "angry", "tongue", "laugh",  "yes", "no", "wait", "rose", "kiss", 
                                                        "love", "brokenheart", "music", "beer", "coffee", "money", "moon", "sun", "star"};

    public static String processSmilesForDevice(String body)
    {
        String out = body;
        int p=-1;
        for(int i=0; i<smileys.length; i++)
        {
            String smile = smileys[i];
            
            String next = out;
            out = "";
            
            p = next.indexOf(smile);
            if(p>=0)
            {
            while(p>=0)
            {
                String part = next.substring(0, p);
                out += part;
                
                out += (SYM + codes[i]);
                
                next = next.substring(p + smile.length(), next.length());
                p = next.indexOf(smile);
            }
            if(next.length()>0)
            {
                out += next;
            }
            }
            else
            {
                out = next;
            }
        }
        return out;   
    }
    
    public static String processSmilesForServer(String body)
    {
        String out = body;
        int p=-1;
        for(int i=0; i<codes.length; i++)
        {
            String code = SYM + codes[i];
            
            String next = out;
            out = "";
            
            p = next.indexOf(code);
            if(p>=0)
            {
                while(p>=0)
                {
                    String part = next.substring(0, p);
                    out += part;
                    
                    out += smileys[i];
                    
                    next = next.substring(p + code.length(), next.length());
                    p = next.indexOf(code);
                }
                if(next.length()>0)
                {
                    out += next;
                }
            }
            else
            {
                out = next;
            }
        }
        return out;
    }
    
    public static boolean sendSupportEmail(String address, String ccaddress)
    {
        boolean result = false;
        try
        {    
            String data = DebugStorage.getInstance().GetLogs();
            System.out.println(data);
            Multipart mp = new Multipart();
    
            //create the file
            SupportedAttachmentPart sap = new SupportedAttachmentPart(mp,"text", "logs.txt", data.getBytes());
            
            String messageBody = "This is Quickblox Chat blackberry logs.";
            TextBodyPart tbp = new TextBodyPart(mp,messageBody);
            
            //add the file to the multipart
            mp.addBodyPart(tbp);
            mp.addBodyPart(sap);
            
            //create a message in the sent items folder
            Folder folders[] = Session.getDefaultInstance().getStore().list(Folder.SENT);    
            final Message message = new Message(folders[0]);
            
            //add recipients to the message and send
            try 
            {
                Address toAdd = new Address(address, address);
                Address toAddCC = new Address(ccaddress, ccaddress);
                Address toAdds[] = new Address[2];
                toAdds[0] = toAdd;
                toAdds[1] = toAddCC;
                
                message.addRecipients(Message.RecipientType.TO,toAdds);
                
                String emailAddress = "undefined blackberry user";
                Session session = Session.getDefaultInstance();
                if(session != null)
                {
                    Store store = session.getStore();
                    ServiceConfiguration serviceConfig = store.getServiceConfiguration();
                    emailAddress = serviceConfig.getEmailAddress();
                }

                Address from = new Address(emailAddress, "Blackberry");
                message.setFrom(from);
                message.setSubject("QuickBloxChat logs"); 
                message.setContent(mp);  
                Transport.send(message);
                result = true;
            } 
            catch (Exception e) 
            {
                Dialog.inform(e.toString());
            }
        }
        catch (Exception e)
        {
            DebugStorage.getInstance().Log(0, "<Utils.sendEmail()> {support} ", e);
        }

        return result;
    }
    
    /**
     * Returns a SHA1 digest of the given string, in hex values lowercase.
     * @param _str
     */
    public static String sha1(String _str) {
        String res;
        SHA1Digest digest = new SHA1Digest();
        String tmp = _str;
        byte in[] = tmp.getBytes();
        digest.update(in, 0, in.length);
        byte out[] = new byte[20];
        digest.doFinal(out, 0);
        
        // builds the hex string (lowercase)
        res = "";
        tmp = ""; // tmp = two characters to append to the hex string
        for (int i = 0; i < 20; i++) {
            int unsigned = out[i];
            if (out[i] < 0) {
                unsigned += 256;
            }
            tmp = Integer.toHexString(unsigned);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            res = res + tmp;
        }
        
        return res;
    }
    /**
     * Returns a SHA1 digest of the given array of bytes, in hex values lowercase.
     * @param _str
     */
    public static String sha1(byte[] in) {
        String res;
        SHA1Digest digest = new SHA1Digest();
        String tmp;
        
        digest.update(in, 0, in.length);
        byte out[] = new byte[20];
        digest.doFinal(out, 0);
        
        // builds the hex string (lowercase)
        res = "";
        tmp = ""; // tmp = two characters to append to the hex string
        for (int i = 0; i < 20; i++) {
            int unsigned = out[i];
            if (out[i] < 0) {
                unsigned += 256;
            }
            tmp = Integer.toHexString(unsigned);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            res = res + tmp;
        }
        
        return res;
    }
    
    /**
     * Escapes the given string, for xml CDATA.
     */
    public static String escapeCDATA(String _str) {

        String escapeSource = "<>&'\"";
        String escapeDest[] = {"&lt;", "&gt;", "&amp;", "&apos;", "&quot;"};
        char ch;
        int pos;
        StringBuffer res = new StringBuffer();
        for (int i=0; i<_str.length(); i++) {
            ch = _str.charAt(i);
            pos = escapeSource.indexOf(ch);
            if (pos!=-1) {
                res.append(escapeDest[pos]);
                
            }
            else {
                res.append(ch);

            }
        }
        return res.toString();
    }
    
    /**
     * Unescapes the given string, from an xml CDATA.
     */
    public static String unescapeCDATA(String _str) {
        String escapeSource = "<>&'\"";
        String escapeDest[] = {"&lt;", "&gt;", "&amp;", "&apos;", "&quot;"};
     
        int pos;  // position of the next amp '&' operator
        StringBuffer res = new StringBuffer();
        
        while ((pos = _str.indexOf('&')) != -1 ) {
            // found a '&' character
            // take the string until the '&'
            res.append(_str.substring(0,pos));
            _str = _str.substring(pos);
            
            // unescape the character
            int i=0;
            boolean found=false;
            do {
                if (_str.startsWith(escapeDest[i])) {
                    found=true;
                } else {
                    i++;
                }
            } while (!found && (i<escapeDest.length));
            if (found) {
                res.append(escapeSource.charAt(i));
                _str = _str.substring(escapeDest[i].length());
            } else {
                // ERROR ***
                System.err.println("Parsing error: wrong escape character");
                _str = _str.substring(1);
            }
        }
        res.append(_str);
        return res.toString();
    }

        public static String formatGtwAddress(String jid)
        {

                if (jid.indexOf("40") == -1)
                        return jid;
                else
                {
                        int i = -1;

                        if ((i = jid.indexOf("40hotmail")) != -1)
                                return jid.substring(0, i) + "\\" + jid.substring(i, jid.length());
                        else if ((i = jid.indexOf("40yahoo")) != -1)
                                return jid.substring(0, i) + "\\" + jid.substring(i, jid.length());
                        else if ((i = jid.indexOf("40aim")) != -1)
                                return jid.substring(0, i) + "\\" + jid.substring(i, jid.length());
                        else
                                return jid;
                }
        }
        
        public static byte[] imageToByte( Image img, int width, int height )
        {
                if( img == null || width < 0 || height < 0 )
                {
                        throw new IllegalArgumentException( "Check arguments" );
                }
        
                int[] imgRgbData = new int[width * height];
        
                try
                {
                        img.getRGB( imgRgbData, 0, width, 0, 0, width, height );
                
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        DataOutputStream dos = new DataOutputStream( baos );
        
                        for( int i = 0; i < imgRgbData.length; i++ )
                        {
                                dos.writeInt( imgRgbData[i] );
                        }
        
                        byte[] imageData = baos.toByteArray();
                        return imageData;
                }catch( Exception e ) { return null;}   
        } 
}
