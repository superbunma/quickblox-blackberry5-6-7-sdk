package com.injoit.examplechat.jmc.controls;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.system.Bitmap;


public class BitmapButton extends BaseButtonField 
{
    private Bitmap[] _bitmaps;
    private static final int NORMAL = 0;
    private static final int FOCUS = 1;
    private int id = 0;
    private String name;
    private String description;
    private Manager parent;
    
    public BitmapButton( Bitmap normalState )
    {        
        this( normalState, normalState, 0 );
    }
    
    public BitmapButton( Bitmap normalState, Bitmap focusState )
    {        
        this( normalState, focusState, 0 );
    }
    
     public BitmapButton( Manager _parent, Bitmap normalState, Bitmap focusState )
    {
        this( normalState, focusState, 0 );
        this.parent = _parent; 
    }
    
    public BitmapButton( Bitmap normalState, Bitmap focusState, long style )
    {        
        super( Field.FOCUSABLE | style );
        setEditable(true);
        
        if( (normalState.getWidth() != focusState.getWidth()) || (normalState.getHeight() != focusState.getHeight()) )
        {
            throw new IllegalArgumentException( "Image sizes don't match" );
        }
        
        _bitmaps = new Bitmap[] { normalState, focusState };
    }
    
    public void setImage( Bitmap normalState )
    {
        _bitmaps[NORMAL] = normalState;
        invalidate();
    }
    
    public void setFocusImage( Bitmap focusState )
    {
        _bitmaps[FOCUS] = focusState;
        invalidate();
    }
    
    public int getPreferredWidth() 
    {
        return _bitmaps[NORMAL].getWidth();
    }
    
    public int getPreferredHeight() 
    {
        return _bitmaps[NORMAL].getHeight();
    }
    
    protected void layout( int width, int height ) 
    {
        setExtent( _bitmaps[NORMAL].getWidth(), _bitmaps[NORMAL].getHeight() );
    }
    
    protected void paint( Graphics g ) 
    {
        int index = g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS ) ? FOCUS : NORMAL;
        g.drawBitmap( 0, 0, _bitmaps[index].getWidth(), _bitmaps[index].getHeight(), _bitmaps[index], 0, 0 );
        
        if(index==FOCUS)
        {
            g.setColor(0x0000ff);
            g.drawRect(0, 0, _bitmaps[index].getWidth(), _bitmaps[index].getHeight());
            g.setColor(0x000000);
        }
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

    public void setName(String _name)
    {
        this.name = _name;
    }
    
    public String getName()
    {
        return this.name;
    }

    public void setDescription(String _description)
    {
        this.description = _description;
    }
    
    public String getDescription()
    {
        return this.description;
    }

    public int getContext()
    {
        return id;
    }
    
    public void setContext(int _id)
    {
        id = _id;
    }
}



