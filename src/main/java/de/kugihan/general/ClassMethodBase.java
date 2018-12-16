/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2011 Gert Nuber (dict@kugihan.de) 

GPL applies - see file COPYING for copyright statement.


This class is the base class for features that are not provided by class Class in GWT (Google Web Toolkit).
The class ClassMethodImpl implements these features. There are two implementations for ClassMethodImpl: 
One for Java and one for Javascript. The one for Java provides the features with normal Java means. 
The one for Javascript is empty (cause those features are not needed in Javascript).
This 'trick' is used to allow compilation with GWT. Otherwise, GWT would stop compilation when it encounters
the non-supported Class methods.

*/

package de.kugihan.dictionaryformids.general;

import de.kugihan.dictionaryformids.translation.normation.*;

import java.io.InputStream;
import java.util.Hashtable;

public class ClassMethodBase {

	final String normationPackageString = "de.kugihan.dictionaryformids.translation.normation.";	

	protected Hashtable  normationClassTable = null;
	
	protected void initValues() {
		normationClassTable = new Hashtable();
		normationClassTable.put("Normation", 		new Normation()		);
		normationClassTable.put("NormationBul", 	new NormationBul() 	);
		normationClassTable.put("NormationCyr1", 	new NormationCyr1() );
		normationClassTable.put("NormationCyr2", 	new NormationCyr2() );
		normationClassTable.put("NormationEng", 	new NormationEng() 	);
		normationClassTable.put("NormationEng2", 	new NormationEng2() );
		normationClassTable.put("NormationEpo", 	new NormationEpo() 	);
		normationClassTable.put("NormationFil", 	new NormationFil() 	);
		normationClassTable.put("NormationGer", 	new NormationGer() 	);
		normationClassTable.put("NormationJpn", 	new NormationJpn() 	);
		normationClassTable.put("NormationLat", 	new NormationLat() 	);
		normationClassTable.put("NormationNor", 	new NormationNor() 	);
		normationClassTable.put("NormationRus", 	new NormationRus() 	);
		normationClassTable.put("NormationRus2", 	new NormationRus2() );
		normationClassTable.put("NormationRusC", 	new NormationRusC() );
		normationClassTable.put("NormationUkr", 	new NormationUkr() 	);
		normationClassTable.put("NormationUkrC", 	new NormationUkrC() );
		normationClassTable.put("NormationVie", 	new NormationVie() 	);
		normationClassTable.put("NormationTam", 	new NormationTam() 	);
	}


	/*
	* Creates an object for a class with a given className. 
	* It tries to find the classname in the Normation classes.
	* If not found there, then the object is loaded with createObjectForClassDynamic.
	*/ 
	public Object createObjectForClass(String className) {
		Object classObj = null;
		if (normationClassTable == null) {
			initValues();
		}
		// try to create object from normationClassTable
		if (className.indexOf(normationPackageString) == 0) {
			String classNameWithoutPackagename = className.substring(normationPackageString.length());
			classObj = normationClassTable.get(classNameWithoutPackagename);
		}
		// if class was not found in normationClassTable then load dynamically 
		if (classObj == null) {
			classObj = createObjectForClassDynamic(className);
		}
		return classObj;
	}
	
	protected Object createObjectForClassDynamic(String className) {
		// Default is to do nothing (= Javascript behaviour).
		// For Java this method is implemented in class ClassMethodImpl.
		return null;
	}
	
	public InputStream getResourceAsStream(String name) {
		// Default is to do nothing (= Javascript behaviour).
		// For Java this method is implemented in class ClassMethodImpl.
		return null;
	}

}
