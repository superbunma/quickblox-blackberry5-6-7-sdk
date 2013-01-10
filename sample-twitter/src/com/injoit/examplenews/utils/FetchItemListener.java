package com.injoit.examplenews.utils;

public interface FetchItemListener {
	// returns true if enough to fetch
	public boolean itemFetched(Object item, int index);
}
