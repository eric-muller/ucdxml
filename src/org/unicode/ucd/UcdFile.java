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

import java.util.HashSet;
import java.util.Set;

public enum UcdFile {
  ArabicShaping,
  BidiBrackets,
  BidiMirroring,
  Blocks,
  CaseFolding,
  CJKRadicalNumbers,
  CompositionExclusions,
  DerivedAge,
  DerivedCoreProperties,
  DerivedNormalizationProps,
  EastAsianWidth,
  EmojiSources,
  EquivalentUnifiedIdeograph,
  HangulSyllableType,
  IndicSyllabicCategory,
  IndicMatraCategory,
  IndicPositionalCategory,
  Jamo,
  LineBreak,
  NameAliases,
  NamedSequences,
  NamedSequencesProv,
  NormalizationCorrections,
  NushuSources,
  PropList,
  PropertyAliases,
  PropertyValueAliases,
  Scripts,
  ScriptExtensions,
  SpecialCasing,
  StandardizedVariants,
  TangutSources,
  UnicodeData,
  Unihan,
  UnihanNumeric,
  VerticalOrientation,

  EmojiData,
  EmojiVariationSequences,

  GraphemeBreakProperty,
  SentenceBreakProperty,
  WordBreakProperty,

  DerivedBidiClass,
  DerivedBinaryProperties,
  DerivedCombiningClass,
  DerivedDecompositionType,
  DerivedEastAsianWidth,
  DerivedGeneralCategory,
  DerivedJoiningGroup,
  DerivedJoiningType,
  DerivedLineBreak,
  DerivedNumericType,
  DerivedNumericValues;
  
  static final Set<UcdFile> allFiles;
  
  static {
    allFiles = new HashSet<UcdFile> ();
    for (UcdFile f : UcdFile.values ()) {
      allFiles.add (f); }
  }
}
