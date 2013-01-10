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
import java.util.Vector;

/**
 * Class for create custom Strings
 * 
 * @author Gabriele Bianchi
 */
public class CustomStringItem extends CustomItem
{
        private String label;
        private int w, h = 20;
        private Vector strings = null;

        public CustomStringItem(String lab, int formW)
        {
                super("");
                label = lab;

                if (formW == 0)
                        w = 160;
                else
                        w = formW;
        }

        public CustomStringItem(String lab, Vector labels, int formW)
        {
                super(lab);
                strings = labels;

                if (formW == 0)
                        w = 160;
                else
                        w = formW;
        }
        
        protected void paint(Graphics g, int w, int h) {
                Font f = g.getFont();
                if (strings == null)
                {
                        g.setColor(g.getDisplayColor(0x0000FF));
                        
                        String shortText = this.label;
                        if (f.stringWidth(this.label) >= this.w)
                        {
                                g.setClip(0, 0, this.w, f.getHeight() * 2 + 1);
                                while (f.stringWidth(shortText) >= this.w)
                                        shortText = shortText.substring(0, shortText.length() - 1);
                                this.label = this.label.substring(shortText.length(), this.label.length());
                                g.drawString(shortText, 1, 0, Graphics.TOP | Graphics.LEFT);
                                g.drawString(this.label, 1, f.getHeight(), Graphics.TOP | Graphics.LEFT);
                        }
                        else
                                g.drawString(this.label, 1, 0, Graphics.TOP | Graphics.LEFT);    
                }
                else
                {
                        g.setClip(0, 0, this.w, f.getHeight() * strings.size() + 1 );
                        g.setColor(g.getDisplayColor(0x0000FF));
                        for (int i=0; i < strings.size(); i++)
                                g.drawString((String)strings.elementAt(i), 1, i * f.getHeight(), Graphics.TOP | Graphics.LEFT);   
                }
        }

        public String getLabel()
        {
                return label;
        }
        protected int getPrefContentHeight(int h) { return getMinContentHeight(); }
        protected int getPrefContentWidth(int w) { return getMinContentWidth(); }
        protected int getMinContentWidth() { return w; }
        protected int getMinContentHeight() { return h; }
}

