SUMMARY = "MPEG Audio Decoder library"
HOMEPAGE = "http://sourceforge.net/projects/mad/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=12349&atid=112349"
LICENSE = "GPL-2.0-or-later"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
    file://COPYRIGHT;md5=8e55eb14894e782b84488d5a239bc23d \
    file://version.h;beginline=1;endline=8;md5=aa07311dd39288d4349f28e1de516454"
SECTION = "libs"
DEPENDS = "libid3tag"

SRC_URI = "ftp://ftp.mars.org/pub/mpeg/libmad-${PV}.tar.gz \
    file://no-force-mem.patch \
    file://add-pkgconfig.patch \
    file://fix_for_mips_with_gcc-4.5.0.patch \
    file://obsolete_automake_macros.patch \
    file://automake-foreign.patch \
    file://0001-configure-Respect-the-cflags-from-environment.patch \
"
SRC_URI:append:toolchain-clang = " file://0004-Remove-clang-unsupported-compiler-flags.patch "

SRC_URI[md5sum] = "1be543bc30c56fb6bea1d7bf6a64e66c"
SRC_URI[sha256sum] = "bbfac3ed6bfbc2823d3775ebb931087371e142bb0e9bb1bee51a76a6e0078690"

S = "${WORKDIR}/libmad-${PV}"

inherit autotools pkgconfig

EXTRA_OECONF = "-enable-speed --enable-shared"
EXTRA_OECONF:append:arm = " --enable-fpm=arm"

do_configure:prepend () {
#   damn picky automake...
    touch NEWS AUTHORS ChangeLog
}

ARM_INSTRUCTION_SET = "arm"
