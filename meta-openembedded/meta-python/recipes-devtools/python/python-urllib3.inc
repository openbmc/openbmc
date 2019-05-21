SUMMARY = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/shazow/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=65715c2eb961313d71b297dd5a04f85e"

SRC_URI[md5sum] = "1ebfbbec8bee21dbb686c009b37f4963"
SRC_URI[sha256sum] = "a53063d8b9210a7bdec15e7b272776b9d42b2fd6816401a0d43006ad2f9902db"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-certifi \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-threading \
"

CVE_PRODUCT = "urllib3"

BBCLASSEXTEND = "native nativesdk"
