SUMMARY = "LibGTop2"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit gnomebase lib_package gtk-doc gobject-introspection gettext upstream-version-is-even

# depends on libxau
inherit features_check
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "c6d67325cd97b2208b41e07e6cc7b947"
SRC_URI[archive.sha256sum] = "78f3274c0c79c434c03655c1b35edf7b95ec0421430897fb1345a98a265ed2d4"

DEPENDS = "glib-2.0 libxau"

EXTRA_OEMAKE += "LIBGTOP_LIBS="
