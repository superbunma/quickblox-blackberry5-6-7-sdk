/*
 * RoomInfo.java
 *
 * © <your company here>, <year>
 * Confidential and proprietary.
 */
package com.injoit.examplechat.jmc.containers;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;

public class RoomInfo 
{
    private String name = "";
    private int count = 0;
    
    public RoomInfo(String _name, int _count)
    {
        name = _name;
        count = _count;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String val)
    {
        name = val;
    }
    
    public int getMembersCount()
    {
        return count;
    }
    
    public void setMembersCount(int val)
    {
        count = val;
    }
}
