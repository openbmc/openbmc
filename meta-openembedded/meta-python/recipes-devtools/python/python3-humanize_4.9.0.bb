SUMMARY = "Python humanize utilities"
HOMEPAGE = "http://github.com/jmoiron/humanize"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[sha256sum] = "582a265c931c683a7e9b8ed9559089dea7edcf6cc95be39a3cbc2c5d5ac2bcfa"

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
