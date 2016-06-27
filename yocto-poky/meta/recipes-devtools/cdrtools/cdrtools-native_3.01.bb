# cdrtools-native OE build file
# Copyright (C) 2004-2006, Advanced Micro Devices, Inc.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)
SUMMARY = "A set of tools for CD recording, including cdrecord"
HOMEPAGE = "http://sourceforge.net/projects/cdrtools/"
SECTION = "console/utils"
LICENSE = "GPLv2 & CDDL-1.0 & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=32f68170be424c2cd64804337726b312"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/cdrtools/cdrtools-${REALPV}.tar.bz2"

SRC_URI[md5sum] = "7d45c5b7e1f78d85d1583b361aee6e8b"
SRC_URI[sha256sum] = "ed282eb6276c4154ce6a0b5dee0bdb81940d0cbbfc7d03f769c4735ef5f5860f"

EXTRA_OEMAKE = "-e MAKEFLAGS="

inherit native

PV = "3.01a31+really3.01"
REALPV = "3.01"

S = "${WORKDIR}/${BPN}-${REALPV}"

do_install() {
	make install GMAKE_NOWARN=true INS_BASE=${prefix} DESTDIR=${D}
}
