/**
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package com.injoit.examplechat.jabber.roster;

import java.util.Vector;
import com.injoit.examplechat.util.Datas;
import com.injoit.examplechat.jabber.roster.Jid;
import com.injoit.examplechat.xmlstreamparser.Node;


/**
 * JUD support class 
 * 
 * @author Gabriele Bianchi
 * Modified by Vladimir Slatvinskiy
 */
public class Jud 
{

    /**
    * registration to jud
    * @param jid name surname
    */
    public static void setRegistration(Jid jid, String name, String surname, String jud_server) 
    {
        String xml = "<iq type='set' from='"+jid.getFullJid()+"' to='"+jud_server+"' id='jud_reg'><query xmlns='jabber:iq:register'><name>"+jid.getUsername()+"</name><first>"+name+"</first><last>"+surname+"</last><nick>"+jid.getUsername()+"</nick><email>"+jid.getMail()+"</email></query></iq>";
        Datas.writerThread.write(xml);
    }  
    
    /**
        * get the jid info found
        * @param node
        * @return the jid
        */
    public static String getJidfromResponse(Node node) 
    {
            Node query = node.getChild("query");
            StringBuffer result = new StringBuffer();
            Vector items = query.getChildren();
            for (int i=0;i<items.size();i++) {
                    Node n = (Node)items.elementAt(i);
                    if (n.name.equals("item")) { //PATCH 2008
                            if (n.getValue("jid") != null)
                                    result.append("jid: ").append(n.getValue("jid")).append("\n");
                            if (n.getChild("name") != null)
                                    result.append("username: ").append(n.getChild("name").text).append("\n");
                            if (n.getChild("first") != null)
                                    result.append("name: ").append(n.getChild("first").text).append("\n");
                            if (n.getChild("last") != null)
                                    result.append("surname: ").append(n.getChild("last").text).append("\n");
                            if (n.getChild("nick") != null)
                                    result.append("nick: ").append(n.getChild("nick").text).append("\n");
                            if (n.getChild("email") != null)
                                    result.append("mail: ").append(n.getChild("email").text).append("\n").append("\n");
                            
                    }
            }
            return result.toString();
    }
        
        
    /**
        * get the jid found
        * @param params order: username, first, last, nick, mail; if empty ""
        * @return the jid
        */
    public static void searchJid(Vector params, String jud_server) 
    {
            //stringa da testare
            String xml = "<iq type='set' id='jud_search' to='"+jud_server+"' ><query xmlns='jabber:iq:search' ><name>"+params.firstElement()+"</name><first>"+params.elementAt(1)+"</first><last>"+params.elementAt(2)+"</last><nick>"+params.elementAt(3)+"</nick><email>"+params.elementAt(4)+"</email></query></iq>";
            Datas.writerThread.write(xml);  //il from??
    }
}
