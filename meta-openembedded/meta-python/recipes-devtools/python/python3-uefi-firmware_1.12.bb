SUMMARY = "Various data structures and parsing tools for UEFI firmware."
DESCRIPTION = "This package contains Python UEFI firmware parser tool. The \
UEFI firmware parser is a simple module and set of scripts for parsing, \
extracting, and recreating UEFI firmware volumes. This includes parsing \
modules for BIOS, OptionROM, Intel ME and other formats too."
HOMEPAGE = "https://github.com/theopolis/uefi-firmware-parser"
LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://setup.py;md5=5a59066a8676f17262ef7e691f8ef253"

SRC_URI = "git://github.com/theopolis/uefi-firmware-parser;protocol=https;branch=master"

SRCREV = "0c5fb74bcecc0e5c80625c38130fe948c2a3656e"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-logging \
"
