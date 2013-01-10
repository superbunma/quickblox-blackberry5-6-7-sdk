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
package com.injoit.examplechat.jmc;

import com.injoit.examplechat.jmc.media.*;
import com.injoit.examplechat.jabber.conversation.*;
import com.injoit.examplechat.jabber.roster.Jid;
import com.injoit.examplechat.jabber.JabberListener;
import com.injoit.examplechat.jabber.presence.*;
import com.injoit.examplechat.jabber.subscription.*;
import com.injoit.examplechat.util.Datas;
import com.injoit.examplechat.util.Contents;

import javax.microedition.lcdui.*;
import java.util.Hashtable;

import net.rim.device.api.util.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.collection.util.*;
import net.rim.device.api.ui.Graphics;

public class ChatEventListener implements JabberListener
{
        private ChatManager chat_manager;
        public ScreenManager display; 
        private Hashtable infopool;
        

        final static int OFFLINE         = 0;
        final static int ONLINE          = 1;
        final static int ROSTER          = 2;
        final static int CONVERSATION    = 3;
        final static int SUBSCRIPTION    = 4;   
        final static int PARAMS          = 5;
        final static int WAIT_CONNECT    = 6;
        final static int WAIT_DISCONNECT = 7;
        final static int ROSTER_DETAILS  = 8;
        final static int MULTI_CHAT      = 9;
        final static int INVITATION      = 10;
        final static int OPTIONS         = 11;
        final static int JUD             = 12;

        public ChatEventListener(ChatManager _chat_manager) 
        {
            chat_manager = _chat_manager;
            infopool = chat_manager.infopool;    
        }

        /**
         * Event method invoked by CommunicationManager when connection is established
         * 
         */
        public void connectedEvent() 
        {
            chat_manager.getGuiOnlineMenu();
            chat_manager.internal_state = ONLINE;
        }
        
        /**
         * @param reason
         */
        public void unauthorizedEvent(String reason) 
        {
            chat_manager.getGuiOfflineMenu(); 
            Datas.readRoster = false;
            chat_manager.internal_state = OFFLINE;
            display.showAlert("Disconnected!");
        }
        
        /**
         * Event method invoked by CommunicationManager when disconnection occurs
         * 
         */
        public void disconnectedEvent() 
        {
            chat_manager.getGuiOfflineMenu(); 
            Datas.readRoster = false;
            chat_manager.internal_state = OFFLINE;
            
            display.showAlert("You have been disconnected!");
        }
        
        /**
         * ConversationListener method for the chat notification error
         *@param Conversation
         *@param Message
         */
        public void notifyError(Conversation _conversation, Message _errorMessage) 
        {
            // should insert&display the error in the conversation history?
            String text = _errorMessage.getText();
            display.showAlert("Error: " + text);
        }
        
        public void DiscoverRooms()
        {
            if(chat_manager.multichatScreen !=null)
            {
                chat_manager.multichatScreen.updateScreen();
            }
        }
        
        public void RoomConfiguration()
        {
            chat_manager.getConfigurationScreen(); 
        }
        
        /**
         * Error in registration to server
         *@param Exception
         *
         */
        public void registrationFailure(Exception e, boolean offline) 
        {
            if (offline)
            {
                chat_manager.getGuiOfflineMenu();
                chat_manager.internal_state = OFFLINE;
                
                display.showAlert("Disconnected...");
            }
            else
            {
                display.showAlert("Registration failed!");
            }
        } 
        
        /**
         * ConversationListener method for a new chat start notification 
         *@param Conversation
         *
         */
        public void newConversationEvent(Conversation _conv) 
        {
        
                System.out.println("NEW_CONVERSATION_EVENT name= " + _conv.name);
                if (chat_manager.internal_state == ONLINE) 
                {                   
                    chat_manager.currentConversation = _conv;
                    chat_manager.internal_state = CONVERSATION;
                    if(chat_manager.currentConversation.isMulti == false)
                    {
                        chat_manager.getGuiConversation();
                    }
                    else
                    {
                        chat_manager.getGuiConversationWithoutMessages();
                    }
                } 
                else if ((chat_manager.internal_state == CONVERSATION) && (_conv == chat_manager.currentConversation)) 
                {
                        // update if its the current conversation
                        chat_manager.getGuiUpdateConversation();
                } 
                else if (chat_manager.internal_state == MULTI_CHAT || chat_manager.internal_state == INVITATION) 
                {
                        chat_manager.currentConversation = _conv;
                        chat_manager.internal_state = CONVERSATION;
                        chat_manager.getGuiConversation();
                }
                else if (chat_manager.internal_state == ROSTER) 
                {
                      System.out.println("---------------------- New ROSTER Message! ---------------------");
                }
                else 
                {
                }
                System.out.println("end of event");
        }
        
