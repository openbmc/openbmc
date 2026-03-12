SUMMARY = "Library for manipulating C and Unicode strings"

DESCRIPTION = "Text files are nowadays usually encoded in Unicode, and may\
 consist of very different scripts from Latin letters to Chinese Hanzi\
 with many kinds of special characters accents, right-to-left writing\
 marks, hyphens, Roman numbers, and much more. But the POSIX platform\
 APIs for text do not contain adequate functions for dealing with\
 particular properties of many Unicode characters. In fact, the POSIX\
 APIs for text have several assumptions at their base which don't hold\
 for Unicode text.  This library provides functions for manipulating\
 Unicode strings and for manipulating C strings according to the Unicode\
 standard.  This package contains documentation."

HOMEPAGE = "http://www.gnu.org/software/libunistring/"
SECTION = "devel"
LICENSE = "LGPL-3.0-or-later | GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
                    file://README;beginline=45;endline=65;md5=3a896a943b4da2c551e6be1af27eff8d \
                    file://doc/libunistring.texi;md5=55cc91e1fe09c7aec78b6dc932202300 \
                    "
DEPENDS = "gperf-native"

SRC_URI = "${GNU_MIRROR}/libunistring/libunistring-${PV}.tar.gz"
SRC_URI[sha256sum] = "e82664b170064e62331962126b259d452d53b227bb4a93ab20040d846fec01d8"

inherit autotools texinfo
BBCLASSEXTEND = "native nativesdk"
