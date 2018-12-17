SUMMARY = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/shazow/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ea114851ad9a8c311aac8728a681a067"

SRC_URI[md5sum] = "1ad43e4b5ab7a0794c89dc057e1d7179"
SRC_URI[sha256sum] = "a68ac5e15e76e7e5dd2b8f94007233e01effe3e50e8daddf69acfd81cb686baf"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-certifi \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
