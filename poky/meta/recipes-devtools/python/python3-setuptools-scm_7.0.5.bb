SUMMARY = "the blessed package to manage your versions by scm tags"
HOMEPAGE = "https://pypi.org/project/setuptools-scm/"
DESCRIPTION = "setuptools_scm handles managing your Python package versions in SCM metadata instead of declaring them as the version argument or in a SCM managed file."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "031e13af771d6f892b941adb6ea04545bbf91ebc5ce68c78aaf3fff6e1fb4844"

PYPI_PACKAGE = "setuptools_scm"
inherit pypi python_setuptools_build_meta

UPSTREAM_CHECK_REGEX = "setuptools_scm-(?P<pver>.*)\.tar"

DEPENDS += "python3-tomli-native python3-packaging-native python3-typing-extensions-native"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-packaging \
    ${PYTHON_PN}-pip \
    ${PYTHON_PN}-pyparsing \
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-tomli \
    ${PYTHON_PN}-typing-extensions \
"

RDEPENDS:${PN}:append:class-target = " \
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-json \
"

BBCLASSEXTEND = "native nativesdk"
