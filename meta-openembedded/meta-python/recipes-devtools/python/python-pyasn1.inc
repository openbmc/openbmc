SUMMARY = "Python library implementing ASN.1 types."
HOMEPAGE = "http://pyasn1.sourceforge.net/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=a14482d15c2249de3b6f0e8a47e021fd"

SRC_URI[md5sum] = "67f1da0cf680761d29169e54e7d53159"
SRC_URI[sha256sum] = "b773d5c9196ffbc3a1e13bdf909d446cad80a039aa3340bcad72f395b76ebc86"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
