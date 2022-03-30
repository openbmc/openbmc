SUMMARY = "driverctl is a device driver control utility for Linux"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/LGPL-2.1-only;md5=1a6d268fd218675ffea8be556788b780"

REQUIRED_DISTRO_FEATURES = "systemd"

DEPENDS = "systemd"
RDEPENDS:${PN} += "bash bash-completion"

SRC_URI = " git://gitlab.com/driverctl/driverctl.git;branch=master"
SRCREV = "fa9dce43d1a667d6e6e26895fbed01b3b04362c9"

S = "${WORKDIR}/git"

inherit pkgconfig systemd features_check

FILES:${PN} += "${libdir}"
FILES:${PN} += "${libdir}/udev"
FILES:${PN} += "${libdir}/udev/rules.d"
FILES:${PN} += "${systemd_unitdir}"
FILES:${PN} += "${systemd_unitdir}/system"
FILES:${PN} += "${datadir}"
FILES:${PN} += "${datadir}/bash-completion"
FILES:${PN} += "${datadir}/bash-completion/completions"

do_install () {
	oe_runmake install DESTDIR=${D}
}
