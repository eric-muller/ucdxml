// COPYRIGHT AND PERMISSION NOTICE
//
// Copyright 2006-2016 Unicode Inc.
//
// All rights reserved.
//
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation
// files (the "Software"), to deal in the Software without
// restriction, including without limitation the rights to use, copy,
// modify, merge, publish, distribute, and/or sell copies of the
// Software, and to permit persons to whom the Software is furnished
// to do so, provided that the above copyright notice(s) and this
// permission notice appear in all copies of the Software and that
// both the above copyright notice(s) and this permission notice
// appear in supporting documentation.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN NO EVENT SHALL THE
// COPYRIGHT HOLDER OR HOLDERS INCLUDED IN THIS NOTICE BE LIABLE FOR
// ANY CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR ANY
// DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
// WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
// ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
// OF THIS SOFTWARE.
//
// Except as contained in this notice, the name of a copyright holder
// shall not be used in advertising or otherwise to promote the sale,
// use or other dealings in this Software without prior written
// authorization of the copyright holder.

package org.unicode.ucd;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;

public class Parser {
  static int verbosity = 0;

  static public abstract class Loader {
    public int currentLine = 0;
    public abstract void process (String[] fields) throws Exception;
  }

  static public void parseTabDelimitedFile (URL baseURL, String filename, String charset, Loader l)
  throws Exception {
    parseDelimitedFile (baseURL, filename, l, '\t', false, charset);
  }

  static public void parseSemiDelimitedFile (URL baseURL, String filename, String charset, Loader l)
  throws Exception {
    parseDelimitedFile (baseURL, filename, l, ';', false, charset);
  }

  static public void parseSemiDelimitedFileWithHeader (URL baseURL, String filename, String charset, Loader l)
  throws Exception {
    parseDelimitedFile (baseURL, filename, l, ';', true, charset);
  }

  static public void parseDelimitedFile (URL baseURL, String filename, Loader l, char delimiter, boolean header, String charset)
  throws Exception {
    URL url = new URL (baseURL, filename);
    LineNumberReader rd = new LineNumberReader (new InputStreamReader (url.openStream (), charset));

    if (verbosity >= 3) {
      System.out.println ("      ... " + url); }

    if (header) {
      String s;
      do {
        s = rd.readLine ();
        l.currentLine = rd.getLineNumber (); }
      while (s.length () != 0); }

    do {
      String s = rd.readLine ();
      l.currentLine = rd.getLineNumber ();
      if (s == null) {
        break; }

      int comment = s.indexOf ('#');
      if (comment != -1) {
        s = s.substring (0, comment); }

      if (s.length () < 2) {
        continue; }

      // Unfortunately, both String.split and StringTokenizer do not
      // behave the way we need on empty fields; so we have to split the
      // fields by hand.
      int start = 0;
      int length = s.length ();
      int nFields = 1;
      while (start < length) {
        int semi = s.indexOf (delimiter, start);
        if (semi == -1) {
           break; }
        nFields++;
        start = semi + 1; }
      String[] fields = new String [nFields];

      start = 0;
      for (int f = 0; f < nFields - 1; f++) {
        int semi = s.indexOf (delimiter, start);
        fields [f] = s.substring (start, semi).trim ();
        start = semi + 1; }
      if (start >= length) {
        fields [nFields-1] = ""; }
      else {
        fields [nFields-1] = s.substring (start).trim (); }

      l.process (fields); }

    while (true);
  }

  static public void parseSemiDelimitedFileWithAtmissings (URL baseURL, String filename, String charset, Loader l)
  throws Exception {
    final String atmissing = "# @missing: ";
    final String allcodespace = "0000..10FFFF; Left_To_Right";
    final char delimiter = ';';
    URL url = new URL (baseURL, filename);
    LineNumberReader rd = new LineNumberReader (new InputStreamReader (url.openStream (), charset));

    if (verbosity >= 3) {
      System.out.println ("      ... " + url); }

    do {
      String s = rd.readLine ();
      l.currentLine = rd.getLineNumber ();
      if (s == null) {
        break; }

      int comment = s.indexOf ('#');
      if (comment != -1) {
        if (s.startsWith (atmissing)) {  // @missing line
          s = s.substring (atmissing.length ());
          if (allcodespace.equals (s)) {
            // Skip the @missing line that covers the entire codespace,
            // because it would try to create too many boundaries.
            // We will fill in the gaps with the L default before the end.
            continue; }}
        else {                           // regular comment line
          s = s.substring (0, comment); }}

      if (s.length () < 2) {
        continue; }

      // Unfortunately, both String.split and StringTokenizer do not
      // behave the way we need on empty fields; so we have to split the
      // fields by hand.
      int start = 0;
      int length = s.length ();
      int nFields = 1;
      while (start < length) {
        int semi = s.indexOf (delimiter, start);
        if (semi == -1) {
           break; }
        nFields++;
        start = semi + 1; }
      String[] fields = new String [nFields];

      start = 0;
      for (int f = 0; f < nFields - 1; f++) {
        int semi = s.indexOf (delimiter, start);
        fields [f] = s.substring (start, semi).trim ();
        start = semi + 1; }
      if (start >= length) {
        fields [nFields-1] = ""; }
      else {
        fields [nFields-1] = s.substring (start).trim (); }

      l.process (fields); }

    while (true);
  }

