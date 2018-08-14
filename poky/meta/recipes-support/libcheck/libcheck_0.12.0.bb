SUMMARY  = "Check - unit testing framework for C code"
HOMEPAGE = "http://check.sourceforge.net/"
SECTION = "devel"

LICENSE  = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=2d5025d4aa3495befef8f17206a5b0a1"

SRC_URI = "https://github.com/${BPN}/check/releases/download/${PV}/check-${PV}.tar.gz"
SRC_URI[md5sum] = "31b17c6075820a434119592941186f70"
SRC_URI[sha256sum] = "464201098bee00e90f5c4bdfa94a5d3ead8d641f9025b560a27755a83b824234"
UPSTREAM_CHECK_URI = "https://github.com/libcheck/check/releases/"

S = "${WORKDIR}/check-${PV}"

inherit autotools pkgconfig texinfo

CACHED_CONFIGUREVARS += "ac_cv_path_AWK_PATH=${bindir}/gawk"

RREPLACES_${PN} = "check (<= 0.9.5)"
RDEPENDS_${PN} += "gawk"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
