/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation;

import java.util.Vector;

import de.kugihan.dictionaryformids.general.Util;

public class SingleTranslation {
	protected TextOfLanguage fromText;
	protected Vector 	     toTexts;  // Vector of TextOfLanguage-elements
	protected boolean        foundAtBeginOfExpression;
	protected DirectoryFileLocation directoryFileLocation;
	protected int 			 primarySortNumber;
	protected int 			 internalSortNumber;
	
	public SingleTranslation(TextOfLanguage        fromTextParam, 
			                 Vector          	   toTextsParam,
			                 boolean      		   foundAtBeginOfExpressionParam,
			                 int 				   primarySortNumberParam,
			                 DirectoryFileLocation directoryFileLocationParam) {
		fromText = fromTextParam;
		toTexts = toTextsParam;
		foundAtBeginOfExpression = foundAtBeginOfExpressionParam;
		primarySortNumber = primarySortNumberParam;
		directoryFileLocation = directoryFileLocationParam;
		determineInternalSortNumber();
	}

	protected void determineInternalSortNumber() {
		// count the number of words in fromText
		int numberOfWords = 1;
		boolean inSeparator = false;
		for (int charCount = 0; charCount < fromText.text.length(); ++charCount) {
			boolean isSeparatorCharacter = Util.isSeparatorCharacter(fromText.text.charAt(charCount));
			if (! inSeparator) {
				if (isSeparatorCharacter)
					++numberOfWords;
			}
			inSeparator = isSeparatorCharacter;
		}
		internalSortNumber = numberOfWords;
	}

	int compareTo(SingleTranslation otherSingleTranslation) {
		int compareResult;
		int directoryFileLocationCompared = directoryFileLocation.compareTo(otherSingleTranslation.directoryFileLocation);
		if ((directoryFileLocationCompared == 0) &&
		    (fromText.getLanguageIndex() == otherSingleTranslation.getFromText().getLanguageIndex())) {
			// translations are identical (same location and same translation direction)
			compareResult = directoryFileLocationCompared;
		}
		else {
			// compare primarySortNumbers
			int primarySortNumberCompared = primarySortNumber - otherSingleTranslation.primarySortNumber;
			if (primarySortNumberCompared == 0) {
				// primarySortNumbers are identical: priorize translations where the expression was found at the beginning
				if (foundAtBeginOfExpression == otherSingleTranslation.foundAtBeginOfExpression) { 
					// no difference in foundAtBeginOfExpression: compare the internalSortNumbers
					int internalSortNumberCompared = internalSortNumber - otherSingleTranslation.internalSortNumber;
					if (internalSortNumberCompared == 0) {
						// cannot determine sorting from sortNumber return sorting from directoryFileLocation
						compareResult = directoryFileLocationCompared;
					}
					else {
						compareResult = internalSortNumberCompared;
					}
				}
				else {
					if (foundAtBeginOfExpression) {
						// expression of this translation was found at beginning: priorize this translation 
						compareResult = -1;
					}
					else {
						// expression of the other translation was found at beginning: priorize other translation 
						compareResult = 1;
					}
				}
			}
			else {
				compareResult = primarySortNumberCompared;
			}
		}
		return compareResult;
	}

	public TextOfLanguage getFromText() {
		return fromText;
	}

	public Vector getToTexts() {
		return toTexts;
	}

	public int getNumberOfToTexts() {
		return toTexts.size();
	}

	public TextOfLanguage getToTextAt(int index) {
		return (TextOfLanguage) toTexts.elementAt(index);
	}

}
