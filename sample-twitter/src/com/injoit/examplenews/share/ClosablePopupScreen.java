package com.injoit.examplenews.share;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.PopupScreen;

public class ClosablePopupScreen extends PopupScreen {
        
        public ClosablePopupScreen(Manager delagate) {
                super(delagate);
        }
        
        protected boolean keyChar(char c, int status, int time) {
                if (c == Characters.ESCAPE)
                        close();
                return super.keyChar(c, status, time);
        }
}
