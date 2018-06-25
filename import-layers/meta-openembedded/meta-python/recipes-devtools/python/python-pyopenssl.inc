SUMMARY = "Simple Python wrapper around the OpenSSL library"
HOMEPAGE = "https://pyopenssl.org/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "openssl ${PYTHON_PN}-cryptography"

SRC_URI[md5sum] = "d9c5134db8bf3183573b7840954cac94"
SRC_URI[sha256sum] = "2c10cfba46a52c0b0950118981d61e72c1e5b1aac451ca1bc77de1a679456773"

PYPI_PACKAGE = "pyOpenSSL"

PACKAGES =+ "${PN}-tests"
FILES_${PN}-tests = "${libdir}/${PYTHON_DIR}/site-packages/OpenSSL/test"

RDEPENDS_${PN}_class-target = " \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-threading \
"
RDEPENDS_${PN}-tests = "${PN}"

BBCLASSEXTEND = "native nativesdk"
