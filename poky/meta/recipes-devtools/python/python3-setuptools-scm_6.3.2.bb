SUMMARY = "the blessed package to manage your versions by scm tags"
HOMEPAGE = "https://pypi.org/project/setuptools-scm/"
DESCRIPTION = "setuptools_scm handles managing your Python package versions in SCM metadata instead of declaring them as the version argument or in a SCM managed file."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "a49aa8081eeb3514eb9728fa5040f2eaa962d6c6f4ec9c32f6c1fba88f88a0f2"

PYPI_PACKAGE = "setuptools_scm"
inherit pypi setuptools3

UPSTREAM_CHECK_REGEX = "setuptools_scm-(?P<pver>.*)\.tar"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-packaging \
    ${PYTHON_PN}-pyparsing \
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-tomli \
"

RDEPENDS:${PN}:append:class-target = " \
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-json \
"

BBCLASSEXTEND = "native nativesdk"
