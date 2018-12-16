package de.kugihan.dictionaryformids.translation;


public interface TranslationExecutionCallback {
	void deletePreviousTranslationResult();
	void newTranslationResult(TranslationResult resultOfTranslation);
}
