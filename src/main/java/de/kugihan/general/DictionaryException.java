/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.general;

public class DictionaryException extends Exception {

	public DictionaryException(Throwable t) {
		super(t.toString());
	}

	public DictionaryException(String message) {
		super(message);
	}
}
