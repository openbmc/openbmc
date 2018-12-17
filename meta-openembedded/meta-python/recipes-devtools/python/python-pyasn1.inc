SUMMARY = "Python library implementing ASN.1 types."
HOMEPAGE = "http://pyasn1.sourceforge.net/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=815ac3f9ca693006349094b358ab3155"

SRC_URI[md5sum] = "0d182b71e1f72fe147d7957bc90be7f2"
SRC_URI[sha256sum] = "f58f2a3d12fd754aa123e9fa74fb7345333000a035f3921dbdaa08597aa53137"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
