package com.injoit.examplenews.news.controls;

import java.util.Vector;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

import com.injoit.examplenews.utils.Utils;
import com.injoit.examplenews.news.*;
import com.injoit.examplenews.utils.TextUtils;

public class NewsItemButton extends Field 
{

        private static final int PADDING_TOP = Display.getWidth()<=320? 9 : 15;
        private static final int PADDING_RIGHT = Display.getWidth()<=320? 9 : 15;
        private static final int PADDING_BOTTOM = Display.getWidth()<=320? 9 : 15;
        private static final int PADDING_LEFT = Display.getWidth()<=320? 6 : 10;

        private int TEXT_ANCHOR = 0;

        private static final int TITLE_COLOR = 0x1597bd;
        private static final int PRETEXT_COLOR = 0xffffff;
        private static final int FONT_SIZE = 7;

        private NewsItem newsItem;
        private Bitmap newsImage;

        private Vector title;
        private Vector text;

        public NewsItemButton(NewsItem newsItem) 
        {
            this.newsItem = newsItem;
            TEXT_ANCHOR = (Utils.getFont(7).getHeight() * 4 + PADDING_TOP *2);
            setEditable(true);
        }

        protected void layout(int width, int height) 
        {
            setExtent(getPreferredWidth(), getPreferredHeight());
        }

        public int getPreferredHeight() 
        {
            return TEXT_ANCHOR;
        }

        public int getPreferredWidth()
        {
            return Display.getWidth();
        }

        protected void paint(Graphics g) 
        {
            g.setColor(TITLE_COLOR);
            Font titleFont = Utils.getFontBold(FONT_SIZE);
            g.setFont(titleFont);
            
            int textWidht = Display.getWidth() - TEXT_ANCHOR - PADDING_RIGHT;
            
            if(title == null)
            {
                title = TextUtils.wrapText(newsItem.getTitle(), textWidht, titleFont);
            }
            int yOff = 0;
            for (int i = 0; i <title.size() ; i++) 
            {
                if(title.size()>2 && i==1)
                {
                    g.drawText(title.elementAt(i).toString() + "...", TEXT_ANCHOR, PADDING_TOP + i * titleFont.getHeight());
                    yOff += g.getFont().getHeight();
                    break;
                }
                else
                {
                    g.drawText(title.elementAt(i).toString(), TEXT_ANCHOR, PADDING_TOP + i * titleFont.getHeight());
                    yOff += g.getFont().getHeight();
                }
            }

            Font textFont = Utils.getFont(FONT_SIZE);
            g.setColor(PRETEXT_COLOR);
            g.setFont(textFont);
            
            if(text == null)
            {
                text = TextUtils.wrapText(newsItem.getBody(), textWidht, textFont);
            }
            
            int titleHeight = PADDING_TOP + title.size() * titleFont.getHeight();
            int contentHeight = getPreferredHeight() - (PADDING_TOP);
            
            for (int i = 0; i < text.size(); i++) 
            {
                if(yOff + g.getFont().getHeight() >= getPreferredHeight() - 2*PADDING_TOP)
                {
                    g.drawText((String) text.elementAt(i) + "..." , TEXT_ANCHOR, titleHeight + i * textFont.getHeight());
                }
                else
                {
                    g.drawText((String) text.elementAt(i) , TEXT_ANCHOR, titleHeight + i * textFont.getHeight());
                }
                
                yOff += g.getFont().getHeight();
                if(yOff >= getPreferredHeight() - 2*PADDING_TOP)
                {
                    break;
                }
            }
                
            if (newsImage != null) 
            {
                try 
                {
                    g.drawBitmap(PADDING_LEFT, PADDING_TOP, newsImage.getWidth(), newsImage.getHeight(), newsImage, 0, 0);
                } 
                catch (Exception e) 
                {
                    drawDummyPhoto(g);
                }
            } 
            else
            {
                drawDummyPhoto(g);
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
            int BGR_COLOR =  graphics .isDrawingStyleSet(Graphics.DRAWSTYLE_FOCUS) ? 0x000000 : 0x393939;
            graphics.setColor(BGR_COLOR);
            graphics.fillRect(0,0, getPreferredWidth(), getPreferredHeight());
            graphics.setColor(0x000000);
            graphics.drawLine(0,getPreferredHeight()-1, getPreferredWidth(), getPreferredHeight()-1);
        }

        protected void drawFocus(Graphics graphics, boolean on) 
        {
            boolean oldDrawStyleFocus = graphics.isDrawingStyleSet(Graphics.DRAWSTYLE_FOCUS);
            try 
            {
                if(on) 
                {
                        graphics.setDrawingStyle(Graphics.DRAWSTYLE_FOCUS, true);
                }
                paintBackground(graphics);
                paint(graphics);
            } 
            finally 
            {
                graphics.setDrawingStyle(Graphics.DRAWSTYLE_FOCUS,oldDrawStyleFocus);
            }
        }

        public boolean isFocusable() {
                return super.isEditable();
        }

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

        public void clickButton()
        {
            try
            {
                UiApplication.getUiApplication().pushScreen( new NewsDetailScreen((NewsScreen) this.getScreen(), newsItem, newsImage));
            }
            catch(Exception ex)
            {
            }
        }

                public NewsItem getItem() {
                        return newsItem;
                }

        public void setItemImage(byte[] image) {
                try {
                        Bitmap temp = Bitmap.createBitmapFromBytes(image, 0, image.length, 1);
            int w = TEXT_ANCHOR - 2*PADDING_TOP;
            this.newsImage = Utils.resizedImage(temp, w, w);
                } catch (Exception e) {
                }
                synchronized (Application.getEventLock()) {
                        NewsItemButton.super.invalidate();
                }

        }

}