        /**
         * ConversationListener method for a new message arrival from the server
         *@param Conversation
         *
         */
        public void newMessageEvent(Conversation _conv) 
        {
            System.out.println("----------NEW_MESSAGE_EVENT---------------");
            if(_conv != null)
            {
                String title = "from \n" + _conv.name;
            
                Message lastMessage = (Message) _conv.messages.lastElement();
                String text = lastMessage.getText();
                
                if ((chat_manager.internal_state == CONVERSATION) && (_conv == chat_manager.currentConversation)) 
                {
                       chat_manager.getGuiUpdateConversation(lastMessage);
                } 
                else 
                {
                    if(chat_manager.multichatScreen != null)
                    {
                        chat_manager.multichatScreen.updateScreen();
                    }
                    else if(chat_manager.onlineScreen!=null)
                    {
                        chat_manager.onlineScreen.updateScreen();
                        // send an alert
                        display.showAlert("New Message " + title);
                        MediaPlayer.playResource("/new.wav", MediaPlayer.TYPE_WAV);
                    }
                }
            }
            else
            {
                if(chat_manager.multichatScreen != null)
                {
                    chat_manager.multichatScreen.updateScreen();
                }
                else if(chat_manager.onlineScreen!=null)
                {
                    chat_manager.onlineScreen.updateScreen();
                }
            }
        }
        
        /**
         * ConversationListener method for a new message arrival from the server
         *@param Conversation
         *
         */
        public void newComposingEvent(Conversation _conv) 
        {
            System.out.println("NEW_Composing_EVENT");
            if ((chat_manager.internal_state == CONVERSATION) && (_conv == chat_manager.currentConversation)) 
            {
                chat_manager.getGuiUpdateConversation();
            } 
        }
        
        /**
         * ConversationListener method for a new invitation arrival from a jid
         *@param from, room
         *
         */
        public void newInvitationEvent(String from, String room) 
        {
            System.out.println("NEW_INVITATION_EVENT");
            
            infopool.put("invit_from", from);
            infopool.put("invit_room", room);
            infopool.put("invit_internal_state", new Integer(chat_manager.internal_state));
            chat_manager.internal_state = INVITATION;
            chat_manager.getGuiChoose("invitation");
        } 
        
        public void newInvitationEvent(String from, String room, String reason) 
        {
            System.out.println("NEW_INVITATION_EVENT_WITH_REASON");
            
            infopool.put("invit_from", from);
            infopool.put("invit_room", room);
            infopool.put("invit_reason", reason);
            infopool.put("invit_internal_state", new Integer(chat_manager.internal_state));
            chat_manager.internal_state = INVITATION;
            chat_manager.getGuiChoose("invitation");
        } 
        
