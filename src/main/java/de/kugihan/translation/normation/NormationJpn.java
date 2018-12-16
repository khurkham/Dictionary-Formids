/*
 DictionaryForMIDs - a free multi-language dictionary for mobile devices.
 Copyright (C) 2005, 2006 Sean Kernohan (webmaster@seankernohan.com)

 GPL applies - see file COPYING for copyright statement.
 */
package de.kugihan.dictionaryformids.translation.normation;

import java.util.Vector;

import de.kugihan.dictionaryformids.dataaccess.fileaccess.DfMInputStreamAccess;
import de.kugihan.dictionaryformids.general.Util;
import de.kugihan.dictionaryformids.translation.SearchedWord;
import de.kugihan.dictionaryformids.translation.normation.normationjpn.Parser;

public class NormationJpn extends Normation
{

	Parser parserObj;
	private final int HIRAGANA = 0;
	private final int KATAKANA = 1;

	public NormationJpn()
	{
		parserObj = new Parser();
	}

	public Vector searchWord(String text)
	{
		parserObj.setDictionaryDataFileISAccess(dictionaryDataFileISAccess);
		
		Vector words = new Vector(2);
		text = Util.convertToLowerCase(new StringBuffer(text)).toString();

		int i = 0;
		char sub = text.charAt(i);
		while (sub == '*' || sub == '?')
			sub = text.charAt(++i);

		if (isRomaji(sub))
		{
			String hiraganaWord = parserObj.convert(text, HIRAGANA, true);
			words.addElement(new SearchedWord(hiraganaWord));
			String katakanaWord = parserObj.convert(text, KATAKANA, true);
			words.addElement(new SearchedWord(katakanaWord));
		}
		else if (isHiragana(sub))
		{
			words.addElement(new SearchedWord(text));
			String romajiWord = parserObj.convert(text, HIRAGANA, false);
			String katakanaWord = parserObj.convert(romajiWord, KATAKANA, true);
			words.addElement(new SearchedWord(katakanaWord));
		}
		else if (isKatakana(sub))
		{
			words.addElement(new SearchedWord(text));
			String romajiWord = parserObj.convert(text, KATAKANA, false);
			String hiraganaWord = parserObj.convert(romajiWord, HIRAGANA, true);
			words.addElement(new SearchedWord(hiraganaWord));
		}
		else
			System.err.println("Illegal input string in normationjpn.");

		return words;
	}

	public boolean isRomaji(char c)
	{
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
			return true;
		return false;
	}

	public boolean isHiragana(char c)
	{
		if (c >= '\u3040' && c <= '\u309F')
			return true;
		return false;
	}

	public boolean isKatakana(char c)
	{
		if (c >= '\u30A0' && c <= '\u30FF')
			return true;
		return false;
	}
}
