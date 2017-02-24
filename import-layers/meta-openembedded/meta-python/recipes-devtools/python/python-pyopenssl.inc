SUMMARY = "Simple Python wrapper around the OpenSSL library"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "openssl ${PYTHON_PN}-cryptography"

SRC_URI[md5sum] = "d8100b0c333f0eeadaf05914da8792a6"
SRC_URI[sha256sum] = "88f7ada2a71daf2c78a4f139b19d57551b4c8be01f53a1cb5c86c2f3bf01355f"

PYPI_PACKAGE = "pyOpenSSL"

PACKAGES =+ "${PN}-tests"
FILES_${PN}-tests = "${libdir}/${PYTHON_DIR}/site-packages/OpenSSL/test"

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-threading \
"
RDEPENDS_${PN}-tests = "${PN}"

BBCLASSEXTEND = "native nativesdk"
