SUMMARY = "the blessed package to manage your versions by scm tags"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "c85b6b46d0edd40d2301038cdea96bb6adc14d62ef943e75afb08b3e7bcf142a"

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
