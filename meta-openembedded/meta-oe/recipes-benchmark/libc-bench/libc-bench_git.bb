SUMMARY = "Tests to compare standard functions of different libc implementations"
DESCRIPTION = "libc-bench is a set of time- and memory-efficiency tests to compare \
implementations of various C/POSIX standard library functions."
HOMEPAGE = "http://www.etalabs.net/libc-bench.html"
SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=9a825c63897c53f487ef900598c31527"

SRCREV = "b6b2ce5f9f87a09b14499cb00c600c601f022634"
PV = "20110206+git${SRCPV}"

SRC_URI = "git://git.musl-libc.org/libc-bench \
           file://0001-build-Do-not-override-ldflags-from-environment.patch \
           "

S = "${WORKDIR}/git"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/libc-bench ${D}${bindir}
}

