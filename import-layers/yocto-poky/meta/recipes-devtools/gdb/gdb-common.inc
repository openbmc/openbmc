SUMMARY = "GNU debugger"
HOMEPAGE = "http://www.gnu.org/software/gdb/"
LICENSE = "GPLv3+"
SECTION = "devel"
DEPENDS = "expat zlib ncurses virtual/libiconv ${LTTNGUST}"

LTTNGUST = "lttng-ust"
LTTNGUST_aarch64 = ""
LTTNGUST_libc-uclibc = ""
LTTNGUST_mips = ""
LTTNGUST_mipsel = ""
LTTNGUST_mips64 = ""
LTTNGUST_mips64el = ""
LTTNGUST_mips64n32 = ""
LTTNGUST_mips64eln32 = ""
LTTNGUST_sh4 = ""
LTTNGUST_libc-musl = ""

LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
		file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674 \
		file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
		file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6"

inherit autotools texinfo

SRCREV = "1a982b689ce4e20523bdf69e47fdd574c4f63934"

SRC_URI = "git://sourceware.org/git/binutils-gdb.git;branch=gdb-7.11-branch \
           file://0001-include-sys-types.h-for-mode_t.patch \
           file://0002-make-man-install-relative-to-DESTDIR.patch \
           file://0003-mips-linux-nat-Define-_ABIO32-if-not-defined.patch \
           file://0004-ppc-ptrace-Define-pt_regs-uapi_pt_regs-on-GLIBC-syst.patch \
           file://0005-Add-support-for-Renesas-SH-sh4-architecture.patch \
           file://0006-Dont-disable-libreadline.a-when-using-disable-static.patch \
           file://0007-use-asm-sgidefs.h.patch \
           file://0008-Use-exorted-definitions-of-SIGRTMIN.patch \
           file://0009-Change-order-of-CFLAGS.patch \
           file://0010-resolve-restrict-keyword-conflict.patch \
           file://0011-avx_mpx.patch \
"

UPSTREAM_CHECK_GITTAGREGEX = "gdb\-(?P<pver>.+)\-release"

S = "${WORKDIR}/git"

B = "${WORKDIR}/build-${TARGET_SYS}"

EXTRA_OEMAKE = "'SUBDIRS=intl mmalloc libiberty opcodes bfd sim gdb etc utils'"

EXPAT = "--with-expat --with-libexpat-prefix=${STAGING_DIR_HOST}"

EXTRA_OECONF = "--disable-gdbtk --disable-tui --disable-x --disable-werror \
                --with-curses --disable-multilib --disable-sim \
                --without-lzma --without-guile \
                ${GDBPROPREFIX} ${EXPAT} \
                ${@bb.utils.contains('DISTRO_FEATURES', 'multiarch', '--enable-64-bit-bfd', '', d)} \
                --disable-rpath \
                --disable-gas --disable-binutils \
                --disable-ld --disable-gold \
                --disable-gprof \
"

PACKAGECONFIG ??= "readline"
# Use --without-system-readline to compile with readline 5.
PACKAGECONFIG[readline] = "--with-system-readline,--without-system-readline,readline"
PACKAGECONFIG[python] = "--with-python=${WORKDIR}/python,--without-python,python3,python3 python3-codecs"
PACKAGECONFIG[babeltrace] = "--with-babeltrace,--without-babeltrace,babeltrace"

GDBPROPREFIX = "--program-prefix=''"

do_configure () {
	# override this function to avoid the autoconf/automake/aclocal/autoheader
	# calls for now
	(cd ${S} && gnu-configize) || die "failure in running gnu-configize"
	oe_runconf
}

# we don't want gdb to provide bfd/iberty/opcodes, which instead will override the
# right bits installed by binutils.
do_install_append() {
	rm -rf ${D}${libdir}
	rm -rf ${D}${includedir}
	rm -rf ${D}${datadir}/locale
}

RRECOMMENDS_gdb_append_linux = " glibc-thread-db "
RRECOMMENDS_gdb_append_linux-gnueabi = " glibc-thread-db "
RRECOMMENDS_gdbserver_append_linux = " glibc-thread-db "
RRECOMMENDS_gdbserver_append_linux-gnueabi = " glibc-thread-db "
