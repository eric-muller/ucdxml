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
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class CJKRadicals {
  SortedMap<String, CJKRadical> numbers = null;

  public CJKRadicals () {
    this.numbers = new TreeMap<String, CJKRadical> ();
  }

  public void internalStats (PrintStream out) {
    out.println ("  " + numbers.size ()
               + " radical numbers");
  }

  public void add (CJKRadical ns) {
    numbers.put (ns.number, ns);
  }

  //-----------------------------------------------------------------------------
  public void fromXML (String qname, Attributes at) {
    if ("cjk-radical".equals (qname)) {
      add (CJKRadical.fromXML (at)); }
  }

  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    if (numbers.isEmpty ()) {
      return; }

    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      SortedSet<CJKRadical> values = new TreeSet<CJKRadical> ();
      for (CJKRadical ns : numbers.values ()) {
        values.add (ns); }
      for (CJKRadical ns: values) {
        AttributesImpl at2 = new AttributesImpl ();
        ns.toXML (ch, "cjk-radical", at2); }
      ch.endElement(Ucd.NAMESPACE, elt, elt); }
  }

  //----------------------------------------------------------------------------

  public void diff (CJKRadicals older, PrintStream out, int detailsLevel) {
    DifferenceCounter cc = new DifferenceCounter ();
    boolean includeDetails = detailsLevel >= 1;

    out.println ("");
    out.println ("=========================================== radical numbers");
    if (includeDetails) {
      out.println (""); }

    for (CJKRadical newNs : numbers.values () ) {
      CJKRadical oldNs = (older == null) ? null : older.numbers.get (newNs.number);

      if (oldNs == null) {
        cc.added ();
        if (includeDetails) {
          out.println ("new: " + newNs); }}
      else if (   ! newNs.radical.equals (oldNs.radical)
               || ! newNs.ideograph.equals (oldNs.ideograph)) {
        cc.changed ();
        if (includeDetails) {
          out.println ("changed: from " + oldNs + " to " + newNs); }}
      else {
        cc.unchanged (); }}

    for (CJKRadical oldNs : older.numbers.values ()) {
      if (numbers.get (oldNs.number) == null) {
        cc.removed ();
        if (includeDetails) {
          out.println ("removed: " + oldNs); }}}

    if (includeDetails) {
      out.println (""); }

    out.println (cc);
  }
}
