package com.injoit.examplenews.utils.thirdparty.models;

import net.rim.device.api.system.*;
import java.io.*;
import java.util.*;
import net.rim.device.api.util.*;
import net.rim.device.api.io.http.*;
import net.rim.device.api.i18n.*;
import net.rim.device.api.util.*;
import net.rim.device.api.collection.util.*;

public class TwitterTokenObject implements Persistable
{
        private String tokenStr;
        private String secretStr;
        private String userIdStr;
        private String usernameStr;
        
        public TwitterTokenObject(String _tokenStr, String _secretStr, String _userIdStr, String _usernameStr)
        {
            this.tokenStr = _tokenStr;
            this.secretStr = _secretStr;
            this.userIdStr = _userIdStr;
            this.usernameStr = _usernameStr;
        }

        public String getToken() 
        {
             return tokenStr;
        }
        
        public String getSecret() 
        {
             return secretStr;
        }
        
        public String getUserId() 
        {
             return userIdStr;
        }
        
        public String getUsername() 
        {
             return usernameStr;
        }
}
