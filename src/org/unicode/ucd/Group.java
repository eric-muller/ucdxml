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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class Group {
  public int firstCp;
  public int lastCp;
  protected Map<Property, Object> properties;



  public Group (int firstCp, int lastCp) {
    this (firstCp, lastCp, null);
  }

  public Group (int firstCp, int lastCp, Group rCopy) {
    this.firstCp = firstCp;
    this.lastCp = lastCp;
    this.properties = new IdentityHashMap<Property, Object> ();
    if (rCopy != null) {
      for (Property p : rCopy.properties.keySet ()) {
        putObjectForced (p, rCopy.getObject (p)); }}
  }

  public String toString () {
    String type = get (Property.type);
    if (type == null) {
      type = "?"; }
    return "{f=" + Ucd.toU (firstCp) + " l=" + Ucd.toU (lastCp)
             + " type=" + type + "}";
  }

  //----------------------------------------------------------------------------
  public boolean sameProperties (Group otherRange) {
    return this.properties.equals (otherRange.properties);
  }

  public String get (Property p) {
    return (String) properties.get (p);
  }

  public Object getObject (Property p) {
    return properties.get (p);
  }

  public void put (Property p, String v) {
    String currentValue = get (p);
    if (currentValue != null && ! v.equals (currentValue) ) {
      System.err.println ("@@@ Attempt to reset " + p.getShortName () + " from " + currentValue
                               + " to " + v + " on " + this + " (ignored)"); }
    else {
      properties.put (p, v); }
  }

  public void putObject (Property p, Object v) {
    Object currentValue = getObject (p);
    if (currentValue != null && ! v.equals (currentValue) ) {
      System.err.println ("@@@ Attempt to reset " + p.getShortName () + " from " + currentValue
                               + " to " + v + " on " + this + " (ignored)"); }
    else {
      properties.put (p, v); }
  }


  public void putForced (Property p, String v) {
    properties.put (p, v);
  }

  public void putObjectForced (Property p, Object v) {
    properties.put (p, v);
  }

  public void putDefault (Property p, String v) {
    String currentValue = get (p);
    if (currentValue == null) {
      properties.put (p, v); }
  }

  public void remove (Property p) {
    properties.remove (p);
  }

  public void collectProperties (Set<Property> ps) {
    ps.addAll (properties.keySet ());
  }

  //----------------------------------------------------------------------------

  static public Group fromXML (String elt, Attributes at, Map<Property, String>gr) {
    int firstCp, lastCp;
    if (at.getValue ("cp") != null) {
      firstCp = Integer.parseInt (at.getValue ("cp"), 16);
      lastCp = firstCp; }
    else {
      firstCp = Integer.parseInt (at.getValue ("first-cp"), 16);
      lastCp = Integer.parseInt (at.getValue ("last-cp"), 16); }

    Group r = new Group (firstCp, lastCp);

    for (int i = 0; i < at.getLength (); i++) {
      String s = at.getLocalName (i);
      if (! ("cp".equals (s) || "first-cp".equals (s) || "last-cp".equals (s))) {
        Property p = Property.fromString (s);
        r.put (p, at.getValue (i).intern()); }}

    if (! "code-point".equals (elt)) {
      r.put (Property.type, elt); }

    if (gr != null) {
      for (Property p : gr.keySet ()) {
        r.putDefault (p, gr.get (p)); }}

    return r;
  }

  public void toXMLAttributes (AttributesImpl at) {
    for (Property p : Property.values ()) {
      String v = get (p);
      if (Property.type != p && Property.nameAlias != p && v != null) {
        at.addAttribute ("", p.getShortName (), p.getShortName (), "CDATA", v); }}
  }

  public void toXMLAttributes2 (TransformerHandler ch)  throws Exception {
    Set<NameAlias> v = (Set<NameAlias>) getObject (Property.nameAlias);
    if (v == null) {
      return; }
    for (NameAlias na : v) {
      na.toXML (ch, "name-alias", new AttributesImpl ()); }
  }

  public void toXMLElements (TransformerHandler ch, int explodeLimit,
                             Group containingGroup) throws Exception {
    if (nbProps () == 0) {
      return; }

    AttributesImpl at = new AttributesImpl ();

    int first, last;

    if (containingGroup == null) {
      first = firstCp;
      last = lastCp; }
    else {
      first = Math.max (containingGroup.firstCp, firstCp);
      last = Math.min (containingGroup.lastCp, lastCp); }

    boolean explode = (last == first) || (last - first - 1 < explodeLimit);

    if (explode) {
      at.addAttribute ("", "cp", "cp", "CDATA", Ucd.toU (first)); }
    else {
      at.addAttribute ("", "first-cp", "first-cp", "CDATA", Ucd.toU (first));
      at.addAttribute ("", "last-cp", "last-cp", "CDATA", Ucd.toU (last)); }

    for (Property p : Property.values ()) {
      if (p == Property.nameAlias) {
        continue; }

      String v = get (p);
      if (   Property.type == p
          || v == null
          || containingGroup != null && v.equals (containingGroup.get (p))) {
        continue; }
      at.addAttribute ("", p.getShortName (), p.getShortName (), "CDATA", v); }

    String type = get (Property.type);
    if (type == null) {
      type = "code-point"; }

    if (explode) {
      for (int cp = first; cp <= last; cp++) {
        at.setValue (0, Ucd.toU (cp));
        ch.startElement (Ucd.NAMESPACE, type, type, at); {
          toXMLAttributes2 (ch);
          ch.endElement (Ucd.NAMESPACE, type, type); }}}
    else {
      ch.startElement (Ucd.NAMESPACE, type, type, at); {
        toXMLAttributes2 (ch);
        ch.endElement (Ucd.NAMESPACE, type, type); }}
  }

  //----------------------------------------------------------------------------
  public int nbProps () {
    return properties.size();
  }
}
