DESCRIPTION = "generate GCC code coverage reports"
HOMEPAGE = "https://gcovr.com"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e59af597b3484fa3b52c0fbfd5d17611"

SRC_URI = "git://github.com/gcovr/gcovr.git;branch=master;protocol=https"
SRCREV = "1040a85ecfb3ef0d01635df9d50a3cae5059d566"

S = "${WORKDIR}/git"

inherit setuptools3
PIP_INSTALL_PACKAGE = "gcovr"

RDEPENDS:${PN} += "${PYTHON_PN}-jinja2 ${PYTHON_PN}-lxml ${PYTHON_PN}-setuptools ${PYTHON_PN}-pygments"

BBCLASSEXTEND = "native nativesdk"
