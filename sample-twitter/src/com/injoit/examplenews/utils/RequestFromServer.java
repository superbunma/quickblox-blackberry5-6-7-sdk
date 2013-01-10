package com.injoit.examplenews.utils;

import com.injoit.examplenews.utils.constants.*;

public class RequestFromServer {
        
        private String typeRequest;
        
        private String apikey;
        private String countrycode;
        private String channeltype;
        private String language;
        private String itemId;
        private String itemType;
        private String count;
        private String id;
        
        public RequestFromServer(String typeRequest, String apikey, String countrycode, String channeltype, String language, String itemId, String itemType, String count, String id){
                this.typeRequest = typeRequest;
                this.apikey = apikey;
                this.countrycode = countrycode;
                this.channeltype = channeltype;
                this.language = language;
                this.itemId = itemId;
                this.itemType = itemType;
                this.count = count;
                this.id = id;
                
                System.out.println("Constants.GET_NEWS " + Constants.GET_NEWS + "; typeRequest " + typeRequest + "; apikey " + apikey + "; countrycode " + countrycode + "; channeltype " + channeltype + "; language " + language + "; itemId " + itemId + "; itemType " + itemType + "; count " + count + "; id " + id);
        }
        
        public String createRequest(){
                
                if(typeRequest.equals(Constants.GET_NEWS)){
                        return new String(Constants.SERVER_IP + Constants.API + validationNewsHighlightsBillboards(Constants.GET_NEWS));
                } else if(typeRequest.equals(Constants.GET_RELATED_NEWS)){
                        return new String(Constants.SERVER_IP + Constants.API + validationGetRelatedNews(Constants.GET_RELATED_NEWS));
                } else if(typeRequest.equals(Constants.GET_NEWS_ITEM)){
                        return new String(Constants.SERVER_IP + Constants.API + validationNewsItem(Constants.GET_NEWS_ITEM));
                } else if(typeRequest.equals(Constants.GET_HIGHLIGHTS)){
                        return new String(Constants.SERVER_IP + Constants.API + validationNewsHighlightsBillboards(Constants.GET_HIGHLIGHTS));
                } else if(typeRequest.equals(Constants.GET_HIGHLIGHTS_ITEM)){
                        return new String(Constants.SERVER_IP + Constants.API + validationHighlightItemAndBillboardId(Constants.GET_HIGHLIGHTS_ITEM));
                } else if(typeRequest.equals(Constants.GET_BILLBOARDS)){
                        return new String(Constants.SERVER_IP + Constants.API + validationNewsHighlightsBillboards(Constants.GET_BILLBOARDS));
                } else if(typeRequest.equals(Constants.GET_BILLBOARDS_ID)){
                        return new String(Constants.SERVER_IP + Constants.API + validationHighlightItemAndBillboardId(Constants.GET_BILLBOARDS_ID));
                }
                return null;
        }
        
        private String validationGetRelatedNews(String type){
                if (apikey != null && itemId != null && itemType != null && countrycode != null && count != null){
                        return type + "?apikey=" + apikey + "&i=" + itemId + "&it=" + itemType + "&c=" + countrycode + "&l=" + language + "&ct=" + count;
                }
                return null;
        }
        
        private String validationHighlightItemAndBillboardId(String type){
                if(apikey != null && countrycode != null && channeltype != null &&  language != null && id != null){
                        return type + "?apikey=" + apikey + "&c=" + countrycode + "&ch=" + channeltype + "&l=" + language + "&id=" + id;
                }
                return null;
        }
        
        private String validationNewsHighlightsBillboards(String type){
                if (apikey != null && countrycode != null && channeltype != null && language != null){
                        return type + "?apikey=" + apikey + "&c=" + countrycode + "&ch=" + channeltype + "&l=" + language;
                }
                return null;
        }
        
        private String validationNewsItem(String type){
                if(apikey != null && id != null){
                        return type + "?apikey=" + apikey + "&id=" + id;
                }
                return null;
        }
}
