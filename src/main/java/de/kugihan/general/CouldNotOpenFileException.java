package de.kugihan.dictionaryformids.general;

public class CouldNotOpenFileException extends DictionaryException {
	
	public CouldNotOpenFileException() {
		super("File could not be opened");
	}

	public CouldNotOpenFileException(String message) {
		super(message);
	}

	public CouldNotOpenFileException(Throwable t) {
		super(t);
	}


}
