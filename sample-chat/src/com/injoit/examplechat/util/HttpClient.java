/**
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package com.injoit.examplechat.util;

import javax.microedition.io.*;
import java.io.IOException;
import java.io.InputStream;

public class HttpClient
{
        /**
         * Send params with a get request
         * @param url
         */
        public static String getViaHttpConnection(String url) throws IOException {
         HttpConnection c = null;
         InputStream is = null;
         int rc;
                 String result = null;
         try {
             c = (HttpConnection)Connector.open(url);

             
             rc = c.getResponseCode();
             if (rc != HttpConnection.HTTP_OK) {
                 throw new IOException("HTTP response code: " + rc);
             }

             is = c.openInputStream();
                         result = new String();
           
             int ch;
             while ((ch = is.read()) != -1) {
                                 result += (char)ch;
             }
             
         } catch (ClassCastException e) {
             throw new IllegalArgumentException("Not an HTTP URL");
         } finally {
             if (is != null)
                 is.close();
             if (c != null)
                 c.close();
         }
                 return Util.unescapeCDATA(result);
     }

 }
