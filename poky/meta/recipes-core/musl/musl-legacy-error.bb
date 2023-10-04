# Copyright (C) 2023 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "error API GNU extention implementation"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://error.h;beginline=1;md5=2ee396b23e8507fbf8f98af0471a77c6"
SECTION = "devel"

SRC_URI = "file://error.h"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

INHIBIT_DEFAULT_DEPS = "1"

S = "${WORKDIR}"

do_install() {
	install -Dm 0644 ${S}/error.h -t ${D}${includedir}
}
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"
DEV_PKG_DEPENDENCY = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
