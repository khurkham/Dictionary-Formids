/*
 LookupTable.java
 Copyright (C) 2005, 2006 Sean Kernohan (webmaster@seankernohan.com), and 
 Leow Hock Soon Sean (leowhss@singnet.com.sg)

 GPL applies - see file COPYING for copyright statement.
 */
package de.kugihan.dictionaryformids.translation.normation.normationjpn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import de.kugihan.dictionaryformids.dataaccess.CsvFile;
import de.kugihan.dictionaryformids.dataaccess.fileaccess.DfMInputStreamAccess;
import de.kugihan.dictionaryformids.general.DictionaryException;

public class LookupTable
{

	private Hashtable romajiToHiraganaTable = null;
	private Hashtable romajiToKatakanaTable = null;
	private Hashtable katakanaToRomajiTable = null;
	private Hashtable hiraganaToRomajiTable = null; // initialise to null for
													// memory conservation

	private final int maxSize = 20480; // 20KB max

	protected DfMInputStreamAccess dictionaryDataFileISAccess;
	
	/** Creates a new instance of LookupTable */
	public LookupTable()
	{
	}

	public void setDictionaryDataFileISAccess(DfMInputStreamAccess dictionaryDataFileISAccessParam) {
		dictionaryDataFileISAccess = dictionaryDataFileISAccessParam;
	}
	
	private Hashtable initialiseTable(String filename, 
                                      boolean reverse)
			throws UnsupportedEncodingException, IOException
	{
		Hashtable map = new Hashtable();
		try
		{
			CsvFile file = new CsvFile(dictionaryDataFileISAccess, filename, '=', "UTF-8", maxSize);
			file.readCsvFileComplete();

			String left;
			String right;
			while (!(left = file.getWord().toString()).equals(""))
			{
				right = file.getWord().toString();
				if (!reverse)
					map.put(left, right);
				else
					map.put(right, left);
			}
		}
		catch (DictionaryException e)
		{
			System.out.println(e);
		}
		return map;
	}

	public Hashtable getRomajiToHiraganaTable()
	{
		if (romajiToHiraganaTable == null)
		{
			try
			{
				romajiToHiraganaTable = initialiseTable(
						"/char_lists/romaji_hiragana_utf8.txt",
						false);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return romajiToHiraganaTable;
	}

	public Hashtable getRomajiToKatakanaTable()
	{
		if (romajiToKatakanaTable == null)
		{
			try
			{
				romajiToKatakanaTable = initialiseTable(
						"/char_lists/romaji_katakana_utf8.txt",
						false);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return romajiToKatakanaTable;
	}

	public Hashtable getHiraganaToRomajiTable()
	{
		if (hiraganaToRomajiTable == null)
		{
			try
			{
				hiraganaToRomajiTable = initialiseTable(
						"/char_lists/romaji_hiragana_utf8.txt",
						true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return hiraganaToRomajiTable;
	}

	public Hashtable getKatakanaToRomajiTable()
	{
		if (katakanaToRomajiTable == null)
		{
			try
			{
				katakanaToRomajiTable = initialiseTable(
						"/char_lists/romaji_katakana_utf8.txt",
						true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return katakanaToRomajiTable;
	}
}
