package com.injoit.examplenews.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import net.rim.device.api.collection.util.*;

import com.injoit.examplenews.utils.constants.Constants;
import com.injoit.examplenews.utils.json.me.*;
import com.injoit.examplenews.utils.*;
import com.injoit.examplenews.news.*;
import com.injoit.examplenews.utils.datastorages.*;

public class ContentProvider extends HttpClient implements Constants {
        
        private static ContentProvider instance;
        
        static
        {
        }
        
        public static ContentProvider getInstance() 
        {
            if (instance == null) 
            {
                instance = new ContentProvider();
            }
            return instance;
        }
        
        public void fetchBillboards(final FetchItemListener listener)
        {
                InputStream stream = null;
                InputConnection connection = null;
                try 
                {
                        String url = "http://197.80.203.118/api/getbillboards?apikey=bda11d91-7ade-4da1-855d-24adfe39d174&c=ZA&ch=Dstv.com&l=english";
                        connection = openConnection(url);
                        DebugStorage.getInstance().Log(0, "<ContentProvider> Opening connection to URL=" + url);
                        if (connection == null) 
                        {
                            DebugStorage.getInstance().Log(0, "<ContentProvider> Cannot open connection (fetchBillboards) ");
                            DialogHelper.alert("Cannot open connection");
                            return;
                        }
                        stream = connection.openInputStream();
                        InputStreamJSONTokener tokenizer = new InputStreamJSONTokener(stream);
                        
                        JSONParseListener parseListener = new JSONParseListener() 
                        {
                            private int index = 0; 
                            public boolean valueParsed(Object object) throws JSONException  
                            {
                                JSONObject _object = (JSONObject) object;
                                BillboardItem item = new BillboardItem();
                                item.setId(_object.getString("Id"));
                                try
                                {
                                    item.setTitle(new String(_object.getString("Title").getBytes(),"UTF-8"));
                                }
                                catch(Exception ex)
                                {
                                }
                                try
                                {
                                    item.setChannel(new String(_object.getString("ChannelName").getBytes(),"UTF-8"));
                                }
                                catch(Exception ex)
                                {
                                }
                                try
                                {
                                    item.setBody(new String(_object.getString("Abstract").getBytes(),"UTF-8"));
                                }
                                catch(Exception ex)
                                {
                                }
                                item.setChannelNumber(_object.getString("ChannelNo"));
                                item.setDate(TextUtils.parseDate(_object.getString("Eventdate")));
                                item.setStartDate(TextUtils.parseDate(_object.getString("Startdate")));
                                item.setEndDate(TextUtils.parseDate(_object.getString("Enddate")));                          
                                item.setImageUrl(SERVER_IMAGES + _object.getString("LargeImage"));
                                item.setLargeImageURL(SERVER_IMAGES + _object.getString("LargeImage"));
                                return listener.itemFetched(item, index++);
                            }
                        };
                        new ScrollableJSONArray(tokenizer, parseListener);
                } 
                catch (IOException e) 
                {
                    DebugStorage.getInstance().Log(0, "<ContentProvider> getbillboards IOException: " + e.toString());
                    //DialogHelper.alert(e.toString());
                } 
                catch (JSONException e) 
                {
                    DebugStorage.getInstance().Log(0, "<ContentProvider> getbillboards JSONException: " + e.toString());
                    //DialogHelper.alert(e.toString());
                } 
                finally 
                {
                    if (stream != null) 
                    {
                        try 
                        {
                            stream.close();
                        } 
                        catch (IOException e) 
                        {
                        }
                    }
                    if (connection != null) 
                    {
                        try 
                        {
                            connection.close();
                        } 
                        catch (IOException e) 
                        {
                        }
                    }
                }
        };
        
