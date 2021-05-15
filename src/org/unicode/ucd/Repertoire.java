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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class Repertoire implements Iterable<Group> {

  // Indexed by code point; the group for that code point.
  protected Group[] groups;


  public Repertoire () {
    Group g = new Group (0x0000, 0x10ffff);

    groups = new Group [0x110000];
    for (int i = 0; i < groups.length; i++) {
      groups [i] = g; }
  }

  public class GroupIterator implements Iterator<Group> {
    int currentCp;
    int lastCp;

    public GroupIterator (int firstCp, int lastCp) {
      currentCp = firstCp;
      this.lastCp = lastCp;}

    public boolean hasNext () {
      return currentCp <= lastCp;
    }

    public Group next () {
      Group r = groups [currentCp];
      currentCp = r.lastCp + 1;
      return r;
    }

    public void remove () throws UnsupportedOperationException {
      throw new UnsupportedOperationException ("remove not supported on Ranges iterator");
    }
  }

  public class IterableGroupSubset implements Iterable<Group> {
    int firstCp, lastCp;
    public IterableGroupSubset (int firstCp, int lastCp) {
      this.firstCp = firstCp;
      this.lastCp = lastCp; }

    public Iterator<Group> iterator () {
      return new GroupIterator (firstCp, lastCp); }
  }

  public Iterator<Group> iterator () {
    return new GroupIterator (0, groups.length - 1);
  }

  public Iterable<Group> onSubset (int firstCp, int lastCp) {
    return new IterableGroupSubset (firstCp, lastCp);
  }

  public String get (int cp, Property p) {
    return groups [cp].get (p);
  }

  public Object getObject (int cp, Property p) {
    return groups [cp].getObject (p);
  }

  public void put (int firstCp, int lastCp, Property p, String v) {
    int cp = firstCp;
    while (cp <= lastCp) {
      createBoundaryBefore (cp);
      if (groups [cp].lastCp > lastCp) {
        createBoundaryBefore (lastCp + 1); }
      groups [cp].put (p, v);
      cp = groups [cp].lastCp + 1; }
  }

  public void putObject (int firstCp, int lastCp, Property p, Object v) {
    int cp = firstCp;
    while (cp <= lastCp) {
      createBoundaryBefore (cp);
      if (groups [cp].lastCp > lastCp) {
        createBoundaryBefore (lastCp + 1); }
      groups [cp].putObject (p, v);
      cp = groups [cp].lastCp + 1; }
  }

  public void putForced (int firstCp, int lastCp, Property p, String v) {
    int cp = firstCp;
    while (cp <= lastCp) {
      createBoundaryBefore (cp);
      if (groups [cp].lastCp > lastCp) {
        createBoundaryBefore (lastCp + 1); }
      groups [cp].putForced (p, v);
      cp = groups [cp].lastCp + 1; }
  }

  public interface DefaultSetter {
    public void process (Group r);
  }

  public void putDefault (Property p, String v) {
    for (Group r : groups) {
      r.putDefault (p, v); }
  }

  public void putDefault (DefaultSetter s) {
    for (Group r : groups) {
      s.process (r); }
  }

  public void normalize () {
    Group previous = null;

    for (Group r : this) {

      if (r.firstCp == r.lastCp) {
        String na = r.get (Property.na);
        String cp = Ucd.toU (r.firstCp);
        if (na != null && na.contains (cp)) {
          r.putForced (Property.na, na.replace (cp, "#")); }

        if (cp.equals (r.get (Property.dm))) {
          r.putForced (Property.dm, "#"); }

        if (cp.equals (r.get (Property.suc))) {
          r.putForced (Property.suc, "#"); }
        if (cp.equals (r.get (Property.slc))) {
          r.putForced (Property.slc, "#"); }
        if (cp.equals (r.get (Property.stc))) {
          r.putForced (Property.stc, "#"); }
        if (cp.equals (r.get (Property.uc))) {
          r.putForced (Property.uc, "#"); }
        if (cp.equals (r.get (Property.lc))) {
          r.putForced (Property.lc, "#"); }
        if (cp.equals (r.get (Property.tc))) {
          r.putForced (Property.tc, "#"); }
        if (cp.equals (r.get (Property.scf))) {
          r.putForced (Property.scf, "#"); }
        if (cp.equals (r.get (Property.cf))) {
          r.putForced (Property.cf, "#"); }}

      if (previous != null && previous.sameProperties (r)) {
        if (previous.lastCp - previous.firstCp > r.lastCp - r.firstCp) {
          for (int cp = r.firstCp; cp <= r.lastCp; cp++) {
            groups [cp] = previous; }
          r.firstCp = previous.firstCp;
          previous.lastCp = r.lastCp; }

        else {
          for (int cp = previous.firstCp; cp <= previous.lastCp; cp++) {
            groups [cp] = r; }
          r.firstCp = previous.firstCp;
          previous.lastCp = r.lastCp;
          previous = r; }}
      else {
        previous = r; }}
  }

  public void createBoundaryBefore (int cp) {
    if (cp == 0 || cp >= groups.length) {
      return; }

    Group rBefore = groups [cp - 1];
    Group rAfter = groups [cp];

    if (rBefore == rAfter) {
      if (cp - rBefore.firstCp > rAfter.lastCp - cp) {
        Group rNew = new Group (cp, rAfter.lastCp, rAfter);
        for (int i = cp; i <= rAfter.lastCp; i++) {
          groups [i] = rNew; }
        rBefore.lastCp = cp - 1; }
      else {
        Group rNew = new Group (rBefore.firstCp, cp - 1, rBefore);
        for (int i = rBefore.firstCp; i < cp; i++) {
          groups [i] = rNew; }
        rAfter.firstCp = cp; }}
  }

  public void remove (Property p) {
    for (Group r : groups) {
      r.remove (p); }
  }

  //----------------------------------------------------------------------------

  final static char[] space = {' '};

  public void toXML (TransformerHandler ch, String elt, AttributesImpl at, int explodeLimit, Repertoire groups) throws Exception  {
    ch.startElement (Ucd.NAMESPACE, elt, elt, at); {
      if (groups == null) {
        for (Group r : this) {
          r.toXMLElements (ch, explodeLimit, null); }}

      else {
        for (Group g : groups) {
          AttributesImpl at2 = new AttributesImpl ();
          g.toXMLAttributes (at2);
          ch.startElement (Ucd.NAMESPACE, "group", "group", at2); {
            for (Group r : this.onSubset (g.firstCp, g.lastCp)) {
              r.toXMLElements (ch, explodeLimit, g); }
            ch.endElement (Ucd.NAMESPACE, "group", "group"); }}}

      ch.endElement (Ucd.NAMESPACE, elt, elt); }
  }

  public Group rangeFromXML (String elt, Attributes at, Map<Property, String> gr) {
    Group r = Group.fromXML (elt, at, gr);
    createBoundaryBefore (r.firstCp);
    createBoundaryBefore (r.lastCp + 1);
    for (int cp = r.firstCp; cp <= r.lastCp; cp++) {
      groups [cp] = r; }
    return r;
  }

  //----------------------------------------------------------------------------
  public Set<Property> collectProperties () {
    Set<Property> properties = new HashSet<Property> ();

    for (Group r : groups) {
      r.collectProperties (properties); }

    return properties;
  }

  public String getUsefulCharacterName (int cp) {
    String name;
    if (cp <= 0x1F || (0x80 <= cp && cp <= 0x9F)) {
      name = get (cp, Property.na1); }
    else {
      name = get (cp, Property.na); }
    if (name == null) {
      name = ""; }
    return name;
  }

  public void diff (Repertoire older, PrintStream out, int detailsLevel) {

    boolean ignoreCodePoint[] = new boolean [0x110000];

    out.println ();
    out.println ("================================ changed type ");

    DifferenceCounter typeDc = new DifferenceCounter ();
    for (int cp = 0; cp < 0x110000; cp++) {

      String newerType = get (cp, Property.type);
      String olderType = older.get (cp, Property.type);
      ignoreCodePoint [cp] = false;

      if (newerType == null && olderType == null) {
        typeDc.undefined ();
        continue; }
      else if (olderType == null) {
        ignoreCodePoint [cp] = true;
        typeDc.added ();
        olderType = "<undefined>"; }
      else if (newerType == null) {
        ignoreCodePoint [cp] = true;
        typeDc.removed ();
        newerType = "<undefined>"; }
      else if (newerType.equals (olderType)) {
        typeDc.unchanged ();
        continue; }
      else {
        ignoreCodePoint [cp] = true;
        typeDc.changed (); }

      if (detailsLevel >= 1) {
        out.println (Ucd.toU (cp) + "\t" + olderType + "\t->\t" + newerType + "\t" + getUsefulCharacterName (cp)); }}

    if (detailsLevel >= 1) {
      out.println (""); }
    out.println (typeDc);


    out.println ("");
    out.println ("=============================== properties added or removed");

    Set<Property> propertiesToCompare = new HashSet<Property> ();
    Set<Property> newerSet = collectProperties ();
    Set<Property> olderSet = older.collectProperties ();

    newerSet.remove (Property.type);
    olderSet.remove (Property.type);

    for (Property p : Property.values ()) {
      boolean inNewer = newerSet.contains (p);
      boolean inOlder = olderSet.contains (p);

      if (inOlder && inNewer) {
        propertiesToCompare.add (p); }
      else if (inOlder) {
        out.println ("removed property: " + p.getBothNames ()); }

      else if (inNewer) {
        out.println ("added property: " + p.getBothNames ()); }}


    out.println ();
    out.println ("================================ changed properties values ");

    for (Property p : Property.values ()) {
      if (! propertiesToCompare.contains (p)) {
        continue; }

      out.println ();
      out.println ("----------------------------------------------- " + p.getBothNames ());
      DifferenceCounter dc = new DifferenceCounter ();

      for (int cp = 0; cp < 0x110000; cp++) {
        if (ignoreCodePoint [cp] && detailsLevel < 2) {
          dc.ignored ();
          continue; }

        Object newerValue = getObject (cp, p);
        Object olderValue = older.getObject (cp, p);

        if (newerValue == null && olderValue == null) {
          dc.undefined ();
          continue; }
        else if (olderValue == null) {
          dc.added (); }
        else if (newerValue == null) {
          dc.removed (); }
        else if (newerValue.equals (olderValue)) {
          dc.unchanged ();
          continue; }
        else {
          dc.changed (); }

        if (detailsLevel >= 1) {
          out.println (p.getShortName () + "\t" + Ucd.toU (cp) + "\t"
                        + (olderValue == null ? "<undefined>" : ("'" + olderValue + "'"))
                        + "\t->\t"
                        + (newerValue == null ? "<undefined>" : ("'" + newerValue + "'"))
                        + "\t" + getUsefulCharacterName (cp)); }}

      if (detailsLevel >= 1) {
        out.println (""); }
      out.println (dc); }
  }

  //----------------------------------------------------------------------------

  public void group (Repertoire groups) {

    // simplify the character names
    for (Group r : groups) {
      if (r.firstCp == r.lastCp) {
        String na = r.get (Property.na);
        String cpInName = Ucd.toU (r.firstCp);
        if (na != null && na.contains (cpInName)) {
          r.putForced (Property.na, na.replace (cpInName, "*")); }}}

    for (Group g : groups) {

      for (Property p : Property.values ()) {

        if (p == Property.nameAlias) {
          continue; }

        boolean groupCanHaveProperty = true;
        int totalCount = 0;
        Map<String, Integer> counters = new HashMap<String, Integer> ();

        for (Group r : onSubset (g.firstCp, g.lastCp)) {

          String v = r.get (p);
          if (v == null) {
            groupCanHaveProperty = false; }
          else {
            int currentCount = (counters.get (v) == null) ? 0 : counters.get (v).intValue ();
            int additionalCount = 1;
            currentCount += additionalCount;
            totalCount += additionalCount;
            counters.put (v, currentCount); }}

        if (groupCanHaveProperty) {
          // find the predominent value, needs to be at least 20% and more than once
          int max = Integer.MIN_VALUE;
          String bestValue = null;
          for (String v : counters.keySet()) {
            int thisCount = counters.get (v);
            if (thisCount > max) {
              max = thisCount;
              bestValue = v; }}
          if (max > 0.2 * totalCount && max > 1) {
            g.put (p, bestValue); }}}}
  }

  public void internalStats (PrintStream out) {
    int nbRanges = 0;
    int nbPropsTotal = 0;
    int maxNbPropsOnARange = Integer.MIN_VALUE;
    int minNbPropsOnARange = Integer.MAX_VALUE;

    for (Group r : this) {
      nbRanges++;

      int nbProps = r.nbProps ();
      nbPropsTotal += nbProps;

      maxNbPropsOnARange = Math.max (maxNbPropsOnARange, nbProps);
      minNbPropsOnARange = Math.min (minNbPropsOnARange, nbProps); }

    out.println ("  " + nbRanges + " ranges ");
    out.println ("     " + collectProperties ().size () + " distinct properties");
    out.println ("     " + nbPropsTotal + " properties assignments (min = "
                 + minNbPropsOnARange
                 + ", max = " + maxNbPropsOnARange + ")");
  }

  public Set<String> getPropertyValues (Property p) {

    Set<String> s = new TreeSet<String> ();

    for (Group r : groups) {
      String x = r.get (p);
      if (x != null) {
        s.add (x); }}

    return s;
  }
}
