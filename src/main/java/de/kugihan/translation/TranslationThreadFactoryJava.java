/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 - 2013 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.translation;

public class TranslationThreadFactoryJava implements TranslationThreadFactoryIF {

	public TranslationThreadIF getTranslationThread(TranslationThreadCallbackIF translationThreadCallbackObj,
													TranslationParameters     translationParametersObj) {
		return new TranslationThreadJava(translationThreadCallbackObj,
				                         translationParametersObj);
	}

}
