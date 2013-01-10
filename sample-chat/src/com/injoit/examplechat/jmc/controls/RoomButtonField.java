package com.injoit.examplechat.jmc.controls;

import net.rim.device.api.ui.*;
import net.rim.device.api.system.*;
import java.util.Vector;

import com.injoit.examplechat.util.*;
import com.injoit.examplechat.jabber.roster.Jid;

public class RoomButtonField extends BaseButtonField
{
    private Bitmap[] _bitmaps;
    private static final int NORMAL = 0;
    private static final int FOCUS = 1;
    private String name;
    private String imageFileName;
    private int userCount = 0;
    private Vector roomCount = null;
    private Bitmap imgAvaliable = Bitmap.getBitmapResource("avaliable.png");
    private Bitmap imgBusy = Bitmap.getBitmapResource("busy.png");

    private static final int PADDING = Display.getWidth() <= 320 ? 6 : 8;   // Space within component boundary
        
    // Static colors
    private static final int TEXT_FOCUS_COLOR = 0x0000FF; 
    private static final int TEXT_COLOR = 0x000000; 
    private int count = 0; 
    private Font font = Font.getDefault().derive(Font.PLAIN, 8, Ui.UNITS_pt);
                
    
    public RoomButtonField( String _name, Bitmap normalState)
    {        
        this( _name, normalState, normalState, 0);
    }
    
    public RoomButtonField(  String _name, Bitmap normalState , long style )
    {        
        this( _name, normalState, normalState, style );
    }
    
    public RoomButtonField(  String _name,Bitmap normalState, Bitmap focusState )
    {        
        this( _name, normalState, focusState, 0 );
    }
    
    
    public RoomButtonField(  String _name, Bitmap normalState, Bitmap focusState, long style)
    {        
        super( Field.USE_ALL_WIDTH | style );
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
        return Display.getWidth();
    }

    public int getPreferredHeight() 
    {
        int height = font.getHeight();
        if(height < _bitmaps[NORMAL].getHeight() + PADDING/2)
        {
            height = _bitmaps[NORMAL].getHeight() + PADDING/2;
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
        int display = Display.getWidth();
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
        x = x + _bitmaps[index].getWidth() + 5;
        g.drawText(Jid.getUsername(this.name), x, 0);
        if (this.getUsersCount()>0) {
            x = display-imgAvaliable.getWidth()-25;
            g.drawBitmap(display-imgAvaliable.getWidth()-25, 2, imgAvaliable.getWidth(), imgAvaliable.getHeight(), imgAvaliable, 0, 0);
        } else {
            x = display-imgBusy.getWidth()-25;
            g.drawBitmap(display-imgBusy.getWidth()-25, 2, imgBusy.getWidth(), imgBusy.getHeight(), imgBusy, 0, 0);
        }
        x = x + imgBusy.getWidth() + 5;
        g.drawText(this.getUsersCount() + "",x , 0);
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
    
    public int getUsersCount()
    {
        return userCount;
    }
    
    public void setUsersCount(int val)
    {
        this.userCount = val;
    }
}
