# Copyright (C) 2018 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "getconf, getent and iconv implementations for musl"
HOMEPAGE = "https://git.alpinelinux.org/cgit/aports/tree/main/musl"
LICENSE = "BSD-2-Clause & GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9d08215e611db87b357e8674b4b42564"
SECTION = "utils"

# Date of the commit in SRCREV
PV = "20170421"

SRCREV = "fb5630138ccabbbc14a19d372096a04e42573c7d"
SRC_URI = "git://github.com/boltlinux/musl-utils"

inherit autotools

S = "${WORKDIR}/git"

PACKAGES =+ "${PN}-iconv"

FILES_${PN}-iconv = "${bindir}/iconv"

COMPATIBLE_HOST = ".*-musl.*"

