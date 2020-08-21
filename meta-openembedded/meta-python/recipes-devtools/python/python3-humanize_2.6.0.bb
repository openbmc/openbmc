SUMMARY = "Python humanize utilities"
HOMEPAGE = "http://github.com/jmoiron/humanize"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[md5sum] = "de3a507d9274912a3a177849a9349edb"
SRC_URI[sha256sum] = "8ee358ea6c23de896b9d1925ebe6a8504bb2ba7e98d5ccf4d07ab7f3b28f3819"

inherit pypi setuptools3

DEPENDS += "\
    ${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
"

BBCLASSEXTEND = "native nativesdk"
