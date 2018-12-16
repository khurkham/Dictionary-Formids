/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation.normation;
import de.kugihan.dictionaryformids.general.Util;

public class NormationGer extends Normation {

	public StringBuffer normateWord(StringBuffer nonNormatedWord,
                                    boolean      fromUserInput) {
		StringBuffer defaultNormatedWord = NormationLib.defaultNormation(nonNormatedWord, fromUserInput);
		StringBuffer normatedWord = new StringBuffer();
		for (int charPos = 0; charPos < defaultNormatedWord.length(); ++charPos) {
			if (defaultNormatedWord.charAt(charPos) == 'ä') {
				normatedWord.append("ae");
			}
			else if (defaultNormatedWord.charAt(charPos) == 'ö') {
				normatedWord.append("oe");
			}
			else if (defaultNormatedWord.charAt(charPos) == 'ü') {
				normatedWord.append("ue");
			}
			else if (defaultNormatedWord.charAt(charPos) == 'ß') {
				normatedWord.append("ss");
			}
			else {
				normatedWord.append(defaultNormatedWord.charAt(charPos));
			}
		}
		return normatedWord;
	}


}
