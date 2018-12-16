/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 - 2011 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.dataaccess;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import de.kugihan.dictionaryformids.general.DictionaryException;
import de.kugihan.dictionaryformids.dataaccess.fileaccess.DfMInputStreamAccess;
import de.kugihan.dictionaryformids.general.Util;

class CsvFileCache {
	// This is a sketch for simple caching where only the last read file is kept 
	// in the cache. This is good enough for optimizing access to the directorynnn.csv-files.
	// Should be extended for further caching, specifically for the searchfiles.
	// This implementation is not yet complete: currently the file is re-opened
	// each time 
	// This cache is not yet active !
	protected InputStream cachedFile = null;
	protected String      fileName = null;
	protected int         lastPositionInStream;

	synchronized InputStream getCsvFile(DfMInputStreamAccess dictionaryDataFileISAccess,
			                            String  fileNameParam,
            					        int 	startPosition) 
				throws IOException, DictionaryException {
		long startTime = System.currentTimeMillis();
		InputStream csvStream = null;
		// check if file is in the cache
		if ((cachedFile != null) && fileName.equals(fileNameParam)) {
			System.out.println("cache hit " + fileNameParam);  // to be removed for final implementation
			// skip additional bytes
			int numberOfBytesToBeSkipped = startPosition - lastPositionInStream;
			if (numberOfBytesToBeSkipped < 0) {
				// stream needs to be reopened
				dictionaryDataFileISAccess.getInputStream(fileNameParam);
				numberOfBytesToBeSkipped = startPosition;
			}
			else {
				// reset to the old file position
				cachedFile.reset();
			}
			// TODO: wrap skip() in a loop to call it until it skipped the
			// specified number of bytes or an error occurred (see below)
			long skippedBytes = cachedFile.skip(numberOfBytesToBeSkipped);
			if (skippedBytes != numberOfBytesToBeSkipped) {
				throw new DictionaryException("CSV file: skipped only " + skippedBytes + " bytes");
			}
			lastPositionInStream = startPosition;
			csvStream = cachedFile;
		}
		else {
			// remember the file name and position into the cache
			fileName = fileNameParam;
			lastPositionInStream = startPosition;
			// close the previously cached file
			if (cachedFile != null)
				cachedFile.close();
			// open new file 
			csvStream =	dictionaryDataFileISAccess.getInputStream(fileName);  // to be done in specific class
			Util.getUtil().logTime("open file", startTime);
			startTime = System.currentTimeMillis();
			if (csvStream != null) {
				long skippedBytes = 0;
				while (skippedBytes != startPosition) {
					long skipResult = csvStream.skip(startPosition - skippedBytes);
					if (skipResult > 0L) {
						skippedBytes += skipResult;
					} else {
						break;
					}
				}
				Util.getUtil().logTime("position file", startTime);
				if (skippedBytes != startPosition) {
					throw new DictionaryException("CSV file: skipped only " + skippedBytes + " bytes");
				}
				// put file into cache, but only when the stream supports marks
				// currently: don't put into cache (= cache deactivated) 
				// if (csvStream.markSupported()) {
				//	csvStream.mark(20000);  // for the moment just assume 20000 bytes to remember
				//  cachedFile = csvStream;
				//}
			}
			else {
				throw new DictionaryException("Could not open file " + fileName);
			}
		}
		return csvStream;
	}
}
