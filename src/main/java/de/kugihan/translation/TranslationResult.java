/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation;
import java.util.*;

import de.kugihan.dictionaryformids.dataaccess.DictionaryDataFile;

public class TranslationResult {

	public long    executionTime; 			          // in ms
	public boolean translationBreakOccurred = false;  // true if the translation was aborted
	public int     translationBreakReason;            // reason why translation was aborted
	
	public 		DictionaryDataFile	dictionary;                   // dictionary that was searched
	public 		TranslationParameters translationParametersObj;   // parameters there were used for the search
	
	protected 	Vector  			translations = new Vector();  // Vector of SingleTranslation
	
	public final static int BreakReasonCancelReceived = 1; 
	public final static int BreakReasonMaxExecutionTimeReached = 2; 
	public final static int BreakReasonCancelMaxNrOfHitsReached = 3; 
	
	public SingleTranslation getTranslationAt(int index) {
		return (SingleTranslation) translations.elementAt(index);
	}

	public Enumeration getAllTranslations() {
		return translations.elements();
	}

	public int numberOfFoundTranslations() {
		return translations.size();
	}

	public boolean translationFound() {
		return numberOfFoundTranslations() >0;
	}

	public void addTranslation(SingleTranslation newSingleTranslation) {
		translations.addElement(newSingleTranslation);
	}

	public void insertTranslationAt(SingleTranslation newSingleTranslation, int index) {
		translations.insertElementAt(newSingleTranslation, index);
	}
	
	public void removeTranslationAt(int index) {
		translations.removeElementAt(index);
	}
	
	/*
	 * The method removeTranslationsWithEmptyToTexts checks each entry in the 
	 * translation vector and removes those entries where all 'toTexts' are empty. 
	 */
	public void removeTranslationsWithEmptyToTexts() {
		int indexTranslation = 0;
		while (indexTranslation < numberOfFoundTranslations()) {
			SingleTranslation translationEntry = getTranslationAt(indexTranslation); 
			// check each toText for that translation
			boolean allToTextsEmpty = true;
			for (int indexToText = 0; indexToText < translationEntry.getNumberOfToTexts(); ++ indexToText) {
				String toTextString = translationEntry.getToTextAt(indexToText).getText();
				if ((toTextString == null) || (toTextString.length() == 0) ) {
					// that entry of toTexts is empty: nothing to do 
				}
				else {
					// that entry of toTexts is not empty
					allToTextsEmpty = false;
				}
			}
			if (allToTextsEmpty) {
				// remove that translation from the translations vector
				removeTranslationAt(indexTranslation);
			}
			else {
				++indexTranslation;
			}
		}
	}

}

