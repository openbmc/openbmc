SUMMARY = "Various data structures and parsing tools for UEFI firmware."
DESCRIPTION = "This package contains Python UEFI firmware parser tool. The \
UEFI firmware parser is a simple module and set of scripts for parsing, \
extracting, and recreating UEFI firmware volumes. This includes parsing \
modules for BIOS, OptionROM, Intel ME and other formats too."
HOMEPAGE = "https://github.com/theopolis/uefi-firmware-parser"
LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://setup.py;md5=f3ddb5c3a389eada068ddcf6b89f62b2"

SRC_URI = "git://github.com/theopolis/uefi-firmware-parser;protocol=https;branch=master;tag=v${PV}"

SRCREV = "e202e576cefd1fefb3f510237bbd0aea8aedca07"

inherit setuptools3

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-logging \
"
