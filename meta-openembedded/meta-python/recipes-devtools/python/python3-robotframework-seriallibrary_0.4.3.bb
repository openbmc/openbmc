SUMMARY = "Robot Framework test library for serial connection"
HOMEPAGE = "https://github.com/whosaysni/robotframework-seriallibrary"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=1af2e051b493d9552af443cf2f99d480"

SRC_URI[sha256sum] = "f20befe5c1106dd8ddca9f60a2f18bf5ec7d5f06f6f09a03fa66bae54777e6bb"

PYPI_PACKAGE = "robotframework-seriallibrary"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-pyserial \
    python3-robotframework \
"
