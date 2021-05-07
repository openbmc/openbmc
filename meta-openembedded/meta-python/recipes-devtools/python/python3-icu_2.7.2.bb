SUMMARY = "Python extension wrapping the ICU C++ API"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=244;endline=252;md5=3e00c41c6d04310707992b93307a224f"

DEPENDS += "pkgconfig icu"

PYPI_PACKAGE = "PyICU"
SRC_URI[sha256sum] = "1382869b22d91cc99274f9b525fa7d9199b44d9007ff0036a09747839a01e9dc"

SRC_URI += "file://0001-Fix-host-contamination-of-include-files.patch"

inherit pypi setuptools3
