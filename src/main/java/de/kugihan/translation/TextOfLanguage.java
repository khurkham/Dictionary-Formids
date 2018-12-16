/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2008 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation;

import de.kugihan.dictionaryformids.dataaccess.DictionaryDataFile;

public class TextOfLanguage {
	String text;
	protected int languageIndex;             // index of the language where the text comes from
	protected DictionaryDataFile dictionary;     // dictinary where the text comes from 
	
	public String getText() {
		return text;
	}

	public int getLanguageIndex() {
		return languageIndex;
	}

	public DictionaryDataFile getDictionary() {
		return dictionary;
	}

	public TextOfLanguage(String text, int languageIndex, DictionaryDataFile dictionaryParam) {
		super();
		this.text = text;
		this.languageIndex = languageIndex;
		this.dictionary = dictionaryParam;
	}

}
