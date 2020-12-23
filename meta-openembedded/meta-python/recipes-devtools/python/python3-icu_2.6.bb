SUMMARY = "Python extension wrapping the ICU C++ API"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7c4bfd81a21e3b6368bdcced992bf59"

DEPENDS += "pkgconfig icu"

PYPI_PACKAGE = "PyICU"
SRC_URI[sha256sum] = "a9a5bf6833360f8f69e9375b91c1a7dd6e0c9157a42aee5bb7d6891804d96371"

SRC_URI += "file://fix_host_include.patch"

inherit pypi setuptools3
