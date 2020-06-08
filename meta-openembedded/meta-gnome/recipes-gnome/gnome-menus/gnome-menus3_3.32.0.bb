SUMMARY = "GNOME menus"
SECTION = "x11/gnome"
LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = "python3 libxml2 popt gtk+3 gnome-common-native"

inherit features_check gnomebase gettext pkgconfig gobject-introspection upstream-version-is-even

REQUIRED_DISTRO_FEATURES = "x11"

BPN = "gnome-menus"

SRC_URI[archive.md5sum] = "4262208c13f266d9ada7d356aada9e1b"
SRC_URI[archive.sha256sum] = "c850c64b2074265fe59e099a340b8689cf3dd4658dc9feddd2ab5e95f1a74b74"

FILES_${PN} += "${datadir}/desktop-directories/"
