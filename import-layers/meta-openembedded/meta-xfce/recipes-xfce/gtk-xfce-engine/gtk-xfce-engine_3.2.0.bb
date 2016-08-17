SUMMARY = "XFCE theme for GTK"
SECTION = "x11/libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "gtk+ xfce4-dev-tools-native"

inherit xfce

SRC_URI[md5sum] = "363d6c16a48a00e26d45c45c2e1fd739"
SRC_URI[sha256sum] = "875c9c3bda96faf050a2224649cc42129ffb662c4de33add8c0fd1fb860b47ed"

PACKAGECONFIG ??= "gtk3"
PACKAGECONFIG[gtk3] = "--enable-gtk3,--disable-gtk3, gtk+3, gtk3-xfce-engine"

PACKAGES += "${PN}-themes gtk3-xfce-engine"
FILES_${PN} += "${libdir}/gtk-2.0/*/engines/*.so"
FILES_gtk3-xfce-engine += "${libdir}/gtk-3.0/*/theming-engines/*.so"
FILES_${PN}-themes += "${datadir}/themes"

FILES_${PN}-dbg += "${libdir}/gtk-3.0/*/theming-engines/.debug \
                    ${libdir}/gtk-2.0/*/engines/.debug"
FILES_${PN}-dev += "${libdir}/gtk-2.0/*/engines/*.la \
                    ${libdir}/gtk-3.0/*/theming-engines/*.la"

RDEPENDS_${PN} += "${PN}-themes"
RDEPENDS_gtk3-xfce-engine += "${PN}-themes"
