SUMMARY = "Asynchronous I/O library"
DESCRIPTION = "Asynchronous input/output library that uses the kernels native interface"
HOMEPAGE = "http://lse.sourceforge.net/io/aio.html"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRC_URI = "git://pagure.io/libaio.git;protocol=https;branch=master \
           file://00_arches.patch \
           file://libaio_fix_for_mips_syscalls.patch \
           file://system-linkage.patch \
           "
SRCREV = "1b18bfafc6a2f7b9fa2c6be77a95afed8b7be448"
S = "${WORKDIR}/git"

EXTRA_OEMAKE =+ "prefix=${prefix} includedir=${includedir} libdir=${libdir}"

do_install () {
    oe_runmake install DESTDIR=${D}
}

BBCLASSEXTEND = "native nativesdk"
