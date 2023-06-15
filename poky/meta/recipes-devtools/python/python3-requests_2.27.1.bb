DESCRIPTION = "Python HTTP for Humans."
HOMEPAGE = "http://python-requests.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI += "file://CVE-2023-32681.patch"

SRC_URI[sha256sum] = "68d7c56fd5a8999887728ef304a6d12edc7be74f1cfa47714fc8b414525c9a61"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
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
