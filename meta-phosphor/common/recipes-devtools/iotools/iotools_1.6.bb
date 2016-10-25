DESCRIPTION = "Command line tools for hardware device registers"
HOMEPAGE = "https://github.com/adurbin/iotools"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SRCREV = "452ad1f4957a4f307fbe204419e5d08a1a1febb9"
SRC_URI = "git://git@github.com/adurbin/iotools.git;protocol=https"
PV = "v1.6+git${SRCPV}"

S = "${WORKDIR}/git"
FILES_${PN} = "${sbindir}"

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
