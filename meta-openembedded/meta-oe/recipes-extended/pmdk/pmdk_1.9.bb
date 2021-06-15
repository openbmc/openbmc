SUMMARY = "Persistent Memory Development Kit"
DESCRIPTION = "Persistent Memory Development Kit"
HOMEPAGE = "http://pmem.io"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b44ee63f162f9cdb18fff1224877aafd"
DEPENDS = "ndctl"

# Required to have the fts.h header for musl
DEPENDS_append_libc-musl = " fts"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/pmem/pmdk.git"

SRCREV = "1926ffb8f3f5f0617b3b3ed32029d437c272f187"

inherit autotools-brokensep pkgconfig

# Fix jemalloc error:
# | configure: error: cannot run C compiled programs.
# | If you meant to cross compile, use `--host'.
#
# Also fix #warning _FORTIFY_SOURCE requires compiling with optimization (-O) [-Werror=cpp]
EXTRA_OEMAKE = "BUILD_EXAMPLES='n' DOC='n' HOST_SYS='${HOST_SYS}' EXTRA_CFLAGS='${SELECTED_OPTIMIZATION}' LIB_PREFIX=${baselib}"

# Fix the missing fts libs when using musl
EXTRA_OEMAKE_append_libc-musl = " EXTRA_LIBS='-lfts'"

do_configure_prepend() {
	touch .skip-doc
}

do_install() {
	oe_runmake prefix=${prefix} DESTDIR=${D} install

	# Remove uneeded files
	rm -rf ${D}/usr/${baselib}/pmdk_debug
}

# Include these by default otherwise the SDK is not very useful
FILES_${PN} += "${bindir}/pmempool ${bindir}/daxio"
FILES_${PN} += "${libdir}/*so*"
FILES_${PN} += "${libdir}/pkgconfig/*.pc"
FILES_${PN} += "${includedir}/libpmemobj++/* ${includedir}/libpmemobj/* /usr/*/include/"
FILES_${PN} += "/usr/etc"
FILES_${PN} += "/usr/share"

COMPATIBLE_HOST='(x86_64).*'
