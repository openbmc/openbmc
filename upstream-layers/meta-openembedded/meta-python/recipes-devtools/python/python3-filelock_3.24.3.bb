# SPDX-License-Identifier: MIT
# Copyright (C) 2023 iris-GmbH infrared & intelligent sensors
SUMMARY = "A single module, which implements a platform independent file lock in Python, which provides a simple way of inter-process communication"
HOMEPAGE = "https://py-filelock.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c6acbdf7bb74caa37512c3a5ca6857b"

SRC_URI += "file://run-ptest"
SRC_URI[sha256sum] = "011a5644dc937c22699943ebbfc46e969cdde3e171470a6e40b9533e5a72affa"

BBCLASSEXTEND = "native nativesdk"

inherit pypi python_hatchling ptest-python-pytest

DEPENDS += "\
    python3-hatch-vcs-native \
"

RDEPENDS:${PN} += " \
    python3-core \
    python3-logging \
    python3-asyncio \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest-asyncio \
    python3-pytest-mock \
    python3-virtualenv \
"
