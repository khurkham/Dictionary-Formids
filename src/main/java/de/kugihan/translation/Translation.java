/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation;
import java.util.Vector;

import de.kugihan.dictionaryformids.dataaccess.*;
import de.kugihan.dictionaryformids.general.DictionaryException;
import de.kugihan.dictionaryformids.general.Util;
import de.kugihan.dictionaryformids.translation.normation.Normation;

public class Translation {

	public DictionaryDataFile	dictionary;
	public String  				toBeTranslatedWordText; 
	public boolean[]			inputLanguages;  
	public boolean[]			outputLanguages; 
	boolean 					searchSubExpressionStart;
	boolean 					searchSubExpressionEnd;
	int							maxHits;
	int 						durationForCancelSearch;
	
	protected                   TranslationParameters translationParametersObj;
	
	public Translation(TranslationParameters translationParametersParam) 
						throws DictionaryException  {
		dictionary = translationParametersParam.getDictionary();
		toBeTranslatedWordText = translationParametersParam.getToBeTranslatedWordText();
		inputLanguages = translationParametersParam.getInputLanguages();
		outputLanguages = translationParametersParam.getOutputLanguages();
		searchSubExpressionStart = translationParametersParam.isSearchSubExpressionStart();
		searchSubExpressionEnd = translationParametersParam.isSearchSubExpressionEnd();
		maxHits = translationParametersParam.getMaxHits();
		durationForCancelSearch = translationParametersParam.getDurationForCancelSearch();
		
		translationParametersObj = translationParametersParam;
		
		if ((inputLanguages.length != dictionary.numberOfAvailableLanguages) || 
			(outputLanguages.length != dictionary.numberOfAvailableLanguages)) {
			throw  new DictionaryException("Incorrect number of array elements for inputLanguages/outputLanguages"); 
		}
	}
	
	// 'shortcuts' for the characters with a special meaning for search: 
	public static final char wildcardAnySeriesOfCharacter = Util.wildcardAnySeriesOfCharacter;
	public static final char wildcardAnySingleCharacter = Util.wildcardAnySingleCharacter;
	public static final char noSearchSubExpressionCharacter = Util.noSearchSubExpressionCharacter;
	
	// returns -1 if no wildcard character exists in word:
	static int positionFirstWildcardCharacter(String word) {
		int index;
		int indexWildcardAnySeriesOfCharacters = word.indexOf(wildcardAnySeriesOfCharacter);
		int indexWildcardAnySingleCharacter = word.indexOf(wildcardAnySingleCharacter);
		if ((indexWildcardAnySingleCharacter == -1) &&
			(indexWildcardAnySeriesOfCharacters == -1)) {
			index = -1;
		}
		else if ((indexWildcardAnySingleCharacter == -1) &&
			     (indexWildcardAnySeriesOfCharacters > -1)) {
				index = indexWildcardAnySeriesOfCharacters;
		}
		else if ((indexWildcardAnySingleCharacter > -1) &&
			     (indexWildcardAnySeriesOfCharacters == -1)) {
				index = indexWildcardAnySingleCharacter;
		}
		else if (indexWildcardAnySingleCharacter < indexWildcardAnySeriesOfCharacters) {
			index = indexWildcardAnySingleCharacter;
		}
		else {
			index = indexWildcardAnySeriesOfCharacters;			
		}
		return index;
	}
	static String expressionTillWildcard(String word) {
		return word.substring(0, positionFirstWildcardCharacter(word));
	}

	protected TranslationResult resultOfTranslation;
	
	long startTime;
	
	volatile boolean translationIsCancelled = false;

	
	public TranslationResult getTranslationResult() {

		resultOfTranslation = new TranslationResult();
		startTime = System.currentTimeMillis();
		Util.memCheck("start translation: ");

		resultOfTranslation.dictionary = dictionary; 
		resultOfTranslation.translationParametersObj = translationParametersObj;
		
		try { 
			for (int languageCount = 0; languageCount < dictionary.numberOfAvailableLanguages; ++languageCount) {
				LanguageDefinition languageDefinitionObj = dictionary.supportedLanguages[languageCount];
				if (languageDefinitionObj.isSearchable && inputLanguages[languageCount]) {
					// search for this language
					int inputLanguageForSearch = languageCount; 
					Normation normationObj = dictionary.supportedLanguages[inputLanguageForSearch].normationObj;
					// determine search words from the to be translated word
					Vector searchWords = normationObj.searchWord(toBeTranslatedWordText);
					// get translation for each searchWord
					for (int wordCount = 0; wordCount < searchWords.size(); ++wordCount) {
						SearchedWord searchWord = (SearchedWord) searchWords.elementAt(wordCount);
						String nonNormatedWord = searchWord.word;
						String toBeTranslatedWordNormated = normationObj.normateWord(new StringBuffer(nonNormatedWord), true).toString();
						if (toBeTranslatedWordNormated.length() > 0) {
							searchTranslationForNormatedWord(inputLanguageForSearch, toBeTranslatedWordNormated);
						}
					}
				}
			}
		}
		catch (Throwable t) {
			Util.getUtil().log(t);
		}
		long endTime = System.currentTimeMillis();
		resultOfTranslation.executionTime = endTime - startTime;
		Util.memCheck("end translation: ");

		return resultOfTranslation;
	}
	
