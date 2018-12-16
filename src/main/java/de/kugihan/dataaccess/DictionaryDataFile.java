/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005 Gert Nuber (dict@kugihan.de) and  2005 Vu Van Quynh
(quynh2003hp@yahoo.com)

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.dataaccess;

import de.kugihan.dictionaryformids.dataaccess.content.*;
import de.kugihan.dictionaryformids.dataaccess.fileaccess.DfMInputStreamAccess;
import de.kugihan.dictionaryformids.general.ClassMethodBase;
import de.kugihan.dictionaryformids.general.ClassMethodImpl;
import de.kugihan.dictionaryformids.general.DictionaryClassNotLoadedException;
import de.kugihan.dictionaryformids.general.DictionaryException;
import de.kugihan.dictionaryformids.general.Util;
import de.kugihan.dictionaryformids.translation.normation.Normation;

public class DictionaryDataFile  {

        /*
         * describes the structure of the dictionary data files
         */

        public int numberOfAvailableLanguages;
        public int numberOfInputLanguages;

        public LanguageDefinition [] supportedLanguages;

        public 		  String infoText;
        public 		  String dictionaryAbbreviation;

        public final  String pathNameFonts = "fonts";

        public final  String prefixSearchListFile = "searchlist";
        public final  String suffixSearchListFile = ".csv";
        public 		  String searchListCharEncoding;
        public 		  char   searchListFileSeparationCharacter;
        public 		  int    searchListFileMaxSize;
        public final  String prefixIndexFile = "index";
        public final  String suffixIndexFile = ".csv";
        public 		  String indexCharEncoding;
        public 		  char   indexFileSeparationCharacter;
        public 		  char   indexFileSeparatorFileNumberToPosition = '-';
        public 		  char   indexFileSeparatorFilePositionToSearchIndicator = '-';
        public 		  char   indexFileSeparatorIndexEntries = ',';
        public 		  int    indexFileMaxSize;
        public final  String prefixDictionaryFile = "directory";
        public final  String suffixDictionaryFile = ".csv";
        public 		  String dictionaryCharEncoding;
        public 		  char   dictionaryFileSeparationCharacter;
        public 		  int    dictionaryFileMaxSize;
        public static String applicationFileNamePrefix = "DictionaryForMIDs";
        public static String propertyFileName = applicationFileNamePrefix + ".properties";
        public 		  char   contentFontColourSeparationCharacter = ',';

        private 	  RGBColour backgroundColour;  // background colour for the display
        private 	  boolean   useBackgroundColour;

        // properties used by DictionaryGeneration only
        public        String dictionaryGenerationInputCharEncoding;
        public        char   dictionaryGenerationSeparatorCharacter;
        public        long   dictionaryGenerationMinNumberOfEntriesPerDictionaryFile;
        public        long   dictionaryGenerationMinNumberOfEntriesPerIndexFile;
        public        boolean dictionaryGenerationOmitParFromIndex;
		public        int	 dictionaryGenerationMaxIndexKeyEntriesPerExpressionWarnLimit;

        public        String fileEncodingFormat;

        public static final  String pathNameDataFiles = "dictionary";
        public static boolean useStandardPath = true;  // indicates that the property file is located in pathNameDataFiles; set to false by dictionary generation tools

        protected     ClassMethodBase classMethodObj = new ClassMethodImpl();  // used for accessing features of class Class that are not available in GWT
        
        protected	  DfMInputStreamAccess dictionaryDataFileISAccess;  // used for accessing the files of the dictionary


