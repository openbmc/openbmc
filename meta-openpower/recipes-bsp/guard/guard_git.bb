HOMEPAGE =  "https://github.com/open-power/guard"
SUMMARY     = "Guard the faulty components"
DESCRIPTION = "Provide a way to guard the faulty component from the system"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit pkgconfig meson

S = "${WORKDIR}/git"

SRC_URI = "git://git@github.com/open-power/guard;branch="main";protocol=https"
SRCREV = "c7c018c6d7735da8150334da6f3e7f8d9357efb9"

DEPENDS = "cli11"

PACKAGECONFIG ??= "${@bb.utils.filter('MACHINE_FEATURES', 'phal', d)}"
PACKAGECONFIG[phal] = "-Ddevtree=enabled, -Ddevtree=disabled, pdata pdbg"
