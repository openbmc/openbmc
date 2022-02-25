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
                    file://doc/libunistring.texi;md5=266e4297d7c18f197be3d9622ba99685 \
                   "
DEPENDS = "gperf-native"

SRC_URI = "${GNU_MIRROR}/libunistring/libunistring-${PV}.tar.gz"
SRC_URI[sha256sum] = "3c0184c0e492d7c208ce31d25dd1d2c58f0c3ed6cbbe032c5b248cddad318544"

inherit autotools texinfo
BBCLASSEXTEND = "native nativesdk"