	public void searchTranslationForNormatedWord(int inputLanguageForSearch, String toBeTranslatedWordNormated) 
						throws DictionaryException  {
		String initialSearchExpression;
		boolean containsWildcard = (positionFirstWildcardCharacter(toBeTranslatedWordNormated) >= 0);
		if (containsWildcard)
			initialSearchExpression = expressionTillWildcard(toBeTranslatedWordNormated);
		else
			initialSearchExpression = toBeTranslatedWordNormated;
		int initialSearchExpressionLength = initialSearchExpression.length();
		
		/* 
		 * read searchlist file
		 */
		String languagePostfix = dictionary.supportedLanguages[inputLanguageForSearch].languageFilePostfix;
		String searchListFileName = dictionary.getPathDataFiles() + 
								    dictionary.prefixSearchListFile + 
									languagePostfix +
									dictionary.suffixSearchListFile; 
		CsvFile searchListFile = new CsvFile(dictionary.getDictionaryDataFileISAccess(),
				                             searchListFileName,
										     dictionary.searchListFileSeparationCharacter,
											 dictionary.searchListCharEncoding,
											 dictionary.searchListFileMaxSize);
		
		Util.memCheck("searchfile open: ");
		String indexFileNumber = null;
		boolean lastIndexFileSearched = false;
		searchListFile.setPositionBefore(initialSearchExpression);
		while (!searchListFile.endOfDictionaryReached) {
			String wordInIndex = searchListFile.getWord().toString();
			if (containsWildcard) {
				int endOfFirstPartWordInIndex = initialSearchExpressionLength;
				int wordInIndexLength = wordInIndex.length();
				if (wordInIndexLength < initialSearchExpressionLength)
					endOfFirstPartWordInIndex = wordInIndexLength; 
				String firstPartWordInIndex = wordInIndex.substring(0, endOfFirstPartWordInIndex);
				if (firstPartWordInIndex.startsWith(initialSearchExpression)) {
					if (! lastIndexFileSearched) {
						if (indexFileNumber != null) {
							if (searchInIndexFileBreakCondition(inputLanguageForSearch, toBeTranslatedWordNormated, indexFileNumber))
								break;
						}
					}
					indexFileNumber = searchListFile.getWord().toString();
					if (searchInIndexFileBreakCondition(inputLanguageForSearch, toBeTranslatedWordNormated, indexFileNumber))
						break;
					lastIndexFileSearched = true;
				}
				else if (firstPartWordInIndex.compareTo(initialSearchExpression) > 0) {
					if (! lastIndexFileSearched)
						if (indexFileNumber != null) {
							if (searchInIndexFileBreakCondition(inputLanguageForSearch, toBeTranslatedWordNormated, indexFileNumber))
								break;
						}
					break;
				}
				else {
					indexFileNumber = searchListFile.getWord().toString();
				}
			}
			else {
				if (wordInIndex.compareTo(initialSearchExpression) >= 0) {
					// the last index file was the right one
					if (indexFileNumber != null) {
						if (searchInIndexFileBreakCondition(inputLanguageForSearch, toBeTranslatedWordNormated, indexFileNumber))
							break;
					}
					// the search is continued 
					// when wordInIndex and initialSearchExpression are equal strings 
					// or 
					// when wordInIndex starts with initialSearchExpression and is wordInIndex
					// is followed by a blank
					boolean continueSearch = false;
					if (wordInIndex.startsWith(initialSearchExpression)) {
						if (wordInIndex.length() > initialSearchExpression.length()) {
							if (wordInIndex.charAt(initialSearchExpression.length()) == ' ') 
								continueSearch = true;
						}
						else 
							continueSearch = true;  // strings are equal
					}
					if (continueSearch) {
						indexFileNumber = searchListFile.getWord().toString();
					}
					else {
						// search is finished
						break;
					}
				}
				else {
					indexFileNumber = searchListFile.getWord().toString();
				}
			}
		}

		if (searchListFile.endOfDictionaryReached && (! lastIndexFileSearched)) {
			// search in the last index file
			searchInIndexFileBreakCondition(inputLanguageForSearch, toBeTranslatedWordNormated, indexFileNumber);
		}
		searchListFile = null; // to allow garbage collection
	}
	
