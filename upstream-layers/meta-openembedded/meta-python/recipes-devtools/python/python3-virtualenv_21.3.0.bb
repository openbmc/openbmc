# SPDX-License-Identifier: MIT
# Copyright (C) 2023 iris-GmbH infrared & intelligent sensors

SUMMARY = "A tool for creating isolated virtual python environments."
HOMEPAGE = "https://github.com/pypa/virtualenv"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0ce089158cf60a8ab6abb452b6405538"

SRC_URI[sha256sum] = "733750db978ec95c2d8eb4feadaa57091002bce404cb39ba69899cf7bd28944e"

BBCLASSEXTEND = "native nativesdk"
inherit pypi python_hatchling

DEPENDS += "\
    python3-hatch-vcs-native \
"

RDEPENDS:${PN} += " \
    python3-compression  \
    python3-distlib \
    python3-filelock \
    python3-json \
    python3-misc \
    python3-modules \
    python3-platformdirs \
"

CVE_PRODUCT = "virtualenv"
