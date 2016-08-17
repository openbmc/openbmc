require recipes-graphics/xorg-driver/xorg-driver-input.inc
SUMMARY = "X.Org X server -- multitouch input driver"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8a71d0475d08eee76d8b6d0c6dbec543"

DEPENDS += "pixman"

PNBLACKLIST[xf86-input-mtev] ?= "BROKEN: doesn't build with B!=S (Makefile without ${S} in sed call)"

SRC_URI = "git://gitorious.org/xorg/xf86-input-mtev.git file://fix-it.patch"
SRCREV = "1eb469166ffc095c5801475f057f911f97a6e641"
S = "${WORKDIR}/git"
PV = "1.0.0+gitr${SRCPV}"
PR = "${INC_PR}.0"

EXTRA_OEMAKE = "'INCLUDE=-I${STAGING_INCDIR}/xorg -I${STAGING_INCDIR}/pixman-1'"

#skip xorg-driver-common.inc AC_CHECK_FILE mangling
do_configure_prepend () {
    sed 's#gcc#${CC}#g' -i Makefile
    return
}
