/*
DictionaryForMIDs - a free multi-language dictionary for mobile devices.
Copyright (C) 2005, 2006 Gert Nuber (dict@kugihan.de)

GPL applies - see file COPYING for copyright statement.

Adapted from GNU Classpath Properties.java
*/


/* Properties.java -- a set of persistent properties
   Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005  Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package de.kugihan.dictionaryformids.general;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * A set of persistent properties, which can be saved or loaded from a stream.
 * A property list may also contain defaults, searched if the main list
 * does not contain a property for a given key.
 *
 * An example of a properties file for the german language is given
 * here.  This extends the example given in ListResourceBundle.
 * Create a file MyResource_de.properties with the following contents
 * and put it in the CLASSPATH.  (The character
 * <code>\</code><code>u00e4</code> is the german umlaut)
 *
 * 
<pre>s1=3
s2=MeineDisk
s3=3. M\<code></code>u00e4rz 96
s4=Die Diskette ''{1}'' enth\<code></code>u00e4lt {0} in {2}.
s5=0
s6=keine Dateien
s7=1
s8=eine Datei
s9=2
s10={0,number} Dateien
s11=Das Formatieren schlug fehl mit folgender Exception: {0}
s12=FEHLER
s13=Ergebnis
s14=Dialog
s15=Auswahlkriterium
s16=1,3</pre>
 *
 * <p>Although this is a sub class of a hash table, you should never
 * insert anything other than strings to this property, or several
 * methods, that need string keys and values, will fail.  To ensure
 * this, you should use the <code>get/setProperty</code> method instead
 * of <code>get/put</code>.
 *
 * Properties are saved in ISO 8859-1 encoding, using Unicode escapes with
 * a single <code>u</code> for any character which cannot be represented.
 *
 * @author Jochen Hoenicke
 * @author Eric Blake (ebb9@email.byu.edu)
 * @see PropertyResourceBundle
 * @status updated to 1.4
 */
public class Properties extends Hashtable
{
  // WARNING: Properties is a CORE class in the bootstrap cycle. See the
  // comments in vm/reference/java/lang/Runtime for implications of this fact.

  /**
   * The property list that contains default values for any keys not
   * in this property list.
   *
   * @serial the default properties
   */
  protected Properties defaults;

  protected String propertyCharEncoding = "ISO-8859-1";

  /**
   * Compatible with JDK 1.0+.
   */
  private static final long serialVersionUID = 4112578634029874840L;

  /**
   * Creates a new empty property list with no default values.
   */
  public Properties()
  {
	propertyCharEncoding = Util.getUtil().getDeviceCharEncoding(propertyCharEncoding);
  }

  /**
   * Create a new empty property list with the specified default values.
   *
   * @param defaults a Properties object containing the default values
   */
  public Properties(Properties defaults)
  {
    this.defaults = defaults;
	propertyCharEncoding = Util.getUtil().getDeviceCharEncoding(propertyCharEncoding);
  }

