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
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class StandardizedVariants {
  // The variants, in the order in which they are inserted
  List<StandardizedVariant> variants = null;
  
  // The variants, indexed by sequence and condition
  SortedMap<String, StandardizedVariant> variantsByKey = null;

  public StandardizedVariants () {
    this.variants = new LinkedList<StandardizedVariant> ();
    this.variantsByKey = new TreeMap<String, StandardizedVariant> ();
  }
  
  public void internalStats (PrintStream out) {
    out.println ("  " + variantsByKey.size () + " standardized variants");
  }
  
  public void add (StandardizedVariant sv) {
    variants.add (sv);
    variantsByKey.put (sv.sequence + sv.condition, sv);
  }
  
  //-----------------------------------------------------------------------------
  public void fromXML (String qname, Attributes at) {
    if ("standardized-variant".equals (qname)) {
      add (StandardizedVariant.fromXML (at)); }
  }
  
  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    if (variants.isEmpty ()) {
      return; }
    
    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      for (StandardizedVariant v : variants) {
        AttributesImpl at2 = new AttributesImpl ();
        v.toXML (ch, "standardized-variant", at2); }
      ch.endElement(Ucd.NAMESPACE, elt, elt); }
  }
  
  //----------------------------------------------------------------------------
 
  public void diff (StandardizedVariants older, PrintStream out, int detailsLevel) {
    DifferenceCounter cc = new DifferenceCounter ();
    boolean includeDetails = detailsLevel >= 1;
    
    out.println ("");
    out.println ("================================= standardized variants");
    if (includeDetails) {
      out.println (""); }
    
    for (StandardizedVariant newNc : variantsByKey.values () ) {
      StandardizedVariant oldNc = (older == null) ? null : older.variantsByKey.get (newNc.sequence+newNc.condition);
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
    
    for (StandardizedVariant oldNc : older.variantsByKey.values ()) {
      if (variantsByKey.get (oldNc.sequence+oldNc.condition) == null) {
        cc.removed ();
        if (includeDetails) {
          out.println ("removed: " + oldNc); }}}
    
    if (includeDetails) {
      out.println (""); }

    out.println (cc);
  }

}
