SUMMARY  = "Check - unit testing framework for C code"
DESCRIPTION = "It features a simple interface for defining unit tests, \
putting little in the way of the developer. Tests are run in a separate \
address space, so both assertion failures and code errors that cause \
segmentation faults or other signals can be caught. Test results are \
reportable in the following: Subunit, TAP, XML, and a generic logging format."
HOMEPAGE = "https://libcheck.github.io/check/"
SECTION = "devel"

LICENSE  = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=2d5025d4aa3495befef8f17206a5b0a1"

SRC_URI = "https://github.com/${BPN}/check/releases/download/${PV}/check-${PV}.tar.gz \
           file://not-echo-compiler-info-to-check_stdint.h.patch"
SRC_URI[sha256sum] = "a8de4e0bacfb4d76dd1c618ded263523b53b85d92a146d8835eb1a52932fa20a"
UPSTREAM_CHECK_URI = "https://github.com/libcheck/check/releases/"

S = "${WORKDIR}/check-${PV}"

inherit autotools pkgconfig texinfo

CACHED_CONFIGUREVARS += "ac_cv_path_AWK_PATH=${bindir}/gawk"

RREPLACES_${PN} = "check (<= 0.9.5)"

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "checkmk"

FILES_checkmk = "${bindir}/checkmk"

RDEPENDS_checkmk = "gawk"

