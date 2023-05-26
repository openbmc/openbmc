SUMMARY = "GNU debugger"
HOMEPAGE = "http://www.gnu.org/software/gdb/"
DESCRIPTION = "GDB, the GNU Project debugger, allows you to see what is going on inside another program while it executes -- or what another program was doing at the moment it crashed."
SECTION = "devel"
DEPENDS = "expat gmp zlib ncurses virtual/libiconv ${LTTNGUST} bison-native"

LTTNGUST = "lttng-ust"
LTTNGUST:arc = ""
LTTNGUST:aarch64 = ""
LTTNGUST:mipsarch = ""
LTTNGUST:sh4 = ""

inherit autotools texinfo

UPSTREAM_CHECK_GITTAGREGEX = "gdb\-(?P<pver>.+)\-release"

B = "${WORKDIR}/build-${TARGET_SYS}"

EXPAT = "--with-expat --with-libexpat-prefix=${STAGING_DIR_HOST}"

EXTRA_OECONF = "--disable-gdbtk --disable-x --disable-werror \
                --with-curses --disable-multilib --disable-sim \
                --without-guile \
                ${GDBPROPREFIX} ${EXPAT} \
                ${@bb.utils.contains('DISTRO_FEATURES', 'multiarch', '--enable-64-bit-bfd', '', d)} \
                --disable-rpath \
                --disable-gas --disable-binutils \
                --disable-ld --disable-gold \
                --disable-gprof \
                --with-libgmp-prefix=${STAGING_EXECPREFIXDIR} \
"

PACKAGECONFIG ??= "readline ${@bb.utils.filter('DISTRO_FEATURES', 'debuginfod', d)} python"
# Use --without-system-readline to compile with readline 5.
PACKAGECONFIG[readline] = "--with-system-readline,--without-system-readline,readline"
PACKAGECONFIG[python] = "--with-python=${WORKDIR}/python,--without-python,python3,python3-codecs"
PACKAGECONFIG[babeltrace] = "--with-babeltrace,--without-babeltrace,babeltrace"
# ncurses is already a hard DEPENDS, but would be added here if it weren't
PACKAGECONFIG[tui] = "--enable-tui,--disable-tui"
PACKAGECONFIG[xz] = "--with-lzma --with-liblzma-prefix=${STAGING_DIR_HOST},--without-lzma,xz"
PACKAGECONFIG[debuginfod] = "--with-debuginfod, --without-debuginfod, elfutils"

GDBPROPREFIX = "--program-prefix=''"

DISABLE_STATIC = ""

do_configure () {
	# override this function to avoid the autoconf/automake/aclocal/autoheader
	# calls for now
	(cd ${S} && gnu-configize) || die "failure in running gnu-configize"
	oe_runconf
}

# we don't want gdb to provide bfd/iberty/opcodes, which instead will override the
# right bits installed by binutils.  Same for bfd.info -- also from binutils.
do_install:append() {
	rm -rf ${D}${libdir}
	rm -rf ${D}${includedir}
	rm -rf ${D}${datadir}/locale
	rm -f ${D}${infodir}/bfd.info
        rm -f ${D}${infodir}/sframe-spec.info
}

RRECOMMENDS:gdb:append:linux = " glibc-thread-db "
RRECOMMENDS:gdb:append:linux-gnueabi = " glibc-thread-db "
RRECOMMENDS:gdbserver:append:linux = " glibc-thread-db "
RRECOMMENDS:gdbserver:append:linux-gnueabi = " glibc-thread-db "
