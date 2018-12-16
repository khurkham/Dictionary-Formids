/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 - 2013 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.translation;

public interface TranslationThreadFactoryIF {
	TranslationThreadIF getTranslationThread(TranslationThreadCallbackIF translationThreadCallbackObj,
			                                 TranslationParameters translationParametersObj);
}
