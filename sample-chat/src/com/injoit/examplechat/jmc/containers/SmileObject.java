package com.injoit.examplechat.jmc.containers;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;

public class SmileObject 
{
    private String code = "";
    private String description = "";
    
    public SmileObject(String _code, String _description)
    {
        code = _code;
        description = _description;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String val)
    {
        code = val;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String val)
    {
        description = val;
    }
}
