SUMMARY = "A privileged helper for utmp/wtmp updates"
DESCRIPTION = "\
This library provides interface for terminal emulators such as \
screen and xterm to record user sessions to utmp and wtmp files."
HOMEPAGE = "ftp://ftp.altlinux.org/pub/people/ldv/utempter"
SECTION = "System Environment/Libraries"
LICENSE = "GPL-2.0-only & GPL-2.0-or-later & LGPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2caced0b25dfefd4c601d92bd15116de"

SRCREV = "63825e2244629d44dae21132b1065d7ecc0491c0"

SRC_URI = "git://git.altlinux.org/people/ldv/packages/libutempter.git;branch=master \
           file://0001-Fix-macro-error.patch \
           file://0002-Proper-macro-path-generation.patch \
           "

S = "${WORKDIR}/git/${BPN}"

CFLAGS += "-DLIBEXECDIR=${libexecdir}"

do_compile() {
    oe_runmake                      \
        libdir=${libdir}            \
        libexecdir=${libexecdir}
}

do_install() {
    oe_runmake install              \
        DESTDIR=${D}                \
        libdir="${libdir}"          \
        libexecdir="${libexecdir}"  \
        includedir=${includedir}    \
        mandir=${mandir}

    rm -f ${D}${libdir}/*.a
}

FILES:${PN} = "${libdir}/*.so.*"
FILES:${PN} += "${libexecdir}/utempter/utempter"
FILES:${PN}-dbg += "${libexecdir}/utempter/.debug/utempter"
