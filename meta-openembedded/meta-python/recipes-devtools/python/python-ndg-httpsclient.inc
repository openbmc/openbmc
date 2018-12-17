DESCRIPTION = "Provides enhanced HTTPS support for httplib and urllib2 using PyOpenSSL"
HOMEPAGE = "http://python-requests.org"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://MANIFEST.in;md5=ce22c0cd986d2de3f7073cd6b5523ae0"

SRC_URI[md5sum] = "b0fc8ea38f87d2c1ab1ed79a95c078f9"
SRC_URI[sha256sum] = "d72faed0376ab039736c2ba12e30695e2788c4aa569c9c3e3d72131de2592210"

PYPI_PACKAGE = "ndg_httpsclient"

DEPENDS += " \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-pyasn1 \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-pyasn1 \
"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = ""
