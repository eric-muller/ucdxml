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

public class CJKRadical implements Comparable<CJKRadical> {
  String number;
  String radical;
  String ideograph;
  
  public CJKRadical (String number, String radical, String ideograph) {
    this.number = number;
    this.radical = radical;
    this.ideograph = ideograph;
  }
  
  public String toString () {
    return "{rad: " + number + ", " + radical + ", " + ideograph + "}";
  }
  
  public boolean equals (Object o) {
    if (o == this) {
      return true; }
    if (o == null) {
      return false; }
    if (! (o instanceof CJKRadical)) {
      return false; }
    CJKRadical other = (CJKRadical) o;
    return number.equals (other.number) && radical.equals (other.radical)
      && ideograph.equals (other.ideograph);
  }
  
  public int hashCode () {
    return number.hashCode ();
  }
  
  public float asFloat () {
    if (number.endsWith ("'")) {
      return Integer.parseInt (number.replace ("'", "")) + 0.5f; }
    else {
      return Integer.parseInt (number); }
  }
  
  public int compareTo (CJKRadical other) {
    float me = asFloat ();
    float o = other.asFloat ();
    if (me < o) {
      return -1; }
    else if (me == 0) {
      return 0; }
    else {
      return 1; }
  }

  //----------------------------------------------------------------------------
  public static CJKRadical fromXML (Attributes at) {
    String number = at.getValue ("number");
    String radical = at.getValue ("radical");
    String ideograph = at.getValue ("ideograph");
    return new CJKRadical (number, radical, ideograph);
  }
  
  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    at.addAttribute ("", "number", "number", "CDATA", number);
    at.addAttribute ("", "radical", "radical", "CDATA", radical);
    at.addAttribute ("", "ideograph", "ideograph", "CDATA", ideograph);
    
    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      ch.endElement (Ucd.NAMESPACE, elt, elt); }
  }

  //----------------------------------------------------------------------------
}
