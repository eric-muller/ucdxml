// COPYRIGHT AND PERMISSION NOTICE
//
// Copyright 2006-2018 Unicode Inc.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.unicode.ucd.Parser.Loader;
import org.unicode.ucd.Parser.LoaderWithCodePoints;
import org.unicode.ucd.Repertoire.DefaultSetter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class Ucd {
  
  public static final String NAMESPACE 
    = "http://www.unicode.org/ns/2003/ucd/1.0";
   
  //----------------------------------------------------------------------------
  
  static public String toU (int n) {
    return toU (Integer.toHexString (n));
  }
  
  static public String toU (String s) {
    if ("".equals (s)) { 
      return s; }
    while (s.length () < 4) {
      s = "0" + s; }
    return s.toUpperCase ();
  }
  
  //----------------------------------------------------------------------------

  public String description;
  public Repertoire repertoire;
  public Blocks blocks;
  public NamedSequences namedSequences;
  public NamedSequences namedSequencesProv;
  public NormalizationCorrections normalizationCorrections; 
  public StandardizedVariants standardizedVariants;
  public CJKRadicals cjkRadicals;
  public EmojiSources emojiSources;
  
  //----------------------------------------------------------------------------
  void normalize () {
    repertoire.normalize ();
  }

  //----------------------------------------------------------------------------
  
  static final String[] choseong = {"G", "GG", "N", "D", "DD", "R", "M", "B",
      "BB", "S", "SS", "", "J", "JJ", "C", "K",
      "T", "P", "H"};
  static final String[] jungseong = {"A", "AE", "YA", "YAE", "EO", "E", "YEO", 
      "YE", "O", "WA", "WAE", "OE", "YO", 
      "U", "WEO", "WE", "WI", "YU", "EU", "YI", "I"};
  static final String[] jongseong = {"", "G", "GG", "GS", "N", "NJ", "NH", "D",
      "L", "LG", "LM", "LB", "LS", "LT", "LP",
      "LH", "M", "B", "BS", "S", "SS", "NG",
      "J", "C", "K", "T", "P", "H"}; 
  
  private void parseUnicodeData (Version version, URL baseURL)
  throws Exception {
    final Version v = version;
    
    Parser.parseSemiDelimitedFile (baseURL, "UnicodeData.txt", "US-ASCII",
       new Parser.Loader () {
        int firstCp = 0;
        
        public void setUnicodeDataFields (int firstCp, int lastCp, String type, String[] fields) throws Exception {
                    
          repertoire.put (firstCp, lastCp, Property.type, type);
          if (! "Cc".equals (fields [2])) {
            repertoire.put (firstCp, lastCp, Property.na, fields [1]); }
          repertoire.put (firstCp, lastCp, Property.gc, fields [2]);
          repertoire.put (firstCp, lastCp, Property.ccc, fields [3]);
          //repertoire.put (firstCp, lastCp, Property.bc, fields [4]);   // the true values are in extracted/DerivedBidi.txt
          
          String dt, dm;
          if ("".equals (fields [5])) {
            dt = "none";
            dm = "#"; }
          else if (fields [5].indexOf (">") >= 0) {
            String tag = fields [5].substring (1, fields [5].indexOf ('>'));
            
            if ("compat".equals (tag)) {          dt = "com"; }
            else if ("circle".equals (tag)) {     dt = "enc"; }
            else if ("final".equals (tag)) {      dt = "fin"; }
            else if ("font".equals (tag)) {       dt = "font"; }
            else if ("fraction".equals (tag)) {   dt = "fra"; }
            else if ("initial".equals (tag)) {    dt = "init"; }
            else if ("isolated".equals (tag)) {   dt = "iso"; }
            else if ("medial".equals (tag)) {     dt = "med"; }
            else if ("narrow".equals (tag)) {     dt = "nar"; }
            else if ("noBreak".equals (tag)) {    dt = "nb"; }
            else if ("small".equals (tag)) {      dt = "sml"; }
            else if ("square".equals (tag)) {     dt = "sqr"; }
            else if ("super".equals (tag)) {      dt = "sup"; }
            else if ("sub".equals (tag)) {        dt = "sub"; }
            else if ("vertical".equals (tag)) {   dt = "vert"; }
            else if ("wide".equals (tag))     {   dt = "wide"; }
            else {
              System.err.println ("Unknown compatibility tag: '" + tag + "'"); 
              dt = "?"; }
            
            dm = fields [5].substring (fields [5].indexOf ('>') + 2); }
          else {
            dt  = "can";
            dm = fields [5]; }
          repertoire.put (firstCp, lastCp, Property.dt, dt);
          repertoire.put (firstCp, lastCp, Property.dm, dm);
          
          if (Version.V2_1_5.equals (v) && "N".equals (fields [8])) {
            // a bug in the 2.1.5 data file: the 12 unified ideographs
            // in the CJK Compatibility block have the N of Bidi_M in
            // the wrong field
              fields [8] = "";
              fields [9] = "N"; }
          
          String nt = null, nv = null;
          if (! "".equals (fields [6])) {
            nt = "De";
            nv = fields [6]; }
          else if (! "".equals (fields [7])) {
            nt = "Di";
            nv = fields [7]; }
          else if (! "".equals (fields [8])) {
            nt = "Nu";
            nv = fields [8]; }
          else {
            nt = "None";
            nv = null; }
          repertoire.put (firstCp, lastCp, Property.nt, nt);
          if (nv != null) {
            repertoire.put (firstCp, lastCp, Property.nv, nv); }
          
          repertoire.put (firstCp, lastCp, Property.bidi_m, fields [9]);
          
          repertoire.put (firstCp, lastCp, Property.na1, fields [10]);
          
          repertoire.put (firstCp, lastCp, Property.isc, fields [11]); 
          
          repertoire.put (firstCp, lastCp, Property.suc, fields [12].equals ("") ? "#" : fields [12]);        
          repertoire.put (firstCp, lastCp, Property.slc, fields [13].equals ("") ? "#" : fields [13]);
          repertoire.put (firstCp, lastCp, Property.stc, fields [14].equals ("") ? "#" : fields [14]);
        }

        public void process (String [] fields) throws Exception {
          int cp = Integer.parseInt (fields [0], 16);
          
          if (fields [1].endsWith (", First>")) {
            firstCp = cp; 
            return; }
          
          if ("<Hangul Syllable, Last>".equals (fields [1])) {       
            for (int hl = 0; hl < 19; hl++) {
              for (int hv = 0; hv < 21; hv++) {
                for (int ht = 0; ht < 28; ht++) {
                  int c1 = 0xac00 + (hl * 21 + hv) * 28;
                  int c = c1 + ht;
                  fields [1] = "HANGUL SYLLABLE " + choseong [hl] + jungseong [hv] + jongseong [ht];
                  String dm;
                  if (ht == 0) {
                    dm = Ucd.toU (0x1100 + hl) + " " + Ucd.toU (0x1161 + hv); }
                  else {
                    dm = Ucd.toU (c1) + " " + Ucd.toU (0x11a7 + ht); }
                  fields [5] = dm; 
                  setUnicodeDataFields (c, c, "char", fields); }}}
            return; }
          
          if (   "<CJK Ideograph, Last>".equals (fields [1])
              || "<CJK Ideograph Extension A, Last>".equals (fields [1])
              || "<CJK Ideograph Extension B, Last>".equals (fields [1])
              || "<CJK Ideograph Extension C, Last>".equals (fields [1])
              || "<CJK Ideograph Extension D, Last>".equals (fields [1])
              || "<CJK Ideograph Extension E, Last>".equals (fields [1])
              || "<CJK Ideograph Extension F, Last>".equals (fields [1])
              || "<CJK Ideograph Extension G, Last>".equals (fields [1])) {
            fields [1] = "CJK UNIFIED IDEOGRAPH-#";
            setUnicodeDataFields (firstCp, cp, "char", fields);
            return; }
          
          if ("<CJK Compatibility Ideograph, Last>".equals (fields [1])) {
            fields [1] = "CJK COMPATIBILITY IDEOGRAPH-#";
            setUnicodeDataFields (firstCp, cp, "char", fields);
            return; }
          
          if ("<Non Private Use High Surrogate, Last>".equals (fields [1])
              || "<Unassigned High Surrogate, Last>".equals (fields [1])
              || "<Private Use High Surrogate, Last>".equals (fields [1]) 
              || "<Low Surrogate, Last>".equals (fields [1])) {
            fields [1] = "";
            setUnicodeDataFields (firstCp, cp, "surrogate", fields);
            return; }
          
          if ("<Private Use, Last>".equals (fields [1])
              || "<Plane 15 Private Use, Last>".equals (fields [1])
              || "<Plane 16 Private Use, Last>".equals (fields [1])) {
            fields [1] = "";
            setUnicodeDataFields (firstCp, cp, "char", fields);
            return; }
          
          if ("<Tangut Ideograph, Last>".equals (fields [1])
              || "<Tangut Ideograph Supplement, Last>".equals (fields [1])) {
            fields [1] = "TANGUT IDEOGRAPH-#";
            setUnicodeDataFields (firstCp, cp, "char", fields);
            return; }
          
          setUnicodeDataFields (cp, cp, "char", fields); }});
     
     repertoire.put (0xfffe, 0xffff, Property.type, "noncharacter");
    
     if (version.isAtLeast (Version.V2_0_0)) {
       for (int plane = 0; plane <= 16; plane++) {
         int cp = plane * 0x10000 + 0xfffe;
         repertoire.put (cp, cp+1, Property.type, "noncharacter"); }}
    
    if (version.isAtLeast (Version.V3_1_0)) {
      repertoire.put (0xfdd0, 0xfdef, Property.type, "noncharacter"); }
    
    repertoire.putDefault (Property.type, "reserved");
    repertoire.putDefault (Property.na, "");
    repertoire.putDefault (Property.gc, "Cn");
    repertoire.putDefault (Property.ccc, "0");
    //repertoire.putDefault (Property.bc, "L");  // the true values are in extracted/DerivedBidi.txt
    repertoire.putDefault (Property.dt, "none");
    repertoire.putDefault (Property.dm, "#"); 
    repertoire.putDefault (Property.nt, "None");
    
    repertoire.putDefault (Property.nv, (v.isAtLeast (Version.V5_1_0)) ? "NaN" : ""); 
    
    repertoire.putDefault (Property.bidi_m, "N");
    
    repertoire.putDefault (Property.na1, "");
    
    repertoire.putDefault (Property.isc, ""); 
    repertoire.putDefault (Property.suc, "#");
    repertoire.putDefault (Property.slc, "#");
    repertoire.putDefault (Property.stc, "#");
  }
  
  //----------------------------------------------------------------------------
  static final Map<String, String> jgMap;
  static {
    jgMap = new HashMap<String, String> ();
    jgMap.put ("AIN",                    "Ain");
    jgMap.put ("ALAPH",                  "Alaph");
    jgMap.put ("ALEF",                   "Alef");
    jgMap.put ("ALEF MAQSURAH",          "Alef_Maqsurah"); // 2.x
    jgMap.put ("BEH",                    "Beh");
    jgMap.put ("BAA",                    "Beh");  // 2.x
    jgMap.put ("BETH",                   "Beth");
    jgMap.put ("BURUSHASKI YEH BARREE",  "Burushaski_Yeh_Barree");
    jgMap.put ("DAL",                    "Dal");
    jgMap.put ("DALATH RISH",            "Dalath_Rish");
    jgMap.put ("E",                      "E");
    jgMap.put ("FE",                     "Fe");
    jgMap.put ("FEH",                    "Feh");
    jgMap.put ("FA",                     "Feh"); // 2.x
    jgMap.put ("FARSI YEH",              "Farsi_Yeh");
    jgMap.put ("FINAL SEMKATH",          "Final_Semkath");
    jgMap.put ("GAF",                    "Gaf");
    jgMap.put ("GAMAL",                  "Gamal");
    jgMap.put ("HAH",                    "Hah");
    jgMap.put ("HAA",                    "Hah"); // 2.x
    jgMap.put ("HAMZA ON HEH GOAL",      "Hamza_On_Heh_Goal");
    jgMap.put ("HAMZAH ON HA GOAL",      "Hamza_On_Heh_Goal"); // 2.x
    jgMap.put ("HE",                     "He");
    jgMap.put ("HEH",                    "Heh");
    jgMap.put ("HA",                     "Heh"); // 2.x
    jgMap.put ("HEH GOAL",               "Heh_Goal");
    jgMap.put ("HA GOAL",                "Heh_Goal"); // 2.x
    jgMap.put ("HETH",                   "Heth");
    jgMap.put ("KAF",                    "Kaf");
    jgMap.put ("CAF",                    "Kaf"); // 2.x
    jgMap.put ("KAPH",                   "Kaph");
    jgMap.put ("KHAPH",                  "Khaph");
    jgMap.put ("KNOTTED HEH",            "Knotted_Heh");
    jgMap.put ("KNOTTED HA",             "Knotted_Heh"); // 2.x
    jgMap.put ("LAM",                    "Lam");
    jgMap.put ("LAMADH",                 "Lamadh");
    jgMap.put ("MEEM",                   "Meem");
    jgMap.put ("MIM",                    "Mim");
    jgMap.put ("<no shaping>",           "No_Joining_Group");
    jgMap.put ("No_Joining_Group",       "No_Joining_Group");
    jgMap.put ("NOON",                   "Noon");
    jgMap.put ("NUN",                    "Nun");
    jgMap.put ("NYA",                    "Nya");
    jgMap.put ("PE",                     "Pe");
    jgMap.put ("QAF",                    "Qaf");
    jgMap.put ("QAPH",                   "Qaph");
    jgMap.put ("REH",                    "Reh");
    jgMap.put ("RA",                     "Reh"); // 2.x
    jgMap.put ("REVERSED PE",            "Reversed_Pe");
    jgMap.put ("ROHINGYA YEH",           "Rohingya_Yeh");
    jgMap.put ("SAD",                    "Sad");
    jgMap.put ("SADHE",                  "Sadhe");
    jgMap.put ("SEEN",                   "Seen");
    jgMap.put ("SEMKATH",                "Semkath");
    jgMap.put ("SHIN",                   "Shin");
    jgMap.put ("SWASH KAF",              "Swash_Kaf");
    jgMap.put ("SWASH CAF",              "Swash_Kaf"); // 2.x
    jgMap.put ("SYRIAC WAW",             "Syriac_Waw");
    jgMap.put ("TAH",                    "Tah");
    jgMap.put ("TAW",                    "Taw");
    jgMap.put ("TEH MARBUTA",            "Teh_Marbuta");
    jgMap.put ("TEH MARBUTA GOAL",       "Teh_Marbuta_Goal");
    jgMap.put ("TAA MARBUTAH",           "Teh_Marbuta");  // 2.x
    jgMap.put ("TETH",                   "Teth");
    jgMap.put ("WAW",                    "Waw");
    jgMap.put ("YEH",                    "Yeh");
    jgMap.put ("YA",                     "Yeh"); // 2.x
    jgMap.put ("YEH BARREE",             "Yeh_Barree");
    jgMap.put ("YA BARREE",              "Yeh_Barree"); // 2.x
    jgMap.put ("YEH WITH TAIL",          "Yeh_With_Tail");
    jgMap.put ("YUDH",                   "Yudh");
    jgMap.put ("YUDH HE",                "Yudh_He");
    jgMap.put ("ZAIN",                   "Zain");
    jgMap.put ("ZHAIN",                  "Zhain");
    
    // Unicode 7.0
    jgMap.put ("STRAIGHT WAW",                       "Straight_Waw");
    jgMap.put ("MANICHAEAN ALEPH",                   "Manichaean_Aleph");
    jgMap.put ("MANICHAEAN AYIN",                    "Manichaean_Ayin");
    jgMap.put ("MANICHAEAN BETH",                    "Manichaean_Beth");
    jgMap.put ("MANICHAEAN DALETH",                  "Manichaean_Daleth");
    jgMap.put ("MANICHAEAN DHAMEDH",                 "Manichaean_Dhamedh");
    jgMap.put ("MANICHAEAN FIVE",                    "Manichaean_Five");
    jgMap.put ("MANICHAEAN GIMEL",                   "Manichaean_Gimel");
    jgMap.put ("MANICHAEAN HETH",                    "Manichaean_Heth");
    jgMap.put ("MANICHAEAN HUNDRED",                 "Manichaean_Hundred");
    jgMap.put ("MANICHAEAN KAPH",                    "Manichaean_Kaph");
    jgMap.put ("MANICHAEAN LAMEDH",                  "Manichaean_Lamedh");
    jgMap.put ("MANICHAEAN MEM",                     "Manichaean_Mem");
    jgMap.put ("MANICHAEAN NUN",                     "Manichaean_Nun");
    jgMap.put ("MANICHAEAN ONE",                     "Manichaean_One");
    jgMap.put ("MANICHAEAN PE",                      "Manichaean_Pe");
    jgMap.put ("MANICHAEAN QOPH",                    "Manichaean_Qoph");
    jgMap.put ("MANICHAEAN RESH",                    "Manichaean_Resh");
    jgMap.put ("MANICHAEAN SADHE",                   "Manichaean_Sadhe");
    jgMap.put ("MANICHAEAN SAMEKH",                  "Manichaean_Samekh");
    jgMap.put ("MANICHAEAN TAW",                     "Manichaean_Taw");
    jgMap.put ("MANICHAEAN TEN",                     "Manichaean_Ten");
    jgMap.put ("MANICHAEAN TETH",                    "Manichaean_Teth");
    jgMap.put ("MANICHAEAN THAMEDH",                 "Manichaean_Thamedh");
    jgMap.put ("MANICHAEAN TWENTY",                  "Manichaean_Twenty");
    jgMap.put ("MANICHAEAN WAW",                     "Manichaean_Waw");
    jgMap.put ("MANICHAEAN YODH",                    "Manichaean_Yodh");
    jgMap.put ("MANICHAEAN ZAYIN",                   "Manichaean_Zayin");

    // Unicode 9.0
    jgMap.put ("AFRICAN FEH",                        "African_Feh");
    jgMap.put ("AFRICAN NOON",                       "African_Noon");
    jgMap.put ("AFRICAN QAF",                        "African_Qaf");
    
    // Unicode 10.0
    jgMap.put ("MALAYALAM NGA",                      "Malayalam_Nga");
    jgMap.put ("MALAYALAM JA",                       "Malayalam_Ja");
    jgMap.put ("MALAYALAM NYA",                      "Malayalam_Nya");
    jgMap.put ("MALAYALAM TTA",                      "Malayalam_Tta");
    jgMap.put ("MALAYALAM NNA",                      "Malayalam_Nna");
    jgMap.put ("MALAYALAM NNNA",                     "Malayalam_Nnna");
    jgMap.put ("MALAYALAM BHA",                      "Malayalam_Bha");
    jgMap.put ("MALAYALAM RA",                       "Malayalam_Ra");
    jgMap.put ("MALAYALAM LLA",                      "Malayalam_Lla");
    jgMap.put ("MALAYALAM LLLA",                     "Malayalam_Llla");
    jgMap.put ("MALAYALAM SSA",                      "Malayalam_Ssa");

    // Unicode 11.0
    jgMap.put ("HANIFI ROHINGYA KINNA YA",           "Hanifi_Rohingya_Kinna_Ya");
    jgMap.put ("HANIFI ROHINGYA PA",                 "Hanifi_Rohingya_Pa");
  }
  
  private void parseArabicShaping (Version v, URL baseURL) throws Exception {
    final Version vv = v;
    if (v.isAtLeast (Version.V2_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "ArabicShaping.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              String jt = fields [2];
              repertoire.put (firstCp, lastCp, Property.jt, jt);
              if (jgMap.get (fields [3]) == null) {
                System.err.println ("** Unknown jg value (" + fields [3] + ") for U+" + toU(firstCp)); }
              repertoire.put (firstCp, lastCp, Property.jg, jgMap.get (fields [3])); }});
      
      repertoire.putDefault (
          new DefaultSetter () {
            public void process (Group r) {
              if (r.get (Property.jt) == null) {
                String gc = r.get (Property.gc);
                if ("Mn".equals (gc) || "Cf".equals (gc) || ("Me".equals (gc) && vv.isAtLeast (Version.V4_1_0))) {
                  r.putDefault (Property.jt, "T"); }
                else {
                  r.putDefault (Property.jt, "U"); }}
              
              if (r.get (Property.jg) == null) {
                r.putDefault (Property.jg, "No_Joining_Group"); }}}); }
  }
  
  private void parseBidiBrackets (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V6_3_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "BidiBrackets.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.bpt, fields [2]);
              repertoire.put (firstCp, lastCp, Property.bpb, fields [1]); }});
      
      repertoire.putDefault (Property.bpt, "n");
      repertoire.putDefault (Property.bpb, "#"); }
  }

  private void parseBidiMirroring (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V3_0_1)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "BidiMirroring.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.bmg, Ucd.toU (fields [1])); }});
      
      repertoire.putDefault (Property.bmg, ""); }
  }

  private void parseCaseFolding (Version v, URL baseURL) throws Exception {
    
    if (v.isAtLeast (Version.V3_1_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "CaseFolding.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              if ("C".equals (fields [1])) {
                repertoire.put (firstCp, lastCp, Property.scf, fields [2]);
                repertoire.put (firstCp, lastCp, Property.cf, fields [2]); }
              if ("S".equals (fields [1])) {
                repertoire.put (firstCp, lastCp, Property.scf, fields [2]); }
              if ("F".equals (fields [1])) {
                repertoire.put (firstCp, lastCp, Property.cf, fields [2]); }}});
      
      repertoire.putDefault (Property.scf, "#");
      repertoire.putDefault (Property.cf, "#");

      return; }
        
    if (v.isAtLeast (Version.V3_0_1)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "CaseFolding.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              if (fields [2].indexOf (' ') == -1) {
                repertoire.put (firstCp, lastCp, Property.scf, fields [2]); }
              repertoire.put (firstCp, lastCp, Property.cf, fields [2]); }});
                    
      repertoire.putDefault (Property.scf, "#");
      repertoire.putDefault (Property.cf, "#");

      return; }
  }
  
  private void parseCompositionExclusions (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V3_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "CompositionExclusions.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.ce, "Y"); }});

      repertoire.putDefault (Property.ce, "N"); }}
      
