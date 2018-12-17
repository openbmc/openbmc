SUMMARY = "Simple Python wrapper around the OpenSSL library"
HOMEPAGE = "https://pyopenssl.org/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "openssl ${PYTHON_PN}-cryptography"

SRC_URI[md5sum] = "c92e9c85b520b7e153fef0f7f3c5dda7"
SRC_URI[sha256sum] = "6488f1423b00f73b7ad5167885312bb0ce410d3312eb212393795b53c8caa580"

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