	public boolean searchInIndexFileBreakCondition(int inputLanguageForSearch, String toBeTranslatedWordNormated, String indexFileNumber) 
							throws DictionaryException  {
		if (translationBreakCondition()) {
			return true;
		}
		else {
			searchInIndexFile(inputLanguageForSearch, toBeTranslatedWordNormated, indexFileNumber);
			return translationBreakCondition();
		}
	}
	
	public void searchInIndexFile(int inputLanguageForSearch, String toBeTranslatedWordNormated, String indexFileNumber) 
	                        throws DictionaryException  {
		Util.getUtil().logDebug("indexFileNumber " + indexFileNumber);
		String languagePostfix = dictionary.supportedLanguages[inputLanguageForSearch].languageFilePostfix;
		String indexFileName = dictionary.getPathDataFiles() + 
	                           dictionary.prefixIndexFile +
							   languagePostfix +
							   indexFileNumber +
		                       dictionary.suffixIndexFile;

		/* 
		 * read index file
		 */
		boolean containsWildcard = (positionFirstWildcardCharacter(toBeTranslatedWordNormated) >= 0);
		String searchExpression;
		String initialSearchExpression;
		searchExpression = toBeTranslatedWordNormated;
		if (containsWildcard) {
			initialSearchExpression = expressionTillWildcard(toBeTranslatedWordNormated);
		}
		else {
			initialSearchExpression = toBeTranslatedWordNormated;
		}
		int initialSearchExpressionLength = initialSearchExpression.length();
		String indexStringLine = null;
		Util.getUtil().logDebug("indexFileName " + indexFileName);
		
		CsvFile indexFile = new CsvFile(dictionary.getDictionaryDataFileISAccess(),
                                 		indexFileName,
									    dictionary.indexFileSeparationCharacter,
										dictionary.indexCharEncoding,
										dictionary.indexFileMaxSize);

		Util.memCheck("indexfile open: ");
		indexFile.setPositionBefore(initialSearchExpression);
		while (! indexFile.endOfDictionaryReached) {
			String indexEntry = indexFile.getWord().toString();
			if (! containsWildcard) {
				if (indexEntry.compareTo(searchExpression) < 0) {
					// skip entry
					indexFile.skipRestOfLine();
				}
				// do an ordinary match on the entry
				else if (indexEntry.startsWith(searchExpression)) {
					if (noWildcardMatchRest(searchExpression, indexEntry)) {
						// entry found
						indexStringLine = indexFile.getRestOfLine().toString();
						getDictionaryEntry(inputLanguageForSearch, indexStringLine);
					}
					else {
						indexFile.skipRestOfLine();
					}
				}
				else {
					break;
				}
			}
			else {
				int endOfFirstPartIndexEntry = initialSearchExpressionLength;
				int indexEntryLength = indexEntry.length();
				if (indexEntryLength < initialSearchExpressionLength)
					endOfFirstPartIndexEntry = indexEntryLength; 
				String firstPartIndexEntry = indexEntry.substring(0, endOfFirstPartIndexEntry);
				if (initialSearchExpression.compareTo(firstPartIndexEntry) < 0) {
					// no more matches possible:
					break;
				}
				// do a wildcard match on the entry
				if (wildcardMatch(indexEntry, 
						          0,
								  indexEntry.length(),
								  searchExpression, 
						          0, 
								  searchExpression.length())) {
					// Strings matched
					indexStringLine = indexFile.getRestOfLine().toString();
					getDictionaryEntry(inputLanguageForSearch, indexStringLine);
				}
				else {
					indexFile.skipRestOfLine();
				}
			}
		}
	}
	
