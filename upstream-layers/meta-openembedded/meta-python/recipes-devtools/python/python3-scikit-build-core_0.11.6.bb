SUMMARY = "Build backend for CMake based projects"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b4e748e5f102e31c9390dcd6fa66f09"

PYPI_PACKAGE = "scikit_build_core"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

DEPENDS = "python3-hatch-vcs-native"

inherit pypi python_hatchling

SRC_URI += " \
	file://0001-builder.py-Check-PYTHON_INCLUDE_DIR.patch \
	file://0001-Find-cmake-from-PATH-instead-of-CMAKE_BIN_DIR.patch \
"
SRC_URI[sha256sum] = "5982ccd839735be99cfd3b92a8847c6c196692f476c215da84b79d2ad12f9f1b"

BBCLASSEXTEND = "native nativesdk"
