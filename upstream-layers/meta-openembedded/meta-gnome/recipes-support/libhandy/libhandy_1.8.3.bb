SUMMARY = "A library full of GTK+ widgets for mobile phones"
DESCRIPTION = "Library with GTK widgets for mobile phones. Libhandy provides \
GTK widgets and GObjects to ease developing applications for mobile phones. \
It was developed by Purism (and used by several official GNOME projects) \
to extend Gtk by providing mobile-friendly widgets and make the creation of \
responsive apps easier."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/libhandy"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libhandy/-/issues"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://src/handy.h;beginline=4;endline=4;md5=20a35c7a7509753627b8c372765d5f22"

DEPENDS = "gtk+3"

inherit gnomebase gobject-introspection vala gettext gi-docgen features_check

SRC_URI[archive.sha256sum] = "05b497229073ff557f10b326e074c5066f8743a302d4820ab97bcb5cd2dab087"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

PACKAGES =+ "${PN}-examples"
FILES:${PN}-examples = "${bindir}"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
