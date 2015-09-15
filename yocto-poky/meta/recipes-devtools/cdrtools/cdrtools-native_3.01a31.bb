# cdrtools-native OE build file
# Copyright (C) 2004-2006, Advanced Micro Devices, Inc.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)
SUMMARY = "A set of tools for CD recording, including cdrecord"
HOMEPAGE = "http://sourceforge.net/projects/cdrtools/"
SECTION = "console/utils"
LICENSE = "GPLv2 & CDDL-1.0 & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=32f68170be424c2cd64804337726b312"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/cdrtools/alpha/cdrtools-${PV}.tar.bz2"

SRC_URI[md5sum] = "78172557894f469b4584d008e93ec469"
SRC_URI[sha256sum] = "183b5c12777779e78d8b69461aae52401f863768e7e7391d60730006f8cadc5a"

S = "${WORKDIR}/cdrtools-3.01"

inherit native

do_install() {
	make install GMAKE_NOWARN=true INS_BASE=${prefix} DESTDIR=${D}
}
