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

package com.injoit.examplechat.jabber.conversation;

import java.util.Vector;
import com.injoit.examplechat.util.Datas;
import com.injoit.examplechat.jabber.conversation.GroupChat;
import com.injoit.examplechat.jabber.roster.Jid;
import com.injoit.examplechat.jabber.conversation.Configuration.*;

/**
 * Multi-chat management class
 * @author Gabriele Bianchi
 * Modified by Vladimir Slatvinskiy
 */
public class ChatHelper {
        
        public static void groupChatCreate(String nick, String chatRoom, String chatServer) 
        { 
            //String sXML = "<iq from='" + Datas.jid.getFullJid() + "' id='create1' to='" + chatRoom + "@" + chatServer + "/" + nick + "' type='set'>" +
            //              "<query xmlns='http://jabber.org/protocol/muc#owner'><x xmlns='jabber:x:data' type='submit'/></query></iq>";
                          
            String sXML = "<iq from='" + Datas.jid.getFullJid() + "' id='create1' to='" + chatRoom + "@" + chatServer + "' type='get'>" +
                          "<query xmlns='http://jabber.org/protocol/muc#owner'/></iq>";
                Datas.writerThread.write(sXML);
        }
        
        public static void groupExistingChatJoin(String nick, String chatRoom, String chatServer)
        {
            //String sXML = "<presence from='" + Datas.jid.getFullJid() + "' to='" + chatRoom + "@" + chatServer + "/" + nick + "'></presence>";
            //String sXML = "<presence to='" + chatRoom + "@" + chatServer + "/" + nick + "'>"  + "<x xmlns='http://jabber.org/protocol/muc'><history maxstanzas='20'/></x></presence>";
            //String sXML = "<presence to='" + chatRoom + "@" + chatServer + "/" + nick + "'>"  + "<x xmlns='http://jabber.org/protocol/muc'><history seconds='1800'/></x></presence>";
            //String sXML = "<presence to='" + chatRoom + "@" + chatServer + "/" + nick + "'>"  + "<x xmlns='http://jabber.org/protocol/muc'><history maxchars='65000'/></x></presence>";
            
            //full history
            String sXML = "<presence to='" + chatRoom + "@" + chatServer + "/" + nick + "'>"  + "<x xmlns='http://jabber.org/protocol/muc'/></presence>";
            
            //history since
            //String sXML = "<presence to='" + chatRoom + "@" + chatServer + "/" + nick + "'>"  + "<x xmlns='http://jabber.org/protocol/muc'><history since='1970-01-01T00:00:00Z'/></x></presence>";

            Datas.writerThread.write(sXML);
        }
        
        public static void groupChatExit(String nick, String chatRoom)
        {
            String sXML = "<presence id='exit2' from='" + Datas.jid.getFullJid() + "' to='" + chatRoom + "/" + nick + "' " + "type='unavailable'" + "></presence>";
            Datas.writerThread.write(sXML);
        }
        
        public static void configRoom(String nick, String chatRoom)
        {
            //String sXML = "<iq from='"  + Datas.jid.getFullJid() + "' id='config1' to='" + chatRoom + "/" + nick + "' type='get'><query xmlns='http://jabber.org/protocol/muc#owner'/></iq>";
            String sXML = "<iq id='config1' to='" + chatRoom +"' type='get'><query xmlns='http://jabber.org/protocol/muc#owner'/></iq>";
            Datas.writerThread.write(sXML);
        }
        
        public static void groupChatSendConfiguration(String nick, String chatRoom, Configuration conf)
        {
            String sXML = "<iq id='config2' to='" + chatRoom + "' type='set'><query xmlns='http://jabber.org/protocol/muc#owner'><x xmlns='jabber:x:data' type='submit'>";
            for(int i=0; i<conf.getFields().size(); i++)
            {
                ConfigField f = (ConfigField) conf.getFields().elementAt(i);
                sXML += f.GetAnswerXml();
            }
            sXML +="</x></query></iq>";
            Datas.writerThread.write(sXML);
        }
        
