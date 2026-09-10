DESCRIPTION = "A library for faking the system time in user-space programs"
SECTION = "libs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV = "86b37fde2fed7336ea2d0c17928e3015a55d9b4a"

SRC_URI = "git://github.com/wolfcw/libfaketime.git;branch=master;tag=v${PV};protocol=https \
           file://0001-Mark-write-only-real_-pointers-as-unused.patch \
"

CFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE"

do_configure[noexec] = "1"

do_compile () {
    oe_runmake
}

do_install () {
    install -d ${D}${libdir}/faketime
    oe_libinstall -C src libfaketime ${D}${libdir}/faketime
    install -d ${D}${bindir}
    install -m 0755 src/faketime ${D}${bindir}
}

FILES:${PN} = "${bindir}/faketime ${libdir}/faketime/lib*${SOLIBS}"
FILES:${PN}-dev += "${libdir}/faketime/lib*${SOLIBSDEV}"

BBCLASSEXTEND = "native"
