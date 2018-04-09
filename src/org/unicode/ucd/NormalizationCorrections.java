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

import java.io.PrintStream;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class NormalizationCorrections {
  // TODO: in principle, we could have the same code point corrected
  // differently in different versions 
  // so the key to corrections should the combination of code point and version

  final SortedMap<String, NormalizationCorrection> corrections;

  public NormalizationCorrections () {
    this.corrections = new TreeMap<String, NormalizationCorrection> ();
  }
  
  public void internalStats (PrintStream out) {
    out.println ("  " + corrections.size () + " normalization corrections");
  }
  
  public String generateKey (String codePoint) {
    while (codePoint.length () < 6) {
        codePoint = "0" + codePoint; }
    return codePoint;
  }
  
  public void add (NormalizationCorrection nc) {
    corrections.put (generateKey (nc.cp), nc);
  }
  
  public NormalizationCorrection find (String codePoint) {
    return corrections.get (generateKey (codePoint));
  }
  
  //-----------------------------------------------------------------------------
  public void fromXML (String qname, Attributes at) {
    if ("normalization-correction".equals (qname)) {
      add (NormalizationCorrection.fromXML (at)); }
  }
  
  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    if (corrections.isEmpty ()) {
      return; }
    
    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      for (NormalizationCorrection c : corrections.values ()) {
        AttributesImpl at2 = new AttributesImpl ();
        c.toXML (ch, "normalization-correction", at2); }
      ch.endElement(Ucd.NAMESPACE, elt, elt); }
  }
  
  //----------------------------------------------------------------------------
 
  public void diff (NormalizationCorrections older, PrintStream out, int detailsLevel) {
    DifferenceCounter cc = new DifferenceCounter ();
    boolean includeDetails = detailsLevel >= 1;
    
    out.println ("");
    out.println ("================================= normalization corrections");
    if (includeDetails) {
      out.println (""); }
    
    for (NormalizationCorrection newNc : corrections.values () ) {
      NormalizationCorrection oldNc = (older == null) ? null : older.find (newNc.cp);
      if (oldNc == null) {
        cc.added ();
        if (includeDetails) {
          out.println ("new: " + newNc); }}
      else if (! newNc.equals (oldNc)) {
        cc.changed ();
        if (includeDetails) {
          out.println ("changed: from " + oldNc + " to " + newNc); }}
      else {
        cc.unchanged (); }}
    
    for (NormalizationCorrection oldNc : older.corrections.values ()) {
      if (find (oldNc.cp) == null) {
        cc.removed ();
        if (includeDetails) {
          out.println ("removed: " + oldNc); }}}
    
    if (includeDetails) {
      out.println (""); }

    out.println (cc);
  }
}
