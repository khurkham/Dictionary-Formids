/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation;

import java.util.Enumeration;
import java.util.Vector;

import de.kugihan.dictionaryformids.dataaccess.DictionaryDataFile;
import de.kugihan.dictionaryformids.dataaccess.fileaccess.DfMInputStreamAccess;
import de.kugihan.dictionaryformids.general.*;

public class TranslationExecution {

	protected static Vector lastTranslationThreads = new Vector();  // Vector with elements of TranslationThreadIF 
	
	protected static TranslationThreadFactoryIF translationThreadFactoryObj = new TranslationThreadFactoryJava();  // WebApp uses TranslationThreadFactoryJavascript instead

	public    static void setTranslationThreadFactory(TranslationThreadFactoryIF translationThreadFactoryParam) {
		translationThreadFactoryObj = translationThreadFactoryParam;
	}
	
	protected static TranslationThreadCallback accessToHMIObj = new TranslationThreadCallback();
	
	public static void setTranslationExecutionCallback(TranslationExecutionCallback translationResultHMIObjParam) {
		// forward to accessToHMIObj 
		accessToHMIObj.setTranslationExecutionCallback(translationResultHMIObjParam);
	}
	
	public static DictionaryDataFile loadDictionary(DfMInputStreamAccess dictionaryDataFileISAccessParam) 
				throws DictionaryException {
		return new DictionaryDataFile(dictionaryDataFileISAccessParam,
				                      false);  // do not read properties for DictionaryGeneration
	}
	
	public static DictionaryDataFile loadDictionaryWithDictionaryGenerationValues(DfMInputStreamAccess dictionaryDataFileISAccessParam) 
				throws DictionaryException {
		return new DictionaryDataFile(dictionaryDataFileISAccessParam,
				                      true);  // do read properties for DictionaryGeneration
	}
	
	public static void unloadDictionary(DictionaryDataFile dictionary) {
		// nothing to be done (possibly for a future extension)
	}

	public static void executeTranslationBatch(TranslationParametersBatch translationParametersBatchObj)
	throws DictionaryException
	{
		synchronized (accessToHMIObj) {
			cancelLastTranslation();
			accessToHMIObj.newTranslation();
			Enumeration allTranslationParameters = translationParametersBatchObj.getAllTranslationParameters();
			while (allTranslationParameters.hasMoreElements()) {
				TranslationParameters translationParametersObj = (TranslationParameters) allTranslationParameters.nextElement(); 
				TranslationThreadIF newTranslationThread = translationThreadFactoryObj.getTranslationThread(accessToHMIObj, translationParametersObj);
		    	lastTranslationThreads.addElement(newTranslationThread);
		    	// start new translation thread
				newTranslationThread.startTranslation();
			}
		}
	}
	
	public static void executeTranslation(TranslationParameters translationParametersObj)
	throws DictionaryException
	{
		TranslationParametersBatch translationParametersBatchObj = new TranslationParametersBatch();
		translationParametersBatchObj.addTranslationParameters(translationParametersObj);
		executeTranslationBatch(translationParametersBatchObj);
	}
	
	public static void cancelLastTranslation() {
		synchronized (accessToHMIObj) {
			// cancel last translation threads that may still be running
			Enumeration lastTranslationThreadsEnum = lastTranslationThreads.elements();
			while (lastTranslationThreadsEnum.hasMoreElements()) {
					TranslationThreadIF lastTranslationThreadToBeCancelled = (TranslationThreadIF) lastTranslationThreadsEnum.nextElement();
					lastTranslationThreadToBeCancelled.cancelTranslation();
			}
		}
			lastTranslationThreads.removeAllElements();
	}
}


class TranslationThreadCallback implements TranslationThreadCallbackIF {

	protected TranslationExecutionCallback translationResultHMIObj = null;
	protected boolean noTranslationDoneYet;
	
	synchronized void setTranslationExecutionCallback(TranslationExecutionCallback translationResultHMIObjParam) {
		translationResultHMIObj = translationResultHMIObjParam;
	}
	
	synchronized void newTranslation() {
		noTranslationDoneYet = true;
	}
	
	public synchronized void translationDone(TranslationResult resultOfTranslation,
										 	 TranslationThreadIF callingThread) 
										 			 throws DictionaryException {
		if (! callingThread.translationIsCancelled()) {
			if (translationResultHMIObj == null) {
				throw new DictionaryException("No callback for translation results set. Call TranslationExecution.setTranslationExecutionCallback.");
			}
			// if this is the first translation result for a translation, then delete previous translation result
			if (noTranslationDoneYet) {
				translationResultHMIObj.deletePreviousTranslationResult();
				noTranslationDoneYet = false;
			}
			translationResultHMIObj.newTranslationResult(resultOfTranslation);
		}
	}
}