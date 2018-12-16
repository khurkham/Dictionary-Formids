/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006  N QUYNH (quynh2003hp (a) yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation.normation;

public class NormationVie extends Normation {
	static final String nguyenam = "aAàÀáÁạẠảẢãÃeEèÈéÉẹẸẻẺẽẼiIìÌíÍịỊỉỈĩĨuUùÙúÚụỤủỦũŨoOòÒóÓọỌỏỎõÕyYỳỲýÝỵỴỷỶỹỸâÂầẦấẤậẬẩẨẫẪăĂằẰắẮặẶẳẲẵẴêÊềỀếẾệỆểỂễỄôÔồỐốỐộỘổỔỗỖơƠờỜớỚợỢởỞỡỠưƯừỪứỨựỰửỬữỮ";
	static final String valuesDD = "đĐ";
	static final String daaus = "\u0001\u0001ffssjjrrxx";
	static final String nguyenamTelex[] = {"a", "e", "i", "u", "o", "y", "aa", "aw", "ee", "oo", "ow", "uw"};
	static final String punctuations = "!\"$§$%&/()=?´`\\{}[]^°+*~#'-_.:,;<>|@";	
	
	public StringBuffer normateWord(StringBuffer nonNormatedWord) {
		StringBuffer normatedWord = new StringBuffer();	
		int normatedWordLength = 0;
		int singleWordLength = 0;
		char whiteSpace = ' ';
		char lastChar = whiteSpace;
		char dau ='\u0001';
		for(int pos = 0; pos < nonNormatedWord.length(); pos++)
		{
			char ch = nonNormatedWord.charAt(pos); 
			int dauPos = nguyenam.indexOf(ch);
			if(dauPos >= 0)
			{
				normatedWord.append(nguyenamTelex[dauPos/12]);
				normatedWordLength += nguyenamTelex[dauPos/12].length();
				singleWordLength++;
				if(dau == '\u0001')
				{
					dau = daaus.charAt(dauPos%12);
				}
			}
			else if(valuesDD.indexOf(ch) >=0)
			{
				normatedWord.append("dd");
				normatedWordLength += 2;
				singleWordLength++;
			}
			else
			{
				ch = Character.toLowerCase(ch);
				dauPos = daaus.indexOf(ch);
				if(dauPos >= 0)
				{
					if((singleWordLength ==0) || (singleWordLength ==1 && ch == 'r'))
					{
						normatedWord.append("r");
						singleWordLength++;
					}
					else dau = ch;
				}
				else if(ch=='w')
				{
					if(singleWordLength > 0)
					{
						if(lastChar !='a' &&  lastChar !='u' && lastChar !='o') 
						{
							normatedWord.append("uw");
							normatedWordLength += 2;
						}
						else 
						{
							normatedWord.append("w");
							normatedWordLength ++;
						}
					}
					else 
					{
						normatedWord.append("uw");
						normatedWordLength += 2;
					}
					singleWordLength ++;
				}
				else if (punctuations.indexOf(ch) != -1 || ch == whiteSpace)
				{
					if(normatedWordLength >0 && lastChar != whiteSpace)
					{
						if(dau != '\u0001')
						{
							normatedWord.append(dau);
							normatedWordLength++;
						}
						normatedWord.append(whiteSpace);
						normatedWordLength++;
						dau = '\u0001';
						singleWordLength = 0;
					}
					ch = whiteSpace;
				}
				else 
				{
					normatedWord.append(ch);
					normatedWordLength++;		
					singleWordLength ++;
				}
			}
			lastChar = ch;
		}// for
		if(dau != '\u0001')  normatedWord.append(dau);
		else
		 {
			if(lastChar == whiteSpace && normatedWordLength>0)
			{
				normatedWord.setLength( normatedWordLength-1);
			}
		}
		return normatedWord;
	}
	
}
