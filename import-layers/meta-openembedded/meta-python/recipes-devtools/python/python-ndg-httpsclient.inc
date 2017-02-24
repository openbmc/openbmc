DESCRIPTION = "Provides enhanced HTTPS support for httplib and urllib2 using PyOpenSSL"
HOMEPAGE = "http://python-requests.org"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://MANIFEST.in;md5=ce22c0cd986d2de3f7073cd6b5523ae0"

SRC_URI[md5sum] = "b3a958c0e9aaf4d3f8dd933f46f8c1a9"
SRC_URI[sha256sum] = "580987ef194334c50389e0d7de885fccf15605c13c6eecaabd8d6c43768eb8ac"

PYPI_PACKAGE = "ndg_httpsclient"

DEPENDS = " \
    ${PYTHON_PN}-pyopenssl \
"

DEPENDS = " \
    ${PYTHON_PN}-pyasn1 \
"

BBCLASSEXTEND = "native nativesdk"
