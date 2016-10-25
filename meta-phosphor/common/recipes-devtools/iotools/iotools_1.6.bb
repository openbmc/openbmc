DESCRIPTION = "Command line tools for hardware device registers"
HOMEPAGE = "https://github.com/adurbin/iotools"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SRCREV = "452ad1f4957a4f307fbe204419e5d08a1a1febb9"
SRC_URI = "git://git@github.com/adurbin/iotools.git;protocol=https"
PV = "v1.6+git${SRCPV}"

S = "${WORKDIR}/git"
sbindir = "/usr/local/sbin"
FILES_${PN} = "${sbindir}"

do_compile() {
    make CC="${CC}" DEBUG="${DEBUG_BUILD-0}" STATIC=0
}

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 iotools ${D}${sbindir}
}

