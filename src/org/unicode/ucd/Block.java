// COPYRIGHT AND PERMISSION NOTICE
//
// Copyright (c) 2006-2009 Unicode Inc.
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

public final class Block {
  int first;
  int last;
  String name;
  
  public Block (int first, int last, String name) {
    this.first = first;
    this.last = last;
    this.name = name;
  }
  
  public String toString () {
    return "{block: " + name + ", " + Ucd.toU (first) + ".." + Ucd.toU (last) + "}";
  }
  
  public boolean equals (Object o) {
    if (o == this) {
      return true; }
    if (o == null) {
      return false; }
    if (! (o instanceof Block)) {
      return false; }
    Block other = (Block) o;
    return first == other.first 
      && last == other.last
      && name.equals (other.name);
  }
  
  public int hashCode () {
    return first ^ last;
  }
  
  //----------------------------------------------------------------------------
  public static Block fromXML (Attributes at) {
    int first = Integer.parseInt (at.getValue ("first-cp"), 16);
    int last = Integer.parseInt (at.getValue ("last-cp"), 16);
    String name = at.getValue ("name");
    return new Block (first, last, name);
  }
   
  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    at.addAttribute ("", "first-cp", "first-cp", "CDATA", Ucd.toU (first));
    at.addAttribute ("", "last-cp", "last-cp", "CDATA", Ucd.toU (last));
    at.addAttribute ("", "name", "name", "CDATA", name);
    
    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      ch.endElement (Ucd.NAMESPACE, elt, elt); }
  }
  
  //----------------------------------------------------------------------------
}
