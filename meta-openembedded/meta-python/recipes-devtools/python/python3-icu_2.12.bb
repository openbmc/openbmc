SUMMARY = "Python extension wrapping the ICU C++ API"
HOMEPAGE = "https://gitlab.pyicu.org/main/pyicu"
BUGTRACKER = "https://gitlab.pyicu.org/main/pyicu/-/issues"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0724597863f1581ab01429e0423e779f"

DEPENDS += "icu"

PYPI_PACKAGE = "PyICU"
SRC_URI[sha256sum] = "bd7ab5efa93ad692e6daa29cd249364e521218329221726a113ca3cb281c8611"

SRC_URI += "file://0001-Fix-host-contamination-of-include-files.patch"

inherit pkgconfig pypi python_setuptools_build_meta
