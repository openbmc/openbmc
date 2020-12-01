SUMMARY = "A library full of GTK+ widgets for mobile phones"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://gitlab.gnome.org/GNOME/${BPN}.git;protocol=https"
SRCREV = "5cee0927b8b39dea1b2a62ec6d19169f73ba06c6"
S = "${WORKDIR}/git"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = 'gtk_doc'

inherit meson gobject-introspection vala gettext gtk-doc features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS += "gtk+3"

PACKAGES =+ "${PN}-examples"
FILES_${PN}-examples = "${bindir}"
