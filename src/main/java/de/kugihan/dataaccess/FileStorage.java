/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 - 2011 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.dataaccess;

// to be analyzed: readCharacterAt is called very frequently; calling this method through
// and interface may impact performance.
public interface FileStorage {
	int  getCharactersInFile();
	char readCharacterAt(int pos);
}
	
