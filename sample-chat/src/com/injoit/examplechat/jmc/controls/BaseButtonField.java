package com.injoit.examplechat.jmc.controls;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;

public abstract class BaseButtonField extends Field 
{
    private XYRect _drawFocusTempRect = new XYRect();
    
    public BaseButtonField()
    {
        this( 0 );
    }
    
    public BaseButtonField( long style )
    {        
        super( Field.FOCUSABLE | style );
    }
        
    protected void layout( int width, int height )
    {
        setExtent( 10, 10 );
    }
    
    protected void drawFocus( Graphics g, boolean on )
    {
        getFocusRect( _drawFocusTempRect );
    
        boolean oldDrawStyleFocus = g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS );
        boolean notEmpty = g.pushContext( _drawFocusTempRect.x, _drawFocusTempRect.y, _drawFocusTempRect.width, _drawFocusTempRect.height, 0, 0 );

        try 
        {
            if( notEmpty ) 
            {
                g.setDrawingStyle( Graphics.DRAWSTYLE_FOCUS, on );
                paintBackground( g );
                paint( g );
            }
        } 
        finally 
        {
            g.popContext();
            g.setDrawingStyle( Graphics.DRAWSTYLE_FOCUS, oldDrawStyleFocus );
        }
    }
    
    protected boolean keyChar( char character, int status, int time ) 
    {
        if( character == Characters.ENTER ) 
        {
            clickButton();
            return true;
        }
        return super.keyChar( character, status, time );
    }
    
    protected boolean navigationClick( int status, int time ) 
    {
        clickButton(); 
        return true;    
    }
    
    protected boolean trackwheelClick( int status, int time )
    {        
        clickButton();    
        return true;
    }
    
    protected boolean invokeAction( int action ) 
    {
        switch( action ) 
        {
            case ACTION_INVOKE: 
            {
                clickButton(); 
                return true;
            }
        }
        return super.invokeAction( action );
    }    

    public void setDirty( boolean dirty )
    {
        
    }
     
    public void setMuddy( boolean muddy ) 
    {

    }
         
    public void clickButton() 
    {
        fieldChangeNotify( 0 );
    }
}

