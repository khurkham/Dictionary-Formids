/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 - 2011 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.dataaccess;

import java.io.IOException;

public class StringFileStorage
	implements FileStorage {

	int charactersInFile;
	public String stringStorage;

	public  StringFileStorage(StringBuffer content) {
		stringStorage = content.toString();
		charactersInFile = stringStorage.length(); 
	}

	public  StringFileStorage(String content) {
		stringStorage = content;
		charactersInFile = stringStorage.length(); 
	}
	
	public  StringFileStorage(byte[] content,
							  int sizeOfFile,
							  String charEncoding)
				throws IOException {
		stringStorage = new String(content, 0, sizeOfFile, charEncoding);
		charactersInFile = stringStorage.length(); 
	}

	public int  getCharactersInFile() {
		return charactersInFile;
	}
	
	public char readCharacterAt(int pos) {
		return stringStorage.charAt(pos);
	}
} 