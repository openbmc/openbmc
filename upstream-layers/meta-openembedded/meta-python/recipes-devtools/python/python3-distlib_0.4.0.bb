# SPDX-License-Identifier: MIT
# Copyright (C) 2023 iris-GmbH infrared & intelligent sensors

SUMMARY = "A library which implements low-level functions that relate to packaging and distribution of Python software."
HOMEPAGE = "https://github.com/pypa/distlib"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f6a11430d5cd6e2cd3832ee94f22ddfc"

SRC_URI[sha256sum] = "feec40075be03a04501a973d81f633735b4b69f98b05450592310c0f401a4e0d"

BBCLASSEXTEND = "native nativesdk"
inherit pypi python_setuptools_build_meta
