SUMMARY = "TOMOYO Linux tools"
DESCRIPTION = "TOMOYO Linux is a Mandatory Access Control (MAC) implementation \
for Linux that can be used to increase the security of a system, while also \
being useful purely as a system analysis tool."
HOMEPAGE = "http://tomoyo.sourceforge.jp/"
SECTION = "System Environment/Kernel"

SRC_URI = "http://jaist.dl.sourceforge.jp/tomoyo/53357/${BP}-20170102.tar.gz"
SRC_URI[md5sum] = "888804d58742452fe213a68f7eadd0ad"
SRC_URI[sha256sum] = "00fedfac5e514321250bbe69eaccc732c8a8158596f77a785c2e3ae9f9968283"

S = "${WORKDIR}/${BPN}"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.tomoyo;md5=751419260aa954499f7abaabaa882bbe"

FILES_${PN}     += "${libdir}/tomoyo"
FILES_${PN}-dbg += "${libdir}/tomoyo/.debug"

DEPENDS = "linux-libc-headers ncurses"

EXTRA_OEMAKE = "-e USRLIBDIR=${libdir}"

do_compile () {
    oe_runmake 'CC=${CC}'
}

do_install() {
    oe_runmake install INSTALLDIR=${D}
}
