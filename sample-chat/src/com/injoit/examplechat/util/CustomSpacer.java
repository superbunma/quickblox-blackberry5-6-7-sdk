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

package com.injoit.examplechat.util;

import javax.microedition.lcdui.*;
import com.injoit.examplechat.util.Contents;

/**
 * Class for create custom Spacer
 * 
 * @author Gabriele Bianchi
 */
public class CustomSpacer extends CustomItem
{
        //private String label = "JabberMixClient";
        private int w, h = 8;

        public CustomSpacer(int formW)
        {
                super("");
                if (formW == 0)
                        w = 160;
                else
                        w = formW;
        }
        public CustomSpacer(int formW, int formH)
        {
                super("");
                if (formW == 0)
                        w = 160;
                else
                        w = formW;
                h = 48;
        }
        public CustomSpacer(String title, int formW, int formH)
        {
                super(title);
                if (formW == 0)
                        w = 160;
                else
                        w = formW;
                h = 48;
        }

        protected void paint(Graphics g, int w, int h)
        {
                
                if (this.h == 8) //draw only a line
                {
                        g.setColor(g.getDisplayColor(0x0000FF));
                        g.drawLine(0, this.h / 2, this.w, this.h / 2);
                }
                else //create a rect for logo
                {
                        try
                        {
                                g.drawImage(Contents.displayImage("logo"), this.w / 2, 15, Graphics.TOP | Graphics.HCENTER);
                        }
                        catch (Exception ex)
                        {
                                return;
                        }
                }
        }

        protected int getPrefContentHeight(int h) { return getMinContentHeight(); }
        protected int getPrefContentWidth(int w) { return getMinContentWidth(); }
        protected int getMinContentWidth() { return w; }
        protected int getMinContentHeight() { return h; }
}
