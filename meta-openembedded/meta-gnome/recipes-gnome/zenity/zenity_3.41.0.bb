SUMMARY = "Display dialog boxes from the command line and shell scripts"
SECTION = "x11/gnome"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase pkgconfig itstool gnome-help features_check gettext

DEPENDS = " \
    yelp-tools-native \
    gtk+3 \
"

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "19b676c3510e22badfcc3204062d432ba537402f5e0ae26128c0d90c954037e1"
SRC_URI += "file://0001-Harcode-shebang-to-avoid-pointing-to-build-system-s-.patch"

PACKAGECONFIG[libnotify] = "-Dlibnotify=true,-Dlibnotify=false,libnotify"
PACKAGECONFIG[webkitgtk] = "-Dwebkitgtk=true,-Dwebkitgtk=false,webkitgtk"

PACKAGES =+ "${PN}-gdialog"
FILES:${PN}-gdialog = "${bindir}/gdialog"
RDEPENDS:${PN}-gdialog += "perl"
