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

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class EmojiSource {
  String unicode;
  String docomo;
  String kddi;
  String softbank;
  
  public EmojiSource (String unicode, String docomo, String kddi, String softbank) {
    this.unicode = unicode;
    this.docomo = docomo;
    this.kddi = kddi;
    this.softbank = softbank;
  }
  
  public String toString () {
    return "{es: " + unicode + ", " + docomo + ", " + kddi + ", " + softbank + "}";
  }
  
  public boolean equals (Object o) {
    if (o == this) {
      return true; }
    if (o == null) {
      return false; }
    if (! (o instanceof EmojiSource)) {
      return false; }
    EmojiSource other = (EmojiSource) o;
    return unicode.equals (other.unicode) && docomo.equals (other.docomo) && kddi.equals (other.kddi) && softbank.equals (other.softbank);
  }
  
  public int hashCode () {
    return unicode.hashCode ();
  }
  
  //----------------------------------------------------------------------------
  public static EmojiSource fromXML (Attributes at) {
    String unicode = at.getValue ("unicode");
    String docomo = at.getValue ("docomo");
    String kddi = at.getValue ("kddi");
    String softbank = at.getValue ("softbank");
    return new EmojiSource (unicode, docomo, kddi, softbank);
  }
  
  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    at.addAttribute ("", "unicode", "unicode", "CDATA", unicode);
    at.addAttribute ("", "docomo", "docomo", "CDATA", docomo);
    at.addAttribute ("", "kddi", "kddi", "CDATA", kddi);
    at.addAttribute ("", "softbank", "softbank", "CDATA", softbank);
    
    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      ch.endElement (Ucd.NAMESPACE, elt, elt); }
  }

  //----------------------------------------------------------------------------

}
