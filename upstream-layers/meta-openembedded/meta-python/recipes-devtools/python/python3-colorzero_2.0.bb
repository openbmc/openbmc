SUMMARY = "colorzero is a color manipulation library for Python"
DESCRIPTION = "colorzero is a color manipulation library for Python \
(yes, another one) which aims to be reasonably simple to use and \
"pythonic" in nature."
HOMEPAGE = " https://github.com/waveform80/colorzero "

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ae6c62710c0646f3c60d1ad812ea9bf9"

RDEPENDS:${PN} += " \
    python3-image \
    python3-numbers \
"

SRC_URI[sha256sum] = "e7d5a5c26cd0dc37b164ebefc609f388de24f8593b659191e12d85f8f9d5eb58"

inherit pypi setuptools3

PYPI_PACKAGE = "colorzero"
