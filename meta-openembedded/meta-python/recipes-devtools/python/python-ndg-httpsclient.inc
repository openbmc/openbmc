DESCRIPTION = "Provides enhanced HTTPS support for httplib and urllib2 using PyOpenSSL"
HOMEPAGE = "http://python-requests.org"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://MANIFEST.in;md5=ce22c0cd986d2de3f7073cd6b5523ae0"

SRC_URI[md5sum] = "a0d349cf9530e331697a6e5ab1f40de6"
SRC_URI[sha256sum] = "fba4d4798dcac2965874f24afb6631b4326baa4bd02505744d34f690c354856a"

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
