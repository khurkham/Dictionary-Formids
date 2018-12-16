/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 Gert Nuber (dict@kugihan.de) and  2005 Vu Van Quynh 
(quynh2003hp@yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.hmi_common.content;

import de.kugihan.dictionaryformids.dataaccess.content.FontStyle;
import de.kugihan.dictionaryformids.dataaccess.content.RGBColour;
import de.kugihan.dictionaryformids.dataaccess.content.SelectionMode;

public class StringColourItemTextPart {
	protected String	    text;  	 		// Text to be displayed
	protected RGBColour     colour;	 		// colour of the text
	protected FontStyle     style;   		// sytle of the text (plain, bold, italic, ...)
	protected SelectionMode seletionMode;	// how selection can be done on this TextPart
	
	public StringColourItemTextPart(String			textParam,
									RGBColour  		colourParam,
									FontStyle   	styleParam,
									SelectionMode 	seletionModeParam) {
		text = textParam;
		colour = colourParam;
		style = styleParam;
		seletionMode = seletionModeParam;
	}

	public RGBColour getColour() {
		return colour;
	}

	public FontStyle getStyle() {
		return style;
	}

	public String getText() {
		return text;
	}
	
	public SelectionMode getSelectionMode(){
		return seletionMode;
	}
	
	public char charAt(int index) {
		return text.charAt(index);
	}
}
