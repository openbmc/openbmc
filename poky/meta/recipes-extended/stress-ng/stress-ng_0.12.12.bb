SUMMARY = "System load testing utility"
DESCRIPTION = "Deliberately simple workload generator for POSIX systems. It \
imposes a configurable amount of CPU, memory, I/O, and disk stress on the system."
HOMEPAGE = "https://kernel.ubuntu.com/~cking/stress-ng/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://kernel.ubuntu.com/~cking/tarballs/${BPN}/${BP}.tar.xz \
           file://0001-Do-not-preserve-ownership-when-installing-example-jo.patch \
           file://0001-Detemine-minimal-stack-size-via-sysconf-then-PTHREAD.patch \
           "
SRC_URI[sha256sum] = "f27af50f6f2308e707fef927674bdd209a046b116734281b792aeca35a4e4499"

DEPENDS = "coreutils-native"

PROVIDES = "stress"
RPROVIDES:${PN} = "stress"
RREPLACES:${PN} = "stress"
RCONFLICTS:${PN} = "stress"

inherit bash-completion

do_install() {
    oe_runmake DESTDIR=${D} install
    ln -s stress-ng ${D}${bindir}/stress
}
