# SPDX-License-Identifier: MIT
# Copyright (C) 2023 iris-GmbH infrared & intelligent sensors

SUMMARY = "pyproject-api aims to abstract away interaction with pyproject.toml style projects in a flexible way."
HOMEPAGE = "https://pyproject-api.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11610a9d8fd95649cf8159be12b98cb7"

SRC_URI[sha256sum] = "b8807d85a293e6c9f133e6575946fed45f1d42b22d58c780b33aa2421a799549"

PYPI_PACKAGE = "pyproject_api"

BBCLASSEXTEND = "native nativesdk"
inherit pypi python_hatchling

DEPENDS += "\
    python3-hatch-vcs-native \
"

RDEPENDS:${PN} += "\
    python3-packaging \
    python3-tomli \
"
