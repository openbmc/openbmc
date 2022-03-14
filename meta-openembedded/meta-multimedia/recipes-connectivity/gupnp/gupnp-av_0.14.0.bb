SUMMARY = "Helpers for AV applications using UPnP"
DESCRIPTION = "GUPnP-AV is a collection of helpers for building AV (audio/video) applications using GUPnP."

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "gupnp"

inherit meson pkgconfig gobject-introspection vala

SRC_URI = "${GNOME_MIRROR}/${BPN}/0.14/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "20aed546fc882e78a3f186a0c8bce5c841cc3a44b7ea528298fbdc82596fb156"
