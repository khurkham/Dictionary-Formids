/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.dataaccess.fileaccess;

import java.io.InputStream;

import de.kugihan.dictionaryformids.general.DictionaryException;

public abstract class DfMInputStreamAccess {

	public abstract InputStream getInputStream(String fileName) throws DictionaryException;

	public abstract boolean fileExists(String fileName) throws DictionaryException;

}
