/*
 * ChatTitleBarManager.java
 *
 * © <your company here>, <year>
 * Confidential and proprietary.
 */

package com.injoit.examplechat.jmc.controls;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.decor.*;

import com.injoit.examplechat.utils.*;

public class  ChatTitleBarManager extends Field
{
    private MainScreen screen;
    private String label;
    private Background bitmapBackground; 
    private int counterWidth = 0;
    private int MARGIN = 8;
    private int titleBarHeight = Utils.getBitmap("titleBar_logo.png").getHeight() + (MARGIN * 2);

    private String count = "0";
    private Bitmap _aval = Utils.getBitmap("Avaliable.png");
    private Bitmap _no_aval = Utils.getBitmap("Busy.png");
    private static final int HPADDING = Display.getWidth() <= 320 ? 6 : 8;
    
    private int px = 0;
    private int py = 0;
    private int pf = 0;
    private Font font9b = Utils.getFontBold(9);
    private Font font6 = Utils.getFont(6);
    
    public ChatTitleBarManager(String _label)
    {
        super(USE_ALL_WIDTH);
        setBackground(BackgroundFactory.createLinearGradientBackground(0x1490b5, 0x1490b5, 0x114353, 0x114353));        
        counterWidth = Display.getWidth()/8;
        label = prepareLabel(_label, Display.getWidth() - HPADDING - counterWidth - Utils.getBitmap("titleBar_logo.png").getWidth(), font9b);
    }
    
    public void paint(Graphics g)
    {
        g.setColor(0xffffff);
        g.setFont(font9b);

        if(px==0 || py==0 || pf==0)
        {
            px = (Display.getWidth() - g.getFont().getAdvance(label) - counterWidth + Utils.getBitmap("titleBar_logo.png").getWidth())/2;
            py = (titleBarHeight - font9b.getHeight())/2;
            pf = (titleBarHeight - font6.getHeight())/2;
        }
        
        g.drawText(label, px, py);
        
        int off = Display.getWidth() - counterWidth;
        
        g.setColor(0x666666);
        g.fillRect(off+1, 0, counterWidth, titleBarHeight-1);
        
        g.setColor(0x000000);
        g.drawLine(off, 0, off, titleBarHeight);
        
        if(count.length()>0)
        {
            g.drawBitmap(off + HPADDING/2, (titleBarHeight - _aval.getHeight())/2, _aval.getWidth(), _aval.getHeight(), _aval, 0, 0);
        }
        else
        {
            g.drawBitmap(off + HPADDING/2, (titleBarHeight - _aval.getHeight())/2, _aval.getWidth(), _aval.getHeight(), _no_aval, 0, 0);
        }
        
        g.setColor(0x7b7b7b);
        g.setFont(font6);
        g.drawText(count, (Display.getWidth() - off - _aval.getWidth() -  font6.getAdvance(count))/2 + off + _aval.getWidth() , pf);
    }
    
    public void setCount(String _count)
    {
        count = _count;
    }
    
    public void layout(int w, int h)
    {
        setExtent(Display.getWidth(), titleBarHeight);
    }
    
    public void paintBackground(Graphics g)
    {
        super.paintBackground(g);
        Bitmap logo = Utils.getBitmap("titleBar_logo.png");        
        g.drawBitmap( MARGIN, MARGIN, logo.getWidth(), logo.getHeight(), logo, 0, 0 );
        g.setColor(0x000000);
        g.drawLine(0, titleBarHeight-1, Display.getWidth(), titleBarHeight-1);
    }
    
    private String prepareLabel(String text, int w, Font font)
    {
        String s = "";
        if(font.getAdvance(text) > w)
        {
            for(int l=0; l<text.length(); l++)
            {
                String temp = text.substring(0, l);
                if(font.getAdvance(temp) >= w)
                {
                    s = text.substring(0, l - 3) + "...";
                    break;
                } 
            }
            
            if(s.length()==0)
            {
                s = text;
            }
        }
        else
        {
            s = text;
        }
        
        return s;
    }
    
    public int getPreferredHeight()
    {
        return titleBarHeight;
    }
}
