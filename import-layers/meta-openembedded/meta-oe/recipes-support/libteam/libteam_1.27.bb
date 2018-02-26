SUMMARY = "Library for controlling team network device"
HOMEPAGE = "http://www.libteam.org/"
SECTION = "libs/network"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libnl libdaemon jansson"

SRC_URI = "git://github.com/jpirko/libteam \
           file://0001-include-sys-select.h-for-fd_set-definition.patch \
           file://0002-teamd-Re-adjust-include-header-order.patch \
           "
SRCREV = "91a928a56a501daac5ce8b3c16bd9943661f1d16"

SRC_URI[md5sum] = "565114d70c41bff6093d8e57be284e8a"
SRC_URI[sha256sum] = "d65286379141db141bea33424ec0507bb0f827a0bf03d9c65004bb593e3d5545"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

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

