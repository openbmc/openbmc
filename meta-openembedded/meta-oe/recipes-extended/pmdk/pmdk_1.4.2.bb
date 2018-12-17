SUMMARY = "Persistent Memory Development Kit"
DESCRIPTION = "Persistent Memory Development Kit"
HOMEPAGE = "http://pmem.io"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7db1106255a1baa80391fd2e21eebab7"
DEPENDS = "ndctl"

# Required to have the fts.h header for musl
DEPENDS_append_libc-musl = " fts"

SRC_URI = "https://github.com/pmem/${BPN}/archive/${PV}.tar.gz \
           file://0001-jemalloc-jemalloc.cfg-Specify-the-host-when-building.patch \
           file://0002-Makefile-Don-t-install-the-docs.patch \
           file://0003-Makefile-Don-t-build-the-examples.patch \
           file://0005-pmempool-Remove-unused-__USE_UNIX98-define.patch \
           file://0006-Makefile.inc-Allow-extra-libs-to-be-specified.patch \
          "

SRC_URI_append_libc-musl = " file://0004-os_posix-Manually-implement-secure_getenv-if-require.patch"

SRC_URI[md5sum] = "bde73bca9ef5b90911deb0fdcfb15ccf"
SRC_URI[sha256sum] = "df7e658e75d28cd80f6d2ff7b9fc9ae2885d52f8923fdbacecfd46215115fb4c"

inherit autotools-brokensep pkgconfig

# Fix jemalloc error:
# | configure: error: cannot run C compiled programs.
# | If you meant to cross compile, use `--host'.
#
# Also fix #warning _FORTIFY_SOURCE requires compiling with optimization (-O) [-Werror=cpp]
EXTRA_OEMAKE = "HOST_SYS='${HOST_SYS}' EXTRA_CFLAGS='${SELECTED_OPTIMIZATION}'"

# Fix the missing fts libs when using musl
EXTRA_OEMAKE_append_libc-musl = " EXTRA_LIBS='-lfts'"

do_install() {
	oe_runmake PREFIX=${prefix} DESTDIR=${D} install

	# Copy these into the standard directories
	install -d ${D}${bindir}/
	mv ${D}/usr/local/bin/pmempool ${D}${bindir}/
	mv ${D}/usr/local/bin/daxio ${D}${bindir}/

	install -d ${D}${libdir}
	mv ${D}/usr/local/lib/*so* ${D}${libdir}/

	install -d ${D}${libdir}/pkgconfig
	mv ${D}/usr/local/lib/pkgconfig/*.pc ${D}${libdir}/pkgconfig/

	install -d ${D}${includedir}
	mv ${D}/usr/local/include/* ${D}${includedir}/

	# Remove uneeded files
	rm -rf ${D}/usr/local/
}

# Include these by default otherwise the SDK is not very useful
FILES_${PN} += "${bindir}/pmempool ${bindir}/daxio"
FILES_${PN} += "${libdir}/*so*"
FILES_${PN} += "${libdir}/pkgconfig/*.pc"
FILES_${PN} += "${includedir}/libpmemobj++/* ${includedir}/libpmemobj/*"

COMPATIBLE_HOST='(x86_64).*'
