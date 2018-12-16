/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2009 Heiko Klein (heiko.klein@gmx.net)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation.normation;

public class NormationNor extends Normation {

	public StringBuffer normateWord(StringBuffer nonNormatedWord,
                                    boolean      fromUserInput) {
		StringBuffer defaultNormatedWord = NormationLib.defaultNormation(nonNormatedWord, fromUserInput);
		StringBuffer normatedWord = new StringBuffer();
		for (int charPos = 0; charPos < defaultNormatedWord.length(); ++charPos) {
			if (defaultNormatedWord.charAt(charPos) == 'æ') {
				normatedWord.append("ae");
			}
			else if (defaultNormatedWord.charAt(charPos) == 'ø') {
				normatedWord.append("oe");
			}
			else if (defaultNormatedWord.charAt(charPos) == 'å') {
				normatedWord.append("aa");
			}
			else if (defaultNormatedWord.charAt(charPos) == 'ô') {
				normatedWord.append("o");
			}
			else if (defaultNormatedWord.charAt(charPos) == 'ò') {
				normatedWord.append("o");
			}
			else {
				normatedWord.append(defaultNormatedWord.charAt(charPos));
			}
		}
		return normatedWord;
	}


}
