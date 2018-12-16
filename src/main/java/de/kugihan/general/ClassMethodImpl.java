/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2011 Gert Nuber (dict@kugihan.de) 

GPL applies - see file COPYING for copyright statement.

See comment in class ClassMethodBase.
This is the Java implementation for the class ClassMethodImpl. 

*/

package de.kugihan.dictionaryformids.general;

import java.io.InputStream;

public class ClassMethodImpl extends de.kugihan.dictionaryformids.general.ClassMethodBase {

	// this is the Java implementation of createObjectForClassDynamic.
	public Object createObjectForClassDynamic(String className) {
        Class classToLoad;
        Object classObj;
        try
        {
            classToLoad = Class.forName(className);
            classObj = classToLoad.newInstance();
        }
        catch (Exception e)
        {
        	classObj = null;
        }
        return classObj;
	}

	// this is the Java implementation of getResourceAsStream.
	public InputStream getResourceAsStream(String name) {
		return getClass().getResourceAsStream(name);
	}
}
