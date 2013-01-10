package com.injoit.examplenews.share;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class ShareScreen extends ClosablePopupScreen {

        private ShareContentProvider provider;

        public ShareScreen(ShareContentProvider provider) {
                super(new VerticalFieldManager());
                this.provider = provider;
                LabelField compose = new LabelField("Compose:");
                add(compose);
                createShareButtons();
        }

        private void createShareButtons() {
            //
        }
}
