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

public class Blocks {
  final SortedMap<Integer, Block> byFirstCp;
  final SortedMap<String, Block> byName;

  public Blocks () {
    super ();
    this.byFirstCp = new TreeMap<Integer, Block> ();
    this.byName = new TreeMap<String, Block> ();
  }

  public void internalStats (PrintStream out) {
    out.println ("  " + byName.size () + " blocks");
  }

  public void add (Block b) {
    byFirstCp.put (new Integer (b.first), b);
    byName.put (b.name, b);
  }

  //-----------------------------------------------------------------------------
  public void fromXML (String qname, Attributes at) {
    if ("block".equals (qname)) {
      add (Block.fromXML (at)); }
  }

  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    if (byFirstCp.isEmpty ()) {
      return; }

    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      for (Block b : byFirstCp.values ()) {
        AttributesImpl at2 = new AttributesImpl ();
        b.toXML (ch, "block", at2); }
      ch.endElement(Ucd.NAMESPACE, elt, elt); }
  }


  //----------------------------------------------------------------------------
  public void diff (Blocks older, PrintStream out, int detailsLevel) {
    DifferenceCounter cc = new DifferenceCounter ();
    boolean includeDetails = detailsLevel >= 1;

    out.println ("");
    out.println ("==================================================== blocks");
    if (includeDetails) {
      out.println (""); }

    for (Block newB : byName.values () ) {
      Block oldB = (older == null) ? null : older.byName.get (newB.name);

      if (oldB == null) {
        cc.added ();
        if (includeDetails) {
          out.println ("new: " + newB); }}
      else if (! (newB.first == oldB.first && newB.last == newB.last)) {
        cc.changed ();
        if (includeDetails) {
          out.println ("changed: from " + oldB + " to " + newB); }}
      else {
        cc.unchanged (); }}

    for (Block oldB : older.byName.values ()) {
      if (byName.get (oldB.name) == null) {
        cc.removed ();
        if (includeDetails) {
          out.println ("removed: " + oldB); }}}

    if (includeDetails) {
      out.println (""); }

    out.println (cc);
  }
}
