SUMMARY = "PyEphem astronomical calculations"
HOMEPAGE = "http://rhodesmill.org/pyephem/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c930b395b435b00bb13ec83b0c99f40"

SRC_URI[sha256sum] = "d03de73ebf6a91681d597eb5b5d43bcf6f0c67e292bba2f9a974734b4f15757e"

PYPI_PACKAGE = "ephem"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-math \
    "
