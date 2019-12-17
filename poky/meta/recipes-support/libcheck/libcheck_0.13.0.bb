SUMMARY  = "Check - unit testing framework for C code"
HOMEPAGE = "https://libcheck.github.io/check/"
SECTION = "devel"

LICENSE  = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=2d5025d4aa3495befef8f17206a5b0a1"

SRC_URI = "https://github.com/${BPN}/check/releases/download/${PV}/check-${PV}.tar.gz \
           file://not-echo-compiler-info-to-check_stdint.h.patch"
SRC_URI[md5sum] = "2c730c40b08482eaeb10132517970593"
SRC_URI[sha256sum] = "c4336b31447acc7e3266854f73ec188cdb15554d0edd44739631da174a569909"
UPSTREAM_CHECK_URI = "https://github.com/libcheck/check/releases/"

S = "${WORKDIR}/check-${PV}"

inherit autotools pkgconfig texinfo

CACHED_CONFIGUREVARS += "ac_cv_path_AWK_PATH=${bindir}/gawk"

RREPLACES_${PN} = "check (<= 0.9.5)"

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "checkmk"

FILES_checkmk = "${bindir}/checkmk"

RDEPENDS_checkmk = "gawk"

