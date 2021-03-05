SUMMARY = "Radio enable/disable command line utility"
HOMEPAGE = "http://linuxwireless.org/en/users/Documentation/rfkill"
SECTION = "base"
LICENSE = "BSD-0-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=c6036d0eb7edbfced28c4160e5d3fa94"

SRC_URI = "http://www.kernel.org/pub/software/network/${BPN}/${BP}.tar.xz \
           file://0001-rfkill-makefile-don-t-use-t-the-OE-install-wrapper-d.patch \
           file://dont.call.git.rev-parse.on.parent.dir.patch"

SRC_URI[sha256sum] = "dffc631c611520478b8a286f57c67a35e8cb5802d376c6ca13b057365432389c"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR=${D} install
}

inherit update-alternatives

ALTERNATIVE_${PN} = "rfkill"
ALTERNATIVE_PRIORITY = "60"
ALTERNATIVE_LINK_NAME[rfkill] = "${sbindir}/rfkill"
