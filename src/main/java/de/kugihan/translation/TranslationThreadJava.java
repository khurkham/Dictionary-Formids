/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 - 2013 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.translation;

import de.kugihan.dictionaryformids.general.DictionaryException;
import de.kugihan.dictionaryformids.general.Util;

class TranslationThreadJava extends Thread implements TranslationThreadIF  {

	protected volatile boolean translationCancelledIndication = false;  // indicates whether the running translation should be aborted
	public synchronized boolean translationIsCancelled() {
		return translationCancelledIndication;
	}
	public synchronized void setTranslationCancelled() {
		translationCancelledIndication = true;
	}
	
	protected Translation translate = null;
	protected TranslationThreadCallbackIF translationThreadCallbackObj;
	protected TranslationParameters translationParametersObj;
	
	public TranslationThreadJava(TranslationThreadCallbackIF translationThreadCallbackParam,
			                     TranslationParameters     translationParametersObjParam) {
		translationThreadCallbackObj = translationThreadCallbackParam;
		translationParametersObj = translationParametersObjParam;
	}
	
	public void startTranslation() 
			throws DictionaryException {
	    if (translationParametersObj.isExecuteInBackground()) {
	    	start(); // run as thread in the background
	    }
	    else {
	    	doTranslation();  // directly execute (not as separate thread)
	    }
	    
	}
	
	public void run()
	{
		try
		{
			doTranslation();			
		}
		catch (Throwable t)
		{
			Util.getUtil().log(t);
		}
	}
	

	public void doTranslation()
			throws DictionaryException
	{
		if (translationParametersObj.isExecuteInBackground()) {
		    // set a low priority for the thread
			setPriority(Thread.NORM_PRIORITY - 2);
		}
		
		// get new translation
		if (! translationParametersObj.toBeTranslatedWordTextIsEmpty()) {
			translate = new Translation(translationParametersObj);

			TranslationResult resultOfTranslation = null;
			if (! translationIsCancelled())
				resultOfTranslation = translate.getTranslationResult();
			
			// report result of translation
			if (! translationIsCancelled())
				translationThreadCallbackObj.translationDone(resultOfTranslation, this);
		}
	}

	// cancels a running translation; called from another thread: 
	public void cancelTranslation() {
		
		// set flag for cancelled translation
		setTranslationCancelled();
		
		// forward cancelling to translate-object
		if (translate != null) {
			translate.cancelTranslation();
		
			// Send interrupt to translation thread 
			// (Thread.interrupt requires at minimum CLDC 1.1)
			interrupt();
		}
	}
}