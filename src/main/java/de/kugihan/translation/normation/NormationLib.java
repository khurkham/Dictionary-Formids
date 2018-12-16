package de.kugihan.dictionaryformids.translation.normation;
import de.kugihan.dictionaryformids.general.Util;

public class NormationLib {

	public static StringBuffer defaultNormation(StringBuffer string,
                                                boolean      fromUserInput) {
		StringBuffer lowerCaseWord = Util.convertToLowerCase(string);
		StringBuffer noPunctuationWord = Util.removePunctuation(lowerCaseWord, fromUserInput);
		StringBuffer noSuperflousWhitespaceWord = Util.filterSuperflousWhitespaces(noPunctuationWord);
		
		return noSuperflousWhitespaceWord;
	}
	
}
