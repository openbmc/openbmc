DESCRIPTION = "A Python SOCKS client module"
HOMEPAGE = "http://python-requests.org"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d457bcffb9661b45f799d4efee72f16"

SRC_URI[md5sum] = "07007ec7ed46292dab06013f2e3af365"
SRC_URI[sha256sum] = "d00329f27efa157db7efe3ca26fcd69033cd61f83822461ee3f8a353b48e33cf"

PYPI_PACKAGE = "PySocks"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-io \
"

BBCLASSEXTEND = "native nativesdk"
