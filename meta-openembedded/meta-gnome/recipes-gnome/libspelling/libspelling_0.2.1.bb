SUMMARY = "A spellcheck library for GTK 4"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/libspelling"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "enchant2 gtk4 gtksourceview5 icu"

inherit gnomebase pkgconfig gettext gi-docgen vala gobject-introspection

GIR_MESON_OPTION = ''
GIDOCGEN_MESON_OPTION = 'docs'

SRC_URI[archive.sha256sum] = "7a787b467bd493f6baffb44138dbc4bef78aaab60efb76a7db88b243bf0f6343"
