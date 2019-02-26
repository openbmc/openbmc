SUMMARY = "Asynchronous I/O library"
DESCRIPTION = "Asynchronous input/output library that uses the kernels native interface"
HOMEPAGE = "http://lse.sourceforge.net/io/aio.html"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRC_URI = "git://pagure.io/libaio.git;protocol=https \
           file://00_arches.patch \
           file://destdir.patch \
           file://libaio_fix_for_mips_syscalls.patch \
           file://system-linkage.patch \
           "
SRCREV = "f66be22ab0a59a39858900ab72a8c6a6e8b0b7ec"
S = "${WORKDIR}/git"

EXTRA_OEMAKE =+ "prefix=${prefix} includedir=${includedir} libdir=${libdir}"

do_install () {
    oe_runmake install DESTDIR=${D}
}

BBCLASSEXTEND = "native nativesdk"
