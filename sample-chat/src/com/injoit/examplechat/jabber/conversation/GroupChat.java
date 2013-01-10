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

import java.util.Vector;
import com.injoit.examplechat.util.Datas;
import com.injoit.examplechat.jabber.conversation.Configuration.*;

public class GroupChat extends Chat
{
  public Vector jids;
  public String nick;
  public String userRole = "";
  public String userAffiliation = "";
  private Configuration configuration;

  public GroupChat(Vector _jids, String _name, String _stanzaType, String _threadId, String _nick) 
  {
      super(_name, _stanzaType, _threadId);
      jids = _jids;
      nick = _nick;
  }
  
  public GroupChat(Vector _jids, String _name, String _stanzaType, String _threadId, String _nick, String role, String aff) 
  {
      super(_name, _stanzaType, _threadId);
      jids = _jids;
      nick = _nick;
      this.userRole = role;
      this.userAffiliation = aff;
  }
  
  public Configuration getConfiguration()
  {
       return this.configuration;
  }  
  
  public void setConfiguration(Configuration value)
  {
       this.configuration = value;
  }  

    /**
     * Send message to members
     * @param Message
     * @author Gabriele Bianchi
     * Modified by Vladimir Slatvinskiy
     */
    public void broadcast(Message _message)
    {
        StringBuffer res = new StringBuffer("<message type='").append(stanzaType).append("' from = '").append(Datas.jid.getFullJid()).append("' to ='").append(name).append("'>").append(_message.getTextAsXML());
        if (!threadId.equals("")) 
        {
            res.append("<thread>").append(threadId).append("</thread>");
        }
        res.append("</message>");

        Datas.writerThread.write(res.toString());
    }  
  
}
