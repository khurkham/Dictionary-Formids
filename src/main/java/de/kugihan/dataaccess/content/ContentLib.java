/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 Gert Nuber (dict@kugihan.de) and  2005 Vu Van Quynh 
(quynh2003hp@yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.dataaccess.content;

import de.kugihan.dictionaryformids.general.DictionaryException;

public class ContentLib {

	static public final char startOfContentChar = '[';
	static public final char endOfContentChar = ']';
	static public final char preservePrependChar  = '\\';

	// adds to each [ or ] an escape character so that these characters are 
	// not interpreted as startOfContentChar/endOfContentChar 
	public static void addContentPrependChar(StringBuffer expression) {
		for (int charPos = 0; charPos < expression.length(); ++charPos) {
			if ((expression.charAt(charPos) == startOfContentChar) ||
				(expression.charAt(charPos) == endOfContentChar)) {
				if (charPos > 0) {
					if (expression.charAt(charPos -1) == preservePrependChar) {
						// preservePrependChar already set: don't set again
						continue;
					}
				}
				expression.insert(charPos, preservePrependChar);
				++charPos;
			}
		}
	}
	
	// returns for a text a string of the content syntax like [03translation]
	public static void addContentFormat(StringBuffer contentString, 
			                            int 		 contentNumber,
			                            int          startCharPos,
			                            int			 endCharPos) 
				throws DictionaryException {
		contentString.insert(endCharPos + 1, endOfContentChar);
		StringBuffer contentNumberString = new StringBuffer(Integer.toString(contentNumber));
		if (contentNumberString.length() > 2)
			throw new DictionaryException("Content number has more than 2 digts"); 
		else if (contentNumberString.length() < 2)
			contentNumberString.insert(0, '0');
		contentString.insert(startCharPos, contentNumberString);
		contentString.insert(startCharPos, startOfContentChar);
	}
}
