//  @ Project : DSTv TV Guide Application
//  @ File Name : BBMScreen.java
//  @ Date : 31/05/2012
//  @ Author : Vladimir Slatvinskyi
//

package com.injoit.examplenews.utils.containers;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.util.*;


public interface RateListener
{
    public abstract void objectRated(Object object, int value);
    public abstract void objectRatedAndShared(Object object, int value);
    public abstract void objectRatedOnQBlox(Object object, int userValue, int serverValue); 
    public abstract void objectRatingError(Object object, int userValue, String text);
} 
