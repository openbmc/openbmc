SUMMARY = "Tools for creating debuginfo and source file distributions"
DESCRIPTION = "debugedit provides programs and scripts for creating \
debuginfo and source file distributions, collect build-ids and rewrite \
source paths in DWARF data for debugging, tracing and profiling."
HOMEPAGE = "https://sourceware.org/debugedit/"

LICENSE = "GPL-2.0-only & GPL-3.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://sourceware.org/ftp/debugedit/${PV}/debugedit-${PV}.tar.xz \
           "

SRC_URI:append:libc-musl = "\
           file://0003-Makefile.am-do-not-update-manual.patch \
           "

SRC_URI[sha256sum] = "ee9b688b2ed8fa62551c54cb5dc31aaa05853e7dedbd9e1237c77894ea5e3626"

DEPENDS = "elfutils xxhash"
DEPENDS:append:libc-musl = " musl-legacy-error"

inherit pkgconfig autotools multilib_script

RDEPENDS:${PN} += "bash elfutils-binutils"

BBCLASSEXTEND = "native nativesdk"

MULTILIB_SCRIPTS = "${PN}:${bindir}/find-debuginfo"

