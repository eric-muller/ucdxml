// COPYRIGHT AND PERMISSION NOTICE
//
// Copyright 2006-2023 Unicode Inc.
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

/** Represents a version of Unicode. */

public final class Version {
  private final int major;
  private final int minor;
  private final int dot;

  public Version (int major, int minor, int dot) {
    this.major = major;
    this.minor = minor;
    this.dot = dot;
  }

  public String toString () {
    return major + "." + minor + "." + dot;
  }

  /** Returns true if this version is at least some other version. */
  public boolean isAtLeast (Version v) {
    if (major < v.major) {
      return false; }
    else if (major > v.major) {
      return true; }
    else {
      if (minor < v.minor) {
        return false; }
      else if (minor > v.minor) {
        return true; }
      else {
        return (dot >= v.dot); }}
  }

  public boolean isAtMost (Version v) {
    if (major > v.major) {
      return false; }
    else if (major < v.major) {
      return true; }
    else {
      if (minor > v.minor) {
        return false; }
      else if (minor < v.minor) {
        return true; }
      else {
        return (dot < v.dot); }}
  }

  public boolean equals (Object o) {
    if (o == null) {
      return false; }
    if (! (o instanceof Version)) {
      return false; }
    Version v = (Version) o;
    return    this.major == v.major
           && this.minor == v.minor
           && this.dot == v.dot;
  }

  public static final Version V2_0_0 = new Version (2, 0, 0);
  public static final Version V2_1_5 = new Version (2, 1, 5);
  public static final Version V2_1_8 = new Version (2, 1, 8);
  public static final Version V3_0_0 = new Version (3, 0, 0);
  public static final Version V3_0_1 = new Version (3, 0, 1);
  public static final Version V3_1_0 = new Version (3, 1, 0);
  public static final Version V3_2_0 = new Version (3, 2, 0);
  public static final Version V4_0_0 = new Version (4, 0, 0);
  public static final Version V4_0_1 = new Version (4, 0, 1);
  public static final Version V4_1_0 = new Version (4, 1, 0);
  public static final Version V5_0_0 = new Version (5, 0, 0);
  public static final Version V5_1_0 = new Version (5, 1, 0);
  public static final Version V5_2_0 = new Version (5, 2, 0);
  public static final Version V6_0_0 = new Version (6, 0, 0);
  public static final Version V6_1_0 = new Version (6, 1, 0);
  public static final Version V6_2_0 = new Version (6, 2, 0);
  public static final Version V6_3_0 = new Version (6, 3, 0);
  public static final Version V7_0_0 = new Version (7, 0, 0);
  public static final Version V8_0_0 = new Version (8, 0, 0);
  public static final Version V9_0_0 = new Version (9, 0, 0);
  public static final Version V10_0_0 = new Version (10, 0, 0);
  public static final Version V11_0_0 = new Version (11, 0, 0);
  public static final Version V12_0_0 = new Version (12, 0, 0);
  public static final Version V12_1_0 = new Version (12, 1, 0);
  public static final Version V13_0_0 = new Version (13, 0, 0);
  public static final Version V14_0_0 = new Version (14, 0, 0);
  public static final Version V15_0_0 = new Version (15, 0, 0);
  public static final Version V15_1_0 = new Version (15, 1, 0);
}
