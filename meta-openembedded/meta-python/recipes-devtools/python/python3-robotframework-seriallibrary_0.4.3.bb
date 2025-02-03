SUMMARY = "Robot Framework test library for serial connection"
HOMEPAGE = "https://github.com/whosaysni/robotframework-seriallibrary"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ad407cda031a6aaf51a1fecaaa6e7d29"

SRC_URI[sha256sum] = "f20befe5c1106dd8ddca9f60a2f18bf5ec7d5f06f6f09a03fa66bae54777e6bb"

PYPI_PACKAGE = "robotframework-seriallibrary"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-pyserial \
    python3-robotframework \
"
