SUMMARY = "Various data structures and parsing tools for UEFI firmware."
DESCRIPTION = "This package contains Python UEFI firmware parser tool. The \
UEFI firmware parser is a simple module and set of scripts for parsing, \
extracting, and recreating UEFI firmware volumes. This includes parsing \
modules for BIOS, OptionROM, Intel ME and other formats too."
HOMEPAGE = "https://github.com/theopolis/uefi-firmware-parser"
AUTHOR = "Teddy Reed <teddy@prosauce.org>"
LICENSE = "BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://setup.py;md5=90fa5bae1547550f1c1993f651eda955"

SRC_URI = "git://github.com/theopolis/uefi-firmware-parser;protocol=https;branch=master"

SRCREV = "502f1bada1ed11fdd1792646fda0bfafb8fa57fb"

S = "${WORKDIR}/git"

inherit setuptools3
