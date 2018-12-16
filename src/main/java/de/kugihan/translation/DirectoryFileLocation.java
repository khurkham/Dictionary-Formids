package de.kugihan.dictionaryformids.translation;

public class DirectoryFileLocation {

	public int    directoryFileNumber;
	public String postfixDictionaryFile;
	public int    positionInDirectoryFile;
	
	DirectoryFileLocation(int    directoryFileNumberParam,
						  String postfixDictionaryFileParam,
						  int    positionInDirectoryFileParam) {
		directoryFileNumber = directoryFileNumberParam; 
		postfixDictionaryFile = postfixDictionaryFileParam;
		positionInDirectoryFile = positionInDirectoryFileParam;
	}
	
	int compareTo(DirectoryFileLocation otherDirectoryFileLocation) {
		int compareResult;
		int postfixDictionaryFileCompared = postfixDictionaryFile.compareTo(otherDirectoryFileLocation.postfixDictionaryFile);
		if (postfixDictionaryFileCompared == 0) {
			int directoryFileNumberCompared = directoryFileNumber - otherDirectoryFileLocation.directoryFileNumber;
			if (directoryFileNumberCompared == 0) {
				int positionInDirectoryFileCompared = positionInDirectoryFile - otherDirectoryFileLocation.positionInDirectoryFile;
				compareResult = positionInDirectoryFileCompared;
			}
			else {
				compareResult = directoryFileNumberCompared;
			}
		}
		else {
			compareResult = postfixDictionaryFileCompared;
		}
		return compareResult;
	}
}
