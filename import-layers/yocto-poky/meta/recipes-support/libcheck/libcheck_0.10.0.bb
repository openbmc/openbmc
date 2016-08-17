SUMMARY  = "Check - unit testing framework for C code"
HOMEPAGE = "http://check.sourceforge.net/"
SECTION = "devel"

LICENSE  = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=2d5025d4aa3495befef8f17206a5b0a1"

SRC_URI = "${SOURCEFORGE_MIRROR}/check/check-${PV}.tar.gz \
          "

SRC_URI[md5sum] = "53c5e5c77d090e103a17f3ed7fd7d8b8"
SRC_URI[sha256sum] = "f5f50766aa6f8fe5a2df752666ca01a950add45079aa06416b83765b1cf71052"

S = "${WORKDIR}/check-${PV}"

inherit autotools pkgconfig texinfo

CACHED_CONFIGUREVARS += "ac_cv_path_AWK_PATH=${bindir}/gawk"

RREPLACES_${PN} = "check (<= 0.9.5)"
RDEPENDS_${PN} += "gawk"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
