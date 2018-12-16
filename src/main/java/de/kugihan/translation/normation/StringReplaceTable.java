/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2013 Gert Nuber (dict@kugihan.de)
Coded by Gert Nuber and support provided by V.P.Guru

GPL applies - see file COPYING for copyright statement.
*/

package de.kugihan.dictionaryformids.translation.normation;
import de.kugihan.dictionaryformids.general.Util;
import java.util.Vector;

public class StringReplaceTable {
  protected Vector stringReplaceVector = new Vector();

  public void addTableEntry(String replaceFrom, String replaceTo) {
     stringReplaceVector.addElement(new StringReplaceTableEntry(replaceFrom, replaceTo));
  }

  public String replaceWithTable(String toBeReplacedString) {
     String replacedString = toBeReplacedString;
     for (int entryCount = 0; entryCount < stringReplaceVector.size(); ++entryCount) {
         StringReplaceTableEntry stringReplaceTableEntryObj = (StringReplaceTableEntry) stringReplaceVector.elementAt(entryCount);
         replacedString = Util.stringReplace(replacedString, stringReplaceTableEntryObj.replaceFrom, stringReplaceTableEntryObj.replaceTo);
     }
     return replacedString;
  }

private class StringReplaceTableEntry {
  public String replaceFrom;
  public String replaceTo;

  StringReplaceTableEntry(String replaceFromParam,
                          String replaceToParam) {
     replaceFrom = replaceFromParam;
     replaceTo   = replaceToParam;
	}
  }
}