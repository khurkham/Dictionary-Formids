/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 Gert Nuber (dict@kugihan.de) and  2005 Vu Van Quynh 
(quynh2003hp@yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.dataaccess.content;

public class ContentDefinition {
	public String     	 contentDisplayText;
	public RGBColour  	 fontColour;
	public FontStyle  	 fontStyle;
	public SelectionMode selectionMode;
	public boolean    	 displaySelectable;
	
	public ContentDefinition(String contentDisplayTextParam) {
		// create ContentDefinition with default values
		setContentDisplayText(contentDisplayTextParam);
		setFontColour(PredefinedContent.fontColourDefault);
		setFontStyle(PredefinedContent.fontStyleDefault);
		setSelectionMode(PredefinedContent.selectionModeNone);
		setDisplaySelectable(true);
	}
	
	protected ContentDefinition(String     		contentDisplayTextParam,
						        RGBColour  		fontColourParam,
						        FontStyle  		fontStyleParam,
						        SelectionMode 	selectionModeParam,
						        boolean    		displaySelectableParam) {
		contentDisplayText = contentDisplayTextParam;
		fontColour = fontColourParam;
		fontStyle = fontStyleParam;
		selectionMode = selectionModeParam;
		displaySelectable = displaySelectableParam;
	}

	public String getContentDisplayText() {
		return contentDisplayText;
	}

	public void setContentDisplayText(String contentDisplayText) {
		this.contentDisplayText = contentDisplayText;
	}

	public boolean isDisplaySelectable() {
		return displaySelectable;
	}

	public void setDisplaySelectable(boolean displaySelectable) {
		this.displaySelectable = displaySelectable;
	}

	public RGBColour getFontColour() {
		return fontColour;
	}

	public void setFontColour(RGBColour fontColour) {
		this.fontColour = fontColour;
	}

	public FontStyle getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(FontStyle fontStyle) {
		this.fontStyle = fontStyle;
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}
	
}
