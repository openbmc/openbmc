SUMMARY = "Simple Python wrapper around the OpenSSL library"
HOMEPAGE = "https://pyopenssl.org/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "openssl ${PYTHON_PN}-cryptography"

SRC_URI[sha256sum] = "c1cc5f86bcacefc84dada7d31175cae1b1518d5f60d3d0bb595a67822a868a6f"

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
