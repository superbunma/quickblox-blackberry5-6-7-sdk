package com.injoit.examplechat.jmc.screens;

import com.injoit.examplechat.util.*;
import com.injoit.examplechat.jmc.controls.*;
import com.injoit.examplechat.jmc.containers.*;

import net.rim.device.api.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.Graphics;

public class SmilesScreen extends PopupScreen implements FieldChangeListener, FocusChangeListener
{
    private SmilesScreen me;
    private InsertSmileListener listener;
    private LabelField label;
    
    public SmilesScreen(InsertSmileListener _listener)
    {
        super(new VerticalFieldManager(Manager.VERTICAL_SCROLL), DEFAULT_MENU|DEFAULT_CLOSE);
        me = this;
        listener = _listener;
        
        try
        {
            VerticalFieldManager manager = new VerticalFieldManager();
            label = new LabelField("smile");
            manager.add(label);
            manager.add(new SeparatorField());
        
            FlowFieldManager man = new FlowFieldManager();
            
            for(int i=0; i<Util.indexes.length; i++)
            {
                SmileObject smile = new SmileObject(Util.SYM + Util.indexes[i], Util.description[i]);
                BitmapButton b = new BitmapButton(Contents.displayBitmap(smile.getCode()));
                b.setCookie(smile);
                b.setChangeListener(this);
                b.setFocusListener(this);
                b.setPadding(0,3,0,3);
                man.add(b);
            }
  
            manager.add(man);
            add(manager);
        }
        catch(Exception ex)
        {
        }
        
        // Show popup screen
        UiApplication.getUiApplication().pushModalScreen(this);
    }    
    
    protected void makeMenu(Menu menu, int instance)
    {
        if (instance == Menu.INSTANCE_DEFAULT)
        {
            menu.add(_selectItem);
            menu.add(_closeItem);
        }
    }
    
    private MenuItem _closeItem = new MenuItem("Close", 260, 260) 
    {
        public void run() 
        {
            onClose();
        }
    }; 
    
    private MenuItem _selectItem = new MenuItem("Select", 250, 250) 
    {
        public void run() 
        {
            Field f = me.getLeafFieldWithFocus();
            if(f!=null)
            {
                select(f);
            }
        }
    }; 
    
    public boolean onClose()
    {
        try
        {
           UiApplication.getUiApplication().popScreen(this);
        }
        catch(Exception ex)
        {
        }
        return true;
    }
    
    public void fieldChanged(Field field, int context)
    {
        select(field);
    }
    
    public void focusChanged(Field field, int eventType)
    {
        try
        {
            Field f = this.getLeafFieldWithFocus();
            if(f!=null)
            {
                if(f instanceof BitmapButton)
                {
                    BitmapButton b = (BitmapButton) field;
                    final SmileObject smile = (SmileObject) b.getCookie();
                    UiApplication.getUiApplication().invokeLater(new Runnable() 
                    {
                       public void run() 
                       {
                           label.setText(smile.getDescription());
                       }
                    });
                }
            }
        }
        catch(Exception ex)
        {
        }
    }
    
    public void select(Field field)
    {
        if(field instanceof BitmapButton)
        {
            BitmapButton b = (BitmapButton) field;
            SmileObject smile = (SmileObject) b.getCookie();
            if(listener!=null)
            {
                listener.smileSelected(smile.getCode());
            }
            onClose();
        }
    }
}

