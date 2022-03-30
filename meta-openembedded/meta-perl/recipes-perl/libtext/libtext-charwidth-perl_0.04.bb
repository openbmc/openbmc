SUMMARY = "Text::CharWidth - Get number of occupied columns of a string on terminal."
DESCRIPTION = "This module supplies features similar as wcwidth(3) and wcswidth(3) \
in C language. \
Characters have its own width on terminal depending on locale. For \
example, ASCII characters occupy one column per character, east Asian \
fullwidth characters (like Hiragana or Han Ideograph) occupy two columns \
per character, and combining characters (apperaring in ISO-8859-11 Thai, \
Unicode, and so on) occupy zero columns per character. mbwidth() gives the \
width of the first character of the given string and mbswidth() gives the \
width of the whole given string."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~kubota/Text-CharWidth-${PV}/"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;md5=d8d54c8c500cbdd57a4c15911d9d96db"

SRC_URI = "${CPAN_MIRROR}/authors/id/K/KU/KUBOTA/Text-CharWidth-${PV}.tar.gz"

SRC_URI[md5sum] = "37a723df0580c0758c0ee67b37336c15"
SRC_URI[sha256sum] = "abded5f4fdd9338e89fd2f1d8271c44989dae5bf50aece41b6179d8e230704f8"

S = "${WORKDIR}/Text-CharWidth-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
