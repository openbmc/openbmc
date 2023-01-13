SUMMARY = "Firmware analysis tool"
DESCRIPTION = "This package contains Python Binwalk tool. Binwalk is a fast, \
easy to use tool for analyzing, reverse engineering, and extracting firmware \
images."
HOMEPAGE = "https://github.com/ReFirmLabs/binwalk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65bbee055d3ea3bfc475f07aecf4de64"

SRC_URI = "git://github.com/ReFirmLabs/binwalk;protocol=https;branch=master"

SRCREV = "fa0c0bd59b8588814756942fe4cb5452e76c1dcd"

S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += "python3-core"
