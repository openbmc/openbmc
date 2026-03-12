# SPDX-License-Identifier: MIT
# Copyright (C) 2023 iris-GmbH infrared & intelligent sensors

SUMMARY = "pyproject-api aims to abstract away interaction with pyproject.toml style projects in a flexible way."
HOMEPAGE = "https://pyproject-api.readthedocs.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=11610a9d8fd95649cf8159be12b98cb7"

SRC_URI[sha256sum] = "40c6f2d82eebdc4afee61c773ed208c04c19db4c4a60d97f8d7be3ebc0bbb330"

PYPI_PACKAGE = "pyproject_api"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

BBCLASSEXTEND = "native nativesdk"
inherit pypi python_hatchling

DEPENDS += "\
    python3-hatch-vcs-native \
"

RDEPENDS:${PN} += "\
    python3-packaging \
    python3-tomli \
"
