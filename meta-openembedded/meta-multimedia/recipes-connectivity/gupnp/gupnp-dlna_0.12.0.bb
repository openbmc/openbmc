SUMMARY = "Helpers for AV applications using DLNA"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://libgupnp-dlna/gupnp-dlna-profile.h;beginline=1;endline=22;md5=1b85459f65cb1e73a885ca137aab6274"

DEPENDS = "libxml2 glib-2.0"

SRC_URI = "http://download.gnome.org/sources/${BPN}/0.12/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "3d53b96f85bc5628d33e367ec9bf2adb38ef2b34d7ad0434a6ba0cf4ad9048e6"

inherit meson pkgconfig gobject-introspection vala

PACKAGECONFIG ?= "gstreamer"
PACKAGECONFIG[gstreamer] = "-Dgstreamer_backend=enabled,-Dgstreamer_backend=disabled,gstreamer1.0 gstreamer1.0-plugins-base"

FILES:${PN} += "${datadir}/gupnp-dlna-2.0/dlna-profiles"