        public void fetchHighlights(final FetchItemListener listener){
                InputStream stream = null;
                InputConnection connection = null;
                try {
                        String url = "http://197.80.203.118/api/gethighlights?apikey=bda11d91-7ade-4da1-855d-24adfe39d174&c=ZA&ch=Dstv.com&l=english";
                        DebugStorage.getInstance().Log(0, "<ContentProvider> Opening connection to URL=" + url);
                        connection = openConnection(url);
                        //connection = (InputConnection)Connector.open("http://dstvapps.dstv.com/api/gethighlights?apikey=bda11d91-7ade-4da1-855d-24adfe39d174&c=ZA&ch=Dstv.com&l=english");
                        if (connection == null) 
                        {
                            DebugStorage.getInstance().Log(0, "<ContentProvider> Cannot open connection (fetchHighlights) ");
                            DialogHelper.alert("Cannot open connection");
                            return;
                        }
                        stream = connection.openInputStream();
                        InputStreamJSONTokener tokenizer = new InputStreamJSONTokener(stream);
                        
                        JSONParseListener parseListener = new JSONParseListener() {
                                
                                private int index = 0; 
                                
                                public boolean valueParsed(Object object) throws JSONException  {
                                        JSONObject jsonObject = (JSONObject) object;
                                        HighlightItem item = new HighlightItem();
                                        item.setId(jsonObject.getString("Id"));
                                        try
                                        {
                                            item.setTitle(new String(jsonObject.getString("Title").getBytes(),"UTF-8"));
                                        }
                                        catch(Exception ex)
                                        {
                                        }
                                        try
                                        {
                                            item.setChannel(new String(jsonObject.getString("ChannelName").getBytes(),"UTF-8"));
                                        }
                                        catch(Exception ex)
                                        {
                                        }
                                        try
                                        {
                                            item.setBody(new String(TextUtils.cleanHtml(jsonObject.getString("MobileBody")).getBytes(),"UTF-8"));
                                        }
                                        catch(Exception ex)
                                        {
                                        }
                                        item.setChannelNumber(jsonObject.getString("ChannelNo"));
                                        item.setDate(TextUtils.parseDate(jsonObject.getString("EventDate")));
                                        item.setImageURL(SERVER_IMAGES + jsonObject.getString("ImageUrl"));
                                        item.setLargeImageURL(SERVER_IMAGES + jsonObject.getString("LargeImageUrl"));
                                        return listener.itemFetched(item, index++);
                                }
                        };
                        new ScrollableJSONArray(tokenizer, parseListener);
                } 
                catch (IOException e) 
                {
                    DebugStorage.getInstance().Log(0, "<ContentProvider> gethighlights IOException: " + e.toString());
                   // DialogHelper.alert(e.toString());
                } 
                catch (JSONException e) 
                {
                    DebugStorage.getInstance().Log(0, "<ContentProvider> gethighlights JSONException: " + e.toString());
                    DialogHelper.alert(e.toString());
                } 
                finally 
                {
                    if (stream != null) 
                    {
                        try 
                        {
                            stream.close();
                        } 
                        catch (IOException e) 
                        {
                        }
                    }
                    if (connection != null) 
                    {
                        try 
                        {
                            connection.close();
                        } 
                        catch (IOException e) 
                        {
                        }
                    }
                }
        };
        
        public void fetchNews(final FetchItemListener listener) {
                InputStream stream = null;
                InputConnection connection = null;
                try {
                        String url = new RequestFromServer(
                                        Constants.GET_NEWS, "bda11d91-7ade-4da1-855d-24adfe39d174",
                                        "za", "dstv.com", "english", null, null, null, null)
                                        .createRequest();
                                        
                        DebugStorage.getInstance().Log(0, "<ContentProvider> Opening connection to URL=" + url);
                        
                        connection = openConnection(url);
                        
                        if (connection == null) 
                        {
                            DebugStorage.getInstance().Log(0, "<ContentProvider> Cannot open connection (fetchNews) ");
                            DialogHelper.alert("Cannot open connection");
                            return;
                        }
                        
                        stream = connection.openInputStream();
                        InputStreamJSONTokener tokenizer = new InputStreamJSONTokener(stream);
                        
                        JSONParseListener parseListener = new JSONParseListener() {
                                
                                private int index = 0; 
                                
                                public boolean valueParsed(Object object) throws JSONException  {
                                        JSONObject jsonObject = (JSONObject) object;
                                        NewsItem item = new NewsItem();
                                        item.setId(jsonObject.getInt("Id"));
                                        try
                                        {
                                            item.setTitle(new String(jsonObject.getString("Title").getBytes(),"UTF-8"));
                                        }
                                        catch(Exception ex)
                                        {
                                        }
                                        try
                                        {
                                            item.setBody(new String(TextUtils.cleanHtml(jsonObject.getString("MobileBody")).getBytes(),"UTF-8"));
                                        }
                                        catch(Exception ex)
                                        {
                                        }
                                        item.setImageUrl(SERVER_IMAGES + jsonObject.getString("ImageFile"));
                                        item.setCreatedBy(jsonObject.getString("CreatedBy"));
                                        item.setCreated(TextUtils.parseDate(jsonObject.getString("PublishDate")));
                                        return listener.itemFetched(item, index++);
                                }
                        };
                        new ScrollableJSONArray(tokenizer, parseListener);
                } 
                catch (IOException e) 
                {
                    DebugStorage.getInstance().Log(0, "<ContentProvider> fetchNews IOException: " + e.toString());
                    //DialogHelper.alert(e.toString());
                } 
                catch (JSONException e) 
                {
                    DebugStorage.getInstance().Log(0, "<ContentProvider> fetchNews JSONException: " + e.toString());
                    //DialogHelper.alert(e.toString());
                } 
                finally 
                {
                    if (stream != null) 
                    {
                        try 
                        {
                            stream.close();
                        } 
                        catch (IOException e) 
                        {
                        }
                    }
                    if (connection != null) 
                    {
                        try 
                        {
                            connection.close();
                        } 
                        catch (IOException e) 
                        {
                        }
                    }
                }
        }
}
