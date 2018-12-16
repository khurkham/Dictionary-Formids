/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.dataaccess.content;

import java.util.Hashtable;

import de.kugihan.dictionaryformids.general.DictionaryException;

public class PredefinedContent {
	public static final FontStyle fontStylePlain = new FontStyle(FontStyle.plain);
	public static final FontStyle fontStyleUnderlined = new FontStyle(FontStyle.underlined);
	public static final FontStyle fontStyleBold = new FontStyle(FontStyle.bold);
	public static final FontStyle fontStyleItalic = new FontStyle(FontStyle.italic);
	public static final FontStyle fontStyleDefault  		= fontStylePlain;

	public static final RGBColour fontColourInputLanguage 	= new RGBColour(0, 0, 80); 
	public static final RGBColour fontColourOutputLanguage 	= new RGBColour(0, 80, 20);
	public static final RGBColour fontColourPronunciation 	= new RGBColour(0, 128, 0);
	public static final RGBColour fontColourDefault 		= new RGBColour(0, 0, 0);
	
	public static SelectionMode selectionModeNone = new SelectionMode(SelectionMode.none);
	public static SelectionMode selectionModeSingle = new SelectionMode(SelectionMode.single);
	public static SelectionMode selectionModeAll = new SelectionMode(SelectionMode.all);
	
	public static final String 	  backgroundColourDefaultName = "backgroundColourDefault";
	public static final RGBColour backgroundColourDefault = new RGBColour(255, 255, 255);
	
	private static ContentDefinition contentNoDefinitionProvided;
	private static ContentDefinition contentInputLanguage;
	private static ContentDefinition contentOutputLanguage;
	private static ContentDefinition contentPronunciation;

	public static final String predefinedContentNamePrefix = "content";
	protected static Hashtable predefinedContentMap = null;
	
	private static void initPredefinedContent() {
		predefinedContentMap = new Hashtable();
		// todo: choose good values for colour/style for each predefined content  
		addPredefinedContent("Definition", 
				             "UIDisplayTextItems.contentDefault",
				             fontColourDefault,
				             fontStyleDefault,
				             selectionModeNone);
		contentInputLanguage =
		addPredefinedContent("InputLanguage", 
				             "UIDisplayTextItems.contentInputLanguage",
				             fontColourInputLanguage,
				             fontStyleBold,
				             selectionModeNone);
		contentOutputLanguage = 
		addPredefinedContent("OutputLanguage", 
				             "UIDisplayTextItems.contentOutputLanguage",
				             fontColourOutputLanguage,
				             fontStyleDefault,
				             selectionModeNone);
		contentNoDefinitionProvided = 
		addPredefinedContent("NoDefinitionProvided", 
				             "UIDisplayTextItems.contentNoDefinitionProvided",
				             fontColourDefault,
				             fontStyleDefault,
				             selectionModeNone);
		contentPronunciation =
		addPredefinedContent("Pronunciation", 
				             "UIDisplayTextItems.contentPronunciation",
				             fontColourPronunciation,
				             fontStyleDefault,
				             selectionModeNone);
		addPredefinedContent("GrammaticalCategory", 
				             "UIDisplayTextItems.contentGrammaticalCategory",
				             new RGBColour(128,0,0),
				             fontStyleDefault,
				             selectionModeNone);
		addPredefinedContent("Notes", 
				             "UIDisplayTextItems.contentExplanation",
				             new RGBColour(128,0,128),
				             fontStyleDefault,
				             selectionModeNone);
		addPredefinedContent("Origin", 
				             "UIDisplayTextItems.contentOrigin",
				             new RGBColour(0,128,128),
				             fontStyleDefault,
				             selectionModeNone);
		addPredefinedContent("SampleUsage", 
				             "UIDisplayTextItems.contentSampleUsage",
				             new RGBColour(0,0,255),
				             fontStyleDefault,
				             selectionModeNone);
		
	}
	
	private static ContentDefinition addPredefinedContent(String 		contentName, 
											 			 String 		contentDisplayText,
														 RGBColour 		fontColour,
														 FontStyle 		fontStyle,
														 SelectionMode  seletionMode) {
		// todo UIDisplayTextItems
		ContentDefinition content = new ContentDefinition(contentDisplayText,
														  fontColour,
														  fontStyle,
														  seletionMode,
														  true);
		predefinedContentMap.put(predefinedContentNamePrefix + contentName, content);
		return content;
	}

	public static void checkInitialization() {
		if (predefinedContentMap == null)
			initPredefinedContent();
	}
	
	public static ContentDefinition getPredefinedContent(String contentName) 
				throws DictionaryException {
		checkInitialization();
		ContentDefinition content = (ContentDefinition) predefinedContentMap.get(contentName);
		if (content == null)
			throw new DictionaryException("Predefined content not found: " + contentName);
		return content;
	}

	public static ContentDefinition getContentNoDefinitionProvided() {
		checkInitialization();
		return contentNoDefinitionProvided;
	}

	public static ContentDefinition getContentInputLanguage() {
		checkInitialization();
		return contentInputLanguage;
	}

	public static ContentDefinition getContentOutputLanguage() {
		checkInitialization();
		return contentOutputLanguage;
	}

	public static ContentDefinition getContentPronunciation() {
		checkInitialization();
		return contentPronunciation;
	}
}
