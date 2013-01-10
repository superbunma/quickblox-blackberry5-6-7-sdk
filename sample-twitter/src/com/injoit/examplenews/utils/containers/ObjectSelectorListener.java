//  @ Project : DSTv TV Guide Application
//  @ File Name : ObjectSelectorListener.java
//  @ Date : 22/06/2012
//  @ Author : Vladimir Slatvinskyi
//

package com.injoit.examplenews.utils.containers;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;

public abstract interface ObjectSelectorListener 
{
    public abstract  void onObjectSelected( Object _object );
} 
