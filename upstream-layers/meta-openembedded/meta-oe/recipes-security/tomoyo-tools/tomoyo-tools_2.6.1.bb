SUMMARY = "TOMOYO Linux tools"
DESCRIPTION = "TOMOYO Linux is a Mandatory Access Control (MAC) implementation \
for Linux that can be used to increase the security of a system, while also \
being useful purely as a system analysis tool."
HOMEPAGE = "http://tomoyo.sourceforge.jp/"
SECTION = "System Environment/Kernel"

SRC_URI = "${SOURCEFORGE_MIRROR}/tomoyo/${BP}.tar.gz"
SRC_URI[sha256sum] = "f5d1f422df0f68937245fb97c18336bcd813221955cbcdb3b1a3f65b3cae1a06"

# there are periodic new releases of the same version, with this date updated
PV .= "-20250707"
S = "${UNPACKDIR}/${BPN}"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.tomoyo;md5=751419260aa954499f7abaabaa882bbe"

FILES:${PN}     += "${libdir}/tomoyo"
FILES:${PN}-dbg += "${libdir}/tomoyo/.debug"

DEPENDS = "linux-libc-headers ncurses"

EXTRA_OEMAKE = "-e USRLIBDIR=${libdir}"

do_compile () {
    oe_runmake 'CC=${CC}'
}

do_install() {
    oe_runmake install SBINDIR=${base_sbindir} INSTALLDIR=${D}
}
