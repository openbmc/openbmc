SUMMARY = "Persistent Memory Development Kit"
DESCRIPTION = "Persistent Memory Development Kit"
HOMEPAGE = "http://pmem.io"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3e2f50552a27ca99772f3d884f98560b"
DEPENDS = "ndctl"

# Required to have the fts.h header for musl
DEPENDS:append:libc-musl = " fts"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/pmem/pmdk.git;branch=master;protocol=https"
SRCREV = "0f0ab391d8e3db52f745f33c92b9d9a462bb3695"

inherit pkgconfig

# Fix jemalloc error:
# | configure: error: cannot run C compiled programs.
# | If you meant to cross compile, use `--host'.
#
# Also fix #warning _FORTIFY_SOURCE requires compiling with optimization (-O) [-Werror=cpp]
EXTRA_OEMAKE = "BUILD_EXAMPLES='n' DOC='n' HOST_SYS='${HOST_SYS}' EXTRA_CFLAGS='${SELECTED_OPTIMIZATION}' LIB_PREFIX=${baselib}"

# Fix the missing fts libs when using musl
EXTRA_OEMAKE:append:libc-musl = " EXTRA_LIBS='-lfts'"

do_configure() {
	touch .skip-doc
}

do_install() {
	oe_runmake prefix=${prefix} DESTDIR=${D} install

	# Remove uneeded files
	rm -rf ${D}/usr/${baselib}/pmdk_debug
}

# Include these by default otherwise the SDK is not very useful
FILES:${PN} += "${bindir}/pmempool ${bindir}/daxio"
FILES:${PN} += "${libdir}/*so*"
FILES:${PN} += "${libdir}/pkgconfig/*.pc"
FILES:${PN} += "${includedir}/libpmemobj++/* ${includedir}/libpmemobj/* /usr/*/include/"
FILES:${PN} += "/usr/etc"
FILES:${PN} += "/usr/share"

COMPATIBLE_HOST = '(x86_64).*'
