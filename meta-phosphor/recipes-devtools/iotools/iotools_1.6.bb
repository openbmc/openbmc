DESCRIPTION = "Command line tools for hardware device registers"
HOMEPAGE = "https://github.com/jonmayergoogle/iotools"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SRCREV = "8d928b3360246b8ead95b442ca3887ce8b8f942f"
PV = "v1.6+git${SRCPV}"

SRC_URI = "git://git@github.com/jonmayergoogle/iotools.git;protocol=https;branch=master"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "iotools-setup.service"

inherit obmc-phosphor-systemd

do_compile() {
    # CC is overridden in the Makefile, so override it harder in the invocation
    oe_runmake CC="${CC}" DEBUG="${DEBUG_BUILD-0}" STATIC=0
}
# The "install" make target runs the binary to create links for subcommands.
# The links are excessive and this doesn't work for cross compiling.
do_install() {
    install -d ${D}${sbindir}
    install -m 0755 iotools ${D}${sbindir}
}

FILES:${PN} = "${sbindir}"
