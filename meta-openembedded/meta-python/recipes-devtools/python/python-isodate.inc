SUMMARY = "ISO 8601 date/time parser"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e910b35b0ef4e1f665b9a75d6afb7709"

SRC_URI[md5sum] = "0e1203fce27ce65e2d01c5f21c4d428f"
SRC_URI[sha256sum] = "2e364a3d5759479cdb2d37cce6b9376ea504db2ff90252a2e5b7cc89cc9ff2d8"

inherit pypi

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native nativesdk"
