SUMMARY = "A spellcheck library for GTK 4"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/libspelling"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "enchant2 gtk4 gtksourceview5 icu"

inherit gnomebase pkgconfig gettext gi-docgen vala gobject-introspection features_check

# reason: gtk4 requires opengl distro feature
REQUIRED_DISTRO_FEATURES = "opengl"

GIR_MESON_OPTION = ''
GIDOCGEN_MESON_OPTION = 'docs'

PACKAGECONFIG ?= ""
PACKAGECONFIG[sysprof] = "-Dsysprof=true,-Dsysprof=false,sysprof"

SRC_URI[archive.sha256sum] = "56e3f01a3a18b575beea4c34349f99cdaba316e1f7c271b1231f7bcf5d9af73b"
