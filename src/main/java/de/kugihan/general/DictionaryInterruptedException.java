package de.kugihan.dictionaryformids.general;

// indicates that during an operation the thread was interrupted
public class DictionaryInterruptedException extends DictionaryException {
	public DictionaryInterruptedException(Throwable t) {
		super(t);
	}
}
