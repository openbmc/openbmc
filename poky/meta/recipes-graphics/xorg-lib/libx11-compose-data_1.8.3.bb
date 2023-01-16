SUMMARY = "Xlib: Compose data files for libx11"
DESCRIPTION = "This package provides the compose data files for libx11."

python () {
    if bb.utils.contains('DISTRO_FEATURES', 'x11', True, False, d):
        raise bb.parse.SkipRecipe("libx11-compose-data is incompatible with x11 distro feature, use libx11 instead.")
}

require xorg-lib-common.inc

LICENSE = "MIT & BSD-1-Clause & HPND & HPND-sell-variant & ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=1d49cdd2b386c5db11ec636d680b7116"

XORG_PN = "libX11"

SRC_URI += "file://0001-Drop-x11-dependencies.patch \
           "

SRC_URI[sha256sum] = "e31565c84006b6b8e01dc9399c806085739710bc2db2e0930f1511ed9d6585bd"

REQUIRED_DISTRO_FEATURES = ""

EXTRA_OECONF += "--disable-xkb"

do_compile() {
    oe_runmake -C nls
}

do_install() {
    oe_runmake DESTDIR=${D} -C nls install
}

PACKAGES = "${PN}"

FILES:${PN} = "${datadir}/X11/locale ${libdir}/X11/locale"
