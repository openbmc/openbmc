SUMMARY = "Display dialog boxes from the command line and shell scripts"
SECTION = "x11/gnome"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase pkgconfig itstool gtk-icon-cache features_check gettext

DEPENDS = " \
    desktop-file-utils-native \
    hicolor-icon-theme \
    gtk+3 \
    gtk+3-native \
"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "c15582301ed90b9d42ce521dbccf99a989f22f12041bdd5279c6636da99ebf65"
SRC_URI += "file://0001-Harcode-shebang-to-avoid-pointing-to-build-system-s-.patch"

PACKAGECONFIG ?= "webkitgtk"
PACKAGECONFIG[webkitgtk] = "-Dwebkitgtk=true,-Dwebkitgtk=false,webkitgtk"

PACKAGES =+ "${PN}-gdialog"
FILES:${PN}-gdialog = "${bindir}/gdialog"
FILES:${PN}-doc = "${datadir}/man ${datadir}/help"
RDEPENDS:${PN}-gdialog += "perl"
