SUMMARY = "A library for collecting system monitoring data"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase lib_package gtk-doc gobject-introspection gettext upstream-version-is-even features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "775676df958e2ea2452f7568f28b2ea581063d312773dd5c0b7624c1b9b2da8c"

DEPENDS = "glib-2.0 libxau"
