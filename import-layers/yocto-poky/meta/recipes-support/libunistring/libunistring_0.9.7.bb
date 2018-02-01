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
LICENSE = "LGPLv3+ | GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
                    file://README;beginline=45;endline=65;md5=08287d16ba8d839faed8d2dc14d7d6a5 \
                    file://doc/libunistring.texi;md5=b86c9fd7acaac623017239640631912c \
                   "

SRC_URI = "${GNU_MIRROR}/libunistring/libunistring-${PV}.tar.gz \
           file://iconv-m4-remove-the-test-to-convert-euc-jp.patch \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
"
SRC_URI[md5sum] = "8e663454aa372181ee8e46edaa3ba4c8"
SRC_URI[sha256sum] = "9ce081cbee1951b55597b30e7030bda9d7b2f034ef901a135ff3a020be5a41e5"

inherit autotools texinfo
BBCLASSEXTEND = "native nativesdk"
