SUMMARY = "A GNU program for formatting C code"
HOMEPAGE = "http://www.gnu.org/software/indent/"
SECTION = "Applications/Text"
DESCRIPTION = "Indent is a GNU program for beautifying C code, so that \
it is easier to read. Indent can also convert from one C writing style \
to a different one. Indent understands correct C syntax and tries to handle \
incorrect C syntax. \
Install the indent package if you are developing applications in C and \
you want a program to format your code."
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "virtual/gettext"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz \
           file://0001-src-indent.c-correct-the-check-for-locale.h.patch \
           file://0001-Makefile.am-remove-regression-dir.patch \
           file://0001-Fix-builds-with-recent-gettext.patch \
           file://0001-Remove-dead-paren_level-code.patch \
           "
SRC_URI[sha256sum] = "e77d68c0211515459b8812118d606812e300097cfac0b4e9fb3472664263bb8b"

inherit autotools gettext texinfo

CFLAGS:append:class-native = " -Wno-error=unused-value"

BBCLASSEXTEND = "native"
