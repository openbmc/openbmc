# SPDX-License-Identifier: MIT
# Copyright (C) 2023 iris-GmbH infrared & intelligent sensors

SUMMARY = "A library which implements low-level functions that relate to packaging and distribution of Python software."
HOMEPAGE = "https://github.com/pypa/distlib"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f6a11430d5cd6e2cd3832ee94f22ddfc"

SRC_URI[sha256sum] = "14bad2d9b04d3a36127ac97f30b12a19268f211063d8f8ee4f47108896e11b46"

BBCLASSEXTEND = "native nativesdk"
inherit pypi python_setuptools_build_meta