	// for perfomance reasons the string positions are passed in to the method wildcardMatch,
	// instead of creating new substrings for each recursion.
	// Note: this method is declared as final only for the reason of better performance
	public final boolean wildcardMatch(String toBeSearchedExpression,
									   int    positionCharacterToBeSearchedExpression,
									   int    lengthToBeSearchedExpression,
					                   String wildcardExpression,
					                   int    positionCharacterWildcardExpression,
					                   int    lengthWildcardExpression) {
		boolean expressionsMatch = false;
		if (positionCharacterWildcardExpression == lengthWildcardExpression) {
			if (positionCharacterToBeSearchedExpression == lengthToBeSearchedExpression) {
				// matched till to the last charcter
				expressionsMatch = true;
			} 
			else {
				// length is different: no match unless the next character in toBeSearchedExpression
				// is a blank character (and searchSubExpressionEnd is true)
				if ((lengthToBeSearchedExpression > positionCharacterToBeSearchedExpression) && searchSubExpressionEnd) {
					if (toBeSearchedExpression.charAt(positionCharacterToBeSearchedExpression) == ' ') {
						expressionsMatch = true;
					}
					else {
						expressionsMatch = false;
					}
				}
				else {
					expressionsMatch = false;
				}
			}
		}
		else if (positionCharacterToBeSearchedExpression == lengthToBeSearchedExpression) {
			// length is different: no match
			// but if the rest of the wilcard expression is wildcardAnySeriesOfCharacter, then
			// the expressions still match
			boolean restIsAnySeriesOfCharacter = true;
			for (int position = positionCharacterWildcardExpression;
			     position < lengthWildcardExpression;
				 ++position) {
				if (wildcardExpression.charAt(position) != wildcardAnySeriesOfCharacter) {
					restIsAnySeriesOfCharacter = false;
					break;
				}
			}
			expressionsMatch = restIsAnySeriesOfCharacter;
		}
		else {
			char characterToBeSearchedExpression = 
				              toBeSearchedExpression.charAt(positionCharacterToBeSearchedExpression);
			char characterWildcardExpression = 
				              wildcardExpression.charAt(positionCharacterWildcardExpression);

			if (characterWildcardExpression == wildcardAnySingleCharacter) {
				// matches to any character: check remaining part of the expressions
				expressionsMatch = wildcardMatch(toBeSearchedExpression,
						 						 positionCharacterToBeSearchedExpression + 1,
												 lengthToBeSearchedExpression,
												 wildcardExpression,
												 positionCharacterWildcardExpression + 1,
												 lengthWildcardExpression);
			}
			else if (characterWildcardExpression == wildcardAnySeriesOfCharacter) {
				// see if the rest of the wildcardExpression matches to any of the remaining 
				// characters of toBeSearchedExpression
				for (int position = positionCharacterToBeSearchedExpression;
				     position <= lengthToBeSearchedExpression;
					 ++position) {
					expressionsMatch = wildcardMatch(toBeSearchedExpression,
												     position,
													 lengthToBeSearchedExpression,
													 wildcardExpression,
													 positionCharacterWildcardExpression + 1,
													 lengthWildcardExpression);
					if (expressionsMatch)
						// match for expressions found, no need to continue search
						break;
				}
			}
			else if (characterWildcardExpression == characterToBeSearchedExpression) {
				// characters match: check remaining part of the expressions
				expressionsMatch = wildcardMatch(toBeSearchedExpression,
												 positionCharacterToBeSearchedExpression + 1,
												 lengthToBeSearchedExpression,
												 wildcardExpression,
												 positionCharacterWildcardExpression + 1,
												 lengthWildcardExpression);
			}
			
		}
		
		return expressionsMatch;
	}

	// checks if both strings are of equal length or if indexEntry is followed by a blank
	// character (and searchSubExpressionEnd is true)
	public boolean noWildcardMatchRest(String searchExpression, String indexEntry) {
		int searchExpressionLength = searchExpression.length();
		int indexEntryLength = indexEntry.length();
		if (searchExpressionLength == indexEntryLength) {
			return true;
		}
		if ((indexEntryLength > searchExpressionLength) && searchSubExpressionEnd) {
			if (indexEntry.charAt(searchExpressionLength) == ' ') {
				return true;
			}
		}
		// false in the other cases
		return false;
	}

