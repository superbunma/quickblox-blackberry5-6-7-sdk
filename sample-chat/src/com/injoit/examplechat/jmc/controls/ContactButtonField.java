package com.injoit.examplechat.jmc.controls;

import com.injoit.examplechat.util.*;
import com.injoit.examplechat.threads.*;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.jabber.presence.*;
import com.injoit.examplechat.jabber.subscription.*;

import net.rim.device.api.ui.*;
import net.rim.device.api.system.*;

public class ContactButtonField extends BaseButtonField
{
    private Bitmap state;
    private static final int NORMAL = 0;
    private static final int FOCUS = 1;
    private Jid jid;
    private String name;
    private String imageFileName;
    private boolean isPaintStatus = true;

    private static final int PADDING = Display.getWidth() <= 320 ? 6 : 8;   // Space within component boundary
        
    // Static colors
    private static final int TEXT_FOCUS_COLOR = 0x0000FF; 
    private static final int TEXT_COLOR = 0x000000; 
    
    private Font font = Font.getDefault().derive(Font.PLAIN, 8, Ui.UNITS_pt);
                  
    public ContactButtonField( Jid _jid)
    {        
        this( _jid, 0 );
    }
    
    public ContactButtonField(  Jid _jid, boolean _isPaintStatus )
    {        
        this( _jid, 0 );
        isPaintStatus = _isPaintStatus;
    }
    
    public ContactButtonField(  Jid _jid, long style )
    {        
        super( Field.FOCUSABLE | style );
        setEditable(true);
        
        jid = _jid;
        if(jid.status_message.length()>0)
        {
            name = jid.getNick()+ " /" + jid.status_message + "/";
        }
        else
        {
            name = jid.getNick();
        }
    }
    
    public int getPreferredWidth() 
    {   
        return font.getAdvance(Jid.getNickUser(this.name)) + Contents.displayBitmap(jid.getPresence()).getWidth() + PADDING;
    }
    
    public int getPreferredHeight() 
    {
        int height = font.getHeight();
        if(height < Contents.displayBitmap(jid.getPresence()).getHeight() + PADDING/2)
        {
            height = Contents.displayBitmap(jid.getPresence()).getHeight() + PADDING/2;
        }
        return height;
    }
    
    protected void layout( int width, int height ) 
    {
        setExtent( this.getPreferredWidth() , this.getPreferredHeight() );
    }
    
    protected void paint( Graphics g ) 
    {
        int index = g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS ) ? FOCUS : NORMAL;
        
        state = Contents.displayBitmap(jid.getPresence());
        
        int x = 0;
        int y = (font.getHeight() - state.getHeight())/2;
        
        if(isPaintStatus == true)
        {
            g.drawBitmap( x, y, state.getWidth(),state.getHeight(), state, 0, 0 );
            x += state.getWidth() + 5;
        }
        
        if(index == FOCUS)
        {
            g.setColor(TEXT_FOCUS_COLOR);
        }
        else
        {
            g.setColor(TEXT_COLOR);
        }
        
        g.setFont(font);
        g.drawText(Jid.getNickUser(this.name), x , 0);  
    }
    
    protected void paintBackground( Graphics g )
    {
        if(g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS )==true)
        {
            g.setColor(0xAAAAAA);
            g.fillRect(0,0,getWidth(), getHeight());
        }
        super.paintBackground(g);
    }
    
    protected void drawFocus( Graphics g, boolean on ) 
    {
        g.setDrawingStyle( Graphics.DRAWSTYLE_FOCUS, true );
        paintBackground( g );
        paint( g );
    }
    
    public boolean isFocusable()
    {
        return isEditable();
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    /*public String getName() 
    {
        return name;
    }*/
    
    public String getName() 
    {
        //System.out.println("================================================================================================ ttttttt " + Jid.getNickUser(name));
                            
        return Jid.getNickUser(name);
    }
    
    public Jid getJid()
    {
        return jid;
    }
}