        /**
         * PresenceListener method for presence notifications 
         *@param Jid
         *@param String
         */
        public void notifyPresence(Jid _roster, String _presence) 
        {
                // need to update the display?
                
                if (_presence.equals("online") || _presence.equals("unavailable") || _presence.equals("away") || _presence.equals("dnd")) 
                {
                        if (_presence.equals("unavailable") && Datas.isGateway(_roster.getServername()))
                        {
                                if (_roster.getUsername() == null)
                                {

                                        display.showAlert("Warning: Registration failed");
                                        Datas.roster.remove(_roster.getLittleJid());
                                }
                                else
                                {
                                        Jid rost = (Jid)Datas.roster.get(_roster.getLittleJid());
                                        if (rost != null)  rost.setPresence(Presence.getPresence(_presence));
                                        return;
                                }
                        }
                        else if ((chat_manager.internal_state == ROSTER) && (_roster.getLittleJid().equals(chat_manager.currentjid.getLittleJid()))) 
                        {
                                if (_roster.status_message.equals(""))
                                        chat_manager.currentjid.setPresence(Presence.getPresence(_presence));
                                else
                                        chat_manager.currentjid.setPresence(Presence.getPresence(_presence), _roster.status_message);
                                chat_manager.getGuiRosterItem();
                                
                        } 
                        else if (chat_manager.internal_state == ONLINE)  
                        {
                                Jid rost = (Jid)Datas.roster.get(_roster.getLittleJid());
                                if (rost != null)
                                {
                                        if (_roster.status_message.equals(""))
                                                rost.setPresence(Presence.getPresence(_presence));
                                        else
                                                rost.setPresence(Presence.getPresence(_presence), _roster.status_message);
                                }
                                chat_manager.getGuiOnlineMenu();
                        }
                        else 
                        {
                                // send an alert
                                String real_presence = Presence.getPresence(_presence);
                                Jid rost = (Jid)Datas.roster.get(_roster.getLittleJid());
                                if (rost != null)
                                {
                                        if (_roster.status_message.equals(""))
                                                rost.setPresence(real_presence);
                                        else
                                                rost.setPresence(real_presence, _roster.status_message);
                                }
                        }
                }
                else if (_presence.equals("unsubscribed")) 
                {
                        if (Datas.isGateway(_roster.getServername())) 
                        {
                                return;//send an alert?
                        }
                        if (Datas.roster.get(_roster.getLittleJid()) == null)  
                        {
                                _roster.setPresence(Presence.getPresence("unsubscribed"));
                                Datas.registerRoster(_roster);
                        }                        
                        chat_manager.getGuiOnlineMenu();//added!
                        display.showAlert(_roster.getLittleJid()+" added!" + "\nSubscription not accepted/pending.");
                        
                }
                else if (_presence.equals("subscribed")) 
                {
                        if ((chat_manager.currentjid = (Jid)Datas.roster.get(_roster.getLittleJid())) == null)  
                        {
                                Datas.registerRoster(_roster);
                                chat_manager.currentjid = _roster;
                        }
                        else 
                        {
                                chat_manager.currentjid.setPresence("subscribed");
                        }

                        chat_manager.getGuiOnlineMenu();
                        
                        display.showAlert(_roster.getUsername() + " subscribed!" + "\nChanges Saved");
                }
                else if (_presence.equals("subscribe")) 
                {
                        //save the state
                        if (chat_manager.currentjid != null && !chat_manager.currentjid.getLittleJid().equals(_roster.getLittleJid()))
                        
                        infopool.put("currentjid", new Jid(chat_manager.currentjid.getFullJid(), chat_manager.currentjid.getPresence()));
                        infopool.put("internal_state", new Integer(chat_manager.internal_state));
                        chat_manager.internal_state = SUBSCRIPTION;
                        chat_manager.currentjid = _roster;
                        chat_manager.getGuiChoose("subscription");
                } 
        }

        /**
         * RosterListener method for the reading of the roster items list
         *@param Jid
         *@param String
         */
        public void notifyRoster() 
        {
                chat_manager.internal_state = ONLINE;
                chat_manager.getGuiOnlineMenu();
        }
        
        /**
         * RosterListener method for alerting a new jud search response event
         *
         *@param String
         */
        public void notifyJudInfo(String info) 
        {
            infopool.put("jud_message", info);
            if (chat_manager.internal_state != JUD)
            {
                display.showAlert("Jud alert");
            }
            else
            { 
                chat_manager.getGuiJudMenu();
            }
        }
        
        /**
         * Notify an error in presence management
         */
        public void notifyPresenceError(final String code) 
        {
            UiApplication.getUiApplication().invokeLater(new Runnable() 
            {
                public void run() 
                {
                    Status.show("Error: " + code);
                    System.out.println("Error: "+code);
                }
            });
        }
        
        /**
         * Refresh screen
         * 
         */
        public void updateScreen()
        {
               chat_manager.setCurrentDisplay();
        }
        
        public void RequestRoomsMembers()
        {
            Runnable task = new Runnable() 
            {
                public void run() 
                {
                    for(int i=0; i<Datas.rooms.size(); i++)
                    {
                        String roomJid = (String) Datas.rooms.elementAt(i);
                        ChatHelper.RequestMembersCount(roomJid);
                    }
                }
            };
            new Thread(task).run();
        };
        
        public void UpdateRoomMembersCount(String roomName, int membersCount)
        {
             chat_manager.multichatScreen.UpdateRoomMembersCount(roomName, membersCount);
        }
}
