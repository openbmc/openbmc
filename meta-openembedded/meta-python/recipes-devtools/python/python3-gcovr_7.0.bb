DESCRIPTION = "generate GCC code coverage reports"
HOMEPAGE = "https://gcovr.com"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c5a58ca91c1758a53f896ba89d8aaac2"

SRC_URI = "git://github.com/gcovr/gcovr.git;branch=main;protocol=https"
SRCREV = "fc190bcb85a25f5738315434a11f8e993edce515"

S = "${WORKDIR}/git"

inherit setuptools3
PIP_INSTALL_PACKAGE = "gcovr"

RDEPENDS:${PN} += "${PYTHON_PN}-jinja2 ${PYTHON_PN}-lxml ${PYTHON_PN}-setuptools ${PYTHON_PN}-pygments ${PYTHON_PN}-multiprocessing"

BBCLASSEXTEND = "native nativesdk"
