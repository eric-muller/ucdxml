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

public class NameAlias implements Comparable {
  String alias;
  String type;

  public NameAlias (String alias, String type) {
    this.alias = alias;
    this.type = type;
  }

  public String toString () {
    return "{na: " + alias + (type == null ? "" : ", " + type) + "}";
  }

  public boolean equals (Object o) {
    if (o == this) {
      return true; }
    if (o == null) {
      return false; }
    if (! (o instanceof NameAlias)) {
      return false; }
    NameAlias other = (NameAlias) o;
    return alias.equals (other.alias) && type.equals (other.type);
  }

  @Override
  public int compareTo (Object o) {
    NameAlias other = (NameAlias) o;
    int a = alias.compareTo (other.alias);
    if (a != 0) {
      return a; }
    else if (other.type == null) {
      if (type == null) {
        return 0; }
      else {
        return 1; }}
    else {
      return other.type.compareTo (type); }
  }

  public int hashCode () {
    return (alias + type).hashCode ();
  }

  //----------------------------------------------------------------------------
  public static NameAlias fromXML (Attributes at) {
    String alias = at.getValue ("alias");
    String type = at.getValue ("type");
    return new NameAlias (alias, type);
  }

  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    at.addAttribute ("", "alias", "alias", "CDATA", alias);
    if (type != null) {
      at.addAttribute ("", "type", "type", "CDATA", type); }

    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      ch.endElement (Ucd.NAMESPACE, elt, elt); }
  }

  //----------------------------------------------------------------------------
}
