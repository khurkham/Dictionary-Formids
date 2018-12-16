/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 - 2013 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.translation;

import de.kugihan.dictionaryformids.general.DictionaryException;

public interface TranslationThreadCallbackIF {
	void translationDone(TranslationResult resultOfTranslation,
		 	             TranslationThreadIF callingThread) throws DictionaryException;
}
