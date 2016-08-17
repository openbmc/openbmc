SUMMARY = "TOMOYO Linux tools"
DESCRIPTION = "TOMOYO Linux is a Mandatory Access Control (MAC) implementation \
for Linux that can be used to increase the security of a system, while also \
being useful purely as a system analysis tool."
HOMEPAGE = "http://tomoyo.sourceforge.jp/"
SECTION = "System Environment/Kernel"

SRC_URI = "http://jaist.dl.sourceforge.jp/tomoyo/53357/${BP}-20140601.tar.gz"
SRC_URI[md5sum] = "888869b793127f00d6439a3246598b83"
SRC_URI[sha256sum] = "118ef6ba1fbf7c0b83018c3a0d4d5485dfb9b5b7f647f37ce9f63841a3133c2a"

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
