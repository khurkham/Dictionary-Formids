/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 Gert Nuber (dict@kugihan.de) and  2005 Vu Van Quynh 
(quynh2003hp@yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.dataaccess.content;

public class SelectionMode {
	public static final int none = 1; 
	public static final int single = 2; 
	public static final int all = 3; 
	public int mode;
	
	public static final String noneString = "none";
	public static final String singleString = "single";
	public static final String allString = "all";
	
	public SelectionMode(int modeParam) {
		mode = modeParam;
	}

}
