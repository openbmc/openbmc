SUMMARY = "A library for collecting system monitoring data"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase lib_package gtk-doc gobject-introspection gettext upstream-version-is-even features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "43ea9ad13f7caf98303e64172b191be9b96bab340b019deeec72251ee140fe3b"

DEPENDS = "glib-2.0 libxau"
