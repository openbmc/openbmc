SUMMARY = "Simple powerful teting with python"
HOMEPAGE = "http://pytest.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c39b24965f4aef64222cb35de9d47cc4"

SRC_URI[md5sum] = "401c005bd57f71e63ba015ab086fe67d"
SRC_URI[sha256sum] = "f46e49e0340a532764991c498244a60e3a37d7424a532b3ff1a6a7653f1a403a"

SRC_URI_append = " file://0001-setup.py-remove-the-setup_requires-for-setuptools-scm.patch \
                   file://pytest_version_fix.patch "

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-py \
    ${PYTHON_PN}-setuptools \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-pytest:"

BBCLASSEXTEND = "native nativesdk"
