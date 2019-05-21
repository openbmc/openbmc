DESCRIPTION = "A Python SOCKS client module"
HOMEPAGE = "http://python-requests.org"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d457bcffb9661b45f799d4efee72f16"

SRC_URI[md5sum] = "ffea8bdc4eee054df2f9f6a9c3752567"
SRC_URI[sha256sum] = "d9031ea45fdfacbe59a99273e9f0448ddb33c1580fe3831c1b09557c5718977c"

PYPI_PACKAGE = "PySocks"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
