SUMMARY = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/shazow/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=06dfcbb88fb0787a2039e26af11276f5"

SRC_URI[md5sum] = "e62d42006807308fea9556c17af8e250"
SRC_URI[sha256sum] = "cc49b66adcb45e19c6944cff35eedb60ee2228f0afba51f4da29c47e7a398f15"

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
