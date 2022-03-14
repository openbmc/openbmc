SUMMARY = "A library full of GTK+ widgets for mobile phones"
DESCRIPTION = "Library with GTK widgets for mobile phones. Libhandy provides \
GTK widgets and GObjects to ease developing applications for mobile phones. \
It was developed by Purism (and used by several official GNOME projects) \
to extend Gtk by providing mobile-friendly widgets and make the creation of \
responsive apps easier."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/libhandy"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/libhandy/-/issues"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://gitlab.gnome.org/GNOME/libhandy.git;protocol=https;branch=master \
           file://0001-Add-private-headers.patch \
"
SRCREV = "8fa8306a79215fc6ebf2483145da98bf9b2495ab"
S = "${WORKDIR}/git"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = 'gtk_doc'

inherit meson gobject-introspection vala gettext gtk-doc features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS += "gtk+3"

PACKAGES =+ "${PN}-examples"
FILES:${PN}-examples = "${bindir}"
