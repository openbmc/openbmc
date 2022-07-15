SUMMARY = "Python humanize utilities"
HOMEPAGE = "http://github.com/jmoiron/humanize"
SECTION = "devel/python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=4ecc42519e84f6f3e23529464df7bd1d"

SRC_URI[sha256sum] = "2bc1fdd831cd00557d3010abdd84d3e41b4a96703a3eaf6c24ee290b26b75a44"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    ${PYTHON_PN}-setuptools-scm-native \
"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
