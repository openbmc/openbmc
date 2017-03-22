SUMMARY = "Asynchronous I/O library"
DESCRIPTION = "Asynchronous input/output library that uses the kernels native interface"
HOMEPAGE = "http://lse.sourceforge.net/io/aio.html"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRC_URI = "http://snapshot.debian.org/archive/debian/20160728T043443Z/pool/main/liba/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://generic-arch-dectection-for-padding-defines.patch \
           file://00_arches.patch \
           file://destdir.patch \
           file://libaio_fix_for_x32.patch \
           file://libaio_fix_for_mips_syscalls.patch \
           file://system-linkage.patch \
           "

SRC_URI[md5sum] = "2a35602e43778383e2f4907a4ca39ab8"
SRC_URI[sha256sum] = "e019028e631725729376250e32b473012f7cb68e1f7275bfc1bbcdd0f8745f7e"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/liba/libaio/"

EXTRA_OEMAKE =+ "prefix=${prefix} includedir=${includedir} libdir=${libdir}"

do_install () {
    oe_runmake install DESTDIR=${D}
}
