# SPDX-License-Identifier: MIT
# Copyright (C) 2023 iris-GmbH infrared & intelligent sensors

SUMMARY = "A single module, which implements a platform independent file lock in Python, which provides a simple way of inter-process communication"
HOMEPAGE = "https://py-filelock.readthedocs.io/"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=911690f51af322440237a253d695d19f"

SRC_URI[sha256sum] = "d13f466618bfde72bd2c18255e269f72542c6e70e7bac83a0232d6b1cc5c8cf4"

BBCLASSEXTEND = "native nativesdk"
inherit pypi python_hatchling

DEPENDS += "\
    python3-hatch-vcs-native \
"
