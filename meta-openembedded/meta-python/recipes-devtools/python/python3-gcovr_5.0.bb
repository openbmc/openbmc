DESCRIPTION = "generate GCC code coverage reports"
HOMEPAGE = "https://gcovr.com"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=08208c66520e8d69d5367483186d94ed"

SRC_URI = "git://github.com/gcovr/gcovr.git;branch=master;protocol=https"
SRCREV = "2b50284e8a6792b4ddcba14e2050c5c05f15deb6"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-jinja2 ${PYTHON_PN}-lxml ${PYTHON_PN}-setuptools ${PYTHON_PN}-pygments"

BBCLASSEXTEND = "native nativesdk"
