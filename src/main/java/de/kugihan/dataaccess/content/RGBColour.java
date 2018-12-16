/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 Gert Nuber (dict@kugihan.de) and  2005 Vu Van Quynh 
(quynh2003hp@yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.dataaccess.content;

public class RGBColour {
	// all three colour components have a value between 0 (no intensity) 
	// and 255 (max intensity)
	static public final int maxRBGColourValue = 255;
	
	public int red;
	public int green;
	public int blue;
	
	public RGBColour(int redParam, int greenParam, int blueParam) {
		red = redParam;
		green = greenParam;
		blue = blueParam;
	}
	
	public String getHexValue()
	{
		StringBuffer hexValue = new StringBuffer();
		
		hexValue.append(toHexString(red));
		hexValue.append(toHexString(green));
		hexValue.append(toHexString(blue));
	
		return hexValue.toString();
	}
	
	private String toHexString(int value)
	{
		if (value <= 16)
		{
			return "0" + Integer.toString(value, 16);
		}
		else
		{
			return Integer.toString(value, 16);
		}
	}
}