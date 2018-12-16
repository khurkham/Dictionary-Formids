/*
 Parser.java
 Copyright (C) 2005, 2006 Sean Kernohan (webmaster@seankernohan.com), and 
 Leow Hock Soon Sean (leowhss@singnet.com.sg)

 GPL applies - see file COPYING for copyright statement.
 */
package de.kugihan.dictionaryformids.translation.normation.normationjpn;

import de.kugihan.dictionaryformids.dataaccess.fileaccess.DfMInputStreamAccess;

public class Parser
{
	private LookupTable lookuptable;

	/** Creates a new instance of Parser */
	public Parser()
	{
		lookuptable = new LookupTable();
	}

	public void setDictionaryDataFileISAccess(DfMInputStreamAccess dictionaryDataFileISAccess) {
		lookuptable.setDictionaryDataFileISAccess(dictionaryDataFileISAccess);
	}

	/**
	 * @param charSet
	 *            0 for hiragana, 1 for katakana
	 * @param normalMode
	 *            true for romaji-kana, false for kana-romaji
	 */
	public String convert(String input, int charSet, boolean normalMode)
	{
		String converted = "";
		String searchString;
		for (int i = 0; i < input.length();)
		{
			for (int j = 5; j > 0; j--)
			{
				try
				{
					searchString = input.substring(i, i + j);
					Object result;
					if (normalMode)
					{
						if (charSet == 1)
							result = lookuptable.getRomajiToKatakanaTable().get(searchString);
						else
							result = lookuptable.getRomajiToHiraganaTable().get(searchString);
					}
					else
					{
						if (charSet == 1)
							result = lookuptable.getKatakanaToRomajiTable().get(searchString);
						else
							result = lookuptable.getHiraganaToRomajiTable().get(searchString);
					}
					if (result != null)
					{
						converted += result.toString();
						i += j;
						j = 1; // character was found, escape from inner loop
					}
					else
					{
						if (j == 1)
						{
							// exhausted all possible characters
							i++;
							converted += searchString;
						}
					}
				}
				catch (StringIndexOutOfBoundsException e)
				{
					// ignore, keep trying more characters
				}
			}
		}
		return converted;
	}
}
