SUMMARY = "Simple Python wrapper around the OpenSSL library"
HOMEPAGE = "https://pyopenssl.org/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "openssl ${PYTHON_PN}-cryptography"

SRC_URI[md5sum] = "d9804fedbd1eb0c7d9243397b1fbf972"
SRC_URI[sha256sum] = "9a24494b2602aaf402be5c9e30a0b82d4a5c67528fe8fb475e3f3bc00dd69507"

PYPI_PACKAGE = "pyOpenSSL"
inherit pypi setuptools3

PACKAGES =+ "${PN}-tests"
FILES_${PN}-tests = "${libdir}/${PYTHON_DIR}/site-packages/OpenSSL/test"

RDEPENDS_${PN}_class-target = " \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-threading \
"
RDEPENDS_${PN}-tests = "${PN}"

BBCLASSEXTEND = "native nativesdk"
