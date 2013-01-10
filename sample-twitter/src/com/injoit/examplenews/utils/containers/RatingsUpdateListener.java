//  @ Project : DSTv TV Guide Application
//  @ File Name : RatingsUpdateListener.java
//  @ Date : 25/09/2012
//  @ Author : Vladimir Slatvinskyi
//

package com.injoit.examplenews.utils.containers;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.util.*;


public interface RatingsUpdateListener
{ 
    public abstract void RatingsUpdateError(String text);
    public abstract void RatingsUpdated(); 
} 
