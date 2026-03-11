# your responsibility to verify that the values are complete and correct.
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3000208d539ec061b899bce1d9ce9404"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase upstream-version-is-even gobject-introspection gtk-icon-cache

DEPENDS += " \
    glib-2.0 \
    gtk4 \
    libadwaita \
"

SRC_URI[archive.sha256sum] = "578ce512278ff2bb5eeebb55099392c52537a5abd9bd0629567f102532b38b25"

PACKAGECONFIG ?= ""
#EXTRA_OEMESON += "-Ddocs=disabled"
#GTKDOC_MESON_DISABLE_FLAG = "disabled"

EXTRA_OEMESON += "-Ddocs=disabled  -Dintrospection=enabled -Dvapi=false"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
