/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 - 2011 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.dataaccess;

import java.io.InterruptedIOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.kugihan.dictionaryformids.general.DictionaryException;
import de.kugihan.dictionaryformids.dataaccess.fileaccess.DfMInputStreamAccess;
import de.kugihan.dictionaryformids.general.Util;
import de.kugihan.dictionaryformids.general.DictionaryInterruptedException;


public class DefaultFileStorageReader {

	static CsvFileCache fileCache = new CsvFileCache();
	
	public FileStorage readFileToFileStorage(DfMInputStreamAccess dictionaryDataFileISAccess,
            							     String  fileName,
											 String  charEncoding,
				       	                     int     maxSizeOfFileData) 
	 						throws DictionaryException {
		byte[] fileData = new byte[maxSizeOfFileData];
		FileStorage fileStorageObj;
		try {
			long startTime = System.currentTimeMillis();
			InputStream csvStream =	dictionaryDataFileISAccess.getInputStream(fileName);
			Util.getUtil().logTime("open file", startTime);
			startTime = System.currentTimeMillis();
			if (csvStream != null) {
				int sizeOfFile = 0;
				int bytesRead;
				do {
					bytesRead = csvStream.read(fileData, sizeOfFile, fileData.length - sizeOfFile);
					if (bytesRead != -1)
						sizeOfFile += bytesRead;
					if (sizeOfFile == fileData.length) {
						// buffer is full: see if there is still more data to be read
						int character = csvStream.read();
						if (character != -1) {
							// there is more data that had not been read
							Util.getUtil().log("Warning: buffer size too small for file " + fileName);
						}
						break;
					}
				}
				while (bytesRead != -1);
				csvStream.close();
				Util.getUtil().logTime("read file", startTime);
				startTime = System.currentTimeMillis();
				fileStorageObj = new StringFileStorage(fileData,
													   sizeOfFile,
													   charEncoding);
				Util.getUtil().logTime("parse file", startTime);
			}
			else
			{
				throw new DictionaryException("Could not open file " + fileName);
			}
		}
		catch (InterruptedIOException e)
		{
			// Thread was interupted during IO operation
			throw new DictionaryInterruptedException(e);
		}
		catch (IOException e)
		{
			throw new DictionaryException(e);
		}
		return fileStorageObj;
	}

	// special method for reading a CSV file starting at byte position 
	// startPosition and reading only one single line
	public FileStorage readCsvFileLine(DfMInputStreamAccess dictionaryDataFileISAccess,
		                               String fileName,
									   String charEncoding,
									   int startPosition)
	 						throws DictionaryException {
		FileStorage fileStorageObj;
		try {
			InputStream csvStream = fileCache.getCsvFile(dictionaryDataFileISAccess, fileName, startPosition);
			long startTime = System.currentTimeMillis();
			int sizeOfFile = 0;
			StringBuffer csvLineString = new StringBuffer();
			InputStreamReader csvStreamReader = new InputStreamReader(csvStream, charEncoding);
			int character;
			boolean endOfLineReached = false;
			// read character by character till eiter newline or EOF is received:
			do {
				character = csvStreamReader.read();
				if ((character != '\n') && (character != -1)) {
					csvLineString.append((char) character);
					++sizeOfFile;
				}
				else {
					endOfLineReached = true;
				}
			} 
			while (! endOfLineReached);
			Util.getUtil().logTime("read/parse file-line", startTime);
			fileStorageObj = new StringFileStorage(csvLineString);
		}
		catch (InterruptedIOException e)
		{
			// Thread was interupted during IO operation
			throw new DictionaryInterruptedException(e);
		}
		catch (IOException e)
		{
			throw new DictionaryException(e);
		}
		return fileStorageObj;
	}
}