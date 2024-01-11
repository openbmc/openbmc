SUMMARY = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/shazow/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=52d273a3054ced561275d4d15260ecda"

SRC_URI[sha256sum] = "c97dfde1f7bd43a71c8d2a58e369e9b2bf692d1334ea9f9cae55add7d0dd0f84"

inherit pypi python_hatchling

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-certifi \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-idna \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-logging \
"

CVE_PRODUCT = "urllib3"

BBCLASSEXTEND = "native nativesdk"
