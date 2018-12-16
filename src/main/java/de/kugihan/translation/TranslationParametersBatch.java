/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2013 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation;

import java.util.Enumeration;
import java.util.Vector;

public class TranslationParametersBatch {

	protected 	Vector  translationParametersVector = new Vector();  // Vector with elements of TranslationParameters

	public TranslationParameters getTranslationParametersAt(int index) {
		return (TranslationParameters) translationParametersVector.elementAt(index);
	}

	public Enumeration getAllTranslationParameters() {
		return translationParametersVector.elements();
	}

	public int numberOfTranslationParameters() {
		return translationParametersVector.size();
	}

	public void addTranslationParameters(TranslationParameters translationParametersObj) {
		translationParametersVector.addElement(translationParametersObj);
	}

	public void insertTranslationParametersAt(TranslationParameters translationParametersObj, int index) {
		translationParametersVector.insertElementAt(translationParametersObj, index);
	}
	
	public void removeTranslationParametersAt(int index) {
		translationParametersVector.removeElementAt(index);
	}

	public void removeAllTranslationParameters() {
		translationParametersVector.removeAllElements();
	}
}
