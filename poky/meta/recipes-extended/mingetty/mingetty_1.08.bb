SUMMARY = "Compact getty terminal handler for virtual consoles only"
SECTION = "console/utils"
HOMEPAGE = "http://sourceforge.net/projects/mingetty/"
LICENSE = "GPLv2"
PR = "r3"

LIC_FILES_CHKSUM = "file://COPYING;md5=0c56db0143f4f80c369ee3af7425af6e"
SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz"

SRC_URI[md5sum] = "2a75ad6487ff271424ffc00a64420990"
SRC_URI[sha256sum] = "0f55c90ba4faa913d91ef99cbf5cb2eb4dbe2780314c3bb17953f849c8cddd17"

# substitute our CFLAGS for "-O2 -Wall -W -pipe"
#
EXTRA_OEMAKE = "CC='${CC}' \
                CFLAGS='${CFLAGS} -D_GNU_SOURCE'"

do_install(){
    install -d ${D}${mandir}/man8 ${D}/${base_sbindir}
    oe_runmake install DESTDIR=${D}
}

inherit update-alternatives

ALTERNATIVE_${PN} = "getty"
ALTERNATIVE_LINK_NAME[getty] = "${base_sbindir}/getty"
ALTERNATIVE_TARGET[getty] = "${base_sbindir}/mingetty"
ALTERNATIVE_PRIORITY = "10"
