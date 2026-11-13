SUMMARY = "Python extension wrapping the ICU C++ API"
HOMEPAGE = "https://gitlab.pyicu.org/main/pyicu"
BUGTRACKER = "https://gitlab.pyicu.org/main/pyicu/-/issues"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0724597863f1581ab01429e0423e779f"

DEPENDS += "icu"

PYPI_PACKAGE = "pyicu"

SRC_URI[sha256sum] = "6138b95ac9592fa3983bf489e0c625e8fcfffd0c562a36a08269cecc4ffb48be"

SRC_URI += "file://0001-Fix-host-contamination-of-include-files.patch"

inherit pkgconfig pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

BBCLASSEXTEND = "native nativesdk"
