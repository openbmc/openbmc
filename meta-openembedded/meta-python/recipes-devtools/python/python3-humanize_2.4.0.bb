SUMMARY = "Python humanize utilities"
HOMEPAGE = "http://github.com/jmoiron/humanize"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[md5sum] = "010e86a4196ed6030112e7bc681e7353"
SRC_URI[sha256sum] = "42ae7d54b398c01bd100847f6cb0fc9e381c21be8ad3f8e2929135e48dbff026"

inherit pypi setuptools3

DEPENDS += "\
    ${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
"

BBCLASSEXTEND = "native nativesdk"
