SUMMARY = "Tools for creating debuginfo and source file distributions"
DESCRIPTION = "debugedit provides programs and scripts for creating \
debuginfo and source file distributions, collect build-ids and rewrite \
source paths in DWARF data for debugging, tracing and profiling."
HOMEPAGE = "https://sourceware.org/debugedit/"

LICENSE = "GPL-2.0-only AND GPL-3.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://sourceware.org/ftp/debugedit/${PV}/debugedit-${PV}.tar.xz \
           file://0003-Makefile.am-do-not-update-manual.patch \
           file://0001-configure.ac-do-not-check-for-executables-needed-onl.patch \
           "

SRC_URI[sha256sum] = "00e1d74d31df9012913975b76ab0d864f0e2569d29e31271317161187803813a"

DEPENDS = "elfutils xxhash"
DEPENDS:append:libc-musl = " musl-legacy-error"

inherit pkgconfig autotools multilib_script

RDEPENDS:${PN} += "bash elfutils-binutils"

EXTRA_OECONF = "${@oe.utils.vartrue('DEBUG_BUILD', '--disable-inlined-xxhash', '', d)}"

BBCLASSEXTEND = "native nativesdk"

MULTILIB_SCRIPTS = "${PN}:${bindir}/find-debuginfo"

