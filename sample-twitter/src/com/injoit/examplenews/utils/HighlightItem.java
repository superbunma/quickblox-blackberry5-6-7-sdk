package com.injoit.examplenews.utils;

import net.rim.device.api.util.Persistable;

public class HighlightItem implements Persistable {
        
        private String id;
        private String title;
        private String channel;
        private String channelNumber;
        private long date;
        private boolean isFavorite = false;
        private String body;
        
        private String imageURL;
        private String largeImageURL;
        
        private byte[] image;
        private byte[] largeImage;
        
        private int rating = 0;
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
        public void setId(String _id) {
                this.id = _id;
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
        public long getDate() {
                return date;
        }
        public void setDate(long date) {
                this.date = date;
        }
        public String getBody() {
                return body;
        }
        public void setBody(String body) {
                this.body = body;
        }
        public String getImageURL() {
                return imageURL;
        }
        public void setImageURL(String imageURL) {
                this.imageURL = imageURL;
        }
        public String getLargeImageURL() {
                return largeImageURL;
        }
        public void setLargeImageURL(String largeImageURL) {
                this.largeImageURL = largeImageURL;
        }
        public byte[] getImage() {
                return image;
        }
        public void setImage(byte[] image) {
                this.image = image;
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
                return obj instanceof HighlightItem && this.id == ((HighlightItem) obj).getId();
        }
        
}
