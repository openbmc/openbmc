SUMMARY = "Sombok - Unicode Text Segmentation Package."
DESCRIPTION = "Sombok library package performs Line Breaking Algorithm described in \
Unicode Standard Annex #14 (UAX #14).  East_Asian_Width informative \
properties defined by Annex #11 (UAX #11) may be concerned to determin \
breaking positions.  This package also implements "default" Grapheme \
Cluster segmentation described in Annex #29 (UAX #29)."
LICENSE = "Artistic-1.0 | GPLv1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=5b122a36d0f6dc55279a0ebc69f3c60b"

SRC_URI = "git://github.com/hatukanezumi/sombok.git;protocol=https \
           file://0001-configure.ac-fix-cross-compiling-issue.patch \
          "

inherit autotools pkgconfig

# sombok-2.4.0
SRCREV = "0098d85a037ef5c99a648a3669a077781a45e8cc"

S = "${WORKDIR}/git"
B = "${S}"

# Disable libthai support
EXTRA_OECONF = "--disable-libthai"

BBCLASSEXTEND = "native"
