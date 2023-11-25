DESCRIPTION = "generate GCC code coverage reports"
HOMEPAGE = "https://gcovr.com"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=6542fc4ce5904ffb741ef56f8fe33452"

SRC_URI = "git://github.com/gcovr/gcovr.git;branch=main;protocol=https"
SRCREV = "1221ef62ff0de15bbeaf79e68e08a65d62c73ff4"

S = "${WORKDIR}/git"

inherit setuptools3
PIP_INSTALL_PACKAGE = "gcovr"

RDEPENDS:${PN} += "${PYTHON_PN}-jinja2 ${PYTHON_PN}-lxml ${PYTHON_PN}-setuptools ${PYTHON_PN}-pygments ${PYTHON_PN}-multiprocessing"

BBCLASSEXTEND = "native nativesdk"
