SUMMARY = "Helpers for AV applications using UPnP"
DESCRIPTION = "GUPnP-AV is a collection of helpers for building AV (audio/video) applications using GUPnP."

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "gupnp"

inherit meson pkgconfig gobject-introspection vala

SRC_URI = "${GNOME_MIRROR}/${BPN}/0.14/${BPN}-${PV}.tar.xz \
           file://0001-all-Drop-xmlRecoverMemory.patch \
          "
SRC_URI[sha256sum] = "b79ce0cc4b0c66d9c54bc22183a10e5709a0011d2af272025948efcab33a3e4f"
