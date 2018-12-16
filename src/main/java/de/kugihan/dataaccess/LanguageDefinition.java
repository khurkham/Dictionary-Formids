/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.dataaccess;
import de.kugihan.dictionaryformids.dataaccess.content.ContentDefinition;
import de.kugihan.dictionaryformids.translation.normation.Normation;

public class LanguageDefinition {
	public String    		  languageDisplayText;
	public String    		  languageFilePostfix;
	public String    		  normationClassName;
	public boolean			  isSearchable; // true if searches are allowed for this language
	public boolean			  hasSeparateDictionaryFile; // true if translations are stored in a language-specific dictionary file
	public String    		  dictionaryUpdateClassName;  // used only by DictionaryGeneration
	public Normation 		  normationObj;
    public int                indexNumberOfSourceEntries; // number of entries in the dictionay for that language 
	public boolean			  contentDefinitionAvailable; // true if ContentDefinitions are available in the properties file
	public ContentDefinition  contents[];
	// the following properties are used only by DictionaryGeneration: 
	public DictionaryUpdateIF dictionaryUpdateObj;  // interface to DictionaryUpdate object
	public boolean 			  generateIndex;        // true if index shall be generated for this language
	public String			  expressionSplitString;  // separator string for multiple expressions, e.g. "," for "to choose, to select"
	// the following properties are used only by the HMI layer
	public String			  languageIcon;  // identifies the icon that the HMI layer should display for this language
	
	public LanguageDefinition(String  languageDisplayTextParam,
			 		          String  languageFilePostfixParam,
			 		          boolean isSearchableParam,
			 		          boolean hasSeparateDictionaryFileParam,
					          String  normationClassNameParam,
					          int     indexNumberOfSourceEntriesParam,
					          boolean contentDefinitionAvailableParam,
					          ContentDefinition contentsParam[],
					          String  dictionaryUpdateClassNameParam,
					          boolean generateIndexParam,
					          String  expressionSplitStringParam,
					          String  languageIconParam) {
		languageDisplayText = languageDisplayTextParam;
		languageFilePostfix = languageFilePostfixParam;
		isSearchable = isSearchableParam;
		hasSeparateDictionaryFile = hasSeparateDictionaryFileParam;
		normationClassName = normationClassNameParam;
		contentDefinitionAvailable = contentDefinitionAvailableParam;
		contents = contentsParam;
		dictionaryUpdateClassName = dictionaryUpdateClassNameParam;
		generateIndex = generateIndexParam;
		expressionSplitString = expressionSplitStringParam;
		indexNumberOfSourceEntries = indexNumberOfSourceEntriesParam;
		languageIcon = languageIconParam;
	}
}