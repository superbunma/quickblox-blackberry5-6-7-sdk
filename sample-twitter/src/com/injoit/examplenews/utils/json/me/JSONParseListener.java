package com.injoit.examplenews.utils.json.me;

public interface JSONParseListener {
	
	// returns true if enough to fetch
	public boolean valueParsed(Object object) throws JSONException;

}