        // constructor for setting up a dictionary with data from DictionaryForMIDs.properties:
        public  DictionaryDataFile(DfMInputStreamAccess dictionaryDataFileISAccessParam,
        		                   boolean initDictionaryGenerationValues)
                                throws DictionaryException
        {
        		dictionaryDataFileISAccess = dictionaryDataFileISAccessParam;
                /*
                 * values from resource file
                 * */
                Util utilObj = Util.getUtil();
                utilObj.openProperties(dictionaryDataFileISAccess, getPathDataFiles());  // property file is never closed, because properties may be read at any time
                infoText = utilObj.getDictionaryPropertyString("infoText");
                dictionaryAbbreviation = utilObj.getDictionaryPropertyString("dictionaryAbbreviation", true);
                numberOfAvailableLanguages = utilObj.getDictionaryPropertyInt("numberOfAvailableLanguages");
                numberOfInputLanguages = 0; // initialised with 0
                supportedLanguages = new LanguageDefinition[numberOfAvailableLanguages];
                for (int indexLanguage = 0;
                 indexLanguage < numberOfAvailableLanguages;
                 ++indexLanguage) {
                        String indexLanguageString = String.valueOf(indexLanguage + 1);
                        String languagePropertyPrefix = "language" + indexLanguageString;
                        String languageDisplayText = utilObj.getDictionaryPropertyString(languagePropertyPrefix + "DisplayText");
                        String languageFilePostfix = utilObj.getDictionaryPropertyString(languagePropertyPrefix + "FilePostfix");
                        boolean isSearchable = utilObj.getDictionaryPropertyBooleanDefault(languagePropertyPrefix + "IsSearchable", true);
                        if (isSearchable)
                                ++numberOfInputLanguages;
                        boolean hasSeparateDictionaryFile = utilObj.getDictionaryPropertyBooleanDefault(languagePropertyPrefix + "HasSeparateDictionaryFile", false);
                        boolean generateIndex = utilObj.getDictionaryPropertyBooleanDefault(languagePropertyPrefix + "GenerateIndex", true);
                        String normationClassName = utilObj.getDictionaryPropertyString(languagePropertyPrefix + "NormationClassName", true);
                        String dictionaryUpdateClassName = utilObj.getDictionaryPropertyString(languagePropertyPrefix + "DictionaryUpdateClassName", true);
                        int indexNumberOfSourceEntries = utilObj.getDictionaryPropertyIntDefault(languagePropertyPrefix + "IndexNumberOfSourceEntries", -1);
                        String expressionSplitString = null;
                        if (initDictionaryGenerationValues) {
                                expressionSplitString = utilObj.getDictionaryPropertyString("dictionaryGenerationLanguage" + indexLanguageString + "ExpressionSplitString", true);
                        }
                        String languageIcon = null; //utilObj.getDictionaryPropertyStringDefault(languagePropertyPrefix + "Icon", "");

                        /*
                         * Content definitions for this language
                         */
                        boolean contentDefinitionAvailable = false;
                        ContentDefinition content[] = null;
                        int numberOfContentDeclarations = 1; // there is at least the default content definiton at array index 0
                        String numberOfContentDeclarationsString = utilObj.getDictionaryPropertyString(languagePropertyPrefix + "NumberOfContentDeclarations", true);
                        if (numberOfContentDeclarationsString != null) {
                                numberOfContentDeclarations = Integer.valueOf(numberOfContentDeclarationsString).intValue() + 1;
                                contentDefinitionAvailable = true;
                        }
                        content = new ContentDefinition[numberOfContentDeclarations];
                        // default content is at array index 0
                        content[0] = PredefinedContent.getContentNoDefinitionProvided();
                        // get the declarations for the remaining contents from the property file:
                        for (int indexContent = 0;
                             indexContent < numberOfContentDeclarations;
                             ++indexContent) {
                                String indexContentString = String.valueOf(indexContent);
                                if (indexContentString.length() == 1)
                                        indexContentString = '0' + indexContentString; // always use 2 digits
                                else if (indexContentString.length() > 2)
                                        throw new DictionaryException("Number of contents too big");
                                String contentPropertyPrefix = languagePropertyPrefix + "Content" + indexContentString;

                                ContentDefinition contentFromPropertyFile;
                                // contentDisplayText
                                String contentDisplayTextProperty = contentPropertyPrefix + "DisplayText";
                                String contentDisplayText = utilObj.getDictionaryPropertyString(contentDisplayTextProperty, true);
                                if (contentDisplayText == null) {
                                        if (indexContent == 0) {
                                                // ok for content 00 (= default content): this is optional
                                                continue;
                                        }
                                        else {
                                                utilObj.propertyNotFound(contentDisplayTextProperty);
                                        }
                                }
                                if (contentDisplayText.startsWith(PredefinedContent.predefinedContentNamePrefix)) {
                                        // content is a predefined content
                                        contentFromPropertyFile = PredefinedContent.getPredefinedContent(contentDisplayText);
                                }
                                else {
                                        contentFromPropertyFile = new ContentDefinition(contentDisplayText);
                                }
                                // read the other content properties from the property file:
                                // fontColour
                                String fontColourString = utilObj.getDictionaryPropertyString(contentPropertyPrefix + "FontColour", true);
                                RGBColour fontColour;
                                if (fontColourString != null) {
                                        fontColour = determineRGBColourFromProperty(fontColourString, contentPropertyPrefix);
                                        contentFromPropertyFile.setFontColour(fontColour);
                                }

                                // fontStyle
                                String fontStyleString = utilObj.getDictionaryPropertyString(contentPropertyPrefix + "FontStyle", true);
                                if (fontStyleString != null) {
                                        FontStyle fontStyle = null;
                                        if      (Util.stringEqualIgnoreCase(fontStyleString, FontStyle.plainString)) fontStyle = PredefinedContent.fontStylePlain;
                                        else if (Util.stringEqualIgnoreCase(fontStyleString, FontStyle.underlinedString)) fontStyle = PredefinedContent.fontStyleUnderlined;
                                        else if (Util.stringEqualIgnoreCase(fontStyleString, FontStyle.boldString)) fontStyle = PredefinedContent.fontStyleBold;
                                        else if (Util.stringEqualIgnoreCase(fontStyleString, FontStyle.italicString)) fontStyle = PredefinedContent.fontStyleItalic;
                                        else throwContentException("Incorrect font style", contentPropertyPrefix);
                                        contentFromPropertyFile.setFontStyle(fontStyle);
                                }

                                // displaySelectable
                                String displaySelectablePropertyName = new String(contentPropertyPrefix + "DisplaySelectable");
                                String displaySelectableString = utilObj.getDictionaryPropertyString(displaySelectablePropertyName, true);
                                if (displaySelectableString != null) {
                                        boolean displaySelectable = utilObj.getBooleanFromProperty(displaySelectablePropertyName, displaySelectableString);
                                        contentFromPropertyFile.setDisplaySelectable(displaySelectable);
                                }

                                // selectionMode
                                String selectionModeString = utilObj.getDictionaryPropertyString(contentPropertyPrefix + "SelectionMode", true);
                                if (selectionModeString != null) {
                                        SelectionMode selectionMode = null;
                                        if (Util.stringEqualIgnoreCase(selectionModeString, SelectionMode.noneString)) selectionMode = PredefinedContent.selectionModeNone;
                                        else if (Util.stringEqualIgnoreCase(selectionModeString, SelectionMode.singleString)) selectionMode = PredefinedContent.selectionModeSingle;
                                        else if (Util.stringEqualIgnoreCase(selectionModeString, SelectionMode.allString)) selectionMode = PredefinedContent.selectionModeAll;
                                        else throwContentException("Incorrect selection mode", contentPropertyPrefix);
                                        contentFromPropertyFile.setSelectionMode(selectionMode);
                                }

                                content[indexContent] = contentFromPropertyFile;
                        }
                        supportedLanguages[indexLanguage] = new LanguageDefinition(languageDisplayText,
                                                                                           languageFilePostfix,
                                                                                           isSearchable,
                                                                                           hasSeparateDictionaryFile,
                                                                                           normationClassName,
                                                                                           indexNumberOfSourceEntries,
                                                                                           contentDefinitionAvailable,
                                                                                           content,
                                                                                           dictionaryUpdateClassName,
                                                                                           generateIndex,
                                                                                           expressionSplitString,
                                                                                           languageIcon);
                }
                searchListCharEncoding = utilObj.getDictionaryPropertyString("searchListCharEncoding");
                searchListCharEncoding = utilObj.getDeviceCharEncoding(searchListCharEncoding);
                searchListFileSeparationCharacter = utilObj.getDictionaryPropertyChar("searchListFileSeparationCharacter");
                searchListFileMaxSize = utilObj.getDictionaryPropertyIntDefault("searchListFileMaxSize", 10000);
                indexCharEncoding = utilObj.getDictionaryPropertyString("indexCharEncoding");
                indexCharEncoding = utilObj.getDeviceCharEncoding(indexCharEncoding);
                indexFileSeparationCharacter = utilObj.getDictionaryPropertyChar("indexFileSeparationCharacter");
                indexFileMaxSize = utilObj.getDictionaryPropertyIntDefault("indexFileMaxSize", 10000);
                dictionaryCharEncoding = utilObj.getDictionaryPropertyString("dictionaryCharEncoding");
                dictionaryCharEncoding = utilObj.getDeviceCharEncoding(dictionaryCharEncoding);
                dictionaryFileSeparationCharacter = utilObj.getDictionaryPropertyChar("dictionaryFileSeparationCharacter");
                dictionaryFileMaxSize = utilObj.getDictionaryPropertyIntDefault("dictionaryFileMaxSize", 10000);
                String backgroundColourProperty = "backgroundColour";
                String backgroundColourString = utilObj.getDictionaryPropertyString(backgroundColourProperty, true);
                if (backgroundColourString == null) {
                        setUseBackgroundColour(false);
                        setBackgroundColour(null);
                }
                else {
                        setUseBackgroundColour(true);
                        if (Util.stringEqualIgnoreCase(backgroundColourString, PredefinedContent.backgroundColourDefaultName)) {
                                setBackgroundColour(PredefinedContent.backgroundColourDefault);
                        }
                        else {
                                setBackgroundColour(determineRGBColourFromProperty(backgroundColourString, backgroundColourProperty));
                        }
                }

                if (initDictionaryGenerationValues) {
                        dictionaryGenerationInputCharEncoding = utilObj.getDictionaryPropertyStringDefault("dictionaryGenerationInputCharEncoding",  "ISO-8859-1");
                        dictionaryGenerationSeparatorCharacter = utilObj.getDictionaryPropertyCharDefault("dictionaryGenerationSeparatorCharacter", '\t');
                        dictionaryGenerationMinNumberOfEntriesPerDictionaryFile = utilObj.getDictionaryPropertyIntDefault("dictionaryGenerationMinNumberOfEntriesPerDictionaryFile", 200);
                        dictionaryGenerationMinNumberOfEntriesPerIndexFile = utilObj.getDictionaryPropertyIntDefault("dictionaryGenerationMinNumberOfEntriesPerIndexFile", 500);
                        dictionaryGenerationOmitParFromIndex = utilObj.getDictionaryPropertyBooleanDefault("dictionaryGenerationOmitParFromIndex", true);
                        dictionaryGenerationMaxIndexKeyEntriesPerExpressionWarnLimit = utilObj.getDictionaryPropertyIntDefault("dictionaryGenerationMaxIndexKeyEntriesPerExpressionWarnLimit", 10);
                }

                fileEncodingFormat = utilObj.getDictionaryPropertyString("fileEncodingFormat", true);
                if (fileEncodingFormat == null) {
                        fileEncodingFormat = new String("plain_format1");
                }

                /*
                 * load normation classes and create normation objects; same for dictionaryUpdate classes/objects
                 */
                for (int indexLanguage = 0;
                 indexLanguage < numberOfAvailableLanguages;
                         ++indexLanguage) {

                        String normationClassName = supportedLanguages[indexLanguage].normationClassName;
                        Normation normationObj =
                                        (Normation) getObjectForClass(normationClassName,
                                                                              Normation.class.getName(),
                                                                              "de.kugihan.dictionaryformids.translation.normation",
                                                                              "de.kugihan.dictionaryformids.translation");
                        normationObj.dictionaryDataFileISAccess = dictionaryDataFileISAccess;
                        supportedLanguages[indexLanguage].normationObj = normationObj;

                        if (initDictionaryGenerationValues) {
                                String dictionaryUpdateClassName = supportedLanguages[indexLanguage].dictionaryUpdateClassName;
                                DictionaryUpdateIF dictionaryUpdateObj =
                                                (DictionaryUpdateIF) getObjectForClass
                                                                         (dictionaryUpdateClassName,
                                                                                                  "de.kugihan.dictionaryformids.dictgen.dictionaryupdate.DictionaryUpdate",
                                                                                              "de.kugihan.dictionaryformids.dictgen.dictionaryupdate",
                                                                                                  "de.kugihan.dictionaryformids.dictgen");
                                dictionaryUpdateObj.setDictionary(this);
                                supportedLanguages[indexLanguage].dictionaryUpdateObj = dictionaryUpdateObj;
                        }
                }
        }

