DESCRIPTION = "A Python SOCKS client module"
HOMEPAGE = "http://python-requests.org"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d457bcffb9661b45f799d4efee72f16"

SRC_URI[md5sum] = "441c4e3bf5b042f7e70613cd871df26c"
SRC_URI[sha256sum] = "3fe52c55890a248676fd69dc9e3c4e811718b777834bcaab7a8125cf9deac672"

PYPI_PACKAGE = "PySocks"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
