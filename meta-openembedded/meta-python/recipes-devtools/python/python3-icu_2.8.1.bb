SUMMARY = "Python extension wrapping the ICU C++ API"
HOMEPAGE = "https://gitlab.pyicu.org/main/pyicu"
BUGTRACKER = "https://gitlab.pyicu.org/main/pyicu/-/issues"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0724597863f1581ab01429e0423e779f"

DEPENDS += "icu"

PYPI_PACKAGE = "PyICU"
SRC_URI[sha256sum] = "f0b9549a87f87ba7c413f13679d137271e0b37f1f39b0109ace38257d4d148d6"

SRC_URI += "file://0001-Fix-host-contamination-of-include-files.patch"

inherit pkgconfig pypi python_setuptools_build_meta
