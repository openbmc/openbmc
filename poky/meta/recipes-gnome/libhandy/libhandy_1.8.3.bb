SUMMARY = "A library full of GTK+ widgets for mobile phones"
DESCRIPTION = "Library with GTK widgets for mobile phones. Libhandy provides \
GTK widgets and GObjects to ease developing applications for mobile phones. \
It was developed by Purism (and used by several official GNOME projects) \
to extend Gtk by providing mobile-friendly widgets and make the creation of \
responsive apps easier."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/libhandy"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libhandy/-/issues"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://gitlab.gnome.org/GNOME/libhandy.git;protocol=https;branch=libhandy-1-8"
SRCREV = "9b0071408ce86a3ef843806fddd723a85f6f2416"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.(\d*[02468])+(\.\d+))"
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

inherit meson gobject-introspection vala gettext gi-docgen features_check pkgconfig

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS += "gtk+3"

PACKAGES =+ "${PN}-examples"
FILES:${PN}-examples = "${bindir}"
