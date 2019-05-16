SUMMARY = "Python library implementing ASN.1 types."
HOMEPAGE = "http://pyasn1.sourceforge.net/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=a14482d15c2249de3b6f0e8a47e021fd"

SRC_URI[md5sum] = "f3c0cfbf882a20811be5ad5a41f74b82"
SRC_URI[sha256sum] = "da2420fe13a9452d8ae97a0e478adde1dee153b11ba832a95b223a2ba01c10f7"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
