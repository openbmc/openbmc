SUMMARY = "A GNU program for formatting C code"
HOMEPAGE = "http://www.gnu.org/software/indent/"
SECTION = "Applications/Text"
DESCRIPTION = "Indent is a GNU program for beautifying C code, so that \
it is easier to read. Indent can also convert from one C writing style \
to a different one. Indent understands correct C syntax and tries to handle \
incorrect C syntax. \
Install the indent package if you are developing applications in C and \
you want a program to format your code."
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS = "virtual/gettext"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz"
SRC_URI[md5sum] = "be35ea62705733859fbf8caf816d8959"
SRC_URI[sha256sum] = "8a9b41be5bfcab5d8c1be74204b10ae78789fc3deabea0775fdced8677292639"

inherit autotools gettext

FILES_${PN}-doc += "/usr/doc/indent/indent.html"

BBCLASSEXTEND = "native"
