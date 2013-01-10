package com.injoit.examplenews.news.controls;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.decor.BackgroundFactory;

import com.injoit.examplenews.utils.*;

public class NewsBodyField extends Field {
        
        private static final int HPADDING = 5;
        private static final int VPADDING = 5;
        
        private final int TEXT_BACKGROUND = 0x292929;
        private final int TEXT_COLOR = 0xc8c8c8;
        
        private final Font font = Utils.getFont(7);
        
        private final String NO_CONTENT = "No content available";
        
        private Vector text;
        
        public NewsBodyField(String text) {
                if (text == null || "".equals(text.trim())) {
                        text = NO_CONTENT;
                }
                this.text = TextUtils.wrapText(text, getPreferredWidth() - 2*HPADDING, font);
                setBackground(BackgroundFactory.createSolidBackground(TEXT_BACKGROUND));
        }

        protected void layout(int width, int height) {
                setExtent(getPreferredWidth(), getPreferredHeight());
        }
        
        public int getPreferredHeight() {
                return text.size()*font.getHeight() + VPADDING*2;
        }
        
        public int getPreferredWidth() {
                return Display.getWidth();
        }

        public void paint(Graphics graphics) {

                graphics.setColor(TEXT_COLOR);
                graphics.setFont(font);
                
                for (int i = 0; i < text.size(); i++) {
                        graphics.drawText(text.elementAt(i).toString(), HPADDING,
                                        VPADDING + i * font.getHeight());
                }

        }
        
        public boolean isFocusable()
        {
            return isEditable();
        }
        
}
