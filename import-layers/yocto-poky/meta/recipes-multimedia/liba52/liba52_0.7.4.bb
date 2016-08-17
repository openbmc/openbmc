SUMMARY = "ATSC A/52 surround sound stream decoder"
HOMEPAGE = "http://liba52.sourceforge.net/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
			file://include/a52.h;beginline=1;endline=12;md5=81152ceb3562bf20a60d1b6018175dd1"
SECTION = "libs"
PR = "r4"

inherit autotools

SRC_URI = "http://liba52.sourceforge.net/files/a52dec-${PV}.tar.gz \
           file://buildcleanup.patch"

SRC_URI[md5sum] = "caa9f5bc44232dc8aeea773fea56be80"
SRC_URI[sha256sum] = "a21d724ab3b3933330194353687df82c475b5dfb997513eef4c25de6c865ec33"

UPSTREAM_CHECK_URI = "http://liba52.sourceforge.net/downloads.html"

S = "${WORKDIR}/a52dec-${PV}"

EXTRA_OECONF = " --enable-shared "

PACKAGES =+ "a52dec a52dec-doc"

FILES_a52dec = " ${bindir}/* "
FILES_a52dec-doc = " ${mandir}/man1/* "
