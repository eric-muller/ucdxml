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

public class EmojiSources {
  // The emoji sources, in the order in which they are added
  List<EmojiSource> sources = null;

  // The emoji sources, indexed by Unicode code point
  SortedMap<String, EmojiSource> sourcesByUnicode = null;

  public EmojiSources () {
    this.sources = new LinkedList<EmojiSource> ();
    this.sourcesByUnicode = new TreeMap<String, EmojiSource> ();
  }

  public void internalStats (PrintStream out) {
    out.println ("  " + sourcesByUnicode.size () + " emoji sources");
  }

  public void add (EmojiSource es) {
    sources.add (es);
    sourcesByUnicode.put (es.unicode, es);
  }

  //-----------------------------------------------------------------------------
  public void fromXML (String qname, Attributes at) {
    if ("emoji-source".equals (qname)) {
      add (EmojiSource.fromXML (at)); }
  }

  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    if (sources.isEmpty ()) {
      return; }

    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      for (EmojiSource v : sources) {
        AttributesImpl at2 = new AttributesImpl ();
        v.toXML (ch, "emoji-source", at2); }
      ch.endElement(Ucd.NAMESPACE, elt, elt); }
  }

  //----------------------------------------------------------------------------

  public void diff (EmojiSources older, PrintStream out, int detailsLevel) {
    DifferenceCounter cc = new DifferenceCounter ();
    boolean includeDetails = detailsLevel >= 1;

    out.println ("");
    out.println ("================================= emoji sources");
    if (includeDetails) {
      out.println (""); }

    for (EmojiSource newNc : sourcesByUnicode.values ()) {

      EmojiSource oldNc = (older == null) ? null : older.sourcesByUnicode.get (newNc.unicode);
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

    for (EmojiSource oldNc : older.sourcesByUnicode.values ()) {
      if (sourcesByUnicode.get (oldNc.unicode) == null) {
        cc.removed ();
        if (includeDetails) {
          out.println ("removed: " + oldNc); }}}

    if (includeDetails) {
      out.println (""); }

    out.println (cc);
  }

}
