SUMMARY = "Xfce4 Widget library and X Window System interaction"
SECTION = "x11/libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=252890d9eee26aab7b432e8b8a616475"
DEPENDS = "intltool-native perl-native gtk+3 libice libsm libxfce4util xfce4-dev-tools xfconf virtual/libx11"

inherit xfce gtk-doc gobject-introspection features_check

REQUIRED_DISTRO_FEATURES = "x11"

# TODO: Check if 0001-... can go
SRC_URI += " \
    file://0001-libxfce4kbd-private-xfce4-keyboard-shortcuts.xml-fix.patch \
    file://0002-Remove-GTK2-leftovers.patch \
"
SRC_URI[sha256sum] = "8b06c9e94f4be88a9d87c47592411b6cbc32073e7af9cbd64c7b2924ec90ceaa"

EXTRA_OECONF += "--with-vendor-info=${DISTRO}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gladeui2] = "--enable-gladeui2,--disable-gladeui2,glade"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"

PACKAGES += "${PN}-glade"
FILES_${PN}-glade = " \
    ${libdir}/glade \
    ${datadir}/glade \
"
