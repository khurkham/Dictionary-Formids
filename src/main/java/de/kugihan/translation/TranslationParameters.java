/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2008 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation;

import de.kugihan.dictionaryformids.dataaccess.DictionaryDataFile;
import de.kugihan.dictionaryformids.general.Util;

public class TranslationParameters {
	protected DictionaryDataFile	dictionary;  			  // dictionary that is searched
	protected String  				toBeTranslatedWordText;   // the expression that has to be translated
	protected boolean[]				inputLanguages;  		  // the languages that are searched
	protected boolean[]				outputLanguages; 		  // the languages of the translations
	protected boolean 				executeInBackground;	  // executes the translation asynchronously in an own thread
	
	protected boolean 				searchSubExpressionStart; // false if no subexpressions shall be searched at the beginning
	protected boolean 				searchSubExpressionEnd;   // false if no subexpressions shall be searched at the end
	
	int								maxHits;                  // maximum number of translation hits for the search
	int 							durationForCancelSearch;  // in milliseconds; search will be cancelled if this time exceeded for the search

	public TranslationParameters(DictionaryDataFile dictionaryParam,
								 String  			toBeTranslatedWordTextInputParam,
								 boolean[]			inputLanguagesParam,
								 boolean[]			outputLanguagesParam,
								 boolean 			executeInBackgroundParam,
								 int           		maxHitsParam,
								 int   				durationForCancelSearchParam) {
		dictionary = dictionaryParam;
		toBeTranslatedWordText = toBeTranslatedWordTextInputParam;
		inputLanguages = inputLanguagesParam;
		outputLanguages = outputLanguagesParam;
		executeInBackground = executeInBackgroundParam;
		maxHits = maxHitsParam;
		durationForCancelSearch = durationForCancelSearchParam;

		toBeTranslatedWordText = Util.removeSuperflousSearchCharacters(toBeTranslatedWordText);

		// check if there is the noSearchSubExpression at the beginning or the end of the
		// to be translated word
		searchSubExpressionStart = true;
		searchSubExpressionEnd = true;
		if ((toBeTranslatedWordText.length() > 0) &&
			(toBeTranslatedWordText.charAt(0) == Translation.noSearchSubExpressionCharacter)) {
			searchSubExpressionStart = false;
			if (toBeTranslatedWordText.length() > 1)
				toBeTranslatedWordText = toBeTranslatedWordText.substring(1);
			else
				toBeTranslatedWordText = new String("");
		}
		else {
			searchSubExpressionStart = true;
		}
		if ((toBeTranslatedWordText.length() > 0) && 
		    (toBeTranslatedWordText.charAt(toBeTranslatedWordText.length() - 1) == Translation.noSearchSubExpressionCharacter)) {
			searchSubExpressionEnd = false;
			toBeTranslatedWordText = toBeTranslatedWordText.substring(0, toBeTranslatedWordText.length() - 1);
		}
		else {
			searchSubExpressionEnd = true;
		}
		
	}

	public boolean toBeTranslatedWordTextIsEmpty() {
		return getToBeTranslatedWordText().length() == 0;
	}

	public boolean isExecuteInBackground() {
		return executeInBackground;
	}


	public boolean[] getInputLanguages() {
		return inputLanguages;
	}


	public boolean[] getOutputLanguages() {
		return outputLanguages;
	}


	public boolean isSearchSubExpressionEnd() {
		return searchSubExpressionEnd;
	}


	public boolean isSearchSubExpressionStart() {
		return searchSubExpressionStart;
	}


	public DictionaryDataFile getDictionary() {
		return dictionary;
	}

	public String getToBeTranslatedWordText() {
		return toBeTranslatedWordText;
	}

	public int getDurationForCancelSearch() {
		return durationForCancelSearch;
	}

	public int getMaxHits() {
		return maxHits;
	}
}