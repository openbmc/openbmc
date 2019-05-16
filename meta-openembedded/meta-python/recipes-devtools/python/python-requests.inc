DESCRIPTION = "Python HTTP for Humans."
HOMEPAGE = "http://python-requests.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8d5a1d1c2d53025e2282c511033f6f7"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-requests:"

SRC_URI[md5sum] = "1bcd0e0977c3f8db1848ba0e2b7ab904"
SRC_URI[sha256sum] = "502a824f31acdacb3a35b6690b5fbf0bc41d63a24a45c4004352b0242707598e"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-ndg-httpsclient \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-pyasn1 \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-pysocks \
    ${PYTHON_PN}-urllib3 \
    ${PYTHON_PN}-chardet \
    ${PYTHON_PN}-idna \
"

CVE_PRODUCT = "requests"

BBCLASSEXTEND = "native nativesdk"
