SUMMARY = "Simple Python wrapper around the OpenSSL library"
HOMEPAGE = "https://pyopenssl.org/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "openssl ${PYTHON_PN}-cryptography"

SRC_URI[md5sum] = "b9876625dc1d5a5a662d748689191537"
SRC_URI[sha256sum] = "aeca66338f6de19d1aa46ed634c3b9ae519a64b458f8468aec688e7e3c20f200"

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
