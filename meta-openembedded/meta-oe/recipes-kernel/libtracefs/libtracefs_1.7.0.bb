# Copyright (C) 2022 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Library to parse raw trace event formats	"
HOMEPAGE = "https://git.kernel.org/pub/scm/libs/libtrace/libtraceevent.git/"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/GPL-2.0;md5=e6a75371ba4d16749254a51215d13f97 \
                    file://LICENSES/LGPL-2.1;md5=b370887980db5dd40659b50909238dbd"
SECTION = "libs"
DEPENDS = "libtraceevent bison-native flex-native"

SRCREV = "aebab37379d0fbadc702d64aca0fe5cf18676404"
SRC_URI = "git://git.kernel.org/pub/scm/libs/libtrace/libtracefs.git;branch=${BPN};protocol=https \
           file://0001-makefile-Do-not-preserve-ownership-in-cp-command.patch \
           file://0001-tracefs-perf-Add-missing-headers-for-syscall-and-SYS.patch \
           "
S = "${WORKDIR}/git"

inherit pkgconfig

do_install() {
    oe_runmake install DESTDIR=${D} pkgconfig_dir=${libdir}/pkgconfig
}
