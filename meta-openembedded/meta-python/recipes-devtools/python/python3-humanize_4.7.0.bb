SUMMARY = "Python humanize utilities"
HOMEPAGE = "http://github.com/jmoiron/humanize"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[sha256sum] = "7ca0e43e870981fa684acb5b062deb307218193bca1a01f2b2676479df849b3a"

inherit pypi python_hatchling

DEPENDS += "\
    ${PYTHON_PN}-setuptools-scm-native \
    ${PYTHON_PN}-hatch-vcs-native \
"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
