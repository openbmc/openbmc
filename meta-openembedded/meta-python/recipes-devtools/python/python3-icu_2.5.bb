SUMMARY = "Python extension wrapping the ICU C++ API"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7c4bfd81a21e3b6368bdcced992bf59"

DEPENDS += "pkgconfig icu"

PYPI_PACKAGE = "PyICU"
SRC_URI[sha256sum] = "a120b68c53f769f37bfb70b7e84ca12c3f4ab1e4df43e87a02dff05ae472cdbc"

SRC_URI += "file://fix_host_include.patch"

inherit pypi setuptools3
