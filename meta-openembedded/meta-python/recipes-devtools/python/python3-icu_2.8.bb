SUMMARY = "Python extension wrapping the ICU C++ API"
HOMEPAGE = "https://gitlab.pyicu.org/main/pyicu"
BUGTRACKER = "https://gitlab.pyicu.org/main/pyicu/-/issues"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7c4bfd81a21e3b6368bdcced992bf59"

DEPENDS += "icu"

PYPI_PACKAGE = "PyICU"
SRC_URI[sha256sum] = "3d80de47045a8163db5aebc947c42b4d429eeea4f0c32af4f40b33981fa872b9"

SRC_URI += "file://0001-Fix-host-contamination-of-include-files.patch"

inherit pkgconfig pypi setuptools3
