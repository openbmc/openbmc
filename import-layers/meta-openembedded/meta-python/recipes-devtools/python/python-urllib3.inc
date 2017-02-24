SUMMARY = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/shazow/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ea114851ad9a8c311aac8728a681a067"

SRC_URI[md5sum] = "1eadd93bd5e58d3528d977111f2997ea"
SRC_URI[sha256sum] = "d30dd4d1cbbc36ebab6e4a2076820cfe0c60f10adf087fecbc5a3edae79fdc81"

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
