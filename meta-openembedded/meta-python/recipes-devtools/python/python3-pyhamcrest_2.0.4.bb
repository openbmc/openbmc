SUMMARY = "Hamcrest framework for matcher objects"
HOMEPAGE = "https://github.com/hamcrest/PyHamcrest"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=79391bf1501c898472d043f36e960612"

SRC_URI[sha256sum] = "b5d9ce6b977696286cf232ce2adf8969b4d0b045975b0936ac9005e84e67e9c1"

inherit pypi python_setuptools_build_meta

DEPENDS += "${PYTHON_PN}-hatch-vcs-native"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/PyHamcrest/"
UPSTREAM_CHECK_REGEX = "/PyHamcrest/(?P<pver>(\d+[\.\-_]*)+)"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-numbers \
"
