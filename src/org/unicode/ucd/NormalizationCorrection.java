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

import java.io.Serializable;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class NormalizationCorrection implements Serializable {
  static final long serialVersionUID = 1;
  public String cp;
  public String oldDecomp;
  public String newDecomp;
  public String version;
  
  public NormalizationCorrection (String cp, 
      String oldDecomp, String newDecomp, String version) {
    this.cp = cp;
    this.oldDecomp = oldDecomp;
    this.newDecomp = newDecomp;
    this.version = version;
  }
  
  public String toString () {
    return "{nc " + cp + " o=" + oldDecomp + " n=" + newDecomp + " v=" + version + "}"; 
  }
     
  public boolean equals (Object o) {
    if (o == this) {
      return true; }
    if (o == null) {
      return false; }
    if (! (o instanceof NormalizationCorrection)) {
      return false; }
    NormalizationCorrection other = (NormalizationCorrection) o;
    return cp.equals (other.cp) 
      && oldDecomp.equals (other.oldDecomp)
      && newDecomp.equals (other.newDecomp)
      && version.equals (other.version);
  }
  
  public int hashCode () {
    return cp.hashCode ();
  }
  
  //----------------------------------------------------------------------------
  public static NormalizationCorrection fromXML (Attributes at) {
    String cp = at.getValue ("cp");
    String oldDecomp = at.getValue ("old");
    String newDecomp = at.getValue ("new");
    String version = at.getValue ("version");
    return new NormalizationCorrection (cp, oldDecomp, newDecomp, version);
  }
  
  public void toXML (TransformerHandler ch, String elt, AttributesImpl at) throws Exception {
    at.addAttribute ("", "cp", "cp", "CDATA", cp);
    at.addAttribute ("", "old", "old", "CDATA", oldDecomp);
    at.addAttribute ("", "new", "new", "CDATA", newDecomp);
    at.addAttribute ("", "version", "version", "CDATA", version);

    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      ch.endElement (Ucd.NAMESPACE, elt, elt); }
  }
  

  //----------------------------------------------------------------------------
}