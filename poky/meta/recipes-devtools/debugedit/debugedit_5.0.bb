SUMMARY = "Tools for creating debuginfo and source file distributions"
DESCRIPTION = "debugedit provides programs and scripts for creating \
debuginfo and source file distributions, collect build-ids and rewrite \
source paths in DWARF data for debugging, tracing and profiling."
HOMEPAGE = "https://sourceware.org/debugedit/"

LICENSE = "GPL-2.0-only & GPL-3.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://sourceware.org/ftp/debugedit/${PV}/debugedit-${PV}.tar.xz"

SRC_URI:append:libc-musl = "\
           file://0002-sepdebugcrcfix.c-do-not-use-64bit-variants.patch \
           file://0003-Makefile.am-do-not-update-manual.patch \
           "

SRC_URI[sha256sum] = "e9ecd7d350bebae1f178ce6776ca19a648b6fe8fa22f5b3044b38d7899aa553e"

DEPENDS = "elfutils"
DEPENDS:append:libc-musl = " musl-legacy-error"

inherit pkgconfig autotools

RDEPENDS:${PN} += "bash elfutils-binutils"

BBCLASSEXTEND = "native nativesdk"
