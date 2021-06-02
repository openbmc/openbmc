SUMMARY = "the blessed package to manage your versions by scm tags"
HOMEPAGE = "https://pypi.org/project/setuptools-scm/"
DESCRIPTION = "setuptools_scm handles managing your Python package versions in SCM metadata instead of declaring them as the version argument or in a SCM managed file."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "d1925a69cb07e9b29416a275b9fadb009a23c148ace905b2fb220649a6c18e92"

PYPI_PACKAGE = "setuptools_scm"
inherit pypi setuptools3

UPSTREAM_CHECK_REGEX = "setuptools_scm-(?P<pver>.*)\.tar"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-py \
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-toml \
"
RDEPENDS_${PN}_class-native = "\
    ${PYTHON_PN}-setuptools-native \
    ${PYTHON_PN}-toml-native \
"

BBCLASSEXTEND = "native nativesdk"
