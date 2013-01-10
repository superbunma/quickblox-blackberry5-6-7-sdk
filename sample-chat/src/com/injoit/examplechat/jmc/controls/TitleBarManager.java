package com.injoit.examplechat.jmc.controls;

import com.injoit.examplechat.util.*;
import com.injoit.examplechat.threads.*;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.presence.*;
import com.injoit.examplechat.jabber.subscription.*;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.decor.*;

public class  TitleBarManager extends Field
{
    private MainScreen screen;
    private String label;
    private Background bitmapBackground; 
    private Font font = Font.getDefault().derive(Font.PLAIN, 8, Ui.UNITS_pt);
    private Bitmap state;
    private Jid jid;
    
    public TitleBarManager(Jid _jid)
    {
        super(USE_ALL_WIDTH);
        setBackground(BackgroundFactory.createSolidBackground(0x000000));
        
        jid = _jid;
        if(jid.status_message.length()>0)
        {
            label = jid.getNickName()+ " /" + jid.status_message + "/";
        }
        else
        {
            label = jid.getNickName();
        }
        state = Contents.displayBitmap(_jid.getPresence());
    }
    
    public TitleBarManager(String _label)
    {
        super(USE_ALL_WIDTH);
        setBackground(BackgroundFactory.createSolidBackground(0x000000));        
        label = _label;
    }
    
    public void paint(Graphics g)
    {
        int x = 0;
        if(state != null)
        {
            int y = (getFont().getHeight() - state.getHeight())/2;
            g.drawBitmap( 0, y, state.getWidth(), state.getHeight(), state, 0, 0 );
            x+=(state.getWidth() + 5);
        }
        
        g.setColor(0xffffff);
        g.setFont(font);
        g.drawText(label, x, 0);
    }
    
    public void layout(int w, int h)
    {
        setExtent(Display.getWidth(), font.getHeight());
    }
    
    public void paintBackground(Graphics g)
    {
        super.paintBackground(g);

        g.setColor(0x000000);
        g.drawLine(0, font.getHeight()-1, Display.getWidth(), font.getHeight()-1);
    }
    
    public void invalidete()
    {
        if(jid != null)
        {
            if(jid.status_message.length()>0)
            {
                label = jid.getNickName()+ " /" + jid.status_message + "/";
            }
            else
            {
                label = jid.getNickName();
            }
            state = Contents.displayBitmap(jid.getPresence());
        }
    }
    
    public void setText(String value)
    {
        this.label = value;
    }
}
