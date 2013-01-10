package com.injoit.examplenews.news.controls;

import java.util.Vector;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.system.Characters;

import com.injoit.examplenews.utils.*;
import com.injoit.examplenews.news.NewsItem;

public class NewsDetailHeader extends Field {
        
        private static final int PADDING_TOP = Display.getWidth()<=320? 9 : 15;
        private static final int PADDING_RIGHT = Display.getWidth()<=320? 9 : 15;
        private static final int PADDING_BOTTOM = Display.getWidth()<=320? 9 : 15;
        private static final int PADDING_LEFT = Display.getWidth()<=320? 6 : 10;

        private int TEXT_ANCHOR = 160;

        private static final int TITLE_COLOR = 0x1597bd;
        private static final int AUTHOR_COLOR = 0xffe8bb;
        private static final int DATE_COLOR = 0xffffff;
        private static final int FONT_SIZE = 7;
        
        private NewsItem newsItem;
        
        private Bitmap newsImage;
        private Vector title = new Vector();
        
        public NewsDetailHeader(NewsItem newsItem, Bitmap newsImage) 
        {
                this.newsItem = newsItem;
                this.newsImage = newsImage;
                TEXT_ANCHOR = Utils.getFont(7).getHeight() * 4 + PADDING_TOP *2;
        }

        protected void layout(int width, int height) 
        {
                setExtent(getPreferredWidth(), getPreferredHeight());           
        }
        
        protected void paint(Graphics graphics) 
        {
            graphics.setColor(TITLE_COLOR);
            Font titleFont = Utils.getFontBold(FONT_SIZE);
            graphics.setFont(titleFont);
            int textWidht = Display.getWidth() - TEXT_ANCHOR - PADDING_RIGHT;
            if(title.size() ==0)
            {
                title = TextUtils.wrapText(newsItem.getTitle(), textWidht, titleFont);
            }

            for (int i = 0; i < title.size(); i++) 
            {
                graphics.drawText(title.elementAt(i).toString(), TEXT_ANCHOR,PADDING_TOP + i * titleFont.getHeight());
            }
            
            Font font = Utils.getFont(FONT_SIZE);
            graphics.setFont(font);
            graphics.setColor(AUTHOR_COLOR);
            graphics.drawText("by " + newsItem.getCreatedBy(), TEXT_ANCHOR, PADDING_TOP + title.size() * titleFont.getHeight());
            
            if (newsItem.getCreated() != 0) 
            {
                    DateFormat dateFormat = DateFormat.getInstance(DateFormat.DATE_DEFAULT);
                    graphics.setColor(DATE_COLOR);
                    graphics.drawText( dateFormat.formatLocal(newsItem.getCreated()),  TEXT_ANCHOR, PADDING_TOP + title.size() * titleFont.getHeight() + font.getHeight());
            }
            
            if (newsImage != null) 
            {
                try 
                {
                    graphics.drawBitmap(PADDING_LEFT, PADDING_TOP, newsImage.getWidth(), newsImage.getHeight(), newsImage, 0, 0);
                } 
                catch (Exception e) 
                {
                    drawDummyPhoto(graphics);
                }
            } 
            else
            {
                drawDummyPhoto(graphics);
            }
        }
        
        private void drawDummyPhoto(Graphics graphics)
        {
            graphics.setColor(0xcccccc);
            int w = TEXT_ANCHOR - 2*PADDING_TOP;
            graphics.fillRoundRect(PADDING_LEFT, PADDING_TOP, w, w, 10, 10);
            
            graphics.setFont(Utils.getFont(5));
            graphics.setColor(0x999999);
            graphics.drawText("NO PHOTO", (TEXT_ANCHOR - graphics.getFont().getAdvance("NO PHOTO"))/2, (TEXT_ANCHOR - graphics.getFont().getHeight())/2 );
        }

        
        protected void paintBackground(Graphics graphics) 
        {
            int BGR_COLOR =  graphics .isDrawingStyleSet(Graphics.DRAWSTYLE_FOCUS) ? 0x000000 : 0x000000;
            graphics.setColor(BGR_COLOR);
            graphics.fillRect(0,0, getPreferredWidth(), getPreferredHeight());
            graphics.setColor(0x000000);
            graphics.drawLine(0,getPreferredHeight()-1, getPreferredWidth(), getPreferredHeight()-1);
        }

        public int getPreferredHeight() {
                return Utils.getFont(7).getHeight() * 4 + PADDING_TOP *2;
        }

        public int getPreferredWidth() {
                return Display.getWidth();
        }
        
        public boolean isFocusable()
        {
            return isEditable();
        }
        
        protected void drawFocus(Graphics g, boolean on )
        {  
            paint(g);
        };
        
        
        protected boolean keyChar(char character, int status, int time) {
                if (character == Characters.ENTER) {
                        clickButton();
                        return true;
                }
                return super.keyChar(character, status, time);
        }

        protected boolean navigationClick(int status, int time) {
                clickButton();
                return true;
        }

        protected boolean trackwheelClick(int status, int time) {
                clickButton();
                return true;
        }

        protected boolean invokeAction(int action) {
                switch (action) {
                case ACTION_INVOKE: {
                        clickButton();
                        return true;
                }
                }
                return super.invokeAction(action);
        }

        public void clickButton() {
            fieldChangeNotify(0);
        }
        
}  


