/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)and Vu Van Quynh 
(quynh2003hp (a) yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation.normation;

import java.util.Vector;

import de.kugihan.dictionaryformids.dataaccess.fileaccess.DfMInputStreamAccess;
import de.kugihan.dictionaryformids.translation.SearchedWord;

public class Normation {

    public  DfMInputStreamAccess dictionaryDataFileISAccess;  // used for accessing the files of the dictionary, e.g. translation tables

	// Old normateWord without parameter fromUserInput
	public StringBuffer normateWord(StringBuffer nonNormatedWord) {
		// default is to do nothing
		return nonNormatedWord;
	}

	// New normateWord with parameter fromUserInput
	// The parameter fromUserInput indicates whether 
	// - the normation shall be done for an expression that was entered by 
	//   the user (fromUserInput = true) or
	// - the normation shall be done for an expression that needs to be normated
	//   for a dictionary index file.
	// If fromUserInput is true, then wildcard characters must not be removed from
	// the normated word.
	public StringBuffer normateWord(StringBuffer nonNormatedWord,
			                        boolean      fromUserInput) {
		// default is to call old version of normateWord
		return normateWord(nonNormatedWord);
	}

	// unNormateWord: introduced by Vu Van Quynh
	public StringBuffer unNormateWord(StringBuffer normatedWord) {
		// default is to do nothing
		return normatedWord;
	}

	// searchWord: introduced by Vu Van Quynh
	public Vector searchWord(String text) {
		// default is to do return only the provided text
		Vector words = new Vector();
		words.addElement(new SearchedWord(text));
		return words;
	}

	// suggestionWord: introduced by Vu Van Quynh
	public Vector suggestionWord(String text) {
		// default is to do return no suggestion
		Vector suggestions = new Vector();
		return suggestions;
	}
	
}
