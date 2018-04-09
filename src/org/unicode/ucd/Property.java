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

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Property {
  // When we generate the XML representation of a set of properties (as
  // attributes), we could simply enumerate the 'properties' map. However,
  // the attributes would appear either in a random order, or in
  // alphabetical order (if we use a SortedMap). Instead, we want 
  // some attribute like "cp" to be at the front, "na" not too far, while
  // the more esoteric attributes like "jg", "ea" or "sc" can go towards 
  // the end.
  // Our trick is to maintain an ArrayList of all the possible keys. We
  // insert the attributes we care about explicitly, and those will show
  // up first, in the order we inserted them. The remaining attributes
  // will show up in an order determined by when they were first seen, 
  // but that's ok.
  
  static private IdentityHashMap<String, Property> propertiesByName = new IdentityHashMap<String, Property> ();
  static private LinkedHashSet<Property> allProperties = new LinkedHashSet<Property> ();
  
  

  public static Property type                        = new Property ("type");
  
  public static Property age                         = new Property ("age", "Age", true);
  public static Property na                          = new Property ("na", "Name");
  public static Property jsn                         = new Property ("JSN", "Jamo_Short_Name", true);
  public static Property gc                          = new Property ("gc", "General_Category", true);
  public static Property ccc                         = new Property ("ccc", "Canonical_Combining_Class", true);
  public static Property dt                          = new Property ("dt", "Decomposition_Type", true);
  public static Property dm                          = new Property ("dm", "Decomposition_Mapping");
  public static Property nt                          = new Property ("nt", "Numeric_Type", true);
  public static Property nv                          = new Property ("nv", "Numeric_Value");
  public static Property bc                          = new Property ("bc", "Bidi_Class", true);
  public static Property bpt                         = new Property ("bpt", "Bidi_Paired_Bracket_Type", true);
  public static Property bpb                         = new Property ("bpb", "Bidi_Paired_Bracket");
  public static Property bidi_m                      = new Property ("Bidi_M", "Bidi_Mirrored", true);
  public static Property bmg                         = new Property ("bmg", "Bidi_Mirroring_Glyph");
  public static Property suc                         = new Property ("suc", "Simple_Uppercase_Mapping");
  public static Property slc                         = new Property ("slc", "Simple_Lowercase_Mapping");
  public static Property stc                         = new Property ("stc", "Simple_Titlecase_Mapping");
  public static Property uc                          = new Property ("uc", "Uppercase_Mapping");
  public static Property lc                          = new Property ("lc", "Lowercase_Mapping");
  public static Property tc                          = new Property ("tc", "Titlecase_Mapping");
  public static Property scc                         = new Property ("scc", "Special_Case_Condition");
  public static Property scf                         = new Property ("scf", "Simple_Case_Folding");
  public static Property cf                          = new Property ("cf", "Case_Folding");
  public static Property jt                          = new Property ("jt", "Joining_Type", true);
  public static Property jg                          = new Property ("jg", "Joining_Group", true);
  public static Property ea                          = new Property ("ea", "East_Asian_Width", true);
  public static Property lb                          = new Property ("lb", "Line_Break", true);
  public static Property sc                          = new Property ("sc", "Script", true);
  public static Property scx                         = new Property ("scx", "Script_Extensions", true);
  public static Property dash                        = new Property ("Dash", "Dash", true);
  public static Property wspace                      = new Property ("WSpace", "White_Space", true);
  public static Property hyphen                      = new Property ("Hyphen", "Hyphen", true);
  public static Property qmark                       = new Property ("QMark", "Quotation_Mark", true);
  public static Property radical                     = new Property ("Radical", "Radical", true);
  public static Property ideo                        = new Property ("Ideo", "Ideographic", true);
  public static Property uideo                       = new Property ("UIdeo", "Unified_Ideograph", true);
  public static Property idsb                        = new Property ("IDSB", "IDS_Binary_Operator", true);
  public static Property idst                        = new Property ("IDST", "IDS_Trinary_Operator", true);
  public static Property hst                         = new Property ("hst", "Hangul_Syllable_Type", true);
  public static Property di                          = new Property ("DI", "Default_Ignorable_Code_Point", true);
  public static Property odi                         = new Property ("ODI", "Other_Default_Ignorable_Code_Point", true);
  public static Property alpha                       = new Property ("Alpha", "Alphabetic", true);
  public static Property oalpha                      = new Property ("OAlpha", "Other_Alphabetic", true);
  public static Property upper                       = new Property ("Upper", "Uppercase", true);
  public static Property oupper                      = new Property ("OUpper", "Other_Uppercase", true);
  public static Property lower                       = new Property ("Lower", "Lowercase", true);
  public static Property olower                      = new Property ("OLower", "Other_Lowercase", true);
  public static Property math                        = new Property ("Math", "Math", true);
  public static Property omath                       = new Property ("OMath", "Other_Math", true);
  public static Property hex                         = new Property ("Hex", "Hex_Digit", true);
  public static Property ahex                        = new Property ("AHex", "ASCII_Hex_Digit", true);
  public static Property nchar                       = new Property ("NChar", "Noncharacter_Code_Point", true);
  public static Property vs                          = new Property ("VS", "Variation_Selector", true);
  public static Property bidi_c                      = new Property ("Bidi_C", "Bidi_Control", true);
  public static Property join_c                      = new Property ("Join_C", "Join_Control", true);
  
  public static Property gr_base                     = new Property ("Gr_Base", "Grapheme_Base", true);
  public static Property gr_ext                      = new Property ("Gr_Ext", "Grapheme_Extend", true);
  public static Property ogr_ext                     = new Property ("OGr_Ext", "Other_Grapheme_Extend", true);
  public static Property gr_link                     = new Property ("Gr_Link", "Grapheme_Link", true);
  
  
  public static Property sterm                       = new Property ("STerm", "Sentence_Terminal", true);
  public static Property ext                         = new Property ("Ext", "Extender", true);
  public static Property term                        = new Property ("Term", "Terminal_Punctuation", true);
  public static Property dia                         = new Property ("Dia", "Diacritic", true);
  public static Property dep                         = new Property ("Dep", "Deprecated", true);
  
  public static Property ids                         = new Property ("IDS", "ID_Start", true);
  public static Property oids                        = new Property ("OIDS", "Other_ID_Start", true);
  public static Property xids                        = new Property ("XIDS", "XID_Start", true);
  public static Property idc                         = new Property ("IDC", "ID_Continue", true);
  public static Property oidc                        = new Property ("OIDC", "Other_ID_Continue", true);
  public static Property xidc                        = new Property ("XIDC", "XID_Continue", true);
  
  public static Property sd                          = new Property ("SD", "Soft_Dotted", true);
  public static Property loe                         = new Property ("LOE", "Logical_Order_Exception", true);
  public static Property pat_ws                      = new Property ("Pat_WS", "Pattern_White_Space", true);
  public static Property pat_syn                     = new Property ("Pat_Syn", "Pattern_Syntax", true);
  public static Property gcb                         = new Property ("GCB", "Grapheme_Cluster_Break", true);
  public static Property wb                          = new Property ("WB", "Word_Break", true);
  public static Property sb                          = new Property ("SB", "Sentence_Break", true);

  public static Property ce                          = new Property ("CE", "Composition_Exclusion", true);
  public static Property comp_ex                     = new Property ("Comp_Ex", "Full_Composition_Exclusion", true);
  public static Property nfc_qc                      = new Property ("NFC_QC", "NFC_Quick_Check", true);
  public static Property nfd_qc                      = new Property ("NFD_QC", "NFD_Quick_Check", true);
  public static Property nfkc_qc                     = new Property ("NFKC_QC", "NFKC_Quick_Check", true);
  public static Property nfkd_qc                     = new Property ("NFKD_QC", "NFKD_Quick_Check", true);
  public static Property xo_nfc                      = new Property ("XO_NFC", "Expands_On_NFC", true);
  public static Property xo_nfd                      = new Property ("XO_NFD", "Expands_On_NFD", true);
  public static Property xo_nfkc                     = new Property ("XO_NFKC", "Expands_On_NFKC", true);
  public static Property xo_nfkd                     = new Property ("XO_NFKD", "Expands_On_NFKD", true);
  public static Property fc_nfkc                     = new Property ("FC_NFKC", "FC_NFC_Closure", "FNC");
  
  // Added in 5.2.0
  public static Property ci                          = new Property ("CI", "Case_Ignorable", true);
  public static Property cased                       = new Property ("Cased", "Cased", true);
  public static Property cwcf                        = new Property ("CWCF", "Changes_When_CaseFolded", true);
  public static Property cwcm                        = new Property ("CWCM", "Changes_When_CaseMapped", true);
  public static Property cwkcf                       = new Property ("CWKCF", "Changes_When_NFKC_Casefolded", true);
  public static Property cwl                         = new Property ("CWL", "Changes_When_Lowercased", true);
  public static Property cwt                         = new Property ("CWT", "Changes_When_Titlecased", true);
  public static Property cwu                         = new Property ("CWU", "Changes_When_Uppercased", true);
  public static Property nfkc_cf                     = new Property ("NFKC_CF", "NFKC_Casefold");

  // Added in 6.0.0
  public static Property InSC                        = new Property ("InSC", "Indic_Syllabic_Category", true);
  public static Property InMC                        = new Property ("InMC", "Indic_Matra_Category", true);

  // Added in 8.0.0 (really, InSC renamed InPc)
  public static Property InPC                        = new Property ("InPC", "Indic_Positional_Category", true);
  public static Property kJa                         = new Property ("kJa");
  
  // Added in 9.0.0
  public static Property pcm                         = new Property ("PCM", "Prepended_Concatenation_Mark", true);
  
  // Added in 10.0.0
  public static Property vo                          = new Property ("vo", "Vertical_Orientation", true);
  public static Property RI                          = new Property ("RI", "Regional_Indicator", true);
  
  public static Property blk                         = new Property ("blk", "Block", true);
  
  public static Property kCompatibilityVariant       = new Property ("kCompatibilityVariant");

  public static Property kRSUnicode                  = new Property ("kRSUnicode");

  public static Property kIRG_RSIndex                = new Property ("kIRG_RSIndex");
  public static Property kIRG_GSource                = new Property ("kIRG_GSource");
  public static Property kIRG_TSource                = new Property ("kIRG_TSource");
  public static Property kIRG_JSource                = new Property ("kIRG_JSource");
  public static Property kIRG_KSource                = new Property ("kIRG_KSource");
  public static Property kIRG_KPSource               = new Property ("kIRG_KPSource");
  public static Property kIRG_VSource                = new Property ("kIRG_VSource");
  public static Property kIRG_HSource                = new Property ("kIRG_HSource");
  public static Property kIRG_USource                = new Property ("kIRG_USource");
  public static Property kIRG_MSource                = new Property ("kIRG_MSource");

  public static Property kIICore                     = new Property ("kIICore");

  public static Property kGB0                        = new Property ("kGB0");
  public static Property kGB1                        = new Property ("kGB1");
  public static Property kGB3                        = new Property ("kGB3");
  public static Property kGB5                        = new Property ("kGB5");
  public static Property kGB7                        = new Property ("kGB7");
  public static Property kGB8                        = new Property ("kGB8");

  public static Property kCNS1986                    = new Property ("kCNS1986");
  public static Property kCNS1992                    = new Property ("kCNS1992");

  public static Property kJis0                       = new Property ("kJis0");
  public static Property kJis1                       = new Property ("kJis1");
  public static Property kJIS0213                    = new Property ("kJIS0213");

  public static Property kKSC0                       = new Property ("kKSC0");
  public static Property kKSC1                       = new Property ("kKSC1");

  public static Property kKPS0                       = new Property ("kKPS0");
  public static Property kKPS1                       = new Property ("kKPS1");

  public static Property kHKSCS                      = new Property ("kHKSCS");

  public static Property kCantonese                  = new Property ("kCantonese");
  public static Property kHangul                     = new Property ("kHangul");
  public static Property kDefinition                 = new Property ("kDefinition");
  public static Property kHanYu                      = new Property ("kHanYu");
  public static Property kAlternateHanYu             = new Property ("kAlternateHanYu");
  public static Property kMandarin                   = new Property ("kMandarin");
  public static Property kCihaiT                     = new Property ("kCihaiT");
  public static Property kSBGY                       = new Property ("kSBGY");
  public static Property kNelson                     = new Property ("kNelson");
  public static Property kCowles                     = new Property ("kCowles");
  public static Property kMatthews                   = new Property ("kMatthews");
  public static Property kOtherNumeric               = new Property ("kOtherNumeric");
  public static Property kPhonetic                   = new Property ("kPhonetic");
  public static Property kGSR                        = new Property ("kGSR");
  public static Property kFenn                       = new Property ("kFenn");
  public static Property kFennIndex                  = new Property ("kFennIndex");
  public static Property kKarlgren                   = new Property ("kKarlgren");
  public static Property kCangjie                    = new Property ("kCangjie");
  public static Property kMeyerWempe                 = new Property ("kMeyerWempe");
  public static Property kSimplifiedVariant          = new Property ("kSimplifiedVariant");
  public static Property kTraditionalVariant         = new Property ("kTraditionalVariant");
  public static Property kSpecializedSemanticVariant = new Property ("kSpecializedSemanticVariant");
  public static Property kSemanticVariant            = new Property ("kSemanticVariant");
  public static Property kVietnamese                 = new Property ("kVietnamese");
  public static Property kLau                        = new Property ("kLau");
  public static Property kTang                       = new Property ("kTang");
  public static Property kZVariant                   = new Property ("kZVariant");
  public static Property kJapaneseKun                = new Property ("kJapaneseKun");
  public static Property kJapaneseOn                 = new Property ("kJapaneseOn");
  public static Property kKangXi                     = new Property ("kKangXi");
  public static Property kAlternateKangXi            = new Property ("kAlternateKangXi");
  public static Property kBigFive                    = new Property ("kBigFive");
  public static Property kCCCII                      = new Property ("kCCCII");
  public static Property kDaeJaweon                  = new Property ("kDaeJaweon");
  public static Property kEACC                       = new Property ("kEACC");
  public static Property kFrequency                  = new Property ("kFrequency");
  public static Property kGradeLevel                 = new Property ("kGradeLevel");
  public static Property kHDZRadBreak                = new Property ("kHDZRadBreak");
  public static Property kHKGlyph                    = new Property ("kHKGlyph");
  public static Property kHanyuPinlu                 = new Property ("kHanyuPinlu");
  public static Property kHanyuPinyin                = new Property ("kHanyuPinyin");
  public static Property kIRGHanyuDaZidian           = new Property ("kIRGHanyuDaZidian");
  public static Property kIRGKangXi                  = new Property ("kIRGKangXi");
  public static Property kIRGDaeJaweon               = new Property ("kIRGDaeJaweon");
  public static Property kIRGDaiKanwaZiten           = new Property ("kIRGDaiKanwaZiten");
  public static Property kKorean                     = new Property ("kKorean");
  public static Property kMainlandTelegraph          = new Property ("kMainlandTelegraph");
  public static Property kMorohashi                  = new Property ("kMorohashi");
  public static Property kAlternateMorohashi         = new Property ("kAlternateMorohashi");
  public static Property kPrimaryNumeric             = new Property ("kPrimaryNumeric");
  public static Property kTaiwanTelegraph            = new Property ("kTaiwanTelegraph");
  public static Property kXerox                      = new Property ("kXerox");
  public static Property kPseudoGB1                  = new Property ("kPseudoGB1");
  public static Property kIBMJapan                   = new Property ("kIBMJapan");
  public static Property kAccountingNumeric          = new Property ("kAccountingNumeric");
  public static Property kCheungBauer                = new Property ("kCheungBauer");
  public static Property kCheungBauerIndex           = new Property ("kCheungBauerIndex");
  public static Property kFourCornerCode             = new Property ("kFourCornerCode");
  public static Property kWubi                       = new Property ("kWubi");
  public static Property kXHC1983                    = new Property ("kXHC1983");

  public static Property kRSKanWa                    = new Property ("kRSKanWa");
  public static Property kRSJapanese                 = new Property ("kRSJapanese");
  public static Property kRSKorean                   = new Property ("kRSKorean");
  public static Property kRSKangXi                   = new Property ("kRSKangXi");
  public static Property kRSAdobe_Japan1_6           = new Property ("kRSAdobe_Japan1_6");
  public static Property kTotalStrokes               = new Property ("kTotalStrokes");
  
  public static Property kRSTUnicode                 = new Property ("kRSTUnicode");
  public static Property kTGT_MergedSrc              = new Property ("kTGT_MergedSrc");
 
  public static Property kSrc_NushuDuben             = new Property ("kSrc_NushuDuben");
  public static Property kReading                    = new Property ("kReading");
  
  public static Property isc                         = new Property ("isc", "ISO_Comment");
  public static Property na1                         = new Property ("na1", "Unicode_1_Name");
  
  public static Property nameAlias                   = new Property ("Name_Alias", "Name_Alias");

  
  private String shortName;
  private String longName;
  private Set<String> aliases;
  private Boolean enumerated;

  private Property (String shortName) {
    this (shortName, shortName);
  }
  
  private Property (String shortName, boolean enumerated) {
    this (shortName, shortName, null, enumerated);
  }
  
  private Property (String shortName, String longName) {
    this (shortName, longName, null);
  }
  
  private Property (String shortName, String longName, boolean enumerated) {
    this (shortName, longName, null, enumerated);
  }
  
  private Property (String shortName, String longName, String alias) {
    this (shortName, longName, alias, false);
  }
  
  private Property (String shortName, String longName, String alias, boolean enumerated) {
    this.shortName = shortName;
    this.longName = longName;
    this.enumerated = enumerated;
    this.aliases = new HashSet<String> ();
    if (alias != null) {
      this.aliases.add (alias); }
    allProperties.add (this);
    propertiesByName.put (shortName, this);
    propertiesByName.put (longName, this);
  }

  
  public String getShortName () {
    return shortName;
  }

  public String getLongName () {
    return longName;
  }

  public String getBothNames () {
    return shortName + " (" + longName + ")";
  }

  public boolean isEnumerated () {
    return enumerated;
  }
  

  public static Property fromString (String s) {
    { Property p = propertiesByName.get (s);
      if (p != null) {
        return p; }}
    
    for (Property p : allProperties) {
      if (p.shortName.equalsIgnoreCase (s)) {
        return p; }
      if (p.longName.equalsIgnoreCase (s)) {
        return p; }
      for (String alias : p.aliases) {
        if (alias.equalsIgnoreCase (s)) {
          return p; }}}
    
    System.err.println ("@@@ unknown property " + s);
    return new Property (s);
  }
  
  public static Set<Property> values () {
    return allProperties;
  }
  
  public String toString () {
    return "";
  }
}
