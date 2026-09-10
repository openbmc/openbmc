SUMMARY = "Simple Python wrapper around the OpenSSL library"
HOMEPAGE = "https://pyopenssl.org/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "openssl python3-cryptography"

SRC_URI[sha256sum] = "28dfcce0162b9211413e26dfbfdf1d24317fbeba18fc93c12400a1856b2a0bc7"

inherit pypi setuptools3 ptest-python-pytest

PACKAGES =+ "${PN}-tests"
FILES:${PN}-tests = "${libdir}/${PYTHON_DIR}/site-packages/OpenSSL/test"

RDEPENDS:${PN}:class-target = " \
    python3-cryptography \
    python3-threading \
"
RDEPENDS:${PN}-tests = "${PN}"

RDEPENDS:${PN}-ptest += " \
    python3-datetime \
    python3-io \
    python3-netclient \
    python3-pretend \
"

CVE_PRODUCT = "jean-paul_calderone:pyopenssl pyca:pyopenssl pyopenssl:pyopenssl pyopenssl_project:pyopenssl"

BBCLASSEXTEND = "native nativesdk"
