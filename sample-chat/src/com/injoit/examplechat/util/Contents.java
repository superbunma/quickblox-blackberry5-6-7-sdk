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
 *
 */

package com.injoit.examplechat.util;

import net.rim.device.api.system.*;

import java.util.Hashtable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Form;
import java.io.InputStream;


public class Contents 
{

        public static Hashtable images;
        public static Hashtable presence; //dictionary: key=protocol presence name, value=customizable presence name
        
        public static String help_text = "Connect: connect to an IM server with jid information saved before\n"
                                  +"User settings: save your userid, password and e-mail.\n"
                                  +"If you are not registered to the server, you will be registrated on the first connection attempt.\n"
                                  +"Web Services: you can check weather conditions, stock quotes or send free sms.";
        public static String chatWarn = "(warning: rooms discovery needs a lot of memory)";
        //Gui Commands
        public static Command ok = new Command("Ok", Command.OK, 1);
        public static Command send = new Command("Send", Command.OK, 1);
        public static Command accept = new Command("Accept", Command.OK, 1);
        public static Command back = new Command("Back", Command.BACK, 0);
        public static Command exit = new Command("Exit", Command.EXIT, 0);
        public static Command disc = new Command("Options", Command.BACK, 0);
        public static Command deny = new Command("Deny", Command.BACK, 0);
        public static Command history = new Command("History", Command.ITEM, 2);
        public static Command active = new Command("Open chat", Command.SCREEN, 2);
        public static Command delete = new Command("Close chat", Command.SCREEN, 3);
        public static Command stop = new Command("Stop", Command.STOP, 0);
        public static Command register = new Command("Register", Command.SCREEN, 1);
        public static Command unregister = new Command("Unregister", Command.SCREEN, 2);
        public static Command chat = new Command("Chat", Command.SCREEN, 1);//NUOVO
        public static Command info = new Command("Info", Command.SCREEN, 2);//NUOVO


        public static Command select = new Command("Select", Command.OK, 1);
        //other gui tools
        public static Alert help = new Alert("Help", help_text, null, AlertType.INFO);
        public static Alert noPhone = new Alert("Warning", "Your device doesn't support this feature!", null, AlertType.WARNING);
        public static Alert noData = new Alert("Warning", "Set your configuration settings first!", null, AlertType.WARNING); //conf data set alert
        public static Alert noJud = new Alert("Warning", "Your Jabber server doesn't support it!", null, AlertType.WARNING);
        public static Alert done = new Alert("Done", "Your request has been sent!", null, AlertType.CONFIRMATION);
        public static Alert noSavedPhone = new Alert("Warning", "Phone number is not saved for this contact", null, AlertType.WARNING);
        public static Alert noGtw = new Alert("Warning", "Gateway name is incorrect", null, AlertType.WARNING);
        public static Alert failGtw = new Alert("Warning", "Registration failed", null, AlertType.WARNING);
        public static Alert subs = new Alert("Warning", "You aren't subscribed to this user, sending the request now..", null, AlertType.WARNING); //NUOVO
        

        public static String[] offlineChoices =  new String[] {"Connect", "User settings", "Web Services", "Help"};
        public static String[] rosterChoices = new String[] {"Change jid", "Delete", "PhoneCall!"};
        public static String[] judChoices = new String[] {"Register", "Search a user"};
        public static String[] sslChoices = new String[] {"Unsecure connection", "SSL connection (port 5223)", "Http connection (port 80)"};
        public static String[] optionsChoices = new String[] { "Disconnect", "Add Contact", "MSN/AIM/ICQ/Yahoo", "Join Multichat", "Change Status", "Accept incoming Wake-up sms","Server Info", "Search users" };
        public static String[] hide = new String[] { "Hide offline contacts", "Show all contacts" };
        static public String[] string_presence = { "offline", "online", "away", "dnd", "unsubscribed" };
        static public String[] mystring_presence = { "offline", "online", "away", "busy"};
        static public String[] online_choices = { "Send message", "Try to subscribe", "Wake-up with SMS"};
        static public String[] ws_choices = { "Weather Forecast", "Stock Quotes", "Free SMS" };
        public static String[] gtwChoices = new String[] {"MSN Messenger", "AIM", "ICQ", "Yahoo"};
        public static String[] avatarChoices =  new String[] {"jmc blue", "jmc red"};
        
