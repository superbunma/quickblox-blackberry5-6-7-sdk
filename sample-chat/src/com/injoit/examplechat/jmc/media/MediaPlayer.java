package com.injoit.examplechat.jmc.media;

import java.io.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.util.*;
import javax.microedition.media.Player;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.PlayerListener; 

public class MediaPlayer
{
    public static final String TYPE_MP3 = "audio/mpeg";
    public static final String TYPE_WAV = "audio/x-wav";
    public static final String TYPE_AU = "audio/basic";
    public static final String TYPE_MIDI = "audio/midi";
    public static final String TYPE_TONE = "audio/x-tone-seq";
    static final long MPFLD_ID = 0x7896234bfa54dL;

    public static boolean playResource(String resource, String type)
    {
        boolean res = false;
        
        try 
        {
            stop();
            
            Class cls = Class.forName("com.injoit.examplechat.jmc.media.MediaPlayer");
            Player player = Manager.createPlayer(cls.getResourceAsStream(resource), type);
            RuntimeStore appReg = RuntimeStore.getRuntimeStore();
            synchronized(appReg)
            {
                if(appReg.get(MPFLD_ID) == null)
                {
                    appReg.put(MPFLD_ID, player);
                }
                else
                {
                    appReg.replace(MPFLD_ID, player);
                }
            }
            
            player.realize();
            player.prefetch();
            player.start();
                        
            res = true;
        }
        catch(ClassNotFoundException cnfe)
        {
            res = false;
        }
        catch(MediaException me)
        {
            res = false;
        }
        catch(IOException ioe)
        {
            res = false;
        }
        
        return res;
    }
    
    public static Player getCurrentPlayer()
    {
        RuntimeStore appReg = RuntimeStore.getRuntimeStore();
        synchronized(appReg)
        {
            Object obj = appReg.get(MPFLD_ID);
            if(obj != null && obj instanceof Player)
            {
                return (Player)obj;
            }
        }
        
        return null;
    }
    
    private static String getDurationStr(Player player)
    {
        String duration = new String();
        
        long cur = player.getMediaTime();
        if(cur != Player.TIME_UNKNOWN)
        {
            StringBuffer sb = new StringBuffer();
            DateTimeUtilities.formatElapsedTime((int)(cur/1000000), sb, false);
            duration = duration.concat(sb.toString()).concat(" of ");
        }
        
        long ms = player.getDuration();
        if(ms == Player.TIME_UNKNOWN)
        {
            duration = duration.concat("unknown");
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            DateTimeUtilities.formatElapsedTime((int)(ms/1000000), sb, false);
            duration = duration.concat(sb.toString());
        }
                
        return duration;
    }
    
    /**
     * Stops playing if it was
     */
    public static void stop()
    {
        Player player = getCurrentPlayer();
        if(player != null)
        {
            try
            {
                player.stop();
                player.deallocate();
                RuntimeStore appReg = RuntimeStore.getRuntimeStore();
                synchronized(appReg)
                {
                    appReg.remove(MPFLD_ID);
                }
            }
            catch(MediaException me)
            {
            }
        }
    }
}

