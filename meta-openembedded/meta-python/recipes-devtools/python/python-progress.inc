SUMMARY = "Easy progress reporting for Python"
HOMEPAGE = "http://github.com/verigak/progress/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aef5566ac4fede9815eccf124c281317"

SRC_URI[md5sum] = "408df0e3db0ad4b74f19f6beec814ae4"
SRC_URI[sha256sum] = "69ecedd1d1bbe71bf6313d88d1e6c4d2957b7f1d4f71312c211257f7dae64372"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-math \
"

BBCLASSEXTEND = "native nativesdk"
