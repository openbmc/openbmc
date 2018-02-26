DESCRIPTION = "Provides enhanced HTTPS support for httplib and urllib2 using PyOpenSSL"
HOMEPAGE = "http://python-requests.org"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://MANIFEST.in;md5=ce22c0cd986d2de3f7073cd6b5523ae0"

SRC_URI[md5sum] = "59a2f0a5b3b60e6c520a9cc7be9aff87"
SRC_URI[sha256sum] = "7bfd8c5cfcbc241a93ca6a4e45f952650f5c7ecf7c49b1dbcf5f4d390240be0b"

PYPI_PACKAGE = "ndg_httpsclient"

DEPENDS += " \
    ${PYTHON_PN}-pyopenssl \
    ${PYTHON_PN}-pyasn1 \
"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = ""
