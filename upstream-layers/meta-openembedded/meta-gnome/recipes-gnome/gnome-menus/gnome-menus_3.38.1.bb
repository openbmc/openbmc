SUMMARY = "GNOME menus"
SECTION = "x11/gnome"
LICENSE = "GPL-2.0-only & LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = "glib-2.0"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gettext pkgconfig gobject-introspection upstream-version-is-even

SRC_URI[archive.sha256sum] = "1198a91cdbdcfb232df94e71ef5427617d26029e327be3f860c3b0921c448118"

FILES:${PN} += "${datadir}/desktop-directories/"
