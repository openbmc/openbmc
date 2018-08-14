SUMMARY = "Radio enable/disable command line utility"
HOMEPAGE = "http://linuxwireless.org/en/users/Documentation/rfkill"
SECTION = "base"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=c6036d0eb7edbfced28c4160e5d3fa94"

SRC_URI = "http://www.kernel.org/pub/software/network/${BPN}/${BP}.tar.bz2 \
           file://0001-rfkill-makefile-don-t-use-t-the-OE-install-wrapper-d.patch \
           file://dont.call.git.rev-parse.on.parent.dir.patch"

SRC_URI[md5sum] = "b957713a6cfbcd8ac0e94420aeddcf1a"
SRC_URI[sha256sum] = "3e160cca504a53679f2b3254f31c53a4fb38a021bc50fed8eb57a436d33dfa07"

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

