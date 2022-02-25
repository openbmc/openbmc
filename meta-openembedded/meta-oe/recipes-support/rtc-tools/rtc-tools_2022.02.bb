SUMMARY = "Useful programs to test rtc drivers"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=74274e8a218423e49eefdea80bc55038"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/abelloni/${BPN}.git;protocol=https;branch=master"
SRCREV = "61839777afedcc7bdb68ea4628c5ce5ca72c2ac8"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "DESTDIR=${D}"

do_install() {
	oe_runmake install
}
