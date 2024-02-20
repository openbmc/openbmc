SUMMARY = "Implementation of rpmatch(3) for musl libc."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=81a81bf31abecc50c20862fc8a716329"

SRC_URI = "gitsm://github.com/pullmoll/musl-rpmatch.git;protocol=https;branch=master"

PV = "1.0+git"
SRCREV = "46267b154987d3e1f25d3a75423faa62bb5ee342"

inherit autotools

S = "${WORKDIR}/git"
