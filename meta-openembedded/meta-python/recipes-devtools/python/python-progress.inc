SUMMARY = "Easy progress reporting for Python"
HOMEPAGE = "http://github.com/verigak/progress/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aef5566ac4fede9815eccf124c281317"

SRC_URI[md5sum] = "057981e2f09029288f5255b1ea3ab64d"
SRC_URI[sha256sum] = "5e2f9da88ed8236a76fffbee3ceefd259589cf42dfbc2cec2877102189fae58a"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-math \
"

BBCLASSEXTEND = "native nativesdk"
