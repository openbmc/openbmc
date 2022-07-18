SUMMARY = "Python humanize utilities"
HOMEPAGE = "http://github.com/jmoiron/humanize"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[sha256sum] = "3a119b242ec872c029d8b7bf8435a61a5798f124b244a08013aec5617302f80e"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    ${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
