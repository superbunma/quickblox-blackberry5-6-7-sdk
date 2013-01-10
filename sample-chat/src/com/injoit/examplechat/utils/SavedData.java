package com.injoit.examplechat.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class SavedData {
        private static final long USER_INFO_ID = 0x730a7fda314c41dcL;
        private static final long OPTIONS_ID = 0x6c69ee88659ab5acL;
        
        public static boolean save_account;
        public static boolean use_ssl;
        
        public static void setUserInfo(String username, String password, String user_id) 
        {

                RecordStore store = null;
                int numRecord = -1;
                try 
                {
                        store = RecordStore.openRecordStore("userinfo", true);
                        numRecord = store.getNumRecords();
                } 
                catch (RecordStoreFullException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (RecordStoreNotFoundException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                catch (RecordStoreException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                
                // convert user info into byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream os = new DataOutputStream(baos);
                try 
                {
                        os.writeBoolean(true);
                        os.writeUTF(username);
                        os.writeUTF(password);
                        os.writeUTF(user_id);
                        os.close();
                } 
                catch (IOException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                
                // check if the store is empty
                try 
                {
                        if (numRecord == 0) 
                        {
                                byte[] data = baos.toByteArray();
                                store.addRecord(data, 0, data.length);
                                store.closeRecordStore();
                        } 
                        else 
                        {
                                byte[] data = baos.toByteArray();
                                store.setRecord(1, data, 0, data.length);
                                store.closeRecordStore();
                        }
                }
                catch (RecordStoreNotOpenException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (RecordStoreFullException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (RecordStoreException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
        
        public static Vector getUserInfo() 
        {
                RecordStore store = null;
                int numRecord = -1;
                try 
                {
                        store = RecordStore.openRecordStore("userinfo", true);
                        numRecord = store.getNumRecords();
                } 
                catch (RecordStoreFullException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (RecordStoreNotFoundException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (RecordStoreException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                // empty recordstore
                if (numRecord == 0)
                        return null;
                
                try {
                        byte[] data = store.getRecord(1);
                        DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));
                        boolean saved = is.readBoolean();
                        
                        if (saved) 
                        {
                                String username = is.readUTF();
                                String password = is.readUTF();
                                String user_id = is.readUTF();
                                Vector v = new Vector();
                                v.addElement(username);
                                v.addElement(password);
                                v.addElement(user_id);
                                store.closeRecordStore();
                                return v;
                        }
                        else 
                        {
                                store.closeRecordStore();
                                return null;
                        }
                        
                } 
                catch (RecordStoreNotOpenException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (InvalidRecordIDException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (RecordStoreException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (IOException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return null;
        }
        
        public static void destroyUserInfo() 
        {
                RecordStore store = null;
                int numRecord = -1;
                try 
                {
                        store = RecordStore.openRecordStore("userinfo", true);
                        numRecord = store.getNumRecords();
                        
                        if (numRecord != 0) 
                        {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                DataOutputStream os = new DataOutputStream(baos);
                                
                                os.writeBoolean(false);
                                os.close();
                                byte[] data = baos.toByteArray();
                                store.setRecord(1, data, 0, data.length);
                                store.closeRecordStore();
                        }
                } 
                catch (RecordStoreFullException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (RecordStoreNotFoundException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (RecordStoreException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } 
                catch (IOException e) 
                {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
}
