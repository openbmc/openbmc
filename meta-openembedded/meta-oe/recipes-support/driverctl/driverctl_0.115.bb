SUMMARY = "driverctl is a device driver control utility for Linux"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

REQUIRED_DISTRO_FEATURES = "systemd"

DEPENDS = "systemd"
RDEPENDS:${PN} += "bash bash-completion"

SRC_URI = "git://gitlab.com/driverctl/driverctl.git;branch=master;protocol=https"
SRCREV = "2ba60536eb20ca0a6a751bd8b6501dba84ec45d3"

S = "${WORKDIR}/git"

inherit pkgconfig systemd features_check

do_install () {
	oe_runmake install DESTDIR=${D}
}

FILES:${PN} += " \
    ${libdir} \
    ${datadir} \
    ${systemd_system_unitdir} \
"

