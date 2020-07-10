SUMMARY = "Python humanize utilities"
HOMEPAGE = "http://github.com/jmoiron/humanize"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[md5sum] = "636e2c0fa1465abf8f1ff677d00e11c4"
SRC_URI[sha256sum] = "8a68bd9bccb899fd9bfb1e6d96c1e84e4475551cc9a5b5bdbd69b9b1cfd19c80"

inherit pypi setuptools3

DEPENDS += "\
    ${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
"

BBCLASSEXTEND = "native nativesdk"
