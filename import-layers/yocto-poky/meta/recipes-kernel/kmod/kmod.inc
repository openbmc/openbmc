# Copyright (C) 2012 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Tools for managing Linux kernel modules"
DESCRIPTION = "kmod is a set of tools to handle common tasks with Linux kernel modules like \
               insert, remove, list, check properties, resolve dependencies and aliases."
HOMEPAGE = "http://packages.profusion.mobi/kmod/"
LICENSE = "GPL-2.0+ & LGPL-2.1+"
LICENSE_libkmod = "LGPL-2.1+"
SECTION = "base"

LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://libkmod/COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                   "
inherit autotools gtk-doc pkgconfig manpages

SRCREV = "aca4eca103d6699543f7ed663335c28c2e9908bb"
# Lookout for PV bump too when SRCREV is changed
PV = "25+git${SRCPV}"

SRC_URI = "git://git.kernel.org/pub/scm/utils/kernel/kmod/kmod.git \
           file://depmod-search.conf \
           file://avoid_parallel_tests.patch \
           file://fix-O_CLOEXEC.patch \
          "

S = "${WORKDIR}/git"

EXTRA_AUTORECONF += "--install --symlink"
EXTRA_OECONF +=" --enable-tools --with-zlib"

CACHED_CONFIGUREVARS += "ac_cv_path_DOLT_BASH='/usr/bin/env bash'"

PACKAGECONFIG[debug] = "--enable-debug,--disable-debug"
PACKAGECONFIG[logging] = " --enable-logging,--disable-logging"
PACKAGECONFIG[manpages] = "--enable-manpages, --disable-manpages, libxslt-native xmlto-native"

GTKDOC_DOCDIR = "${S}/libkmod/docs"

do_configure_append () {
	sed -i 's#}libtool#}${TARGET_SYS}-libtool#' ${B}/doltlibtool
}
