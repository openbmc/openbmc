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

SRC_URI[archive.sha256sum] = "ec0372d83f42b65aee3734248ef8e2ffbfba4ea91268419c98ea44a00ef3e83f"