  public interface LoaderWithCodePoints {
    public abstract void process (int first, int last, String[] fields) throws Exception;
  }

  static public void parseSemiDelimitedFileWithCodePointsAndAtmissings (URL baseURL, String filename, int field, String charset, LoaderWithCodePoints l)
  throws Exception {
    final int cpField = field;
    final LoaderWithCodePoints l2 = l;
    parseSemiDelimitedFileWithAtmissings (baseURL, filename, charset,
                                          new Loader ()  {
      public void process (String [] fields) throws Exception {
        int first, last;
        int dotdot = fields [cpField].indexOf ("..");
        if (dotdot != -1) {
          first = Integer.parseInt (fields [cpField].substring (0, dotdot), 16);
          last = Integer.parseInt (fields [cpField].substring (dotdot+2), 16); }
        else {
          first = Integer.parseInt (fields [cpField], 16);
          last = first; }
        l2.process (first, last, fields); }});
  }

  static public void parseSemiDelimitedFileWithCodePoints (URL baseURL, String filename, int field, String charset, LoaderWithCodePoints l)
  throws Exception {
    final int cpField = field;
    final LoaderWithCodePoints l2 = l;
    parseSemiDelimitedFile (baseURL, filename, charset,
                            new Loader ()  {
      public void process (String [] fields) throws Exception {
        int first, last;
        int dotdot = fields [cpField].indexOf ("..");
        if (dotdot != -1) {
          first = Integer.parseInt (fields [cpField].substring (0, dotdot), 16);
          last = Integer.parseInt (fields [cpField].substring (dotdot+2), 16); }
        else {
          first = Integer.parseInt (fields [cpField], 16);
          last = first; }
        l2.process (first, last, fields); }});
  }

  static public void parseSemiDelimitedFileWithUCodePoints (URL baseURL, String filename, int field, String charset, LoaderWithCodePoints l)
  throws Exception {
    final int cpField = field;
    final LoaderWithCodePoints l2 = l;
    final String filename2 = filename;
    parseSemiDelimitedFile (baseURL, filename, charset,
                            new Loader ()  {
      public void process (String [] fields) throws Exception {
        int first, last;
        int dotdot = fields [cpField].indexOf ("..");
        if (dotdot != -1) {
          first = Integer.parseInt (fields [cpField].substring (0, dotdot), 16);
          last = Integer.parseInt (fields [cpField].substring (dotdot+2), 16); }
        else {
          if (! fields [cpField].startsWith ("U+")) {
            System.err.println ("@@@ in '" + filename2 + "', code point does not start with U+ ('" + fields [cpField] + "')"); }
          first = Integer.parseInt (fields [cpField].substring (2), 16);
          last = first; }
        l2.process (first, last, fields); }});
  }




  static public void parseTabularFileWithHeader (URL baseURL, String filename, String charset, int[] cols, Loader l)
  throws Exception {
    URL url = new URL (baseURL, filename);
    LineNumberReader rd = new LineNumberReader (new InputStreamReader (url.openStream (), charset));

    if (verbosity >= 3) {
      System.out.println ("      ... " + url); }

    String s;
    String[] fields = new String [cols.length -1];

    // Skip the header, which is terminated by a blank line.
    do {
      s = rd.readLine (); }
    while (s.length () != 0);

    do {
      s = rd.readLine ();

      if (s != null) {
        for (int f = 0; f < cols.length - 1; f++) {
          fields [f] = s.substring (cols [f]-1, cols [f+1]-1).trim () ; }
        l.process (fields); }
    }  while (s != null);
  }
}
