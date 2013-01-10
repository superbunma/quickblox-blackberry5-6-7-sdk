package com.injoit.examplenews.utils;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class DialogHelper {
	
	public static void alert(final String message) {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			
			public void run() {
				Dialog.alert(message);
				
			}
		});
	}
	
	public static void alert(Exception e) {
		alert(e.toString());
	}

}
