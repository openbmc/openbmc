SUMMARY = "A privileged helper for utmp/wtmp updates"
DESCRIPTION = "\
This library provides interface for terminal emulators such as \
screen and xterm to record user sessions to utmp and wtmp files."
HOMEPAGE = "ftp://ftp.altlinux.org/pub/people/ldv/utempter"
SECTION = "System Environment/Libraries"
LICENSE = "GPLv2 & GPLv2+ & LGPLv2 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

SRCREV = "3ef74fff310f09e2601e241b9f042cd39d591018"
PV = "1.1.6-alt2+git${SRCPV}"

SRC_URI = "git://git.altlinux.org/people/ldv/packages/libutempter.git;branch=master \
           file://0001-Fix-macro-error.patch \
           file://0002-Proper-macro-path-generation.patch \
           file://libutempter-remove-glibc-assumption.patch \
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
