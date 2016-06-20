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
LICENSE = "GPLv3 & LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LIB;md5=6a6a8e020838b23406c81b19c1d46df6"

SRC_URI = "${GNU_MIRROR}/libunistring/libunistring-${PV}.tar.gz \
           file://iconv-m4-remove-the-test-to-convert-euc-jp.patch \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
"
SRC_URI[md5sum] = "dfae4ff5583649ed24d8f368f1d0543b"
SRC_URI[sha256sum] = "9625eec2507f4789ebb6fc48ebda98be0e0168979a2f68aa8b680bf8eeabbd47"

inherit autotools texinfo
BBCLASSEXTEND = "native nativesdk"
