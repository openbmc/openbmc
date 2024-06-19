SUMMARY = "Library for controlling team network device"
HOMEPAGE = "http://www.libteam.org/"
SECTION = "libs/network"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libnl libdaemon jansson"

SRC_URI = "git://github.com/jpirko/libteam;branch=master;protocol=https \
           file://0002-teamd-Re-adjust-include-header-order.patch \
           file://0001-team_basic_test.py-disable-RedHat-specific-test.patch \
           file://0001-team_basic_test.py-switch-to-python3.patch \
           file://0001-team_basic_test.py-check-the-return-value.patch \
           file://0001-teamd-Pass-correct-parameter-type-to-accept-API.patch \
           file://run-ptest \
           "
SRCREV = "8b843e93cee1dab61fb79b01791201cdad45e1d1"

S = "${WORKDIR}/git"

inherit autotools pkgconfig ptest

FILES:${PN} = "${libdir}/libteam${SOLIBS} \
"

PACKAGES += "${PN}-dctl ${PN}-utils"
FILES:${PN}-dctl = "${libdir}/libteamdctl${SOLIBS} \
"
FILES:${PN}-utils = "${bindir}/bond2team \
                     ${bindir}/teamd \
                     ${bindir}/teamdctl \
                     ${bindir}/teamnl \
"

RDEPENDS:${PN}-utils = "bash"
RDEPENDS:${PN}-ptest = "python3-core libteam-utils"
# Ensure kernel has these mods enabled features/net/team/team.scc
RRECOMMENDS:${PN} += "kernel-module-team kernel-module-team-mode-activebackup \
                      kernel-module-team-mode-broadcast \
                      kernel-module-team-mode-loadbalance \
                      kernel-module-team-mode-random \
                      kernel-module-team-mode-roundrobin"

do_install_ptest() {
	install ${S}/scripts/team_basic_test.py ${D}${PTEST_PATH}/
}
