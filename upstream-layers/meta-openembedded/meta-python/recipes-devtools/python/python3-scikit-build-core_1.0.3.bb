SUMMARY = "Build backend for CMake based projects"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b4e748e5f102e31c9390dcd6fa66f09"

PYPI_PACKAGE = "scikit_build_core"

DEPENDS = "python3-hatch-vcs-native"

inherit pypi python_hatchling

SRC_URI += " \
	file://0001-builder.py-Check-PYTHON_INCLUDE_DIR.patch \
	file://0001-Find-cmake-from-PATH-instead-of-CMAKE_BIN_DIR.patch \
	file://0001-src-scikit_build_core-Don-t-use-module.patch \
"
SRC_URI[sha256sum] = "a4d7a05978ee37975c37743510c8991e2debce7ef83afb0a07c0c576fd4f16e8"

BBCLASSEXTEND = "native nativesdk"