        public static Form subsc_form = new Form("Subscription request");
        public static Form invit_form = new Form("Chat Invitation");
        public static Form offline_form = new Form("Offline Menu");
        public static Form wait_form = new Form("Connecting");
        public static Form options_form = new Form("Other Options");
        
        public static String errorCode = "Operation not executed! ";
        public static String jud_success = "You have been registered to Jud!";
        public static String jud_search = "Jud Search response: ";
        public static String new_convers = "New conversation from ";    
        public static String choose_status = "Choose status";
        public static String saved = "Data saved";
        public static String jid_sintax_error = "No changes: Jid syntax not correct";
        public static String no_changes = "No changes";
        public static String composing = " is typing..";
        public static String inactive = " has closed chat..";
        public static String noRoster = "Sorry, there aren't contacts: go to 'Menu'->'add contact'";
        public static String explainGtw = "You can chat with your contacts from different IM protocols. Select your preferite and insert your credentials.";
        
        
        public Contents() 
        {
            images = new Hashtable(70);
            images.put("online","online.png");
            images.put("offline","offline.png");
            images.put("away","away.png");
            images.put("dnd","dnd.png");
            
            images.put("disconnected","disconnected.png");
            images.put("connected","connected.png");
            
            images.put("message","message.png");
            images.put("unsubscribed", "question_mark.png");
            images.put("1smile1","smile.png");
            images.put("1smile2","sad.png");
            images.put("1smile3", "riso.png");
            images.put("1smile4", "prr.png");
            images.put("logo", "jmc_back.png");
            images.put("choice", "choice.png");
            images.put("chat", "chat.png");
            images.put("icon", "icon.png");
            images.put("jmcAvatar", "jmc.png");
            
            images.put("Ú01", "01.png");
            images.put("Ú02", "02.png");
            images.put("Ú03", "03.png");
            images.put("Ú04", "04.png");
            images.put("Ú05", "05.png");
            images.put("Ú06", "06.png");
            images.put("Ú07", "07.png");
            images.put("Ú08", "08.png");
            images.put("Ú09", "09.png");
            images.put("Ú10", "10.png");
            images.put("Ú11", "11.png");
            images.put("Ú12", "12.png");
            images.put("Ú13", "13.png");
            images.put("Ú14", "14.png");
            images.put("Ú15", "15.png");
            images.put("Ú16", "16.png");
            images.put("Ú17", "17.png");
            images.put("Ú18", "18.png");
            images.put("Ú19", "19.png");
            images.put("Ú20", "20.png");
            images.put("Ú21", "21.png");
            

            presence = new Hashtable(5);
            presence.put("online", string_presence[1]);
            presence.put("unavailable", string_presence[0]);
            presence.put("dnd", string_presence[3]);
            presence.put("unsubscribed", string_presence[4]);
            presence.put("away", string_presence[2]);

            help.setTimeout(9000);
        }
        
        
        public static String getImage(String name)
        {
              return (String)images.get(name);
        }
        
        
        public static Image displayImage(String name) 
        {
            Image image;
            try 
            {
                    image = Image.createImage(getImage(name));
            }
            catch (java.io.IOException e) 
            {
                    return null;
            }
            return image;
        }
        
        public static Bitmap displayBitmap(String name) 
        {
            Bitmap image;
            try 
            {
                    image = Bitmap.getBitmapResource(getImage(name));
            }
            catch (Exception e) 
            {
                    return null;
            }
            return image;
        }
        
        
        public static Image displayImage(InputStream stream) 
        {
            Image image;
            try 
            {
                image = Image.createImage(stream);
            }
            catch (java.io.IOException e) 
            {
                return null;
            }
            return image;
        }
}
