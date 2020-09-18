SUMMARY = "Simple module to parse ISO 8601 dates"
HOMEPAGE = "http://pyiso8601.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b05625f2336fa024e8d57e65c6595844"

SRC_URI[md5sum] = "df6e924305fd799d90be63336b2ab8b3"
SRC_URI[sha256sum] = "f7dec22af52025d4526be94cc1303c7d8f5379b746a3f54a8c8446384392eeb1"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
