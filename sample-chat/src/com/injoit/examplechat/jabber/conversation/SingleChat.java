/**
 * MicroJabber, jabber for light java devices. Copyright (C) 2004, Gregoire Athanase
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with 
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, 
 * Suite 330, Boston, MA 02111-1307 USA.
 */

package com.injoit.examplechat.jabber.conversation;

import com.injoit.examplechat.xmlstreamparser.*;
import com.injoit.examplechat.jabber.roster.*;
import com.injoit.examplechat.util.Datas;
import com.injoit.examplechat.util.Util;

/**
 * Class for chat one to one
 */
public class SingleChat extends Chat {
    public Jid roster; // the person we are talking with
    boolean isPrivate = false;
    
    /**
     * Modified by Gabriele Bianchi 04/01/2006
     * Modified by Vladimir Slatvinskiy
     * @param _roster
     * @param _stanzaType
     * @param _threadId
     */
    public SingleChat(Jid _roster, String _stanzaType, String _threadId) 
    {
        super(_roster.getUsername(), _stanzaType, _threadId);
        roster = _roster;
        
        this.avatar = Jid.createAvatar(roster.getAvatar());
    }
    
    public SingleChat(Jid _roster, String _stanzaType, String _threadId, boolean _isPrivate) 
    {
        super(_roster.getUsername(), _stanzaType, _threadId);
        roster = _roster;
        isPrivate = _isPrivate;
        
        this.avatar = Jid.createAvatar(roster.getAvatar());
    }

    
    /**
     * @param _message
     */
    public void broadcast(Message _message) 
    {

         StringBuffer res = new StringBuffer("<message type='")
                                              .append(stanzaType).append("' from = '")
                                              .append(Datas.jid.getFullJid())
                                              .append("' to ='")
                                              .append(Util.formatGtwAddress(roster.getFullJid()))
                                              .append("'>")
                                              .append(_message.getTextAsXML());
        if (!threadId.equals("")) 
        {
            res.append("<thread>").append(threadId).append("</thread>");
        }
        res.append("</message>");

        com.injoit.examplechat.util.Datas.writerThread.write(res.toString());
    }
    
    /**
     * Tests if the conversation matches the given message stanza
     * @param _node
     * @return boolean
     */
    public boolean match(Node _node) 
    {
        boolean res = false;
        if (Jid.getLittleJid(_node.getValue("from")).equals(roster.getLittleJid())) 
        {
            // should also test the threadID? stanzaType?
            res = true;
        }
        return res;
    }
    
}
