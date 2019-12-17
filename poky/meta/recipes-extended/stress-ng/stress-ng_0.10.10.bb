SUMMARY = "System load testing utility"
DESCRIPTION = "Deliberately simple workload generator for POSIX systems. It \
imposes a configurable amount of CPU, memory, I/O, and disk stress on the system."
HOMEPAGE = "https://kernel.ubuntu.com/~cking/stress-ng/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://kernel.ubuntu.com/~cking/tarballs/${BPN}/${BP}.tar.xz \
           file://0001-Do-not-preserve-ownership-when-installing-example-jo.patch \
           "
SRC_URI[md5sum] = "ad4de38d6ff1ccf3640573e967dfb136"
SRC_URI[sha256sum] = "031de5ebc44b49992c11633f10aae96da05a85b326aebdffd88ba8c445d23cf4"

DEPENDS = "coreutils-native"

PROVIDES = "stress"
RPROVIDES_${PN} = "stress"
RREPLACES_${PN} = "stress"
RCONFLICTS_${PN} = "stress"

inherit bash-completion

do_install() {
    oe_runmake DESTDIR=${D} install
}
