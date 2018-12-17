DESCRIPTION = "Python HTTP for Humans."
HOMEPAGE = "http://python-requests.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bfbeafb85a2cee261510d65d5ec19156"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-requests:"

SRC_URI += "file://0001-Strip-Authorization-header-whenever-root-URL-changes.patch \
            file://0002-Rework-authorization-stripping-logic-as-discussed.patch \
            "

SRC_URI[md5sum] = "6c1a31afec9d614e2e71a91ee6ca2878"
SRC_URI[sha256sum] = "ec22d826a36ed72a7358ff3fe56cbd4ba69dd7a6718ffd450ff0e9df7a47ce6a"

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

BBCLASSEXTEND = "native nativesdk"