	public void getDictionaryEntry(int inputLanguageForSearch, String indexStringLine) 
					throws DictionaryException {
		int posIndexFileSeparatorIndexEntries;
		int posFirstCharIndexString = 0;
		int posLastCharIndexString;
		do {
			posIndexFileSeparatorIndexEntries = 
				indexStringLine.indexOf(dictionary.indexFileSeparatorIndexEntries, 
						                posFirstCharIndexString);
			if (posIndexFileSeparatorIndexEntries == -1)
				posLastCharIndexString = indexStringLine.length();
			else 
				posLastCharIndexString = posIndexFileSeparatorIndexEntries;
			String indexString = indexStringLine.substring(posFirstCharIndexString, 
					                                       posLastCharIndexString);
			posFirstCharIndexString = posLastCharIndexString + 1;
			int posIndexFileSeparatorFileNumberToPosition = 
			        indexString.indexOf(dictionary.indexFileSeparatorFileNumberToPosition);
			String directoryFileNumberString = indexString.substring(0, posIndexFileSeparatorFileNumberToPosition);
			int directoryFileNumber = Integer.parseInt(directoryFileNumberString);
			int posIndexFileSeparatorFilePositionToSearchIndicator = 
				    indexString.indexOf(dictionary.indexFileSeparatorFilePositionToSearchIndicator, 
				    		            posIndexFileSeparatorFileNumberToPosition + 1);
			if (posIndexFileSeparatorFilePositionToSearchIndicator == -1) {
				throw new DictionaryException("Indexfile has no searchindicator - use DictionaryGeneration 2.4.4 or newer");
			}
			int positionInDirectoryFile = 
				 Integer.valueOf(indexString.substring((posIndexFileSeparatorFileNumberToPosition + 1), 
						                               posIndexFileSeparatorFilePositionToSearchIndicator)).intValue();
			SearchIndicator searchIndicatorObj = new SearchIndicator(indexString.charAt(posIndexFileSeparatorFilePositionToSearchIndicator + 1));
			// if the index entry comes from a subexpression but searchSubExpressionStart is false then this entry is ignored
			if (!(searchSubExpressionStart || searchIndicatorObj.isBeginOfExpression())) {
				// just do nothing: don't get the corresponding dictionary entry
			}
			else {
				// ok, get the corresponding dictionary entry 
				String postfixDictionaryFile;
				LanguageDefinition supportedLanguage = dictionary.supportedLanguages[inputLanguageForSearch]; 
				if (supportedLanguage.hasSeparateDictionaryFile) {
					// use the file postfix also for the dictionary file
					postfixDictionaryFile =
						supportedLanguage.languageFilePostfix;
				}
				else {
					// no suffix: empty string
					postfixDictionaryFile = "";
				}
				
				if (! translationBreakCondition()) {
					
					/*
					 *  read dictionary file
					 */ 
					DirectoryFileLocation directoryFileLocation = 
						             new DirectoryFileLocation(directoryFileNumber,
														       postfixDictionaryFile,
														       positionInDirectoryFile);
					getTranslation(inputLanguageForSearch, 
							       directoryFileLocation, 
							       searchIndicatorObj.isBeginOfExpression());
				}
				else {
					break;
				}
			}
		}
		while (posIndexFileSeparatorIndexEntries > 0);
	}
	
	public void getTranslation(int inputLanguageForSearch, 
			                   DirectoryFileLocation directoryFileLocation, 
			                   boolean foundAtBeginOfExpression) 
			throws DictionaryException
	{
		String dictionaryFileName = dictionary.getPathDataFiles() + 
							        dictionary.prefixDictionaryFile +
							        directoryFileLocation.postfixDictionaryFile +
							        directoryFileLocation.directoryFileNumber +
							        dictionary.suffixDictionaryFile;
		Util.getUtil().logDebug("dictionaryFileName " + dictionaryFileName);
		Util.getUtil().logDebug("position " + String.valueOf(directoryFileLocation.positionInDirectoryFile));
		CsvFile dictionaryFile = new CsvFile(dictionary.getDictionaryDataFileISAccess(),
                							 dictionaryFileName, 
	    							         dictionary.dictionaryFileSeparationCharacter,
											 dictionary.dictionaryCharEncoding,
											 dictionary.dictionaryFileMaxSize,
											 directoryFileLocation.positionInDirectoryFile);

		Util.memCheck("dictionaryfile open: ");
		TextOfLanguage fromText = null; 		
		Vector toTexts = new Vector();
		
		for (int indexLanguage = 0;
  	         indexLanguage < dictionary.numberOfAvailableLanguages;
	         ++indexLanguage) {
			StringBuffer word = dictionaryFile.getWord();

			if ("weakCrypt".equals(dictionary.fileEncodingFormat)) {
				weakDecrypt(word);
			}

			if (inputLanguageForSearch == indexLanguage) {
				Util.getUtil().convertFieldAndLineSeparatorChars(word);
				fromText = new TextOfLanguage(word.toString(), indexLanguage, dictionary);
			}
			if (outputLanguages[indexLanguage]) {
				Util.getUtil().convertFieldAndLineSeparatorChars(word);
				toTexts.addElement(new TextOfLanguage(word.toString(), indexLanguage, dictionary));
			}
		}
		addTranslation(fromText, 
				       toTexts, 
				       foundAtBeginOfExpression, 
				       directoryFileLocation);
		dictionaryFile = null; // to allow garbage collection
	}


