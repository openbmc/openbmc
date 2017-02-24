SUMMARY = "ssmping is a tool for checking whether one can receive SSM from a given host"
HOMEPAGE = "http://www.venaas.no/multicast/ssmping/"
SECTION = "net"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://asmping.c;beginline=2;endline=11;md5=1ca8d1a1ca931e5cfe604ebf20a78b71"

SRC_URI = "http://www.venaas.no/multicast/ssmping/${BP}.tar.gz \
    file://0001-Makefile-tweak-install-dir.patch \
"
SRC_URI[md5sum] = "ad8e3d13f6d72918f73be7e7975d7fad"
SRC_URI[sha256sum] = "22103a37eaa28489169a0927bc01e0596c3485fc4d29fc8456c07fd2c70fca6d"

CFLAGS += "-D_GNU_SOURCE "

do_install() {
    oe_runmake 'DESTDIR=${D}' 'PREFIX=${prefix}' install
}
