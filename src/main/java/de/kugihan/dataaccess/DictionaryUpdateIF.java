/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.dataaccess;


// Interface within DictionaryForMIDs for the DictionaryGeneration DictionaryUpdate classes
public interface DictionaryUpdateIF {
	// mostly empty because DictionaryForMIDs does not use the DictionaryUpdate interface (only DictionaryGeneration)
	public DictionaryDataFile getDictionary();
	public void               setDictionary(DictionaryDataFile dictionary);
}
