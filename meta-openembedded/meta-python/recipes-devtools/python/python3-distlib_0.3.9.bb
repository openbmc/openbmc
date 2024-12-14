# SPDX-License-Identifier: MIT
# Copyright (C) 2023 iris-GmbH infrared & intelligent sensors

SUMMARY = "A library which implements low-level functions that relate to packaging and distribution of Python software."
HOMEPAGE = "https://github.com/pypa/distlib"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f6a11430d5cd6e2cd3832ee94f22ddfc"

SRC_URI[sha256sum] = "a60f20dea646b8a33f3e7772f74dc0b2d0772d2837ee1342a00645c81edf9403"

BBCLASSEXTEND = "native nativesdk"
inherit pypi python_setuptools_build_meta
