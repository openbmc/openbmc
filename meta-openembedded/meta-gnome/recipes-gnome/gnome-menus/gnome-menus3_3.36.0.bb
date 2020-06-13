SUMMARY = "GNOME menus"
SECTION = "x11/gnome"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = "python3 libxml2 popt gtk+3 gnome-common-native"

inherit features_check gnomebase gettext pkgconfig gobject-introspection upstream-version-is-even

REQUIRED_DISTRO_FEATURES = "x11"

BPN = "gnome-menus"

SRC_URI[archive.md5sum] = "a8fd71fcf31a87fc799d80396a526829"
SRC_URI[archive.sha256sum] = "d9348f38bde956fc32753b28c1cde19c175bfdbf1f4d5b06003b3aa09153bb1f"

FILES_${PN} += "${datadir}/desktop-directories/"