        protected Object getObjectForClass(String className,
                                           String fallbackClassName,
                                           String newPackageName,  // in support of pre 3.0 Jar-files for the PC-Version
                                           String oldPackageName)  // in support of pre 3.0 Jar-files for the PC-Version
                                throws DictionaryException {
                if (className == null) {
                        // use fallbackClassName instead
                        className = fallbackClassName;
                }
                Object classObj;
                classObj = classMethodObj.createObjectForClass(className);
                if (classObj == null) {
                        // try old package name
                        StringBuffer classNameNewPackage = new StringBuffer(className);
                        // delete old package name:
                        classNameNewPackage.delete(0, oldPackageName.length());
                        // prepend new package name:
                        String classNameNewPackageStr = newPackageName + classNameNewPackage.toString();
                        classObj = classMethodObj.createObjectForClass(classNameNewPackageStr);
                        if (classObj == null) {
                                // did not work neither:
                                throw new DictionaryClassNotLoadedException("Class could not be loaded: " + className);
                        }
                }
                return classObj;
        }


        public int determineColourComponent(String rbgString, String propertyName)
                                        throws DictionaryException {
                String rbgStringTrimmed = rbgString.trim();
                int rgbValue = 0;
                try {
                        rgbValue = Integer.parseInt(rbgStringTrimmed);
                }
                catch (NumberFormatException e) {
                        throwContentException("RGB value is incorrect: " + e.getMessage(), propertyName);
                }
                if (rgbValue > RGBColour.maxRBGColourValue)
                        throwContentException("RGB value is bigger than " + RGBColour.maxRBGColourValue, propertyName);
                return rgbValue;
        }