        public static void deleteRoom(String nick, String chatRoom)
        {
            String sXML = "<iq id='delete1' to='" + chatRoom + "' type='set'><query xmlns='http://jabber.org/protocol/muc#owner'><destroy jid='" + chatRoom + "'><reason>I want to close room!</reason></destroy></query></iq>";
            
            //String sXML = "<iq from='"  + Datas.jid.getFullJid() + "' to='" + chatRoom + "/" + nick + "' type='set'><query xmlns='http://jabber.org/protocol/muc#owner'><destroy><reason>I want to close room!</reason></destroy></query></iq>";
            Datas.writerThread.write(sXML);
        }
        
        public static  void RequestMembersCount(String roomJid)
        {
            String sXML = "<iq id='members1' to='" + roomJid + "' type='get'><query xmlns='http://jabber.org/protocol/disco#items'></query></iq>";
            Datas.writerThread.write(sXML);
        }
        
        /**
         * Chat Service discovery 
         * @param chatServer
         */
        public static void serviceRequest(String chatServer) {
                String res = "<iq from='"+Datas.jid.getFullJid()+"' id='discoRooms' to='"+chatServer+"' type='get'><query xmlns='http://jabber.org/protocol/disco#items'/></iq>";
                Datas.writerThread.write(res);
        }

        /**
         * Create a new multichat object
         * @param jids
         * @param name
         * @param nick
         * @return
         */
        public static GroupChat createChat(Vector jids, String name, String nick) {
                GroupChat chat = new GroupChat(jids, name, "groupchat", "", nick);
                Datas.multichat.put(name, chat);
                return chat;
                
        }
        
         public static GroupChat createChat(Vector jids, String name, String nick, String role, String affiliation) {
                GroupChat chat = new GroupChat(jids, name, "groupchat", "", nick, role, affiliation);
                Datas.multichat.put(name, chat);
                return chat;
                
        }

        /**
         * Add a multichat member
         * @param name
         * @param jid
         */
        public static void addMember(String name, String jid) {
                if (jid.indexOf('@') == -1)
                        jid += "@"+Datas.server_name;
                GroupChat chat = (GroupChat)Datas.multichat.remove(Jid.getLittleJid(name));
                chat.jids.addElement(jid); //PATCH 2008
                Datas.multichat.put(chat.name, chat);
        }

        /**
         * Delete a multichat member
         * @param name
         * @param jid
         */
        public static void deleteMember(String name, String jid) 
        {
            try
            {
                GroupChat chat = (GroupChat)Datas.multichat.remove(Jid.getLittleJid(name));
                //?
                if(chat!=null)
                {
                    chat.jids.removeElement(jid);
                    Datas.multichat.put(chat.name, chat);
                }
                
                /*if(jid.indexOf(Datas.jid.getUsername())!=-1)
                {
                    Vector conv = Datas.conversations;
                    conv.removeElement(chat);
                    Datas.multichat.remove(Jid.getLittleJid(name));
                }*/
                
                if(Datas.conversations.size()>0)
                {
                    Vector conv = Datas.conversations;
                    conv.removeElement(chat);
                }
            }
            catch(Exception ex)
            {
                int a=0;
            }
        }
        /**
         * invite a new multichat member
         * @param room
         * @param jid
         */
        public static void inviteContact (String jid, String room) {
                String res ="<message from=\'"+Datas.jid.getFullJid()+"\' to=\'"+room+"\'><x xmlns=\'http://jabber.org/protocol/muc#user\'><invite to=\'"+jid+"\'>"+
                                "<reason>"+
                                "Hey "+Jid.getLittleJid(jid)+", Come in chat with me!"+
                                "</reason>"+
                                "</invite>"+
                                "</x>"+
                                "</message>";
                Datas.writerThread.write(res);
        }
}
