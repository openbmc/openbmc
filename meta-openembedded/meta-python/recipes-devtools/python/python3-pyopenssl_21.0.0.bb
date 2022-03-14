SUMMARY = "Simple Python wrapper around the OpenSSL library"
HOMEPAGE = "https://pyopenssl.org/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "openssl ${PYTHON_PN}-cryptography"

SRC_URI[sha256sum] = "5e2d8c5e46d0d865ae933bef5230090bdaf5506281e9eec60fa250ee80600cb3"

PYPI_PACKAGE = "pyOpenSSL"
inherit pypi setuptools3

PACKAGES =+ "${PN}-tests"
FILES:${PN}-tests = "${libdir}/${PYTHON_DIR}/site-packages/OpenSSL/test"

RDEPENDS:${PN}:class-target = " \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-threading \
"
RDEPENDS:${PN}-tests = "${PN}"

BBCLASSEXTEND = "native nativesdk"