        protected RGBColour determineRGBColourFromProperty(String fontColourString, String propertyName)
                                throws DictionaryException {
                String[] fontColourStringElements = Util.stringSplit(fontColourString, contentFontColourSeparationCharacter);
                if (fontColourStringElements.length != 3) {
                        throwContentException("3 components reqired for font colour (red, green, blue)", propertyName);
                }
                int red = determineColourComponent(fontColourStringElements[0], propertyName);
                int green = determineColourComponent(fontColourStringElements[1], propertyName);
                int blue = determineColourComponent(fontColourStringElements[2], propertyName);
                return new RGBColour(red, green, blue);
        }

        /*
         * Access to display text properties
         */
        public String getDisplayText(String propertyName)
                        throws DictionaryException {
                Util utilObj = Util.getUtil();
                String displayText;
                String displayTextString = utilObj.getDictionaryPropertyString(propertyName, true);
                if (displayTextString != null) {
                        // displayTextString is of the following format: "standard translation";language"language translation";...
                        if ((displayTextString.charAt(0) != '\"') || (displayTextString.charAt(displayTextString.length()-1)) != '\"') {
                                throwContentException("String must start with \" and end with \"", propertyName);
                        }
                        displayText = displayTextString.substring(1, displayTextString.length()-1);
                        if (displayText.indexOf('\"') != -1) {
                                throwContentException("String must not contain a \"-character", propertyName);
                        }
                        // translations are not yet handled: needs to be implemented
                }
                else {
                        displayText = null;
                }
                return displayText;
        }


        protected void throwContentException(String message, String propertyName)
                                        throws DictionaryException {
                throw new DictionaryException(propertyName + ": " + message);
        }

        public static String getPathDataFiles()
        {
                if (useStandardPath)
                        return "/" + pathNameDataFiles + "/";
                else
                        return "";
        }

        public static String getDfMPropertyFileLocation(String dfmBaseDirectory) {
                return         dfmBaseDirectory.toString() +
                                getPathDataFiles() +
                                propertyFileName;
        }

        public RGBColour getBackgroundColour() {
                return backgroundColour;
        }

        public void setBackgroundColour(RGBColour backgroundColourParam) {
               backgroundColour = backgroundColourParam;
        }

        public boolean isUseBackgroundColour() {
                return useBackgroundColour;
        }

        public void setUseBackgroundColour(boolean useBackgroundColourParam) {
                useBackgroundColour = useBackgroundColourParam;
        }

        public DfMInputStreamAccess getDictionaryDataFileISAccess() {
    		return dictionaryDataFileISAccess;
    	}

}
