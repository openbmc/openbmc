SUMMARY = "Job scheduling for humans"
HOMEPAGE = "https://github.com/dbader/schedule"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=6400f153491d45ea3459761627ca24b2"

SRC_URI[sha256sum] = "b4ad697aafba7184c9eb6a1e2ebc41f781547242acde8ceae9a0a25b04c0922d"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-datetime ${PYTHON_PN}-logging ${PYTHON_PN}-math"
