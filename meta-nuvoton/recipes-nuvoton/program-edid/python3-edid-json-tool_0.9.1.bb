SUMMARY = "EDID to JSON tool"
DESCRIPTION = "This is a collection of tools that helps you work with \
EDID files, by parsing them as well as by converting between EDID \
binaries and JSON files."
HOMEPAGE = "https://github.com/rpavlik/edid-json-tools"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=dc64d2d7c140d9bd69045b5abaede3a7"

SRC_URI = "git://github.com/rpavlik/edid-json-tools.git;branch=main;protocol=https"
SRCREV = "1cb9416c10c8186b572fbbc98b51b67c00ec2e70"

S = "${WORKDIR}/git"

inherit setuptools3
PIP_INSTALL_PACKAGE = "edid-json-tools"

RDEPENDS:${PN} += " \
    python3-click \
"
BBCLASSEXTEND = "native"
