SUMMARY = "Xlib: Compose data files for libx11"
DESCRIPTION = "This package provides the compose data files for libx11."

require xorg-lib-common.inc

LICENSE = "MIT & BSD-1-Clause & HPND & HPND-sell-variant & ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=1d49cdd2b386c5db11ec636d680b7116"

XORG_PN = "libX11"

SRC_URI += "file://0001-Drop-x11-dependencies.patch \
           "

SRC_URI[sha256sum] = "fa026f9bb0124f4d6c808f9aef4057aad65e7b35d8ff43951cef0abe06bb9a9a"

REQUIRED_DISTRO_FEATURES = ""
CONFLICT_DISTRO_FEATURES = "x11"

EXTRA_OECONF += "--disable-xkb"

do_compile() {
    oe_runmake -C nls
}

do_install() {
    oe_runmake DESTDIR=${D} -C nls install
}

PACKAGES = "${PN}"

FILES:${PN} = "${datadir}/X11/locale ${libdir}/X11/locale"
