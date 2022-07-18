SUMMARY = "Flite: a small run-time speech synthesis engine"
HOMEPAGE = "http://cmuflite.org"
SECTION = "libs/multimedia"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b3b732d1349633a53e69356607fd2d6c"

inherit autotools-brokensep

SRC_URI = "git://github.com/festvox/flite.git;protocol=https;branch=master"

SRCREV = "e9e2e37c329dbe98bfeb27a1828ef9a71fa84f88"

S = "${WORKDIR}/git"

EXTRA_OECONF += "--enable-shared"

do_install:append() {
    chown -R root:root ${D}${libdir}/*
}
