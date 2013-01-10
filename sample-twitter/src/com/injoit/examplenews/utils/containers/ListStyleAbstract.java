//  @ Project : DSTv TV Guide Application
//  @ File Name : ListStyleAbstract.java
//  @ Date : 10/06/2012
//  @ Author : Vladimir Slatvinskyi
//

package com.injoit.examplenews.utils.containers;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;

public abstract interface ListStyleAbstract 
{
    public static int DRAWPOSITION_TOP = 0;
    public static int DRAWPOSITION_BOTTOM = 1;
    public static int DRAWPOSITION_MIDDLE = 2;
    public static int DRAWPOSITION_SINGLE = 3;
    
    public abstract  void setDrawPosition( int drawPosition );
} 
