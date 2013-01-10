//  @ Project : DSTv TV Guide Application
//  @ File Name : TitleBarManager.java
//  @ Date : 28/05/2012
//  @ Author : Vladimir Slatvinskyi

package com.injoit.examplenews.utils.controls;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.decor.*;

import com.injoit.examplenews.utils.containers.*;
import com.injoit.examplenews.utils.*;

public class  TitleBarManager extends Field
{
    private MainScreen screen;
    private String label;
    private Background bitmapBackground; 
    private Bitmap logo = Utils.getBitmap("titleBar_logo.png");
    private int MARGIN = 8;
    private int titleBarHeight = Utils.getBitmap("titleBar_logo.png").getHeight() + (MARGIN * 2);
    
    public TitleBarManager(String _label)
    {
        super(USE_ALL_WIDTH);
        setBackground(BackgroundFactory.createLinearGradientBackground(0x1490b5, 0x1490b5, 0x114353, 0x114353));
        
        label = _label;
    }
    
    public void paint(Graphics g)
    {
        g.setColor(0xffffff);
        g.setFont(Utils.getFontBold(9));
        int xoff = (Display.getWidth() - g.getFont().getAdvance(label))/2;
        if(xoff < logo.getWidth())
        {
            xoff =  logo.getWidth();
        }
        g.drawText(label, xoff + (MARGIN * 2), (titleBarHeight - g.getFont().getHeight())/2);
    }
    
    public void layout(int w, int h)
    {
        setExtent(Display.getWidth(), titleBarHeight);
    }
    
    public void paintBackground(Graphics g)
    {
        super.paintBackground(g);
        g.drawBitmap( MARGIN, MARGIN, logo.getWidth(), logo.getHeight(), logo, 0, 0 );
        g.setColor(0x000000);
        g.drawLine(0, titleBarHeight-1, Display.getWidth(), titleBarHeight-1);
    }
    
    public void invalidete()
    {}
    
    public void setText(String value)
    {
        this.label = value;
    }
}
