package com.injoit.examplechat.jmc.controls;

import net.rim.device.api.ui.*;
import net.rim.device.api.system.*;

public class BitmapTextButton extends BaseButtonField
{
    private Bitmap[] _bitmaps;
    private static final int NORMAL = 0;
    private static final int FOCUS = 1;
    private String name;
    private String imageFileName;

    private static final int PADDING = Display.getWidth() <= 320 ? 6 : 8;   // Space within component boundary
        
    // Static colors
    private static final int TEXT_FOCUS_COLOR = 0xFFFFFF;   // White
    private static final int TEXT_COLOR = 0x4A4A4A; // dark gray
    
    private Font font = Font.getDefault().derive(Font.PLAIN, 8, Ui.UNITS_pt);
                
    
    public BitmapTextButton( String _name, Bitmap normalState)
    {        
        this( _name, normalState, normalState, 0 );
    }
    
    public BitmapTextButton(  String _name, Bitmap normalState , long style )
    {        
        this( _name, normalState, normalState, style );
    }
    
    public BitmapTextButton(  String _name, Bitmap normalState, Bitmap focusState )
    {        
        this( _name, normalState, focusState, 0 );
    }
    
    
    public BitmapTextButton(  String _name, Bitmap normalState, Bitmap focusState, long style )
    {        
        super( Field.FOCUSABLE | style );
        setEditable(true);
        
        if( (normalState.getWidth() != focusState.getWidth()) || (normalState.getHeight() != focusState.getHeight()) )
        {
            throw new IllegalArgumentException( "Image sizes don't match" );
        }
        
        _bitmaps = new Bitmap[] { normalState, focusState };
        name = _name;
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
    
    public void setNewImage( Bitmap image ){
        _bitmaps[NORMAL] = image;
        _bitmaps[FOCUS] = image;
        invalidate();
    }
    
    public int getPreferredWidth() 
    {
        return font.getAdvance(this.name) + _bitmaps[NORMAL].getWidth() + PADDING;
    }
    
    public int getPreferredHeight() 
    {
        return _bitmaps[NORMAL].getHeight() + PADDING/2;
    }
    
    protected void layout( int width, int height ) 
    {
        setExtent( this.getPreferredWidth() , this.getPreferredHeight() );
    }
    
    protected void paint( Graphics g ) 
    {
        int index = g.isDrawingStyleSet( Graphics.DRAWSTYLE_FOCUS ) ? FOCUS : NORMAL;
       
        int x = 0;
        int y = (font.getHeight() - _bitmaps[index].getHeight())/2;
        g.drawBitmap( x, y, _bitmaps[index].getWidth(), _bitmaps[index].getHeight(), _bitmaps[index], 0, 0 );
        
        if(index == FOCUS)
        {
            g.setColor(TEXT_FOCUS_COLOR);
        }
        else
        {
            g.setColor(TEXT_COLOR);
        }
        
        g.setFont(font);
        g.drawText(this.name, x+_bitmaps[index].getWidth() + 5 , 0);  
    }
    
    protected void paintBackground( Graphics g )
    {
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

    public void setImageFileName(String imageFileName) 
    {
        this.imageFileName = imageFileName;
    }

    public String getImageFileName()
    {
        return imageFileName;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
}




