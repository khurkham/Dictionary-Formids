/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 Gert Nuber (dict@kugihan.de) and  2005 Vu Van Quynh 
(quynh2003hp@yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/
package de.kugihan.dictionaryformids.hmi_common.content;

import java.util.Stack;

import de.kugihan.dictionaryformids.dataaccess.content.ContentDefinition;
import de.kugihan.dictionaryformids.dataaccess.content.ContentLib;
import de.kugihan.dictionaryformids.dataaccess.content.PredefinedContent;
import de.kugihan.dictionaryformids.general.DictionaryException;
import de.kugihan.dictionaryformids.translation.TextOfLanguage;

public class ContentParser {

	StringColourItemText stringColourItemText;
	Stack  		 contentHierarchy = new Stack();
	StringBuffer text = new StringBuffer();
	
	public StringColourItemText determineItemsFromContent(TextOfLanguage 		contentText, 
			                                              boolean 				changeInputAndOutputContent,
			                                              boolean 				isInput) 
				throws DictionaryException {

		ContentDefinition[] contents =                   contentText.getDictionary().supportedLanguages[contentText.getLanguageIndex()].contents;
		boolean             contentDefinitionAvailable = contentText.getDictionary().supportedLanguages[contentText.getLanguageIndex()].contentDefinitionAvailable;

		String contentString = contentText.getText();
		stringColourItemText = new StringColourItemText();
		int currentContentNumber = 0;
		// default content is the outmost level:
		pushNewContent(contents[0], changeInputAndOutputContent, isInput);
		
		int contentStringLength = contentString.length();
		int charCount = 0;
		while (charCount < contentStringLength) {
			char contentChar = contentString.charAt(charCount);
			if (contentDefinitionAvailable) {
				if (contentChar == ContentLib.startOfContentChar) {
					// the next two characters give the content number
					if (charCount + 2 >= contentStringLength) 
						throwContentFormatException("Start of content ("+ContentLib.startOfContentChar+ ") without complete content number");
					int contentNumber = getStartContentDigitValue(contentString.charAt(charCount+1))*10 +
										getStartContentDigitValue(contentString.charAt(charCount+2));
					currentContentNumber = contentNumber;
					charCount += 2;
					addStringColourItemTextPart();
					if ((contentNumber < 1) || (contentNumber >= contents.length)) {
						throwContentFormatException("Incorrect content number: " + contentNumber);
					}
					pushNewContent(contents[contentNumber], changeInputAndOutputContent, isInput);
				}
				else if (contentChar == ContentLib.endOfContentChar) {
					addStringColourItemTextPart();
					popContent();
				}
				else if (contentChar == ContentLib.preservePrependChar) {
					if (charCount + 1 < contentStringLength) {
						char nextChar = contentString.charAt(charCount+1); 
						if ((nextChar == ContentLib.startOfContentChar) || 
							(nextChar == ContentLib.endOfContentChar)) {
							// this is an startOfContentChar or startOfContentChar that was escaped by \ 
							// \[ or \]
							++charCount;
							contentChar = nextChar;
						}
						addText(contentChar);
					}
				}
				else {
//					if (DictionarySettings.getContentIsShown(languageIndex, currentContentNumber - 1)){
						addText(contentChar);
//					}
				}
			}			
			else {
				addText(contentChar);
			}
			++charCount;
		}
		addStringColourItemTextPart();
		return stringColourItemText;
	}
	
	void pushNewContent(ContentDefinition newContent,
                        boolean changeInputAndOutputContent,
                        boolean isInput) throws DictionaryException {
		if (changeInputAndOutputContent) {
			// if newContent is the contentNoDefinitionProvided, then replace by contentInput/OutputLanguage if applicable
			if (newContent == PredefinedContent.getContentNoDefinitionProvided()) {
				if (isInput)
					newContent = PredefinedContent.getContentInputLanguage();
				else // output
					newContent = PredefinedContent.getContentOutputLanguage();
	        }
		}
		contentHierarchy.push(newContent);
		text.setLength(0);
	}
	
	void popContent() 
			throws DictionaryException {
		contentHierarchy.pop();
		if (contentHierarchy.isEmpty())
			throwContentFormatException("End of content without start of content");
	}

	void addText(char contentChar) {
		text.append(contentChar);		
	}
 
	void addStringColourItemTextPart() {
		if (text.length() > 0) {
			ContentDefinition topContent = getTopContent();
			StringColourItemTextPart itemTextPart = getItemTextPartFromContent(text.toString(), topContent);
			stringColourItemText.addItemTextPart(itemTextPart);
		}
		text.setLength(0);
	}
	
	ContentDefinition getTopContent() {
		return (ContentDefinition) contentHierarchy.peek();
	}
	
	StringColourItemTextPart getItemTextPartFromContent(String itemTextPartString, 
			                                            ContentDefinition content) {
		StringColourItemTextPart itemTextPart = 
					new StringColourItemTextPart(itemTextPartString,
				    							 content.fontColour,
				    							 content.fontStyle,
				    							 content.selectionMode);
		return itemTextPart;
	}
	
	int getStartContentDigitValue(char charParam) 
					throws DictionaryException {
		if (! Character.isDigit(charParam))
			throwContentFormatException("Start of content ("+ContentLib.startOfContentChar+ ") is not followed by 2 digits");
		return Character.digit(charParam, 10);
	}
	
	void throwContentFormatException(String message) 
			throws DictionaryException {
		throw new DictionaryException("Error in content format: " + message);
	}
	
	public StringBuffer getTextFromStringColourItemText(StringColourItemText stringColourItemText) {			
		// collect all texts from stringColourItemText into one string
		StringBuffer itemText = new StringBuffer();
		for (int itemTextPartCount = 0; itemTextPartCount < stringColourItemText.size(); ++ itemTextPartCount) {
			itemText.append(stringColourItemText.getItemTextPart(itemTextPartCount).getText());
		}
		return itemText;
	}

}
