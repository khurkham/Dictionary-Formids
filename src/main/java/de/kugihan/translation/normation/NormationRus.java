/*dictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006  N QUYNH (quynh2003hp (a) yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation.normation;

public class NormationRus extends Normation {
	
		
	public StringBuffer normateWord(StringBuffer nonNormatedWord) {
		StringBuffer normatedWord = new StringBuffer();
		int normatedWordLength = 0;
		String punctuations = "!\"$§$%&/()=?´`\\{}[]^°+*~#'-_.:,;<>|@";
		char whiteSpace = ' ';
		char trongAm = '́';
		char duoiTu = '|';
		char valueE = '\u0451';
		char lastChar = whiteSpace;
		for (int pos = 0;  pos < nonNormatedWord.length();  pos++) {
			char ch = nonNormatedWord.charAt(pos); 
			if(ch == trongAm || ch == duoiTu)
			{
			}
			else if(punctuations.indexOf(ch) != -1 || ch == whiteSpace)
			{
				if(normatedWordLength >0)
				{
					if(lastChar != whiteSpace)
					{
						lastChar = whiteSpace;
						normatedWord.append(lastChar);
						normatedWordLength++;
					}
				}
			}
			else 
			{
				lastChar =Character.toLowerCase(ch);
				if(lastChar == valueE) lastChar = '\u0435';
				normatedWord.append(lastChar);
				normatedWordLength++;
			}
		}
		if(lastChar == whiteSpace && normatedWordLength>0)
		{
			normatedWord.setLength( normatedWordLength-1);
		}
		return normatedWord;
	}
	
}