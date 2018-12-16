/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 Gert Nuber (dict@kugihan.de) and  2005 Vu Van Quynh 
(quynh2003hp@yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.dataaccess.content;

public class FontStyle { 
	public static final int plain = 1; 
	public static final int underlined = 2; 
	public static final int bold = 3; 
	public static final int italic = 4; 
	public int style;
	
	public static final String plainString = "plain";
	public static final String underlinedString = "underlined";
	public static final String boldString = "bold";
	public static final String italicString = "italic";
	
	public FontStyle(int styleParam) {
		style = styleParam;
	}
}
