# SPDX-License-Identifier: MIT
# Copyright (C) 2023 iris-GmbH infrared & intelligent sensors

SUMMARY = "A library which implements low-level functions that relate to packaging and distribution of Python software."
HOMEPAGE = "https://github.com/pypa/distlib"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f6a11430d5cd6e2cd3832ee94f22ddfc"

SRC_URI[sha256sum] = "ca4175b4ac7f71223cc30ed7248c45d2b38c2439d8ee6e498ab67ed63cf3508d"

BBCLASSEXTEND = "native nativesdk"
inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"
