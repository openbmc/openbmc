SUMMARY = "PyEphem astronomical calculations"
HOMEPAGE = "https://rhodesmill.org/pyephem/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c930b395b435b00bb13ec83b0c99f40"

SRC_URI[sha256sum] = "3c4fd64f453e8f40cf862420a70da95a71b6487ace75e8e0cf85d73707db6065"

PYPI_PACKAGE = "ephem"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-math \
    "