  /**
   * Reads a property list from an input stream.  The stream should
   * have the following format: <br>
   *
   * An empty line or a line starting with <code>#</code> or
   * <code>!</code> is ignored.  An backslash (<code>\</code>) at the
   * end of the line makes the line continueing on the next line
   * (but make sure there is no whitespace after the backslash).
   * Otherwise, each line describes a key/value pair. <br>
   *
   * The chars up to the first whitespace, = or : are the key.  You
   * can include this caracters in the key, if you precede them with
   * a backslash (<code>\</code>). The key is followed by optional
   * whitespaces, optionally one <code>=</code> or <code>:</code>,
   * and optionally some more whitespaces.  The rest of the line is
   * the resource belonging to the key. <br>
   *
   * Escape sequences <code>\t, \n, \r, \\, \", \', \!, \#, \ </code>(a
   * space), and unicode characters with the
   * <code>\\u</code><em>xxxx</em> notation are detected, and
   * converted to the corresponding single character. <br>
   *
   * 
<pre># This is a comment
key     = value
k\:5      \ a string starting with space and ending with newline\n
# This is a multiline specification; note that the value contains
# no white space.
weekdays: Sunday,Monday,Tuesday,Wednesday,\\
          Thursday,Friday,Saturday
# The safest way to include a space at the end of a value:
label   = Name:\\u0020</pre>
   *
   * @param in the input stream
   * @throws IOException if an error occurred when reading the input
   * @throws NullPointerException if in is null
   */
  public void load(InputStream inStream) throws IOException
  {
    // The spec says that the file must be encoded using ISO-8859-1.
	InputStreamReader reader = new InputStreamReader(inStream, propertyCharEncoding);
    String line;

    while ((line = readLine(reader)) != null)
      {
        char c = 0;
        int pos = 0;
	// Leading whitespaces must be deleted first.
        while (pos < line.length()
               && Util.isWhitespace(c = line.charAt(pos)))
          pos++;

        // If empty line or begins with a comment character, skip this line.
        if ((line.length() - pos) == 0
	    || line.charAt(pos) == '#' || line.charAt(pos) == '!')
          continue;

        // The characters up to the next Whitespace, ':', or '='
        // describe the key.  But look for escape sequences.
        StringBuffer key = new StringBuffer();
        while (pos < line.length()
               && ! Util.isWhitespace(c = line.charAt(pos++))
               && c != '=' && c != ':')
          {
            if (c == '\\')
              {
                if (pos == line.length())
                  {
                    // The line continues on the next line.
                    line = readLine(reader);
                    pos = 0;
                    while (pos < line.length()
                           && Util.isWhitespace(c = line.charAt(pos)))
                      pos++;
                  }
                else
                  {
                    c = line.charAt(pos++);
                    switch (c)
                      {
                      case 'n':
                        key.append('\n');
                        break;
                      case 't':
                        key.append('\t');
                        break;
                      case 'r':
                        key.append('\r');
                        break;
                      case 'u':
                        if (pos + 4 <= line.length())
                          {
                            char uni = (char) Integer.parseInt
                              (line.substring(pos, pos + 4), 16);
                            key.append(uni);
                            pos += 4;
                          }        // else throw exception?
                        break;
                      default:
                        key.append(c);
                        break;
                      }
                  }
              }
            else
              key.append(c);
          }

        boolean isDelim = (c == ':' || c == '=');
        while (pos < line.length()
               && Util.isWhitespace(c = line.charAt(pos)))
          pos++;

        if (! isDelim && (c == ':' || c == '='))
          {
            pos++;
            while (pos < line.length()
                   && Util.isWhitespace(c = line.charAt(pos)))
              pos++;
          }

        StringBuffer element = new StringBuffer(line.length() - pos);
        while (pos < line.length())
          {
            c = line.charAt(pos++);
            if (c == '\\')
              {
                if (pos == line.length())
                  {
                    // The line continues on the next line.
                    line = readLine(reader);

		    // We might have seen a backslash at the end of
		    // the file.  The JDK ignores the backslash in
		    // this case, so we follow for compatibility.
		    if (line == null)
		      break;

                    pos = 0;
                    while (pos < line.length()
                           && Util.isWhitespace(c = line.charAt(pos)))
                      pos++;
                    element.ensureCapacity(line.length() - pos +
                                           element.length());
                  }
                else
                  {
                    c = line.charAt(pos++);
                    switch (c)
                      {
                      case 'n':
                        element.append('\n');
                        break;
                      case 't':
                        element.append('\t');
                        break;
                      case 'r':
                        element.append('\r');
                        break;
                      case 'u':
                        if (pos + 4 <= line.length())
                          {
                            char uni = (char) Integer.parseInt
                              (line.substring(pos, pos + 4), 16);
                            element.append(uni);
                            pos += 4;
                          }        // else throw exception?
                        break;
                      default:
                        element.append(c);
                        break;
                      }
                  }
              }
            else
              element.append(c);
          }
        put(key.toString(), element.toString());
      }
  }

  /**
   * Gets the property with the specified key in this property list.
   * If the key is not found, the default property list is searched.
   * If the property is not found in the default, null is returned.
   *
   * @param key The key for this property
   * @return the value for the given key, or null if not found
   * @throws ClassCastException if this property contains any key or
   *         value that isn't a string
   * @see #defaults
   * @see #setProperty(String, String)
   * @see #getProperty(String, String)
   */
  public String getProperty(String key)
  {
    return getProperty(key, null);
  }

  /**
   * Gets the property with the specified key in this property list.  If
   * the key is not found, the default property list is searched.  If the
   * property is not found in the default, the specified defaultValue is
   * returned.
   *
   * @param key The key for this property
   * @param defaultValue A default value
   * @return The value for the given key
   * @throws ClassCastException if this property contains any key or
   *         value that isn't a string
   * @see #defaults
   * @see #setProperty(String, String)
   */
  public String getProperty(String key, String defaultValue)
  {
    Properties prop = this;
    // Eliminate tail recursion.
    do
      {
        String value = (String) prop.get(key);
        if (value != null)
          return value;
        prop = prop.defaults;
      }
    while (prop != null);
    return defaultValue;
  }

  boolean lastCharacterWasCR = false;
  
  private String readLine(InputStreamReader isr) throws IOException {
	  StringBuffer readLine = new StringBuffer();
	  char readCharacter;
	  int  readValue;
	  boolean endOfLineReached = false;
	  do {
		  readValue = isr.read();
		  if (readValue == -1) {
			  endOfLineReached = true;
		  }
		  else {
			  readCharacter = (char) readValue;
			  if (readCharacter == '\n') {
				  if (lastCharacterWasCR) {
					  // sequence was \r \n: ignore this \n 
				  }
				  else {
					  endOfLineReached = true;
				  }
				  lastCharacterWasCR = false;
			  }
			  else if (readCharacter == '\r') {
				  endOfLineReached = true;
				  lastCharacterWasCR = true;
			  }
			  else {
				  lastCharacterWasCR = false;
				  readLine.append(readCharacter);
			  }
		  }
	  }
	  while (! endOfLineReached);

	  String readLineReturnValue;
	  // Gert Nuber: last line was ignored if not followed by empty line; modified from "if (readValue == -1)"
	  if ((readValue == -1) && (readLine.length() == 0)) { 
		  readLineReturnValue = null;
	  }
	  else {
		  readLineReturnValue = readLine.toString();
	  }
	  return readLineReturnValue;
  }
  
} // class Properties
