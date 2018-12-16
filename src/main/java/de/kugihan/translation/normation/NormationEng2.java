/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 VU VAN QUYNH (quynh2003hp (a) yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/

// this is a specific version for English normation where English words may contain Roman 
// letters such as é etc.
// Currently used by VU VAN QUYNH for Vietnamese dictionaries

package de.kugihan.dictionaryformids.translation.normation;

public class NormationEng2 extends Normation {
		
	static final String noneNormationChar = "àáâÀÁÂùúÙÚòóôõÒÓÔÕèéêÈÉÊïÏñÑçÇ";
	static final String normationChar = "aaaaaauuuuooooooooeeeeeeiinncc";
	String punctuationChar = "'!§$%&/(){}[]=;:,.+-#~@|<>\\ ";
	
	public StringBuffer normateWord(StringBuffer nonNormatedWord) {
		StringBuffer normatedWord = new StringBuffer();	
		int strLength = nonNormatedWord.length();
		char whiteSpace = ' ';
		char lastChar = whiteSpace;
		int normatedWordLength = 0;
		for (int charPos = 0; charPos < strLength; ++charPos) {
			char ch = nonNormatedWord.charAt(charPos); 
			if (noneNormationChar.indexOf(ch) != -1){
				normatedWord.append(normationChar.charAt(noneNormationChar.indexOf(ch)));
				normatedWordLength++;
			}
			else if(punctuationChar.indexOf(ch) != -1){
				if(lastChar != whiteSpace && normatedWordLength > 0)	{
					normatedWord.append(whiteSpace);
					normatedWordLength++;
				}
				ch = whiteSpace;
			}
			else {
				normatedWord.append(Character.toLowerCase(ch));
				normatedWordLength++;
			}
			lastChar = ch;
		}
		if(lastChar == whiteSpace && normatedWordLength > 0) normatedWord.setLength(normatedWordLength-1);
		return normatedWord;
	}
	
}