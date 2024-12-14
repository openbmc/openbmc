# cdrtools-native OE build file
# Copyright (C) 2004-2006, Advanced Micro Devices, Inc.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)
SUMMARY = "A set of tools for CD recording, including cdrecord"
HOMEPAGE = "http://sourceforge.net/projects/cdrtools/"
DESCRIPTION = "cdrecord tool is Highly portable CD/DVD/BluRay command line recording software."
SECTION = "console/utils"
LICENSE = "GPL-2.0-only & CDDL-1.0 & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=32f68170be424c2cd64804337726b312"

DEPENDS += "gnu-config-native"

SRC_URI = " \
	${SOURCEFORGE_MIRROR}/project/cdrtools/cdrtools-${PV}.tar.bz2 \
	file://0001-Don-t-set-uid-gid-during-install.patch \
	file://riscv64-linux-gcc.rul \
	file://gcc14-fix.patch \
	"

SRC_URI[md5sum] = "7d45c5b7e1f78d85d1583b361aee6e8b"
SRC_URI[sha256sum] = "ed282eb6276c4154ce6a0b5dee0bdb81940d0cbbfc7d03f769c4735ef5f5860f"

EXTRA_OEMAKE = "-e MAKEFLAGS= CPPOPTX='${CPPFLAGS}' COPTX='${CFLAGS}' C++OPTX='${CXXFLAGS}' LDOPTX='${LDFLAGS}' GMAKE_NOWARN='true'"

# Stop failures when 'cc' can't be found
export ac_cv_prog_CC = "${CC}"

inherit sourceforge-releases native

# Use -std=gnu89 to build with gcc-14 (https://bugs.gentoo.org/903876)
# this needs to be after native inherit (which sets CFLAGS to BUILD_CFLAGS)
CFLAGS += "-std=gnu89"

do_configure() {
        # cdda2wav does not build with GCC 14
        rm -f ${S}/TARGETS/55cdda2wav

        install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/autoconf
        install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/autoconf
        install -m 0644 ${UNPACKDIR}/riscv64-linux-gcc.rul ${S}/RULES/
}

do_install() {
	make install GMAKE_NOWARN=true INS_BASE=${prefix} DESTDIR=${D}
}
