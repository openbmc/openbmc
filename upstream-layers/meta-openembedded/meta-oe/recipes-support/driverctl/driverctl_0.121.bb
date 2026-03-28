SUMMARY = "driverctl is a device driver control utility for Linux"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

REQUIRED_DISTRO_FEATURES = "systemd"

DEPENDS = "systemd"
RDEPENDS:${PN} += "bash bash-completion"

SRC_URI = "git://gitlab.com/driverctl/driverctl.git;branch=master;protocol=https;tag=${PV}"
SRCREV = "0a72c5b1091d87e839ff083f686477f9eff5fb97"


inherit pkgconfig systemd features_check

do_install () {
	oe_runmake install DESTDIR=${D}
}

FILES:${PN} += " \
    ${libdir} \
    ${datadir} \
    ${systemd_system_unitdir} \
"

