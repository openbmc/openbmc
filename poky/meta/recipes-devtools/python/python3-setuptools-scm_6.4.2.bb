SUMMARY = "the blessed package to manage your versions by scm tags"
HOMEPAGE = "https://pypi.org/project/setuptools-scm/"
DESCRIPTION = "setuptools_scm handles managing your Python package versions in SCM metadata instead of declaring them as the version argument or in a SCM managed file."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "6833ac65c6ed9711a4d5d2266f8024cfa07c533a0e55f4c12f6eff280a5a9e30"

PYPI_PACKAGE = "setuptools_scm"
inherit pypi python_setuptools_build_meta

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
