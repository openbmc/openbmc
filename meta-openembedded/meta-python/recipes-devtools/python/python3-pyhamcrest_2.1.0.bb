SUMMARY = "Hamcrest framework for matcher objects"
HOMEPAGE = "https://github.com/hamcrest/PyHamcrest"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=79391bf1501c898472d043f36e960612"

SRC_URI[sha256sum] = "c6acbec0923d0cb7e72c22af1926f3e7c97b8e8d69fc7498eabacaf7c975bd9c"

inherit pypi python_setuptools_build_meta

DEPENDS += "${PYTHON_PN}-hatch-vcs-native"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/PyHamcrest/"
UPSTREAM_CHECK_REGEX = "/PyHamcrest/(?P<pver>(\d+[\.\-_]*)+)"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-numbers \
"