//    new DefaultSetter () {
//      public void process (Range r) {       
//          r.putDefault (Property.ce, "N"); }}); }
//      repertoire.putDefault (
//          new DefaultSetter () {
//            public void process (Range r) {
//              if (r.get (Property.ce) == null) {
//                String exclude = "N";
//                if (r.get (Property.dt) == "can") {
//                  String dm = r.get (Property.dm);
//                  if (dm != "" && dm.indexOf (' ') == -1) { // singleton decomposition
//                    exclude = "Y"; }
//                  else {
//                    int firstCp = Integer.parseInt (dm.substring (0, dm.indexOf (' ')), 16); 
//                    if (! "0".equals (repertoire.get (firstCp, Property.ccc))) {
//                      exclude = "Y"; }}}
//                
//                r.putDefault (Property.ce, exclude); }}}); }

  

  private void parseDerivedAge (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V3_2_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "DerivedAge.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.age, fields [1]); }}); 
      
      repertoire.putDefault (Property.age, "unassigned"); }
  }
  
  private void parseEquivalentUnifiedIdeograph (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V11_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "EquivalentUnifiedIdeograph.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.EqUIdeo, fields [1]); }}); }
  }
  
  private void parseEastAsianWidth (Version v, URL baseURL) throws Exception {   
    if (v.isAtLeast (Version.V3_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "EastAsianWidth.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.ea, fields [1]); }}); 
      
      repertoire.putDefault (Property.ea, "N"); }
  }
 
  private void parseHangulSyllableType (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V4_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "HangulSyllableType.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.hst, fields [1]); }});
      
      repertoire.putDefault (Property.hst, "NA"); }
  }
        
  private void parseIndicSyllabicCategory (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V6_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "IndicSyllabicCategory.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.InSC, fields [1]); }});
      
      repertoire.putDefault (Property.InSC, "Other"); }
  }
        
  private void parseIndicMatraCategory (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V6_0_0) && v.isAtMost (Version.V7_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "IndicMatraCategory.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.InMC, fields [1]); }});
      
      repertoire.putDefault (Property.InMC, "NA"); }
  }
        
  private void parseIndicPositionalCategory (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V8_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "IndicPositionalCategory.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.InPC, fields [1]); }});
      
      repertoire.putDefault (Property.InPC, "NA"); }
  }
        
  private void parseJamo (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V3_0_1)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "Jamo.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.jsn, fields [1]); }}); 
      
      repertoire.putDefault (Property.jsn, ""); }
    else if (v.isAtLeast (Version.V2_0_0)) {
      Parser.parseSemiDelimitedFileWithUCodePoints (baseURL, "Jamo.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.jsn, fields [1]); }}); 
      
      repertoire.putDefault (Property.jsn, ""); }
  }
        
  private void parseCJKRadicals (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V5_2_0)) {     
      Parser.parseSemiDelimitedFile (baseURL, "CJKRadicals.txt", "US-ASCII",
          new Loader () {
        public void process (String[] fields) {
          cjkRadicals.add (new CJKRadical (fields [0], fields [1], fields [2])); }}); }
  }
 
  private void parseLineBreak (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V3_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "LineBreak.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.lb, fields [1]); }}); 
      
      repertoire.putDefault (Property.lb, v.isAtLeast (Version.V4_0_0) ? "XX" : "AL"); }
  }
        
  
  private void parseBinaryPropertyFile (Version v, URL baseURL, String filename, String charset) throws Exception {
    final Set<Property> properties = new HashSet<Property> ();
    
    Parser.parseSemiDelimitedFileWithCodePoints (baseURL, filename, 0, charset,
      new LoaderWithCodePoints () {
        public void process (int firstCp, int lastCp, String[] fields) {
          Property p = Property.fromString (fields [1]);
          properties.add (p);
          repertoire.put (firstCp, lastCp, p, "Y"); }});
  
    for (Property p : properties) {
      repertoire.putDefault (p, "N"); }
  }
    
  private void parsePropList (Version v, URL baseURL) throws Exception {
    //TODO : propList.txt exists since 2.0.0, but the format is different
    if (v.isAtLeast (Version.V3_1_0)) {
      parseBinaryPropertyFile (v, baseURL, "PropList.txt", "US-ASCII"); }
  }
    
  private void parseDerivedCoreProperties (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V3_1_0)) {
      parseBinaryPropertyFile (v, baseURL, "DerivedCoreProperties.txt", "US-ASCII"); }
  }
  
  private void parseDerivedNormalizationProperties (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V3_1_0)) {
      
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, 
        v.isAtLeast (Version.V3_2_0) ? "DerivedNormalizationProps.txt" : "DerivedNormalizationProperties.txt", 
           0, "US-ASCII",
        new LoaderWithCodePoints () {
          public void process (int firstCp, int lastCp, String[] fields) {
            if ("NFD_NO".equals (fields[1])) {
              repertoire.put (firstCp, lastCp, Property.nfd_qc, "N"); }
            else if ("NFC_MAYBE".equals (fields[1])) {
              repertoire.put (firstCp, lastCp, Property.nfc_qc, "M"); }
            else if ("NFC_NO".equals (fields[1])) {
              repertoire.put (firstCp, lastCp, Property.nfc_qc, "N"); }
            else if ("NFKD_NO".equals (fields[1])) {
              repertoire.put (firstCp, lastCp, Property.nfkd_qc, "N"); }
            else if ("NFKC_MAYBE".equals (fields[1])) {
              repertoire.put (firstCp, lastCp, Property.nfkc_qc, "M"); }
            else if ("NFKC_NO".equals (fields[1])) {
              repertoire.put (firstCp, lastCp, Property.nfkc_qc, "N"); }
            else {
              Property p = Property.fromString (fields [1]);
              if (fields.length == 2) {
                repertoire.put (firstCp, lastCp, p, "Y"); }
              else {   
                repertoire.put (firstCp, lastCp, p, fields[2]); }}}});
      
      repertoire.putDefault (Property.fc_nfkc, "#"); 
      repertoire.putDefault (Property.comp_ex, "N");
      repertoire.putDefault (Property.nfc_qc, "Y");
      repertoire.putDefault (Property.nfd_qc, "Y");
      repertoire.putDefault (Property.nfkc_qc, "Y");
      repertoire.putDefault (Property.nfkd_qc, "Y");
      repertoire.putDefault (Property.xo_nfc, "N");
      repertoire.putDefault (Property.xo_nfd, "N");
      repertoire.putDefault (Property.xo_nfkc, "N");
      repertoire.putDefault (Property.xo_nfkd, "N");

      if (v.isAtLeast (Version.V5_2_0)) {
        repertoire.putDefault (Property.cwkcf, "N");
        repertoire.putDefault (Property.nfkc_cf, "#"); } 
    }
  }
  
  static final Map<String, String> scriptMap;
  static {
    scriptMap = new HashMap<String, String> ();
    scriptMap.put ("ARABIC",                   "Arab");
    scriptMap.put ("ARMENIAN",                 "Armn");
    scriptMap.put ("AVESTAN",                  "Avst");
    scriptMap.put ("BALINESE",                 "Bali");
    scriptMap.put ("BAMUM",                    "Bamu");
    scriptMap.put ("BATAK",                    "Batk");
    scriptMap.put ("BRAHMI",                   "Brah");
    scriptMap.put ("BENGALI",                  "Beng");
    scriptMap.put ("BOPOMOFO",                 "Bopo");
    scriptMap.put ("BRAILLE",                  "Brai");
    scriptMap.put ("BUGINESE",                 "Bugi");
    scriptMap.put ("BUHID",                    "Buhd");
    scriptMap.put ("CANADIAN_ABORIGINAL",      "Cans");
    scriptMap.put ("CANADIAN-ABORIGINAL",      "Cans");
    scriptMap.put ("CARIAN",                   "Cari");
    scriptMap.put ("CHAKMA",                   "Cakm");
    scriptMap.put ("CHAM",                     "Cham");
    scriptMap.put ("CHEROKEE",                 "Cher");
    scriptMap.put ("COMMON",                   "Zyyy");
    scriptMap.put ("COPTIC",                   "Copt");
    scriptMap.put ("CUNEIFORM",                "Xsux");
    scriptMap.put ("CYPRIOT",                  "Cprt");
    scriptMap.put ("CYRILLIC",                 "Cyrl");
    scriptMap.put ("DESERET",                  "Dsrt");
    scriptMap.put ("DEVANAGARI",               "Deva");
    scriptMap.put ("EGYPTIAN_HIEROGLYPHS",     "Egyp");
    scriptMap.put ("ETHIOPIC",                 "Ethi");
    scriptMap.put ("GEORGIAN",                 "Geor");
    scriptMap.put ("GLAGOLITIC",               "Glag");
    scriptMap.put ("GOTHIC",                   "Goth");
    scriptMap.put ("GREEK",                    "Grek");
    scriptMap.put ("GUJARATI",                 "Gujr");
    scriptMap.put ("GURMUKHI",                 "Guru");
    scriptMap.put ("HAN",                      "Hani");
    scriptMap.put ("HANGUL",                   "Hang");
    scriptMap.put ("HANUNOO",                  "Hano");
    scriptMap.put ("HEBREW",                   "Hebr");
    scriptMap.put ("HIRAGANA",                 "Hira");
    scriptMap.put ("INHERITED",                "Zinh");
    scriptMap.put ("IMPERIAL_ARAMAIC",         "Armi");
    scriptMap.put ("INSCRIPTIONAL_PARTHIAN",   "Prti");
    scriptMap.put ("INSCRIPTIONAL_PAHLAVI",    "Phli");
    scriptMap.put ("JAVANESE",                 "Java");
    scriptMap.put ("KAITHI",                   "Kthi");
    scriptMap.put ("KANNADA",                  "Knda");
    scriptMap.put ("KATAKANA",                 "Kana");
    scriptMap.put ("KATAKANA_OR_HIRAGANA",     "Hrkt");
    scriptMap.put ("KAYAH_LI",                 "Kali");
    scriptMap.put ("KHAROSHTHI",               "Khar");
    scriptMap.put ("KHMER",                    "Khmr");
    scriptMap.put ("LAO",                      "Laoo");
    scriptMap.put ("LATIN",                    "Latn");
    scriptMap.put ("LEPCHA",                   "Lepc");
    scriptMap.put ("LIMBU",                    "Limb");
    scriptMap.put ("LINEAR_B",                 "Linb");
    scriptMap.put ("LISU",                     "Lisu");
    scriptMap.put ("LYCIAN",                   "Lyci");
    scriptMap.put ("LYDIAN",                   "Lydi");
    scriptMap.put ("MALAYALAM",                "Mlym");
    scriptMap.put ("MANDAIC",                  "Mand");
    scriptMap.put ("MEETEI_MAYEK",             "Mtei");
    scriptMap.put ("MEROITIC_CURSIVE",         "Merc");
    scriptMap.put ("MEROITIC_HIEROGLYPHS",     "Mero");
    scriptMap.put ("MONGOLIAN",                "Mong");
    scriptMap.put ("MIAO",                     "Plrd");
    scriptMap.put ("MYANMAR",                  "Mymr");
    scriptMap.put ("NEW_TAI_LUE",              "Talu");
    scriptMap.put ("NKO",                      "Nkoo");
    scriptMap.put ("OGHAM",                    "Ogam");
    scriptMap.put ("OL_CHIKI",                 "Olck");
    scriptMap.put ("OLD_ITALIC",               "Ital");
    scriptMap.put ("OLD-ITALIC",               "Ital");
    scriptMap.put ("OLD_PERSIAN",              "Xpeo");
    scriptMap.put ("OLD_SOUTH_ARABIAN",        "Sarb");
    scriptMap.put ("OLD_TURKIC",               "Orkh");
    scriptMap.put ("ORIYA",                    "Orya");
    scriptMap.put ("OSMANYA",                  "Osma");
    scriptMap.put ("PHAGS_PA",                 "Phag");
    scriptMap.put ("PHOENICIAN",               "Phnx");
    scriptMap.put ("REJANG",                   "Rjng");
    scriptMap.put ("RUNIC",                    "Runr");
    scriptMap.put ("SAMARITAN",                "Samr");
    scriptMap.put ("SAURASHTRA",               "Saur");
    scriptMap.put ("SHARADA",                  "Shrd");
    scriptMap.put ("SHAVIAN",                  "Shaw");
    scriptMap.put ("SIGN_WRITING",             "Sgnw");
    scriptMap.put ("SINHALA",                  "Sinh");
    scriptMap.put ("SORA_SOMPENG",             "Sora");
    scriptMap.put ("SUNDANESE",                "Sund");
    scriptMap.put ("SYLOTI_NAGRI",             "Sylo");
    scriptMap.put ("SYRIAC",                   "Syrc");
    scriptMap.put ("TAGALOG",                  "Tglg");
    scriptMap.put ("TAGBANWA",                 "Tagb");
    scriptMap.put ("TAI_LE",                   "Tale");
    scriptMap.put ("TAI_THAM",                 "Lana");
    scriptMap.put ("TAI_VIET",                 "Tavt");
    scriptMap.put ("TAKRI",                    "Takr");
    scriptMap.put ("TAMIL",                    "Taml");
    scriptMap.put ("TELUGU",                   "Telu");
    scriptMap.put ("THAANA",                   "Thaa");
    scriptMap.put ("THAI",                     "Thai");
    scriptMap.put ("TIBETAN",                  "Tibt");
    scriptMap.put ("TIFINAGH",                 "Tfng");
    scriptMap.put ("UGARITIC",                 "Ugar");
    scriptMap.put ("VAI",                      "Vaii");
    scriptMap.put ("YI",                       "Yiii");
    
    // Unicode 7.0
    scriptMap.put ("CAUCASIAN_ALBANIAN",       "Aghb");
    scriptMap.put ("BASSA_VAH",                "Bass");
    scriptMap.put ("DUPLOYAN",                 "Dupl");
    scriptMap.put ("ELBASAN",                  "Elba");
    scriptMap.put ("GRANTHA",                  "Gran");
    scriptMap.put ("PAHAWH_HMONG",             "Hmng");
    scriptMap.put ("KHOJKI",                   "Khoj");
    scriptMap.put ("LINEAR_A",                 "Lina");
    scriptMap.put ("MAHAJANI",                 "Mahj");
    scriptMap.put ("MANICHAEAN",               "Mani");
    scriptMap.put ("MENDE_KIKAKUI",            "Mend");
    scriptMap.put ("MODI",                     "Modi");
    scriptMap.put ("MRO",                      "Mroo");
    scriptMap.put ("OLD_NORTH_ARABIAN",        "Narb");
    scriptMap.put ("NABATAEAN",                "Nbat");
    scriptMap.put ("PALMYRENE",                "Palm");
    scriptMap.put ("PAU_CIN_HAU",              "Pauc");
    scriptMap.put ("OLD_PERMIC",               "Perm");
    scriptMap.put ("PSALTER_PAHLAVI",          "Phlp");
    scriptMap.put ("SIDDHAM",                  "Sidd");
    scriptMap.put ("KHUDAWADI",                "Sind");
    scriptMap.put ("TIRHUTA",                  "Tirh");
    scriptMap.put ("WARANG_CITI",              "Wara");

    // Unicode 8.0
    scriptMap.put ("AHOM",                     "Ahom");
    scriptMap.put ("ANATOLIAN_HIEROGLYPHS",    "Hluw");
    scriptMap.put ("HATRAN",                   "Hatr");
    scriptMap.put ("MULTANI",                  "Mult");
    scriptMap.put ("OLD_HUNGARIAN",            "Hung");
    scriptMap.put ("SIGNWRITING",              "Sgnw");
    
    // Unicode 9.0
    scriptMap.put ("ADLAM",                    "Adlm");
    scriptMap.put ("BHAIKSUKI",                "Bhks");
    scriptMap.put ("MARCHEN",                  "Marc");
    scriptMap.put ("NEWA",                     "Newa");
    scriptMap.put ("OSAGE",                    "Osge");
    scriptMap.put ("TANGUT",                   "Tang");
    
    // Unicode 10.0
    scriptMap.put ("MASARAM_GONDI",            "Gonm");
    scriptMap.put ("NUSHU",                    "Nshu");
    scriptMap.put ("SOYOMBO",                  "Soyo");
    scriptMap.put ("ZANABAZAR_SQUARE",         "Zanb");

    // Unicode 11.0
    scriptMap.put ("DOGRA",                    "Dogr");
    scriptMap.put ("GUNJALA_GONDI",            "Gong");
    scriptMap.put ("MAKASAR",                  "Maka");
    scriptMap.put ("MEDEFAIDRIN",              "Medf");
    scriptMap.put ("HANIFI_ROHINGYA",          "Rohg");
    scriptMap.put ("SOGDIAN",                  "Sogd");
    scriptMap.put ("OLD_SOGDIAN",              "Sogo");

    // Unicode 12.0
    scriptMap.put ("ELYMAIC",                  "Elym");
    scriptMap.put ("NANDINAGARI",              "Nand");
    scriptMap.put ("NYIAKENG_PUACHUE_HMONG",   "Hmnp");
    scriptMap.put ("WANCHO",                   "Wcho");

    // Unicode 13.0
    scriptMap.put ("CHORASMIAN",               "Chrs");
    scriptMap.put ("DIVES_AKURU",              "Diak");
    scriptMap.put ("KHITAN_SMALL_SCRIPT",      "Kits");
    scriptMap.put ("YEZIDI",                   "Yezi");
  }
  
  
  private void parseScripts (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V3_1_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "Scripts.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              String sc = scriptMap.get (fields [1].toUpperCase ());
              if (sc == null) { 
                System.err.println ("Unknown script: " + fields [1]); }
              repertoire.put (firstCp, lastCp, Property.sc, sc); }});
      repertoire.putDefault (Property.sc, v.isAtLeast (Version.V5_0_0) ? "Zzzz" : "Zyyy"); }
  }

  private void parseScriptExtensions (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V6_1_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "ScriptExtensions.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              repertoire.put (firstCp, lastCp, Property.scx, fields [1]); }});
      
      repertoire.putDefault (
          new DefaultSetter  ()  {
            public void process (Group r) {
              if (r.get (Property.scx) == null) {             
                r.putDefault (Property.scx, r.get (Property.sc)); }}}); }
  }

  private void parseSpecialCasing (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V2_1_8)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "SpecialCasing.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
            public void process (int firstCp, int lastCp, String[] fields) {
              if ("".equals (fields [4])) {
                repertoire.put (firstCp, lastCp, Property.lc, fields [1]); 
                repertoire.put (firstCp, lastCp, Property.tc, fields [2]);
                repertoire.put (firstCp, lastCp, Property.uc, fields [3]); }}});
      
      for (Group r : repertoire.groups) {
        r.putDefault (Property.lc, r.get (Property.slc)); 
        r.putDefault (Property.tc, r.get (Property.stc)); 
        r.putDefault (Property.uc, r.get (Property.suc)); }}
  }
 
  static final Map<String, String> gcbMap;
  static {
    gcbMap = new HashMap<String, String> ();
    gcbMap.put ("Control",            "CN");
    gcbMap.put ("CR",                 "CR");
    gcbMap.put ("Extend",             "EX");
    gcbMap.put ("L",                  "L");
    gcbMap.put ("LF",                 "LF");
    gcbMap.put ("LV",                 "LV");
    gcbMap.put ("LVT",                "LVT");
    gcbMap.put ("Prepend",            "PP");
    gcbMap.put ("Regional_Indicator", "RI");
    gcbMap.put ("SpacingMark",        "SM");
    gcbMap.put ("T",                  "T");
    gcbMap.put ("V",                  "V");
    gcbMap.put ("Other",              "XX");
    
    // Unicode 9.0
    gcbMap.put ("E_Base",             "EB");
    gcbMap.put ("E_Base_GAZ",         "EBG");
    gcbMap.put ("E_Modifier",         "EM");
    gcbMap.put ("Glue_After_Zwj",     "GAZ");
    gcbMap.put ("ZWJ",                "ZWJ");    
  }
  
  private void parseGraphemeBreak (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V4_1_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "auxiliary/GraphemeBreakProperty.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
        public void process (int firstCp, int lastCp, String[] fields) {
          if (gcbMap.get (fields [1]) == null) {
            System.err.println ("** Unknown gcb value (" + fields [1] + ") for U+" + toU(firstCp)); }
          repertoire.put (firstCp, lastCp, Property.gcb, gcbMap.get (fields [1])); }});
      
      repertoire.putDefault (Property.gcb, "XX"); }
  }
        
  static final Map<String, String> wbMap;
  static {
    wbMap = new HashMap<String, String> ();
    wbMap.put ("CR",                 "CR");
    wbMap.put ("Double_Quote",       "DQ");
    wbMap.put ("ExtendNumLet",       "EX");
    wbMap.put ("Extend",             "Extend");
    wbMap.put ("Format",             "FO");
    wbMap.put ("Hebrew_Letter",      "HL");
    wbMap.put ("Katakana",           "KA");
    wbMap.put ("ALetter",            "LE");
    wbMap.put ("LF",                 "LF");
    wbMap.put ("MidNumLet",          "MB");
    wbMap.put ("MidLetter",          "ML");
    wbMap.put ("MidNum",             "MN");
    wbMap.put ("Newline",            "NL");
    wbMap.put ("Numeric",            "NU");
    wbMap.put ("Other",              "XX");
    wbMap.put ("Regional_Indicator", "RI");
    wbMap.put ("Single_Quote",       "SQ");
    
    // Unicode 9.0
    wbMap.put ("E_Base",             "EB");
    wbMap.put ("E_Base_GAZ",         "EBG");
    wbMap.put ("E_Modifier",         "EM");
    wbMap.put ("Glue_After_Zwj",     "GAZ");
    wbMap.put ("ZWJ",                "ZWJ");

    // Unicode 11.0
   wbMap.put ("WSegSpace",          "WSegSpace");
  }
  
  private void parseWordBreak (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V4_1_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "auxiliary/WordBreakProperty.txt", 0, "US-ASCII",
        new LoaderWithCodePoints () {
        public void process (int firstCp, int lastCp, String[] fields) {
          if (wbMap.get (fields [1]) == null) {
            System.err.println ("** Unknown wb value (" + fields [1] + ") for U+" + toU(firstCp)); }
          repertoire.put (firstCp, lastCp, Property.wb, wbMap.get (fields [1])); }});
      
      repertoire.putDefault (Property.wb, "XX"); }
  }
  
  static final Map<String, String> sbMap;
  static {
    sbMap = new HashMap<String, String> ();
    sbMap.put ("ATerm",              "AT");
    sbMap.put ("Close",              "CL");
    sbMap.put ("CR",                 "CR");
    sbMap.put ("Extend",             "EX");
    sbMap.put ("Format",             "FO");
    sbMap.put ("OLetter",            "LE");
    sbMap.put ("LF",                 "LF");
    sbMap.put ("Lower",              "LO");
    sbMap.put ("Numeric",            "NU");
    sbMap.put ("SContinue",          "SC");
    sbMap.put ("Sep",                "SE");
    sbMap.put ("Sp",                 "SP");
    sbMap.put ("STerm",              "ST");
    sbMap.put ("Upper",              "UP");
    sbMap.put ("Other",              "XX");
  }
  
  private void parseSentenceBreak (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V4_1_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "auxiliary/SentenceBreakProperty.txt", 0, "US-ASCII",
        new LoaderWithCodePoints () {
          public void process (int firstCp, int lastCp, String[] fields) {
            if (sbMap.get (fields [1]) == null) {
              System.err.println ("** Unknown sb value (" + fields [1] + ") for U+" + toU(firstCp)); }
            repertoire.put (firstCp, lastCp, Property.sb, sbMap.get (fields [1])); }});
      
      repertoire.putDefault (Property.sb, "XX"); }
  }

  private void parseDerivedBidiClass (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V3_2_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "extracted/DerivedBidiClass.txt", 0, "US-ASCII",
        new LoaderWithCodePoints () {
          public void process (int firstCp, int lastCp, String[] fields) {
            repertoire.put (firstCp, lastCp, Property.bc, fields [1]); }});
      
      repertoire.putDefault (Property.bc, "L"); }
  }

  private void parseUnihan (Version v, URL baseURL, boolean numericValuesOnly) throws Exception {
    final String currentFile = new URL (baseURL, "Unihan.txt").toString ();
    final boolean numericValsOnly = numericValuesOnly;

    final boolean[] unihanCodePoint = new boolean[0x110000];
    for (int cp = 0; cp < 0x110000; cp++) {
      unihanCodePoint [cp] = false; }
    
    Parser.parseTabDelimitedFile (baseURL, "Unihan.txt", "UTF-8",
        new Loader () {
          public void process (String[] fields) {
            if (fields.length != 3) {
              System.err.println ("@@@ Error in file " + currentFile + " at line " + currentLine);
              System.err.println ("    " + fields.length + " fields when 3 are expected; ignoring this line"); 
              return; }
            int cp = Integer.parseInt (fields [0].substring (2), 16);
            Property p = Property.fromString (fields [1]);
            unihanCodePoint [cp] = true;
            repertoire.put (cp, cp, Property.type, "char");
            if (! numericValsOnly) {
              repertoire.put (cp, cp, p, fields [2]); }
            if (Property.kAccountingNumeric == p
                || Property.kPrimaryNumeric == p 
                || Property.kOtherNumeric == p) {
              // the numeric properties may have already been set from  
              // UnicodeData.txt, and we need to override them,
              // hence the "putForced" rather than the the usual "put".
              repertoire.putForced (cp, cp, Property.nt, "Nu");
              repertoire.putForced (cp, cp, Property.nv, fields [2]); }}});
   
    if (! numericValuesOnly) {
      final Version vv = v;
      repertoire.putDefault (
          new DefaultSetter () {
            public void process (Group r) {
              // we know that each code point in Unihan is going to be its own range
              // because it must have a unique set of sources
              if (unihanCodePoint [r.firstCp]) {
                r.putDefault (Property.kCompatibilityVariant, "");
                r.putDefault (Property.kIRG_GSource, "");
                r.putDefault (Property.kIRG_TSource, "");
                r.putDefault (Property.kIRG_JSource, "");
                r.putDefault (Property.kIRG_KSource, "");
                r.putDefault (Property.kIRG_KPSource, "");
                r.putDefault (Property.kIRG_VSource, "");
                r.putDefault (Property.kIRG_HSource, "");
                r.putDefault (Property.kIRG_USource, "");
                
                if (vv.isAtLeast (Version.V5_2_0)) {
                  r.putDefault (Property.kIRG_MSource, ""); }

                if (vv.isAtLeast (Version.V13_0_0)) {
                  r.putDefault (Property.kIRG_UKSource, "");
                  r.putDefault (Property.kIRG_SSource, ""); }}}}); }
  }
  
  private void parseTangutSources (Version v, URL baseURL) throws Exception {
    final String currentFile = new URL (baseURL, "TangutSources.txt").toString ();

    if (v.isAtLeast (Version.V9_0_0)) {
      Parser.parseTabDelimitedFile (baseURL, "TangutSources.txt", "UTF-8",
                                    new Loader () {
        public void process (String[] fields) {
          if (fields.length != 3) {
            System.err.println ("@@@ Error in file " + currentFile + " at line " + currentLine);
            System.err.println ("    " + fields.length + " fields when 3 are expected; ignoring this line"); 
            return; }
          int cp = Integer.parseInt (fields [0].substring (2), 16);
          Property p = Property.fromString (fields [1]);
          repertoire.put (cp, cp, p, fields[2]); 
        }}); }
  }

  private void parseNushuSources (Version v, URL baseURL) throws Exception {
    final String currentFile = new URL (baseURL, "NushuSources.txt").toString ();

    if (v.isAtLeast (Version.V10_0_0)) {
      Parser.parseTabDelimitedFile (baseURL, "NushuSources.txt", "UTF-8",
                                    new Loader () {
        public void process (String[] fields) {
          if (fields.length != 3) {
            System.err.println ("@@@ Error in file " + currentFile + " at line " + currentLine);
            System.err.println ("    " + fields.length + " fields when 3 are expected; ignoring this line"); 
            return; }
          int cp = Integer.parseInt (fields [0].substring (2), 16);
          Property p = Property.fromString (fields [1]);
          repertoire.put (cp, cp, p, fields[2]); 
        }}); }
  }
  
  private String normalizeBlockName (String s) {
    return s.replaceAll  ("[_\\- ]", "").toUpperCase ();
  }
  
  private void parseBlocks (Version v, URL baseURL) throws Exception {
    final Map<String,String> longBlk2shortBlk = new TreeMap<String, String> ();
    
    if (v.isAtLeast (Version.V6_1_0)) {
      Parser.parseSemiDelimitedFile (baseURL, "PropertyValueAliases.txt", "US-ASCII",
          new Loader () {
        public void process (String[] fields) {
          if ("blk".equals (fields [0])) {
            longBlk2shortBlk.put (normalizeBlockName (fields [2]), fields [1]); }}}); } 

//    System.err.println ("collected " + longBlk2shortBlk.size () + " block names");
//    for (String s : longBlk2shortBlk.keySet ()) {
//      System.err.println ("    '" + s + "'    '" + longBlk2shortBlk.get (s) + "'"); }

    if (v.isAtLeast (Version.V3_1_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "Blocks.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
        public void process (int first, int last, String[] fields) {
          blocks.add (new Block (first, last, fields [1]));
          String v = longBlk2shortBlk.get (normalizeBlockName (fields [1]));
          if (v == null) {
            v = fields [1]; }
          repertoire.put (first, last, Property.blk, v); }}); }
        
    else if (v.isAtLeast (Version.V2_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "Blocks.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
        public void process (int first, int last, String[] fields) {
          blocks.add (new Block (first, Integer.parseInt (fields [1], 16), fields[2]));
          String v = longBlk2shortBlk.get (normalizeBlockName (fields [1]));
          if (v == null) {
            v = fields [1]; }
          repertoire.put (first, last, Property.blk, v);  }}); }
    
    repertoire.putDefault (Property.blk, longBlk2shortBlk.get (normalizeBlockName ("No_Block")));
  }
  
  private void parseNameAliases (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V5_0_0)) {
      final Version vv = v;
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "NameAliases.txt", 0, "US-ASCII",
          new LoaderWithCodePoints () {
        public void process (int first, int last, String[] fields) {
          Set<NameAlias> nameAliases = (Set<NameAlias>) repertoire.getObject (first, Property.nameAlias);
          if (nameAliases == null) {
            nameAliases = new TreeSet<NameAlias> ();
            repertoire.putObject (first, first, Property.nameAlias, nameAliases); }
          nameAliases.add (new NameAlias (fields [1],
              vv.isAtLeast (Version.V6_1_0) ? fields [2] : null)); }}); }
  }
    
  private void parseNamedSequences (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V4_1_0)) {  
      Parser.parseSemiDelimitedFile (baseURL, "NamedSequences.txt", "US-ASCII",
          new Loader () {
        public void process (String[] fields) {
          namedSequences.add (new NamedSequence (fields [0], fields [1])); }}); }
  }
    
  private void parseNamedSequencesProv (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V5_0_0)) {  
      Parser.parseSemiDelimitedFile (baseURL, "NamedSequencesProv.txt", "US-ASCII",
          new Loader () {
        public void process (String[] fields) {
          namedSequencesProv.add (new NamedSequence (fields [0], fields [1])); }}); }
  }
    
  private void parseNormalizationCorrections (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V3_2_0)) {     
      Parser.parseSemiDelimitedFile (baseURL, "NormalizationCorrections.txt", "US-ASCII",
          new Loader () {
        public void process (String[] fields) {
          normalizationCorrections.add (new NormalizationCorrection (fields [0], fields [1], fields [2], fields [3])); }}); }
  }
  
  private void parseStandardizedVariants (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V4_0_0)) {
      Parser.parseSemiDelimitedFile (baseURL, "StandardizedVariants.txt",  "US-ASCII",
        new Loader () {
          public void process (String[] fields) {
            standardizedVariants.add (new StandardizedVariant (fields [0], fields [1], fields [2])); }}); }
  }
  
  private void parseEmojiVariationSequences (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V13_0_0)) {
      Parser.parseSemiDelimitedFile (baseURL, "emoji/emoji-variation-sequences.txt",  "US-ASCII",
        new Loader () {
          public void process (String[] fields) {
            standardizedVariants.add (new StandardizedVariant (fields [0], fields [1], fields [2])); }}); }
  }
  
  private void parseEmojiSources (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V6_0_0)) {
      Parser.parseSemiDelimitedFile (baseURL, "EmojiSources.txt", "US-ASCII",
        new Loader () {
          public void process (String[] fields) {
            emojiSources.add (new EmojiSource (fields [0], fields [1], fields [2], fields [3])); }}); }
  }

  private void parseVerticalOrientation (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V10_0_0)) {
      Parser.parseSemiDelimitedFileWithCodePoints (baseURL, "VerticalOrientation.txt", 0, "US-ASCII",
        new LoaderWithCodePoints () {
          public void process (int firstCp, int lastCp, String[] fields) {
            repertoire.put (firstCp, lastCp, Property.vo, fields [1]); }});
      
      repertoire.putDefault (Property.vo, "R"); }
  }

  private void parseEmojiData (Version v, URL baseURL) throws Exception {
    if (v.isAtLeast (Version.V13_0_0)) {
      parseBinaryPropertyFile (v, baseURL, "emoji/emoji-data.txt", "US-ASCII"); }
  }

  public Ucd fromUCD (Version v, URL baseURL, Set<UcdFile> files) throws Exception {
    
    description = "Unicode " + v;
    repertoire = new Repertoire ();
    blocks = new Blocks ();
    namedSequences = new NamedSequences (false);
    namedSequencesProv = new NamedSequences (true);
    normalizationCorrections = new NormalizationCorrections ();
    standardizedVariants = new StandardizedVariants ();
    cjkRadicals = new CJKRadicals ();
    emojiSources = new EmojiSources ();
          
    if (files.contains (UcdFile.UnicodeData)) {
      parseUnicodeData (v, baseURL); }
    if (files.contains (UcdFile.DerivedBidiClass)) {
      parseDerivedBidiClass (v, baseURL); }
    if (files.contains (UcdFile.ArabicShaping)) {
      parseArabicShaping (v, baseURL); }
    if (files.contains (UcdFile.BidiBrackets)) {
      parseBidiBrackets (v, baseURL); }
    if (files.contains (UcdFile.BidiMirroring)) {
      parseBidiMirroring (v, baseURL); }
    if (files.contains (UcdFile.CaseFolding)) {
      parseCaseFolding (v, baseURL); }
    if (files.contains (UcdFile.CompositionExclusions)) {
      parseCompositionExclusions (v, baseURL); }
    if (files.contains (UcdFile.DerivedAge)) {
      parseDerivedAge (v, baseURL); }
    if (files.contains (UcdFile.EastAsianWidth)) {
      parseEastAsianWidth (v, baseURL); }
    if (files.contains (UcdFile.HangulSyllableType)) {
      parseHangulSyllableType (v, baseURL); }
    if (files.contains (UcdFile.IndicSyllabicCategory)) {
      parseIndicSyllabicCategory (v, baseURL); }
    if (files.contains (UcdFile.IndicMatraCategory)) {
      parseIndicMatraCategory (v, baseURL); }
    if (files.contains (UcdFile.IndicPositionalCategory)) {
      parseIndicPositionalCategory (v, baseURL); }
    if (files.contains (UcdFile.Jamo)) {
      parseJamo (v, baseURL); }
    if (files.contains (UcdFile.LineBreak)) {
      parseLineBreak (v, baseURL); }
    if (files.contains (UcdFile.PropList)) {
      parsePropList (v, baseURL); }
    if (files.contains (UcdFile.Scripts)) {
      parseScripts (v, baseURL); }
    if (files.contains (UcdFile.ScriptExtensions)) {
      parseScriptExtensions (v, baseURL); }
    if (files.contains (UcdFile.SpecialCasing)) {
      parseSpecialCasing (v, baseURL); }
    if (files.contains (UcdFile.DerivedCoreProperties)) {
      parseDerivedCoreProperties (v, baseURL); }
    if (files.contains (UcdFile.DerivedNormalizationProps)) {
      parseDerivedNormalizationProperties (v, baseURL); }
    
    if (files.contains (UcdFile.GraphemeBreakProperty)) {
      parseGraphemeBreak (v, baseURL); }
    if (files.contains (UcdFile.WordBreakProperty)) {
      parseWordBreak (v, baseURL); }
    if (files.contains (UcdFile.SentenceBreakProperty)) {
      parseSentenceBreak (v, baseURL); }
    
    if (files.contains (UcdFile.EquivalentUnifiedIdeograph)) {
      parseEquivalentUnifiedIdeograph (v, baseURL); }

    if (files.contains (UcdFile.Unihan)) {
      parseUnihan (v, baseURL, false); }
    if (files.contains (UcdFile.UnihanNumeric)) {
      parseUnihan (v, baseURL, true); }
      
    if (files.contains (UcdFile.Blocks)) {
      parseBlocks (v, baseURL); }
    if (files.contains (UcdFile.NamedSequences)) {
      parseNamedSequences (v, baseURL); }
    if (files.contains (UcdFile.NamedSequencesProv)) {
      parseNamedSequencesProv (v, baseURL); }
    if (files.contains (UcdFile.NormalizationCorrections)) {
      parseNormalizationCorrections (v, baseURL); }
    if (files.contains (UcdFile.StandardizedVariants)) {
      parseStandardizedVariants (v, baseURL); }
    if (files.contains (UcdFile.CJKRadicalNumbers)) {
      parseCJKRadicals (v, baseURL); }
    if (files.contains (UcdFile.EmojiSources)) {
      parseEmojiSources (v, baseURL); }
    if (files.contains (UcdFile.TangutSources)) {
      parseTangutSources (v, baseURL); }
    if (files.contains (UcdFile.NushuSources)) {
      parseNushuSources (v, baseURL); }
    if (files.contains (UcdFile.NameAliases)) {
      parseNameAliases (v, baseURL); }
    
    if (files.contains (UcdFile.VerticalOrientation)) {
      parseVerticalOrientation (v, baseURL); }
 
    if (files.contains (UcdFile.EmojiData)) {
      parseEmojiData (v, baseURL); }
    if (files.contains (UcdFile.EmojiVariationSequences)) {
      parseEmojiVariationSequences (v, baseURL); }

    normalize ();
    
    return this;
  }
    
  //----------------------------------------------------------------------------

  /* if 'v' starts with "4", then convert the integer
   * starting at position 2 from decimal to four hexadecimal digits.
   */
  static String fixK4 (String v) {
    if (v.length() == 0) {
      return v; }
    
    if (v.charAt (0) == '4') {
      int val = Integer.parseInt (v.substring (2));
      String hv = Integer.toHexString (val).toUpperCase ();
      v = "4-";
      for (int i = hv.length (); i < 4; i++) {
        v += '0'; }
      v += hv; }
    
    return v;
  }
        
  static String cleanGSource (String s) {
//    if (s.startsWith ("G_G")) {
//      return s.substring (3); }
    if (s.startsWith ("G_")) {
      return s.substring (2); }
    else if (s.startsWith ("G")) {
      return s.substring (1); }
    return s;
  }
  
  static String cleanTSource (String s) {
    if (s.startsWith ("T")) {
      return s.substring (1); }
    return s;
  }
  
  static String cleanJSource (String s) {
    if (s.startsWith ("J_ARIB")) {
      return s.substring (2); }
    if (s.startsWith ("J")) {
      return s.substring (1); }
    return s;
  }
  
  static String cleanKSource (String s, boolean doFixK4) {
    if (s.startsWith ("K")) {
      s = s.substring (1); }
    if (doFixK4) {
      return fixK4 (s); }
    
    return s;
  }
  
  static String cleanVSource (String s) {
    if (s.startsWith ("V")) {
      return s.substring (1); }
    return s;
  }
  
  static String cleanHSource (String s) {
    if (s.startsWith ("H-")) {
      return s.substring (2); }
    return s;
  }
  
  static String cleanUSource (String s) {
    if (s.startsWith ("U0-")) {
      return "U+" + s.substring (3); }
    return s;
  }
  
  static String cleanKPSource (String s) {
    return s;
  }
  
  static String cleanMSource (String s) {
    return s;
  }
  
  public Ucd fromISO2001 (URL baseURL) throws Exception {
    
    description = "From ISO 10646:2001 at " + baseURL;
    repertoire = new Repertoire ();
    blocks = new Blocks ();
    namedSequences = new NamedSequences (false);
    namedSequencesProv = new NamedSequences (true);
    normalizationCorrections = new NormalizationCorrections ();

    Loader l =       new Loader () {
      public void process (String[] fields) {
        // UCODE:  1-5  OCTET   
        // GCODE:  6-12 OCTET
        // TCODE: 13-19 OCTET
        // JCODE: 20-26 OCTET
        // KCODE: 27-33 OCTET
        // VCODE: 34-40 OCTET
        // HCODE: 41-47 OCTET
        // KPCODE:48-55 OCTET

        int cp = Integer.parseInt (fields [0], 16);
        repertoire.put (cp, cp, Property.type, "char");
        repertoire.put (cp, cp, Property.kCompatibilityVariant, "");
        repertoire.put (cp, cp, Property.uideo, "Y");
        
        repertoire.put (cp, cp, Property.kIRG_GSource, cleanGSource (fields [1]));
        repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
        repertoire.put (cp, cp, Property.kIRG_JSource, cleanJSource (fields [3]));
        repertoire.put (cp, cp, Property.kIRG_KSource, cleanKSource (fields [4], true));
        repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [7]));
        repertoire.put (cp, cp, Property.kIRG_VSource, cleanVSource (fields [5]));
        repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [6]));
        repertoire.put (cp, cp, Property.kIRG_USource, ""); }};
        
    Parser.parseTabularFileWithHeader (baseURL, "CJKUA_SR.TXT", "US-ASCII", new int[] {1, 6, 13, 20, 27, 34, 41, 48, 56},  l);
    Parser.parseTabularFileWithHeader (baseURL, "CJKB_SR.TXT", "US-ASCII", new int[] {1, 6, 13, 20, 27, 34, 41, 48, 56},  l);
    
    
    Parser.parseTabularFileWithHeader (baseURL, "CJKC0SR.TXT", "US-ASCII", new int[] {1, 7, 13, 21, 29, 37, 45},
      new Loader () {
        public void process (String[] fields) {
          // CJK Compatibility Ideograph Code Position: 01-06 OCTET   
          // CJK Unified Ideograph Code Position: 07-12 OCTET
          // Kanji J3 and J4 Sources: 13-20 OCTET
          // Hanja K0 source: 21-28 OCTET
          // Unicode U0 source: 29-36
          // Hanja KP1 source: 37-44 OCTET
          
          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "U+" + Ucd.toU (Integer.parseInt (fields [1], 16)));
          repertoire.put (cp, cp, Property.uideo, "N");
          
          repertoire.put (cp, cp, Property.kIRG_GSource, "");
          repertoire.put (cp, cp, Property.kIRG_TSource, "");
          repertoire.put (cp, cp, Property.kIRG_JSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_KSource, cleanHSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [5])); 
          repertoire.put (cp, cp, Property.kIRG_VSource, ""); 
          repertoire.put (cp, cp, Property.kIRG_HSource, ""); 
          repertoire.put (cp, cp, Property.kIRG_USource, cleanKPSource (fields [4])); }}); 

    Parser.parseTabularFileWithHeader (baseURL, "CJKC_SR.TXT", "US-ASCII", new int[] {1, 7, 13, 21, 28, 36},
      new Loader () {
        public void process (String[] fields) {
          // CJK Compatibility Ideograph Code Position: 01-06 OCTET   
          // CJK Unified Ideograph Code Position: 07-12 OCTET
          // Hanzi T3, T4, T5, T6, T7 and TF sources: 13-20 OCTET
          // Hanzi H source: 21-27 OCTET
          // Hanja KP1 source: 28-35 OCTET

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "U+" + Ucd.toU (Integer.parseInt (fields [1], 16)));
          repertoire.put (cp, cp, Property.uideo, "N");
         
          repertoire.put (cp, cp, Property.kIRG_GSource, "");
          repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_JSource, "");
          repertoire.put (cp, cp, Property.kIRG_KSource, "");
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [4]));
          repertoire.put (cp, cp, Property.kIRG_VSource, "");
          repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_USource, ""); }}); 
            
    return this;
  }

  public Ucd fromISO2003 (URL baseURL) throws Exception {
    
    description = "From ISO 10646:2003 at " + baseURL;
    repertoire = new Repertoire ();
    blocks = new Blocks ();
    namedSequences = new NamedSequences (false);
    namedSequencesProv = new NamedSequences (true);
    normalizationCorrections = new NormalizationCorrections ();

    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKU_SR.TXT", "US-ASCII", 
      new Loader () {
        public void process (String[] fields) {
          // 1st field: CJK Unified Ideograph Code Position
          // 2nd field: Hanzi G sources
          // 3rd field: Hanzi T sources
          // 4th field: Kanji J sources
          // 5th field: Hanja K sources
          // 6th field: ChuNom V sources
          // 7th field: Hanzi H source
          // 8th field: Hanja KP sources

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "");
          repertoire.put (cp, cp, Property.uideo, "Y");
          
          repertoire.put (cp, cp, Property.kIRG_GSource, cleanGSource (fields [1]));
          repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_JSource, cleanJSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_KSource, cleanKSource (fields [4], false));
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [7]));
          repertoire.put (cp, cp, Property.kIRG_VSource, cleanVSource (fields [5]));
          repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [6]));
          repertoire.put (cp, cp, Property.kIRG_USource, ""); }});
    
    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKC_SR.TXT", "US-ASCII",
      new Loader () {
        public void process (String[] fields) {
          // 1st field: CJK Compatibility Ideograph Code Position   
          // 2nd field: CJK Unified Ideograph Corresponding Code Position
          // 3rd field: Hanzi T sources
          // 4th field: Hanzi H source
          // 5th field: Kanji J sources
          // 6th field: Hanja K source
          // 7th field: Unicode U source
          // 8th field: Hanja KP source

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "U+" + Ucd.toU (Integer.parseInt (fields [1], 16)));
          repertoire.put (cp, cp, Property.uideo, "N");
          
          repertoire.put (cp, cp, Property.kIRG_GSource, "");
          repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_JSource, cleanJSource (fields [4]));
          repertoire.put (cp, cp, Property.kIRG_KSource, cleanKSource (fields [5], false));
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [7]));
          repertoire.put (cp, cp, Property.kIRG_VSource, "");
          repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_USource, cleanUSource (fields [6])); }}); 
            
    return this;
  }

  public Ucd fromISO2003Amd1234 (URL baseURL, String label) throws Exception {
    
    description = "From " + label + " at " + baseURL;
    repertoire = new Repertoire ();
    blocks = new Blocks ();
    namedSequences = new NamedSequences (false);
    namedSequencesProv = new NamedSequences (true);
    normalizationCorrections = new NormalizationCorrections ();

    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKU_SR.TXT", "US-ASCII", 
      new Loader () {
        public void process (String[] fields) {
          // 1st field: CJK Unified Ideograph Code Position
          // 2nd field: Hanzi G sources
          // 3rd field: Hanzi T sources
          // 4th field: Kanji J sources
          // 5th field: Hanja K sources
          // 6th field: ChuNom V sources
          // 7th field: Hanzi H source
          // 8th field: Hanja KP sources
          // 9th field: Unicode U sources

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "");
          repertoire.put (cp, cp, Property.uideo, "Y");
         
          repertoire.put (cp, cp, Property.kIRG_GSource, cleanGSource (fields [1]));
          repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_JSource, cleanJSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_KSource, cleanKSource (fields [4], false));
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [7]));
          repertoire.put (cp, cp, Property.kIRG_VSource, cleanVSource (fields [5]));
          repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [6]));
          repertoire.put (cp, cp, Property.kIRG_USource, cleanUSource (fields [8])); }});
    
    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKC_SR.TXT", "US-ASCII",
      new Loader () {
        public void process (String[] fields) {
          // 1st field: CJK Compatibility Ideograph Code Position   
          // 2nd field: CJK Unified Ideograph Corresponding Code Position
          // 3rd field: Hanzi T sources
          // 4th field: Hanzi H source
          // 5th field: Kanji J sources
          // 6th field: Hanja K source
          // 7th field: Unicode U source
          // 8th field: Hanja KP source

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "U+" + Ucd.toU (Integer.parseInt (fields [1], 16)));
          repertoire.put (cp, cp, Property.uideo, "N");
          
          repertoire.put (cp, cp, Property.kIRG_GSource, "");
          repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_JSource, cleanJSource (fields [4]));
          repertoire.put (cp, cp, Property.kIRG_KSource, cleanKSource (fields [5], false));
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [7]));
          repertoire.put (cp, cp, Property.kIRG_VSource, "");
          repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_USource, cleanUSource (fields [6])); }}); 
    
    Parser.parseTabularFileWithHeader (baseURL, "IICORE.txt", "US-ASCII", 
                                       new int[] {1, 6, 9, 12, 15, 18, 21, 24, 27, 28}, 
      new Loader () {
       public void process (String[] fields) {
         // UCODE         1-5  BYTE
         // GSOURCE       6-8  BYTE
         // TSOURCE      9-11  BYTE
         // JSOURCE     12-14  BYTE
         // HSOURCE     15-17  BYTE
         // KSOURCE     18-20  BYTE
         // MSOURCE     21-23  BYTE
         // KPSOURCE    24-26  BYTE
         // Category       27  byte
         
         int cp = Integer.parseInt (fields [0], 16);
         
         repertoire.put (cp, cp, Property.kIICore, "2.1"); }});
        
    return this;
  }
        
  public Ucd fromISO2003Amd5(URL baseURL, String label) throws Exception {
    
    description = "From " + label + " at " + baseURL;
    repertoire = new Repertoire ();
    blocks = new Blocks ();
    namedSequences = new NamedSequences (false);
    namedSequencesProv = new NamedSequences (true);
    normalizationCorrections = new NormalizationCorrections ();

    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKU_SR.TXT", "US-ASCII", 
      new Loader () {
        public void process (String[] fields) {
          // 1st field: CJK Unified Ideograph Code Position
          // 2nd field: Hanzi G sources
          // 3rd field: Hanzi T sources
          // 4th field: Kanji J sources
          // 5th field: Hanja K sources
          // 6th field: ChuNom V sources
          // 7th field: Hanzi H source
          // 8th field: Hanja KP sources
          // 9th field: Unicode U sources
          // 10th field: Hanzi M source

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "");
          repertoire.put (cp, cp, Property.uideo, "Y");
          
          repertoire.put (cp, cp, Property.kIRG_GSource, cleanGSource (fields [1]));
          repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_JSource, cleanJSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_KSource, cleanKSource (fields [4], false));
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [7]));
          repertoire.put (cp, cp, Property.kIRG_VSource, cleanVSource (fields [5]));
          repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [6]));
          repertoire.put (cp, cp, Property.kIRG_USource, cleanUSource (fields [8]));
          repertoire.put (cp, cp, Property.kIRG_MSource, cleanMSource (fields [9])); }});
    
    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKC_SR.TXT", "US-ASCII",
      new Loader () {
        public void process (String[] fields) {
          // 1st field: CJK Compatibility Ideograph Code Position   
          // 2nd field: CJK Unified Ideograph Corresponding Code Position
          // 3rd field: Hanzi T sources
          // 4th field: Hanzi H source
          // 5th field: Kanji J sources
          // 6th field: Hanja K source
          // 7th field: Unicode U source
          // 8th field: Hanja KP source

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "U+" + Ucd.toU (Integer.parseInt (fields [1], 16)));
          repertoire.put (cp, cp, Property.uideo, "N");
          
          repertoire.put (cp, cp, Property.kIRG_GSource, "");
          repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_JSource, cleanJSource (fields [4]));
          repertoire.put (cp, cp, Property.kIRG_KSource, cleanKSource (fields [5], false));
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [7]));
          repertoire.put (cp, cp, Property.kIRG_VSource, "");
          repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_USource, cleanUSource (fields [6])); }}); 
    
    Parser.parseTabularFileWithHeader (baseURL, "IICORE.txt", "US-ASCII", 
                                       new int[] {1, 6, 9, 12, 15, 18, 21, 24, 27, 28}, 
      new Loader () {
       public void process (String[] fields) {
         // UCODE         1-5  BYTE
         // GSOURCE       6-8  BYTE
         // TSOURCE      9-11  BYTE
         // JSOURCE     12-14  BYTE
         // HSOURCE     15-17  BYTE
         // KSOURCE     18-20  BYTE
         // MSOURCE     21-23  BYTE
         // KPSOURCE    24-26  BYTE
         // Category       27  byte
         
         int cp = Integer.parseInt (fields [0], 16);
         
         repertoire.put (cp, cp, Property.kIICore, "2.1"); }});
        
    return this;
  }

  public Ucd fromISO2003Amd6(URL baseURL, String label) throws Exception {
    
    description = "From " + label + " at " + baseURL;
    repertoire = new Repertoire ();
    blocks = new Blocks ();
    namedSequences = new NamedSequences (false);
    namedSequencesProv = new NamedSequences (true);
    normalizationCorrections = new NormalizationCorrections ();

    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKU_SR.TXT", "US-ASCII", 
      new Loader () {
        public void process (String[] fields) {
//          1st field: CJK Unified Ideograph Code Position
//          2nd field: Hanzi G sources
//          3rd field: Hanzi T sources
//          4th field: Kanji J sources
//          5th field: Hanja K sources
//          6th field: ChuNom V sources
//          7th field: Hanzi H source
//          8th field: Hanja KP sources
//          9th field: Unicode U sources
//          10th field: Hanzi M source

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "");
          repertoire.put (cp, cp, Property.uideo, "Y");
           
          repertoire.put (cp, cp, Property.kIRG_GSource, cleanGSource (fields [1]));
          repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_JSource, cleanJSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_KSource, cleanKSource (fields [4], false));
          repertoire.put (cp, cp, Property.kIRG_VSource, cleanVSource (fields [5]));
          repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [6]));
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [7]));
          repertoire.put (cp, cp, Property.kIRG_USource, cleanUSource (fields [8]));
          repertoire.put (cp, cp, Property.kIRG_MSource, cleanMSource (fields [9])); }});
    
    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKC_SR.TXT", "US-ASCII",
      new Loader () {
        public void process (String[] fields) {
          // 1st field: CJK Compatibility Ideograph Code Position   
          // 2nd field: CJK Unified Ideograph Corresponding Code Position
          // 3rd field: Hanzi T sources
          // 4th field: Hanzi H source
          // 5th field: Kanji J sources
          // 6th field: Hanja K source
          // 7th field: Unicode U source
          // 8th field: Hanja KP source

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "U+" + Ucd.toU (Integer.parseInt (fields [1], 16)));
          repertoire.put (cp, cp, Property.uideo, "N");
          
          repertoire.put (cp, cp, Property.kIRG_GSource, "");
          repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_JSource, cleanJSource (fields [4]));
          repertoire.put (cp, cp, Property.kIRG_KSource, cleanKSource (fields [5], false));
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [7]));
          repertoire.put (cp, cp, Property.kIRG_VSource, "");
          repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_USource, cleanUSource (fields [6])); 
          repertoire.put (cp, cp, Property.kIRG_MSource, ""); }}); 
    
    Parser.parseTabularFileWithHeader (baseURL, "IICORE.txt", "US-ASCII", 
                                       new int[] {1, 6, 9, 12, 15, 18, 21, 24, 27, 28}, 
      new Loader () {
       public void process (String[] fields) {
         // UCODE         1-5  BYTE
         // GSOURCE       6-8  BYTE
         // TSOURCE      9-11  BYTE
         // JSOURCE     12-14  BYTE
         // HSOURCE     15-17  BYTE
         // KSOURCE     18-20  BYTE
         // MSOURCE     21-23  BYTE
         // KPSOURCE    24-26  BYTE
         // Category       27  byte
         
         int cp = Integer.parseInt (fields [0], 16);
         
         repertoire.put (cp, cp, Property.kIICore, "2.1"); }});
        
    return this;
  }

  public Ucd fromISO2003Amd8(URL baseURL, String label) throws Exception {
    
    description = "From " + label + " at " + baseURL;
    repertoire = new Repertoire ();
    blocks = new Blocks ();
    namedSequences = new NamedSequences (false);
    namedSequencesProv = new NamedSequences (true);
    normalizationCorrections = new NormalizationCorrections ();

    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKU_SR.TXT", "US-ASCII", 
      new Loader () {
        public void process (String[] fields) {
//          1st field: CJK Unified Ideograph Code Position
//          2nd field; Radical Stroke index
//          3rd field: Hanzi G sources
//          4th field: Hanzi T sources
//          5th field: Kanji J sources
//          6th field: Hanja K sources
//          7th field: ChuNom V sources
//          8th field: Hanzi H source
//          9th field: Hanja KP sources
//          10th field: Unicode U sources
//          11th field: Unicode M source

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "");
          repertoire.put (cp, cp, Property.uideo, "Y");
           
          repertoire.put (cp, cp, Property.kIRG_RSIndex, fields[1]);
          
          repertoire.put (cp, cp, Property.kIRG_GSource, fields [2]);
          repertoire.put (cp, cp, Property.kIRG_TSource, fields [3]);
          repertoire.put (cp, cp, Property.kIRG_JSource, fields [4]);
          repertoire.put (cp, cp, Property.kIRG_KSource, fields [5]);
          repertoire.put (cp, cp, Property.kIRG_VSource, fields [6]);
          repertoire.put (cp, cp, Property.kIRG_HSource, fields [7]);
          repertoire.put (cp, cp, Property.kIRG_KPSource, fields [8]);
          repertoire.put (cp, cp, Property.kIRG_USource, fields [9]);
          repertoire.put (cp, cp, Property.kIRG_MSource, fields [10]); }});
    
    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKC_SR.TXT", "US-ASCII",
      new Loader () {
        public void process (String[] fields) {
          // 1st field: CJK Compatibility Ideograph Code Position   
          // 2nd field: CJK Unified Ideograph Corresponding Code Position
          // 3rd field: Hanzi T sources
          // 4th field: Hanzi H source
          // 5th field: Kanji J sources
          // 6th field: Hanja K source
          // 7th field: Unicode U source
          // 8th field: Hanja KP source

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "U+" + Ucd.toU (Integer.parseInt (fields [1], 16)));
          repertoire.put (cp, cp, Property.uideo, "N");
          
          repertoire.put (cp, cp, Property.kIRG_GSource, "");
          repertoire.put (cp, cp, Property.kIRG_TSource, cleanTSource (fields [2]));
          repertoire.put (cp, cp, Property.kIRG_JSource, cleanJSource (fields [4]));
          repertoire.put (cp, cp, Property.kIRG_KSource, cleanKSource (fields [5], false));
          repertoire.put (cp, cp, Property.kIRG_KPSource, cleanKPSource (fields [7]));
          repertoire.put (cp, cp, Property.kIRG_VSource, "");
          repertoire.put (cp, cp, Property.kIRG_HSource, cleanHSource (fields [3]));
          repertoire.put (cp, cp, Property.kIRG_USource, cleanUSource (fields [6])); 
          repertoire.put (cp, cp, Property.kIRG_MSource, ""); }}); 
    
    Parser.parseTabularFileWithHeader (baseURL, "IICORE.txt", "US-ASCII", 
                                       new int[] {1, 6, 9, 12, 15, 18, 21, 24, 27, 28}, 
      new Loader () {
       public void process (String[] fields) {
         // UCODE         1-5  BYTE
         // GSOURCE       6-8  BYTE
         // TSOURCE      9-11  BYTE
         // JSOURCE     12-14  BYTE
         // HSOURCE     15-17  BYTE
         // KSOURCE     18-20  BYTE
         // MSOURCE     21-23  BYTE
         // KPSOURCE    24-26  BYTE
         // Category       27  byte
         
         int cp = Integer.parseInt (fields [0], 16);
         
         repertoire.put (cp, cp, Property.kIICore, "2.1"); }});
        
    return this;
  }

  public Ucd fromISO2011 (URL baseURL, String label) throws Exception {
    
    description = "From " + label + " at " + baseURL;
    repertoire = new Repertoire ();
    blocks = new Blocks ();
    namedSequences = new NamedSequences (false);
    namedSequencesProv = new NamedSequences (true);
    normalizationCorrections = new NormalizationCorrections ();

    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKU_SR.TXT", "US-ASCII", 
      new Loader () {
        public void process (String[] fields) {
//          1st field: CJK Unified Ideograph Code Position
//          2nd field; Radical Stroke index
//          3rd field: Hanzi G sources
//          4th field: Hanzi T sources
//          5th field: Kanji J sources
//          6th field: Hanja K sources
//          7th field: ChuNom V sources
//          8th field: Hanzi H source
//          9th field: Hanja KP sources
//          10th field: Unicode U sources
//          11th field: Unicode M source

          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "");
          repertoire.put (cp, cp, Property.uideo, "Y");
           
          repertoire.put (cp, cp, Property.kIRG_RSIndex, fields[1]);
          
          repertoire.put (cp, cp, Property.kIRG_GSource, fields [2]);
          repertoire.put (cp, cp, Property.kIRG_TSource, fields [3]);
          repertoire.put (cp, cp, Property.kIRG_JSource, fields [4]);
          repertoire.put (cp, cp, Property.kIRG_KSource, fields [5]);
          repertoire.put (cp, cp, Property.kIRG_VSource, fields [6]);
          repertoire.put (cp, cp, Property.kIRG_HSource, fields [7]);
          repertoire.put (cp, cp, Property.kIRG_KPSource, fields [8]);
          repertoire.put (cp, cp, Property.kIRG_USource, fields [9]);
          repertoire.put (cp, cp, Property.kIRG_MSource, fields [10]); }});
    
    Parser.parseSemiDelimitedFileWithHeader (baseURL, "CJKC_SR.TXT", "US-ASCII",
      new Loader () {
        public void process (String[] fields) {
//          1st field: CJK Compatibility Ideograph Code Position   
//          2nd field: CJK Unified Ideograph Corresponding Code Position
//          3rd field; Radical Stroke index
//          4th field: Hanzi T sources
//          5th field: Hanzi H source
//          6th field: Kanji J sources
//          7th field: Hanja K source
//          8th field: Unicode U source
//          9th field: Hanja KP source


          int cp = Integer.parseInt (fields [0], 16);
          repertoire.put (cp, cp, Property.type, "char");
          repertoire.put (cp, cp, Property.kCompatibilityVariant, "U+" + Ucd.toU (Integer.parseInt (fields [1], 16)));
          repertoire.put (cp, cp, Property.uideo, "N");

          repertoire.put (cp, cp, Property.kIRG_RSIndex, fields[2]);

          repertoire.put (cp, cp, Property.kIRG_GSource, "");
          repertoire.put (cp, cp, Property.kIRG_TSource, fields [3]);
          repertoire.put (cp, cp, Property.kIRG_JSource, fields [5]);
          repertoire.put (cp, cp, Property.kIRG_KSource, fields [6]);
          repertoire.put (cp, cp, Property.kIRG_KPSource, fields [8]);
          repertoire.put (cp, cp, Property.kIRG_VSource, "");
          repertoire.put (cp, cp, Property.kIRG_HSource, fields [4]);
          repertoire.put (cp, cp, Property.kIRG_USource, fields [7]); 
          repertoire.put (cp, cp, Property.kIRG_MSource, ""); }}); 
    
    Parser.parseTabularFileWithHeader (baseURL, "IICORE.txt", "US-ASCII", 
                                       new int[] {1, 6, 9, 12, 15, 18, 21, 24, 27, 28}, 
      new Loader () {
       public void process (String[] fields) {
         // UCODE         1-5  BYTE
         // GSOURCE       6-8  BYTE
         // TSOURCE      9-11  BYTE
         // JSOURCE     12-14  BYTE
         // HSOURCE     15-17  BYTE
         // KSOURCE     18-20  BYTE
         // MSOURCE     21-23  BYTE
         // KPSOURCE    24-26  BYTE
         // Category       27  byte
         
         int cp = Integer.parseInt (fields [0], 16);
         
         repertoire.put (cp, cp, Property.kIICore, "2.1"); }});
        
    return this;
  }
  
  public Ucd fromISO (URL baseURL, String version) throws Exception {
    if ("iso2001".equals (version)) {
      return fromISO2001 (baseURL); }
    if ("iso2003".equals (version)) {
      return fromISO2003 (baseURL); }
    if ("iso2003amd1".equals (version)) {
      return fromISO2003Amd1234 (baseURL, "ISO 10646:2003 Amendment 1"); }
    if ("iso2003amd2".equals (version)) {
      return fromISO2003Amd1234 (baseURL, "ISO 10646:2003 Amendment 2"); }
    if ("iso2003amd3".equals (version)) {
      return fromISO2003Amd1234 (baseURL, "ISO 10646:2003 Amendment 3"); }
    if ("iso2003amd4".equals (version)) {
      return fromISO2003Amd1234 (baseURL, "ISO 10646:2003 Amendment 4"); }
    if ("iso2003amd5".equals (version)) {
      return fromISO2003Amd5 (baseURL, "ISO 10646:2003 Amendment 5"); }
    if ("iso2003amd6".equals (version)) {
      return fromISO2003Amd6 (baseURL, "ISO 10646:2003 Amendment 6"); }
    if ("iso2003amd8".equals (version)) {
      return fromISO2003Amd8 (baseURL, "ISO 10646:2003 Amendment 8"); }
    if ("iso2011".equals (version)) {
      return fromISO2011 (baseURL, "ISO 10646:2010"); }
    System.err.println ("Unsupported iso version: " + version);
    return null;
  }
  

  //----------------------------------------------------------------------------
  public class XMLHandler extends DefaultHandler {
    HashMap<Property, String> groupProperties; 
    String s;
    NamedSequences currentNamedSequences;
    Group currentGroup = null;

    public void startElement (String uri, String qname, String localname, Attributes at) {
    
      if ("description".equals (qname)) {
        s = new String (); }
      
      else if ("repertoire".equals (qname)) {
        repertoire = new Repertoire (); }
      
      else if ("group".equals (qname)) {
        groupProperties = new HashMap<Property, String> (); 
        for (int i = 0; i < at.getLength (); i++) {
          groupProperties.put (Property.fromString (at.getLocalName (i)), at.getValue (i)); }}
      
      else if ("char".equals (qname) 
            || "reserved".equals (qname) 
            || "surrogate".equals (qname)
            || "noncharacter".equals (qname)
            || "code-point".equals (qname)) {       
        currentGroup = repertoire.rangeFromXML (qname, at, groupProperties); }
           
      else if ("name-alias".equals (qname)) {
        Set<NameAlias> nameAliases;
        Object o = currentGroup.getObject (Property.nameAlias);
        if (o == null) {
          nameAliases = new TreeSet<NameAlias> ();
          currentGroup.putObject (Property.nameAlias, nameAliases); }
        else {
          nameAliases = (Set<NameAlias>) o; }
        nameAliases.add (NameAlias.fromXML (at)); }
      
      else if ("named-sequences".equals (qname)) {
        namedSequences = new NamedSequences (false); 
        namedSequences.fromXML (qname, at); 
        currentNamedSequences = namedSequences; }
      
      else if ("provisional-named-sequences".equals (qname)) {
        namedSequencesProv = new NamedSequences (true); 
        namedSequencesProv.fromXML (qname, at);
        currentNamedSequences = namedSequencesProv; }
      
      else if ("named-sequence".equals (qname)) {
        currentNamedSequences.fromXML (qname, at); }
      
      else if ("blocks".equals (qname)) {
        blocks = new Blocks ();
        blocks.fromXML (qname, at); }

      else if ("block".equals (qname)) {
        blocks.fromXML (qname, at); }
      
      else if ("normalization-corrections".equals (qname)) {
        normalizationCorrections = new NormalizationCorrections ();
        normalizationCorrections.fromXML (qname, at); }
      
      else if ("normalization-correction".equals (qname)) {
        normalizationCorrections.fromXML (qname, at); }
      
      else if ("standardized-variants".equals (qname)) {
        standardizedVariants = new StandardizedVariants ();
        standardizedVariants.fromXML (qname, at); }

      else if ("standardized-variant".equals (qname)) {
        standardizedVariants.fromXML (qname, at); }    
      
      else if ("cjk-radicals".equals (qname)) {
        cjkRadicals = new CJKRadicals ();
        cjkRadicals.fromXML (qname, at); }

      else if ("cjk-radical".equals (qname)) {
        cjkRadicals.fromXML (qname, at); }

      else if ("emoji-sources".equals (qname)) {
        emojiSources = new EmojiSources ();
        emojiSources.fromXML (qname, at); }
      
      else if ("emoji-source".equals (qname)) {
        emojiSources.fromXML (qname, at); }
      
//      else if ("name-aliases".equals (qname)) {
//        nameAliases = new NameAliases ();
//        nameAliases.fromXML (qname, at); }
//      
//      else if ("name-alias".equals (qname)) {
//        nameAliases.fromXML (qname, at); }
}
    
    public void characters (char[] chars, int start, int len) {
      if (s != null) {
        s += (new String (chars, start, len)); }
    }


    public void endElement (String uri, String qname, String localname) {
      if ("group".equals (qname)) {
        groupProperties = null; }
      
      else if ("description".equals (qname)) {
        description = s; 
        s = null; }
    }
  }
  
  public Ucd fromXML (URL src) throws Exception {
    XMLHandler handler = new XMLHandler ();
    
    SAXParserFactory spf = SAXParserFactory.newInstance ();
    spf.setNamespaceAware (true);
    spf.setValidating (false);
    SAXParser sp = spf.newSAXParser ();
//    System.err.println ("interning = " + sp.getXMLReader().getFeature( "http://xml.org/sax/features/string-interning"));
//    sp.getXMLReader().setFeature ("http://xml.org/sax/features/string-interning", false);
    sp.parse (new InputSource (src.openStream ()), handler);
    
    normalize ();
    
    return this;
  }
    
  //----------------------------------------------------------------------------
    
  public void toXML (File file, Repertoire groups) throws Exception {
    TransformerFactory tfactory = TransformerFactory.newInstance ();
    
    if (tfactory.getFeature (SAXSource.FEATURE)) {
      SAXTransformerFactory sfactory = (SAXTransformerFactory) tfactory;
     
      // no transform; we just want a serializer
      TransformerHandler ch = sfactory.newTransformerHandler ();
      
      FileOutputStream f = new FileOutputStream (file);
      ch.setResult (new StreamResult (f));
      
      Transformer transformer = ch.getTransformer ();
      transformer.setOutputProperty (OutputKeys.INDENT, "yes");
      transformer.setOutputProperty (OutputKeys.STANDALONE, "yes");
      transformer.setOutputProperty (OutputKeys.METHOD, "xml");

      ch.startDocument (); {
        
        char[] c = "\n\n".toCharArray ();
        ch.characters (c, 0, c.length);
        c = " \u00A9 2019 Unicode\u00AE, Inc. ".toCharArray ();
        ch.comment (c, 0, c.length);
        c = "\n".toCharArray ();
        ch.characters (c, 0, c.length);
        c = " For terms of use, see http://www.unicode.org/terms_of_use.html ".toCharArray ();
        ch.comment (c, 0, c.length);
        c = "\n\n".toCharArray ();
        ch.characters (c, 0, c.length);

        AttributesImpl at = new AttributesImpl ();
        ch.startElement (NAMESPACE, "ucd", "ucd", at); {

          if (description != null) {
            ch.startElement (NAMESPACE, "description", "description", at); {
              char[] d = description.toCharArray ();
              ch.characters (d, 0, d.length);
              ch.endElement (NAMESPACE, "description", "description"); }}

          if (repertoire != null) {
            AttributesImpl atb = new AttributesImpl ();
            repertoire.toXML (ch, "repertoire", atb, 0, groups); }
          
          if (blocks != null) {
            AttributesImpl atb = new AttributesImpl ();
            blocks.toXML (ch, "blocks", atb); }
          
          if (namedSequences != null) {
            AttributesImpl atb = new AttributesImpl ();
            namedSequences.toXML (ch, "named-sequences", atb); }
          
          if (namedSequencesProv != null) {
            AttributesImpl atb = new AttributesImpl ();
            namedSequencesProv.toXML (ch, "provisional-named-sequences", atb); }

          if (normalizationCorrections != null) {
            AttributesImpl atb = new AttributesImpl ();
            normalizationCorrections.toXML (ch, "normalization-corrections", atb); }
          
          if (standardizedVariants != null) {
            AttributesImpl atb = new AttributesImpl ();
            standardizedVariants.toXML (ch, "standardized-variants", atb); }
          
          if (cjkRadicals != null) {
            AttributesImpl atb = new AttributesImpl ();
            cjkRadicals.toXML (ch, "cjk-radicals", atb); }
          
          if (emojiSources != null) {
            AttributesImpl atb = new AttributesImpl ();
            emojiSources.toXML (ch, "emoji-sources", atb); }
                   
          ch.endElement (NAMESPACE, "ucd", "ucd"); }
        
        ch.endDocument (); }
      f.close (); }
    
    else {
      System.err.println ("SAXSource.FEATURE not supported"); }
  }
  
  //----------------------------------------------------------------------------
  
  public void addDerivedProperties (Property p) {
    // Alphabetic: Other_Alphabetic + Lu + Ll + Lt + Lm + Lo + Nl
    
    for (Group r : repertoire) {
      String gc = r.get (Property.gc);
      String oalpha = r.get (Property.oalpha);
      
      if ("Y".equals (oalpha) || "Lu".equals (gc) || "Ll".equals (gc) || "Lt".equals (gc) || "Lm".equals (gc) || "Lo".equals (gc) || "Nl".equals (gc)) {
        r.putDefault (Property.alpha, "Y"); }
      else {
        r.putDefault (Property.alpha, "N"); }}
  }
  
  //---------------------------------------------------------------------------
  public void remove (Property p) {
    repertoire.remove (p);
    description += "; removed " + p;
  }
  
  public void diff (Ucd other, PrintStream out, int detailsLevel) {
    out.println ("comparing:");
    out.println ("  " + other.description);
    out.println ("");
    out.println ("with:");
    out.println ("  " + description);
    out.println ("");
    
    
    if (repertoire != null && other.repertoire != null) {
      repertoire.diff (other.repertoire, out, detailsLevel); }
    if (blocks != null && other.blocks != null) {
      blocks.diff (other.blocks, out, detailsLevel); }
    if (namedSequences != null && other.namedSequences != null) {
      namedSequences.diff (other.namedSequences, out, detailsLevel); }
    if (namedSequencesProv != null && other.namedSequencesProv != null) {
      namedSequencesProv.diff (other.namedSequencesProv, out, detailsLevel); }
    if (normalizationCorrections != null && other.normalizationCorrections != null) {
      normalizationCorrections.diff (other.normalizationCorrections, out, detailsLevel); }
    if (standardizedVariants != null && other.standardizedVariants != null) {
      standardizedVariants.diff (other.standardizedVariants, out, detailsLevel); }
    if (cjkRadicals != null && other.cjkRadicals != null) {
      cjkRadicals.diff (other.cjkRadicals, out, detailsLevel); }
    if (emojiSources != null && other.emojiSources != null) {
      emojiSources.diff (other.emojiSources, out, detailsLevel); }
     
    out.println ("");
    out.println ("============================================= end of report");
  }

  public Set<String> getPropertyValues (Property p) {
    return repertoire.getPropertyValues (p);
  }
  //----------------------------------------------------------------------------
  
  public Repertoire group () {
    Repertoire groups = new Repertoire ();
    
    for (Block b : blocks.byFirstCp.values ()) {
      // each block beginning and end
      groups.createBoundaryBefore (b.first);
      groups.createBoundaryBefore (b.last + 1); }
    
    // C0 controls / printable characters
    groups.createBoundaryBefore (0x20);

    // C1 controls / printable characters
    groups.createBoundaryBefore (0xa0);
    
    //hangul jamos choseon / jungseon
    groups.createBoundaryBefore (0x1160); 
    //hangul jamos jungseon / jongseong
    groups.createBoundaryBefore (0x11A8);

    // regional indicators
    groups.createBoundaryBefore (0x1F1E6);
    repertoire.group (groups);
    
    return groups;
  }
  
  public void internalStats (PrintStream out) {
    out.println ("Internal statistics for " + description);
    repertoire.internalStats (out);
    blocks.internalStats (out);
    namedSequences.internalStats (out);
    namedSequencesProv.internalStats (out);
    normalizationCorrections.internalStats (out);
    standardizedVariants.internalStats (out);
    cjkRadicals.internalStats (out);
    emojiSources.internalStats (out);
  }
  
  public static long getActiveMemory () {
    Runtime r = Runtime.getRuntime ();
    return (r.totalMemory() - r.freeMemory());
  }
    
  public static void usage () {
    System.out.println ("Manipulation of  UCD data.");
    System.out.println ("");
    System.out.println ("This programs lets you load UCD representations from disk");
    System.out.println ("in a number of formats (released UCD, XML, ISO),");
    System.out.println ("manipulate them, write them to disk and compare them.");
    System.out.println ("");
    System.out.println ("Each argument is one of the following commands:");
    System.out.println ("");    
    System.out.println ("<id> = load (<dir>, <major>.<minor>.<dot> [, includeunihan])");
    System.out.println ("   <dir> contains a UCD in the released format (UnicodeData.txt, etc)");
    System.out.println ("   <major>, <minor>, and <dot> are the version number of this release;");
    System.out.println ("     these are used to determine exact set of files and their format.");
    System.out.println ("   includeunihan, if present, will cause Unihan.txt to be loaded");
    System.out.println ("   the resulting internal representation is identified by <id>");
    System.out.println ("");
    
    System.out.println ("<id> = load (<file>)");
    System.out.println ("   <file> contains an XML representation of a UCD, which");
    System.out.println ("   loaded as <id>");
    System.out.println ("");
    
    System.out.println ("to be continued ...");
  }
  
  static int verbosity = 0;

  public static void main (String[] args) throws Exception {
    final String s = "\\s*";
    final String idx = "([\\w]+)";
    final String file = "'([^']*)'";
    final String ucdVersion = "(\\d*)[.](\\d*)[.](\\d*)";
    final String isoVersion = "([\\w]*)";
    final String detailsOption = "(?:," + s + "details" + s + "=" + s + "([0-9])" + s + ")?";
    final String groupedOption = "(," + s + "grouped" + s + ")?";
    final String ucdfilesOption = "(?:," + s + "((?:include)|(?:exclude))" + s + "=" + s + "\\(" + s + "(" + idx + "(?:" + s + "," + s + idx + ")*)" + s + "\\)" +  s + ")??";
    
    final Pattern verbosityPattern = Pattern.compile ("verbosity" + s + "=" + s + "([0-9]*)");
    final Pattern loadUcdPattern = Pattern.compile (idx + s + "=" + s + "loaducd" + s + "\\(" + s + file + s + "," + s + ucdVersion + s + ucdfilesOption + s + "\\)");
    final Pattern loadISOPattern = Pattern.compile (idx + s + "=" + s + "loadiso" + s + "\\(" + s + file + s + "," + s + isoVersion + s + "\\)");
    final Pattern loadXMLPattern = Pattern.compile (idx + s + "=" + s + "loadxml" + s + "\\(" + s + file + s + "\\)");
    final Pattern toXMLPattern = Pattern.compile ("toxml" + s + "\\(" + s + idx + s + "," + s + file + s + groupedOption + s + "\\)");
    final Pattern dropPattern = Pattern.compile ("dropproperty" + s + "\\(" + s + idx + s + "," + s + idx + "\\)");
    final Pattern diffPattern = Pattern.compile ("diff" + s + "\\(" + s + idx + s + "," + s + idx + s + "," + s + file + s + detailsOption + s + "\\)");
    final Pattern deletePattern = Pattern.compile ("delete" + s + "\\(" + s + idx + s + "\\)");
    final Pattern enumeratedValuesPattern = Pattern.compile ("enumeratedvalues" + s + "\\(" + s + idx + s + "\\)");
    final Pattern usagePattern = Pattern.compile ("((?:-h)|(?:--help))");
    final Pattern internalStatsPattern = Pattern.compile ("internalstats" + s + "\\(" + s + idx + s + "\\)");

   
    Map<String, Ucd> ucds = new HashMap<String, Ucd> ();
    
    long commandStartTime = System.currentTimeMillis ();
    long commandStartMemory = getActiveMemory ();
    
    for (int i = 0; i < args.length; i++) {
     
      args [i] = args [i].trim ();
      
      Matcher m;
      
      if ((m = verbosityPattern.matcher (args [i])).matches()) {
        verbosity = Integer.parseInt (m.group (1)); 
        Parser.verbosity = verbosity;
        continue; }
      
      else if ((m = loadUcdPattern.matcher (args [i])).matches ()) {
        String id = m.group (1);
        String ucdDir = m.group (2);
        Version v = new Version (Integer.parseInt (m.group (3)),
                                 Integer.parseInt (m.group (4)),
                                 Integer.parseInt (m.group (5)));
        
        ucds.put (id, null);
        
        Set<UcdFile> files;
        String msg = "";
        if ("exclude".equals (m.group (6))) {
          msg += "all but: ";
          files = UcdFile.allFiles;
          for (String f : m.group (7).split(",")) {
            f = f.trim ();
            try {
              files.remove (UcdFile.valueOf (f));
              msg += f + " "; }
            catch (IllegalArgumentException e) {
              System.err.println ("@@@ '" + f + "' is not a UCD file; ignoring it"); }}}
        else if ("include".equals (m.group (6))) {
          msg += "only: ";
          files = new HashSet<UcdFile> ();
          for (String f : m.group (7).split(",")) {
            f = f.trim ();
            try {
              files.add (UcdFile.valueOf (f.trim ()));
              msg += f + " "; }
            catch (IllegalArgumentException e) {
              System.err.println ("@@@ '" + f + "' is not a UCD file; ignoring it"); }}}
        else {
          msg = "all files";
          files = UcdFile.allFiles; }
        
        if (verbosity >= 1 ) {
          System.out.println ("loading " + id + " from " + ucdDir + " as ucd version " + v + " (" + msg.trim () + ")"); }
        ucds.put (id, new Ucd ().fromUCD (v,
                                          new File (ucdDir).getAbsoluteFile ().toURI ().toURL (),
                                          files)); }
      
      else if ((m = loadISOPattern.matcher (args [i])).matches ()) {
        String id = m.group (1);
        String ucdDir = m.group (2);
        String v = m.group (3);
        if (verbosity >= 1 ) {
          System.out.println ("loading " + id + " from " + ucdDir + " as iso version " + v); }
        ucds.put (id, new Ucd ().fromISO (new File (ucdDir).getAbsoluteFile ().toURI ().toURL (), v)); }
      
      else if ((m = loadXMLPattern.matcher (args [i])).matches ()) {
        String id = m.group (1);
        URL url = new File (m.group (2)).getAbsoluteFile ().toURI ().toURL ();
        if (verbosity >= 1 ) {
          System.out.println ("loading " + id + " from " + m.group (2) + " as xml"); }
        ucds.put (id, new Ucd ().fromXML (url)); }
      
      else if ((m = toXMLPattern.matcher (args [i])).matches ()) { 
        String id = m.group (1);
        File f = new File (m.group (2));
        boolean grouped = (m.group (3) != null);
        if (verbosity >= 1 ) {
          System.out.println ("writing " + id + " to " + f + " (grouped=" + grouped + ")"); }
        Ucd ucd = ucds.get (id);
        ucd.toXML (f, grouped ? ucd.group () : null); }
      
      else if ((m = dropPattern.matcher (args [i])).matches ()) {
        String id = m.group (1);
        String p = m.group (2);
        if (verbosity >= 1 ) {
          System.out.println ("dropping '" + p + "' from " + id); }
        ucds.get (id).remove (Property.fromString (p)); }
      
      else if ((m = diffPattern.matcher (args [i])).matches ()) {
        String id1 = m.group (1);
        String id2 = m.group (2);
        File f = new File (m.group (3));
        PrintStream out = new PrintStream (f);
        int detailsLevel = m.group (4) == null ? 1 : Integer.parseInt (m.group (4));
        if (verbosity >= 1 ) {
          System.out.println ("diffing " + id1 + " and " + id2 + " to " + f + " (details=" + detailsLevel + ")"); }
        ucds.get (id2).diff (ucds.get (id1), out, detailsLevel); }
      
      else if ((m = internalStatsPattern.matcher (args [i])).matches ()) {
        String id = m.group (1);
        ucds.get (id).internalStats (System.out); }
            
      else if ((m = deletePattern.matcher (args [i])).matches ()) {
        String id = m.group (1);
        ucds.put (id, null); }
      
      else if ((m = enumeratedValuesPattern.matcher (args [i])).matches ()) {
        Ucd ucd = ucds.get (m.group (1));
        for (Property p : Property.values ()) {
          if (p.isEnumerated ()) {
            Set<String> values = ucd.getPropertyValues (p);
            System.out.println ("#  " + p.getBothNames ());
            System.out.println ("");
            for (String v : values) {
              System.out.println (p.getShortName () + "; " + v); }
            System.out.println (""); }}}

      else if ((m = usagePattern.matcher (args [i])).matches ()) {
        usage (); }
      
      else {
        System.err.println ("unrecognized command: '" + args[i] + "'");
        System.exit (1); }
      

      if (verbosity >= 2) {
        long commandDuration = System.currentTimeMillis () - commandStartTime;
        System.gc ();
        long activeMemoryNow = getActiveMemory ();
        
        System.out.println ("     " + (commandDuration / 1000) + " seconds, " + (activeMemoryNow / (1024 * 1024)) + " Mb ("
                            + (activeMemoryNow >= commandStartMemory ? "+" : "")
                            + ((activeMemoryNow - commandStartMemory) / (1024 * 1024))
                            + " Mb)");
        commandStartTime = System.currentTimeMillis ();
        commandStartMemory = activeMemoryNow; }}
    
    if (verbosity >= 2) {
      System.out.println ("-------"); }
  }
}
