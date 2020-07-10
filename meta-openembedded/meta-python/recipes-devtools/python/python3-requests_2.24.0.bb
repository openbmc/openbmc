DESCRIPTION = "Python HTTP for Humans."
HOMEPAGE = "http://python-requests.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19b6be66ed463d93fa88c29f7860bcd7"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-requests:"

SRC_URI[md5sum] = "b54bff26a389e5932e8b1c4983a99ce7"
SRC_URI[sha256sum] = "b3559a131db72c33ee969480840fff4bb6dd111de7dd27c8ee1f820f4f00231b"

inherit pypi setuptools3

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
