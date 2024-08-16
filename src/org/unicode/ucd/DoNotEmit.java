// COPYRIGHT AND PERMISSION NOTICE
//
// Copyright 2024 Unicode Inc.
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

public class DoNotEmit {
  List<Instead> insteads = null;
  SortedMap<String, Instead> insteadsByOf = null;
  
  public DoNotEmit () {
    this.insteads = new LinkedList<Instead> ();
    this.insteadsByOf = new TreeMap<String, Instead> ();
  }

  public void internalStats (PrintStream out) {
    out.println ("  " + insteadsByOf.size () + " do not emit");
  }

  public void add (Instead instead) {
    insteads.add (instead);
    insteadsByOf.put (instead.of, instead);
  }

  //-----------------------------------------------------------------------------
  public void fromXML (String qname, Attributes at) {
    if ("instead".equals (qname)) {
      add (Instead.fromXML (at)); }
  }

  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    if (insteads.isEmpty ()) {
      return; }

    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      for (Instead instead : insteads) {
        AttributesImpl at2 = new AttributesImpl ();
        instead.toXML (ch, "instead", at2); }
      ch.endElement(Ucd.NAMESPACE, elt, elt); }
  }

  //----------------------------------------------------------------------------

  public void diff (DoNotEmit older, PrintStream out, int detailsLevel) {
    DifferenceCounter cc = new DifferenceCounter ();
    boolean includeDetails = detailsLevel >= 1;

    out.println ("");
    out.println ("================================= do not emit");
    if (includeDetails) {
      out.println (""); }

    for (Instead newInstead : insteadsByOf.values ()) {

      Instead oldInstead = (older == null) ? null : older.insteadsByOf.get (newInstead.of);
      if (oldInstead == null) {
        cc.added ();
        if (includeDetails) {
          out.println ("new: " + newInstead); }}
      else if (! newInstead.equals (oldInstead)) {
        cc.changed ();
        if (includeDetails) {
          out.println ("changed: from " + oldInstead + " to " + newInstead); }}
      else {
        cc.unchanged (); }}

    for (Instead oldInstead : older.insteadsByOf.values ()) {
      if (insteadsByOf.get (oldInstead.of) == null) {
        cc.removed ();
        if (includeDetails) {
          out.println ("removed: " + oldInstead); }}}

    if (includeDetails) {
      out.println (""); }

    out.println (cc);
  }
}
