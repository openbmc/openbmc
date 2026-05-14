SUMMARY = "Simple Python wrapper around the OpenSSL library"
HOMEPAGE = "https://pyopenssl.org/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "openssl python3-cryptography"

SRC_URI[sha256sum] = "737f0a2275c5bc54f3b02137687e1a765931fb3949b9a92a825e4d33b9eec08b"

inherit pypi setuptools3

PACKAGES =+ "${PN}-tests"
FILES:${PN}-tests = "${libdir}/${PYTHON_DIR}/site-packages/OpenSSL/test"

RDEPENDS:${PN}:class-target = " \
    python3-cryptography \
    python3-threading \
"
RDEPENDS:${PN}-tests = "${PN}"

BBCLASSEXTEND = "native nativesdk"
