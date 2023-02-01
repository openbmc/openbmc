# Copyright (C) 2019 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A standalone library to implement GNU libc's obstack"
DESCRIPTION = "copy + paste of the obstack functions and macros found in GNU gcc libiberty library for use with musl libc"
HOMEPAGE = "https://github.com/pullmoll/musl-obstack"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=3d23e4eef8243efcaab6f0a438078932"
SECTION = "libs"

PV = "1.2.3"
SRCREV = "f4385255be1615688c6a5f042277304d7ab288b1"
SRC_URI = "git://github.com/void-linux/musl-obstack;branch=master;protocol=https"

UPSTREAM_CHECK_COMMITS = "1"

inherit autotools pkgconfig

S = "${WORKDIR}/git"

COMPATIBLE_HOST = ".*-musl.*"

