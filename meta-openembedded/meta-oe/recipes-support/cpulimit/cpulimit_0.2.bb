SUMMARY = "cpulimit is a tool which limits the CPU usage of a process"
HOMEPAGE = "http://cpulimit.sourceforge.net"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86c1c0d961a437e529db93aa3bb32dc4"
SRCREV ?= "f4d2682804931e7aea02a869137344bb5452a3cd"

SRC_URI = "git://g...@github.com/opsengine/cpulimit.git;protocol=https;branch=master \
           file://0001-Remove-sys-sysctl.h-and-add-missing-libgen.h-include.patch \
           "

S = "${WORKDIR}/git"

do_compile() {
    oe_runmake all
}
do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${B}/src/${PN} ${D}${sbindir}/
}

CFLAGS += "-D_GNU_SOURCE ${LDFLAGS}"

