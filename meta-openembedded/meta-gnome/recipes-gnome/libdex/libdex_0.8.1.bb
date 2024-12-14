LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

GNOMEBASEBUILDCLASS = "meson"
inherit features_check gnomebase upstream-version-is-even gobject-introspection

DEPENDS += " \
    glib-2.0 \
"
DEPENDS:append:libc-musl = " libucontext"

LDFLAGS:append:libc-musl = " -lucontext"

SRC_URI[archive.sha256sum] = "955475ad3e43aabd6f6f70435264b5ee77bd265bd95545211fee026b08d378a0"

PACKAGECONFIG ?= ""
EXTRA_OEMESON += "-Dintrospection=enabled -Dvapi=false"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
