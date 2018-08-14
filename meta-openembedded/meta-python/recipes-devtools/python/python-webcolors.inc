SUMMARY = "Simple Python module for working with HTML/CSS color definitions."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=197add016087e6884a012b0f185d44ad"

SRC_URI[md5sum] = "40890db38b2a856e526a568864025fe6"
SRC_URI[sha256sum] = "030562f624467a9901f0b455fef05486a88cfb5daa1e356bd4aacea043850b59"

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-stringold \
"

BBCLASSEXTEND = "native nativesdk"
