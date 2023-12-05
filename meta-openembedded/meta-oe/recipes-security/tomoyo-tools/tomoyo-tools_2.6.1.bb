SUMMARY = "TOMOYO Linux tools"
DESCRIPTION = "TOMOYO Linux is a Mandatory Access Control (MAC) implementation \
for Linux that can be used to increase the security of a system, while also \
being useful purely as a system analysis tool."
HOMEPAGE = "http://tomoyo.sourceforge.jp/"
SECTION = "System Environment/Kernel"

SRC_URI = "http://jaist.dl.sourceforge.jp/tomoyo/70710/${BP}-20210910.tar.gz"
SRC_URI[sha256sum] = "47a12cdb1fe7bbd0b2e3486150fe1e754fa9c869aeefd42fd311c4022b78010a"

S = "${WORKDIR}/${BPN}"

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
