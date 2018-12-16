/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.general;

import de.kugihan.dictionaryformids.dataaccess.DictionaryDataFile;

public class CouldNotOpenPropertyFileException extends CouldNotOpenFileException {

	public CouldNotOpenPropertyFileException() {
		super("Property file could not be opened: " + DictionaryDataFile.propertyFileName);
	}
}
