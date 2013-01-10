package com.injoit.examplenews.utils.json.me;

import java.io.InputStream;

public class InputStreamJSONTokener implements JSONTokener {

	/**
	 * The index of the next character.
	 */
	private int myIndex;

	/**
	 * The source string being tokenized.
	 */
	private InputStream mySource;

	private StringBuffer stringBuffer;

	/**
	 * Construct a JSONTokener from a string.
	 * 
	 * @param s
	 *            A source string.
	 */
	public InputStreamJSONTokener(InputStream source) {
		this.myIndex = 0;
		this.mySource = source;
		this.stringBuffer = new StringBuffer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.injoit.examplenews.utils.json.me.IJSONTokenizer#back()
	 */
	public void back() {
		if (this.myIndex > 0) {
			this.myIndex -= 1;
		}
	}

	/**
	 * Get the hex value of a character (base16).
	 * 
	 * @param c
	 *            A character between '0' and '9' or between 'A' and 'F' or
	 *            between 'a' and 'f'.
	 * @return An int between 0 and 15, or -1 if c was not a hex digit.
	 */
	public static int dehexchar(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		}
		if (c >= 'A' && c <= 'F') {
			return c - ('A' - 10);
		}
		if (c >= 'a' && c <= 'f') {
			return c - ('a' - 10);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.injoit.examplenews.utils.json.me.IJSONTokenizer#more()
	 */
	public boolean more() {
		return this.myIndex < this.availableMore(this.myIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.injoit.examplenews.utils.json.me.IJSONTokenizer#next()
	 */
	public char next() {
		if (more()) {
			char c = this.getChar(this.myIndex);
			this.myIndex += 1;
			return c;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.injoit.examplenews.utils.json.me.IJSONTokenizer#next(char)
	 */
	public char next(char c) throws JSONException {
		char n = next();
		if (n != c) {
			throw syntaxError("Expected '" + c + "' and instead saw '" + n
					+ "'.");
		}
		return n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.injoit.examplenews.utils.json.me.IJSONTokenizer#next(int)
	 */
	public String next(int n) throws JSONException {
		int i = this.myIndex;
		int j = i + n;
		if (j >= this.availableMore(j)) {
			throw syntaxError("Substring bounds error");
		}
		this.myIndex += n;
		getChar(j);
		return this.stringBuffer.toString().substring(i, j);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.injoit.examplenews.utils.json.me.IJSONTokenizer#nextClean()
	 */
	public char nextClean() throws JSONException {
		for (;;) {
			char c = next();
			if (c == '/') {
				switch (next()) {
				case '/':
					do {
						c = next();
					} while (c != '\n' && c != '\r' && c != 0);
					break;
				case '*':
					for (;;) {
						c = next();
						if (c == 0) {
							throw syntaxError("Unclosed comment.");
						}
						if (c == '*') {
							if (next() == '/') {
								break;
							}
							back();
						}
					}
					break;
				default:
					back();
					return '/';
				}
			} else if (c == '#') {
				do {
					c = next();
				} while (c != '\n' && c != '\r' && c != 0);
			} else if (c == 0 || c > ' ') {
				return c;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.injoit.examplenews.utils.json.me.IJSONTokenizer#nextString(char)
	 */
	public String nextString(char quote) throws JSONException {
		char c;
		StringBuffer sb = new StringBuffer();
		for (;;) {
			c = next();
			switch (c) {
			case 0:
			case '\n':
			case '\r':
				throw syntaxError("Unterminated string");
			case '\\':
				c = next();
				switch (c) {
				case 'b':
					sb.append('\b');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 'u':
					sb.append((char) Integer.parseInt(next(4), 16));
					break;
				case 'x':
					sb.append((char) Integer.parseInt(next(2), 16));
					break;
				default:
					sb.append(c);
				}
				break;
			default:
				if (c == quote) {
					return sb.toString();
				}
				sb.append(c);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.injoit.examplenews.utils.json.me.IJSONTokenizer#nextTo(char)
	 */
	public String nextTo(char d) {
		StringBuffer sb = new StringBuffer();
		for (;;) {
			char c = next();
			if (c == d || c == 0 || c == '\n' || c == '\r') {
				if (c != 0) {
					back();
				}
				return sb.toString().trim();
			}
			sb.append(c);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.injoit.examplenews.utils.json.me.IJSONTokenizer#nextTo(java.lang
	 * .String)
	 */
	public String nextTo(String delimiters) {
		char c;
		StringBuffer sb = new StringBuffer();
		for (;;) {
			c = next();
			if (delimiters.indexOf(c) >= 0 || c == 0 || c == '\n' || c == '\r') {
				if (c != 0) {
					back();
				}
				return sb.toString().trim();
			}
			sb.append(c);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.injoit.examplenews.utils.json.me.IJSONTokenizer#nextValue()
	 */
	public Object nextValue() throws JSONException {
		char c = nextClean();
		String s;

		switch (c) {
		case '"':
		case '\'':
			return nextString(c);
		case '{':
			back();
			return new JSONObject(this);
		case '[':
			back();
			return new JSONArray(this);
		}

		/*
		 * Handle unquoted text. This could be the values true, false, or null,
		 * or it can be a number. An implementation (such as this one) is
		 * allowed to also accept non-standard forms.
		 * 
		 * Accumulate characters until we reach the end of the text or a
		 * formatting character.
		 */

		StringBuffer sb = new StringBuffer();
		char b = c;
		while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
			sb.append(c);
			c = next();
		}
		back();

		/*
		 * If it is true, false, or null, return the proper value.
		 */

		s = sb.toString().trim();
		if (s.equals("")) {
			throw syntaxError("Missing value.");
		}
		if (s.equalsIgnoreCase("true")) {
			return Boolean.TRUE;
		}
		if (s.equalsIgnoreCase("false")) {
			return Boolean.FALSE;
		}
		if (s.equalsIgnoreCase("null")) {
			return JSONObject.NULL;
		}

		/*
		 * If it might be a number, try converting it. We support the 0- and 0x-
		 * conventions. If a number cannot be produced, then the value will just
		 * be a string. Note that the 0-, 0x-, plus, and implied string
		 * conventions are non-standard. A JSON parser is free to accept
		 * non-JSON forms as long as it accepts all correct JSON forms.
		 */

		if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
			if (b == '0') {
				if (s.length() > 2
						&& (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
					try {
						return new Integer(Integer.parseInt(s.substring(2), 16));
					} catch (Exception e) {
						/* Ignore the error */
					}
				} else {
					try {
						return new Integer(Integer.parseInt(s, 8));
					} catch (Exception e) {
						/* Ignore the error */
					}
				}
			}
			try {
				return Integer.valueOf(s);
			} catch (Exception e) {
				try {
					return new Long(Long.parseLong(s));
				} catch (Exception f) {
					try {
						return Double.valueOf(s);
					} catch (Exception g) {
						return s;
					}
				}
			}
		}
		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.injoit.examplenews.utils.json.me.IJSONTokenizer#skipTo(char)
	 */
	public char skipTo(char to) {
		char c;
		int index = this.myIndex;
		do {
			c = next();
			if (c == 0) {
				this.myIndex = index;
				return c;
			}
		} while (c != to);
		back();
		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.injoit.examplenews.utils.json.me.IJSONTokenizer#syntaxError(java
	 * .lang.String)
	 */
	public JSONException syntaxError(String message) {
		return new JSONException(message + toString());
	}

	/**
	 * Make a printable string of this JSONTokener.
	 * 
	 * @return " at character [this.myIndex] of [this.mySource]"
	 */
	public String toString() {
		return " at character " + this.myIndex + " of " + this.mySource;
	}

	private int availableMore(int i) {
		getChar(i);
		return stringBuffer.length();
	}

	private char getChar(int index) {
		while (stringBuffer.length() <= index) {
			try {
				byte[] data = new byte[256];
				int length = 0;
				if (-1 != (length = mySource.read(data))) {
					stringBuffer.append(new String(data, 0, length));
				} else {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return stringBuffer.charAt(index);
	}

}
