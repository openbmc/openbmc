SUMMARY = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/shazow/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=65715c2eb961313d71b297dd5a04f85e"

SRC_URI[md5sum] = "dbf9b868b90880b24b1ac278094e912e"
SRC_URI[sha256sum] = "3018294ebefce6572a474f0604c2021e33b3fd8006ecd11d62107a5d2a963527"

inherit pypi setuptools3

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
