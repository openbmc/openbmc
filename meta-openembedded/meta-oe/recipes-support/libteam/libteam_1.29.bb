SUMMARY = "Library for controlling team network device"
HOMEPAGE = "http://www.libteam.org/"
SECTION = "libs/network"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libnl libdaemon jansson"

SRC_URI = "git://github.com/jpirko/libteam \
           file://0001-include-sys-select.h-for-fd_set-definition.patch \
           file://0002-teamd-Re-adjust-include-header-order.patch \
           file://0001-team_basic_test.py-disable-RedHat-specific-test.patch \
           file://run-ptest \
           "
SRCREV = "e897f02bf2403d4859a99e090e34ea768b4c0540"

S = "${WORKDIR}/git"

inherit autotools pkgconfig ptest

FILES_${PN} = "${libdir}/libteam${SOLIBS} \
"

PACKAGES += "${PN}-dctl ${PN}-utils"
FILES_${PN}-dctl = "${libdir}/libteamdctl${SOLIBS} \
"
FILES_${PN}-utils = "${bindir}/bond2team \
                     ${bindir}/teamd \
                     ${bindir}/teamdctl \
                     ${bindir}/teamnl \
"

RDEPENDS_${PN}-utils = "bash"
RDEPENDS_${PN}-ptest = "python"

do_install_ptest() {
	install ${S}/scripts/team_basic_test.py ${D}${PTEST_PATH}/
}
