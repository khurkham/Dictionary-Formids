/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 - 2011 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.dataaccess;

import java.io.IOException;
import java.io.InterruptedIOException;

import de.kugihan.dictionaryformids.dataaccess.fileaccess.DfMInputStreamAccess;
import de.kugihan.dictionaryformids.general.DictionaryException;
import de.kugihan.dictionaryformids.general.DictionaryInterruptedException;
import de.kugihan.dictionaryformids.general.Util;

public class CsvFile {

	public static DefaultFileStorageReader fileStorageReader = new DefaultFileStorageReader();
	
	public boolean endOfDictionaryReached = false;	
	
	protected FileStorage fileStorageObj;
	protected int position = 0;
	protected int columnNumber = 0;
	protected char separatorCharacter;	
	protected DfMInputStreamAccess dictionaryDataFileISAccess;
	protected String fileName;
	protected int maxSizeOfFileData;
	protected String  charEncoding;
	
	public CsvFile(DfMInputStreamAccess dictionaryDataFileISAccessParam,
                   String  fileNameParam,
	       	       char    separatorCharacterParam,
		       	   String  charEncodingParam,
		       	   int     maxSizeOfFileDataParam) throws DictionaryException {
		setParams(dictionaryDataFileISAccessParam, fileNameParam, separatorCharacterParam, charEncodingParam, maxSizeOfFileDataParam);
		readCsvFileComplete();
	}

	public CsvFile(DfMInputStreamAccess dictionaryDataFileISAccessParam,
                   String  fileNameParam,
    	           char    separatorCharacterParam,
		       	   String  charEncodingParam,
		       	   int     maxSizeOfFileDataParam,
		       	   int 	   startPositionParam) throws DictionaryException {
		setParams(dictionaryDataFileISAccessParam, fileNameParam, separatorCharacterParam, charEncodingParam, maxSizeOfFileDataParam);
		fileStorageObj = fileStorageReader.readCsvFileLine(dictionaryDataFileISAccess,
				                                           fileName,
														   charEncoding,
														   startPositionParam);
	}

	private void setParams(DfMInputStreamAccess dictionaryDataFileISAccessParam,
                           String  fileNameParam,
				           char    separatorCharacterParam,
				           String  charEncodingParam,
				       	   int     maxSizeOfFileDataParam) {
		dictionaryDataFileISAccess = dictionaryDataFileISAccessParam;
        separatorCharacter = separatorCharacterParam;
		charEncoding       = charEncodingParam;
		maxSizeOfFileData  = maxSizeOfFileDataParam;
		fileName = fileNameParam;
	}
	
	public void readCsvFileComplete() 
	 						throws DictionaryException {
		fileStorageObj = fileStorageReader.readFileToFileStorage
									(dictionaryDataFileISAccess,
								     fileName,
									 charEncoding,
				       	             maxSizeOfFileData);
	}


	public void setPosition(int newPosition) {
		position = newPosition;
	}
	
	// Note: this method is declared as final only for the reason of better performance
	public final void skipRestOfLine() {
		int charactersInFile = fileStorageObj.getCharactersInFile();
		boolean endOfLineFound = false;

		if (position < charactersInFile) {
			do {
				char currentCharacter = fileStorageObj.readCharacterAt(position);
				if (position == charactersInFile -1) {
					endOfLineFound = true;
					endOfDictionaryReached = true;
				}
				else if (currentCharacter == '\n') {
					columnNumber = 0;
					endOfLineFound = true;
					++position;
				} else {
					++position;
				}
			}
			while (!endOfLineFound);
		}
	}
	
	// Note: this method is declared as final only for the reason of better performance
	public final StringBuffer getRestOfLine() {
		int charactersInFile = fileStorageObj.getCharactersInFile();
		StringBuffer line = new StringBuffer();
		boolean endOfLineFound = false;
		line.setLength(0); 
		if (position < charactersInFile) {
			do {
				char currentCharacter = fileStorageObj.readCharacterAt(position);
				if (currentCharacter == '\n') {
					columnNumber = 0;
					endOfLineFound = true;
					++position;
				} else {
					// add character if not carriage return
					if (currentCharacter != '\r') {
						line.append(currentCharacter);
					}
					++position;
				}
				if (position == charactersInFile) {
					endOfLineFound = true;
					endOfDictionaryReached = true;
				}
			}
			while (! endOfLineFound);
		}
		return line;
	}

	// Note: this method is declared as final only for the reason of better performance
	public final StringBuffer getWord() {
		int charactersInFile = fileStorageObj.getCharactersInFile();
		StringBuffer word = new StringBuffer();  // check performance impact of creating a new StringBuffer-object 
												 // for each call to getWord 
		boolean endOfWordFound = false;
		word.setLength(0); 
		if (position < charactersInFile) {
			do {
				char currentCharacter = fileStorageObj.readCharacterAt(position);
				if (currentCharacter == '\n') {
					columnNumber = 0;
					endOfWordFound = true;
					++position;
				} else if (currentCharacter == separatorCharacter) {
					++columnNumber;
					endOfWordFound = true;
					++position;
				} else {
					// add character if not carriage return
					if (currentCharacter != '\r') {
						word.append(currentCharacter);
					}
					++position;
				}
				if (position == charactersInFile) {
					endOfWordFound = true;
					endOfDictionaryReached = true;
				}
			}
			while (! endOfWordFound);
		}
		return word;
	}

	// Searches for a position before the occurence of searchCriteria (searchCriteria is 
	// searched in in the first column).
	// The position may be exactly at the first occurence of searchCriteria or before
	// The values in the first column must be sorted and unique (no diplicates allowed) 
	public void setPositionBefore(String searchCriteria) {
		int charactersInFile = fileStorageObj.getCharactersInFile();
		int lastPositionBefore = 0;  // beginning of file
		int currentPosition = charactersInFile / 2;  // divide by 2: middle of file
		int lastPositionAfter = charactersInFile - 1 ;  // end of file
		
		int numberOfLoops = 0;
		do {
			++numberOfLoops;

			// search for beginning of next line
			while ((currentPosition < charactersInFile) &&
				   (fileStorageObj.readCharacterAt(currentPosition) != '\n')) {
				++currentPosition;
			}
			++currentPosition;
			if (currentPosition >= charactersInFile) {
				// end of file reached, take last position
				break;
			}
			// read next word
			position = currentPosition;
			StringBuffer word = getWord();
			int comparison = searchCriteria.compareTo(word.toString());
			if (comparison < 0) {
				if (currentPosition == lastPositionAfter) {
					// same hit as before
					break;
				}
				else {
					// search before
					lastPositionAfter = currentPosition;
					currentPosition = currentPosition - ((currentPosition - lastPositionBefore) / 2);
				}
			}
			else if (comparison > 0) {
				if (currentPosition == lastPositionAfter) {
					// same hit as before
					break;
				}
				else {
					// search after
					lastPositionBefore = currentPosition;
					currentPosition = currentPosition + ((lastPositionAfter - currentPosition) / 2);
				}
			}
			else if (comparison == 0) {
				// direct hit 
				lastPositionBefore = currentPosition;
				break;
			}
		}
		while (true);
		position = lastPositionBefore;
		Util.getUtil().log("pos before: " + position, Util.logLevelMax);
	}
}