	/**
	 * Very weak encrytion/decryption mechanism
	 * See http://dictionarymid.german-fighters.com/forum/index.php?topic=215.0
	 */
	private static void weakDecryptOld(StringBuffer word) {
		int n = word.length();
		while (--n>=0) {
			char ch = word.charAt(n);
			if (ch>=60 && ch<124) word.setCharAt(n, (char) (((ch-60)^'+') + 60));
		}
  }

	// intentional misspelling "Espernato" for Esperanto
	private static final char[] weakEncrypt_password = "EspernatoEstasBona".toCharArray();
	/**
	 * A little bit less weak encrytion/decryption mechanism.
	 * See http://dictionarymid.german-fighters.com/forum/index.php?topic=215.0
	 */
	private static void weakDecrypt(StringBuffer word) {

		//System.err.println("word = " + word);
		// Assume we start on a word boundary and restart each time we encounter a char outside range 60-124
		int n = 0;
		int limit=word.length();
		for (int i = 0; i<limit; i++) {
			char ch = word.charAt(i);
			if (ch>=60 && ch<124) {
				ch = (char) ((((ch-60)^'+') -weakEncrypt_password[n]+256)%64+ 60);
				word.setCharAt(i, ch);
				n  = (n+1) % weakEncrypt_password.length;
			} else {
				n = 0; // outside range - restart
			}
		}
		//System.err.println("word2 = " + word);
	}

	public void addTranslation(TextOfLanguage 	fromText, 
			                   Vector		 	toTexts,
			                   boolean          foundAtBeginOfExpression,
			                   DirectoryFileLocation directoryFileLocation) {
		SingleTranslation newSingleTranslation = 
						new SingleTranslation(fromText, 
						                      toTexts,
							                  foundAtBeginOfExpression,
							                  fromText.getLanguageIndex(), // primary sorting according to language of fromText
							                  directoryFileLocation);
		// sort entries and remove duplicates 
		// optimization: this needs to be implemented more efficiently; current implementation is
		//               slow if many results exist
		int indexTranslation = 0;
		int numberOfTranslations = resultOfTranslation.translations.size();
		while (indexTranslation < numberOfTranslations) {
			SingleTranslation translation = (SingleTranslation) resultOfTranslation.translations.elementAt(indexTranslation);
			int translationsCompared = newSingleTranslation.compareTo(translation);
			if (translationsCompared == 0) {
				// duplicate entry: new translation is ignored
				break;
			}
			else if (translationsCompared < 0) {
				// insert new translation at current position
				resultOfTranslation.insertTranslationAt(newSingleTranslation, indexTranslation);
				break;
			} 
			else {
				// continue to search for the right position 
			}
			++indexTranslation;
		}
		if (indexTranslation == numberOfTranslations) {
			// add new translation at end
			resultOfTranslation.addTranslation(newSingleTranslation);
		}
	}

	boolean translationBreakCondition() {
		if (resultOfTranslation.numberOfFoundTranslations() >= maxHits) { 
			resultOfTranslation.translationBreakOccurred = true;
			resultOfTranslation.translationBreakReason = TranslationResult.BreakReasonCancelMaxNrOfHitsReached;
			return true;
		}
		else if (System.currentTimeMillis() - startTime >= durationForCancelSearch) {
			resultOfTranslation.translationBreakOccurred = true;
			resultOfTranslation.translationBreakReason = TranslationResult.BreakReasonMaxExecutionTimeReached;
			return true;
		}
		else if (translationIsCancelled) {
			resultOfTranslation.translationBreakOccurred = true;
			resultOfTranslation.translationBreakReason = TranslationResult.BreakReasonCancelReceived;
			return true;
		}
		else {
			return false;
		}
	}
	
	// called from another thread: 
	public void cancelTranslation() {
		// set flag for cancelled translation
		translationIsCancelled = true;
	}
}
