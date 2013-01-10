package com.injoit.examplenews.utils;

import net.rim.device.api.util.Persistable;

public class BillboardItem implements Persistable {
        
        private String id;
        private String title;
        private String channel;
        private String channelNumber;
        private String channelLogoUrl;
        private long date;
        private long startDate;
        private long endDate;
                
        private boolean isFavorite = false;
        
        private String body;
        private String largeImageURL;
        private byte[] largeImage;
        private int rating = 0;
               
        private String imageUrl;
        private byte[] channelLogo;
        private byte[] image;
        
        private boolean isRated = false;
        private long ratingsUpdateStamp = 0L;
        
        public long getRatingsUpdateTime()
        {
            return this.ratingsUpdateStamp;
        }
        
        public void setRatingsUpdateStamp(long val)
        {
            this.ratingsUpdateStamp = val;
        }

        public boolean isRated()
        {
            return this.isRated;
        }
        
        public void setRated(boolean val)
        {
            this.isRated = val;
        }
        
        public void setServerRating(int value)
        {
            this.rating = value;
        }
        
        public String getId() {
                return id;
        }
        public void setId(String id) {
                this.id = id;
        }
        public String getTitle() {
                return title;
        }
        public void setTitle(String title) {
                this.title = title;
        }
        public String getChannel() {
                return channel;
        }
        public void setChannel(String channel) {
                this.channel = channel;
        }
        public String getChannelNumber() {
                return channelNumber;
        }
        public void setChannelNumber(String channelNumber) {
                this.channelNumber = channelNumber;
        }
        public String getChannelLogoUrl() {
                return channelLogoUrl;
        }
        public void setChannelLogoUrl(String channelLogoUrl) {
                this.channelLogoUrl = channelLogoUrl;
        }
        public long getDate() {
                return date;
        }
        public void setDate(long date) {
                this.date = date;
        }
        public long getStartDate() {
                return startDate;
        }
        public void setStartDate(long date) {
                this.startDate = date;
        }
        public long getEndDate() {
                return endDate;
        }
        public void setEndDate(long date) {
                this.endDate = date;
        }
        public String getImageUrl() {
                return imageUrl;
        }
        public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
        }
        public byte[] getChannelLogo() {
                return channelLogo;
        }
        public void setChannelLogo(byte[] channelLogo) {
                this.channelLogo = channelLogo;
        }
        public byte[] getImage() {
                return image;
        }
        public void setImage(byte[] image) {
                this.image = image;
        }
        
        public String getBody() {
                return body;
        }
        public void setBody(String body) {
                this.body = body;
        }
        
        public String getLargeImageURL() {
                return largeImageURL;
        }
        public void setLargeImageURL(String largeImageURL) {
                this.largeImageURL = largeImageURL;
        }
        
        public byte[] getLargeImage() {
                return largeImage;
        }
        public void setLargeImage(byte[] largeImage) {
                this.largeImage = largeImage;
        }
        
        public int getRating() {
                return rating;
        }
        public void setRating(int rating) {
                this.rating = rating;
                isRated = true;
        }
        
        public boolean isFavorite()
        {
            return this.isFavorite;
        }
        
        public void setFavorite(boolean value)
        {
            this.isFavorite = value;
        }
        
        public boolean equals(Object obj) {
                return obj instanceof BillboardItem && this.id == ((BillboardItem) obj).getId();
        }

}
