SUMMARY = "Xfce4 Widget library and X Window System interaction"
SECTION = "x11/libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=252890d9eee26aab7b432e8b8a616475"
DEPENDS = "perl-native glib-2.0 gtk+ gtk+3 intltool-native libxfce4util xfconf xfce4-dev-tools virtual/libx11 libsm libice"

inherit xfce autotools gettext gtk-doc distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " \
    file://0001-libxfce4kbd-private-xfce4-keyboard-shortcuts.xml-fix.patch \
"
SRC_URI[md5sum] = "ea9fad7d059fe8f531fe8db42dabb5a9"
SRC_URI[sha256sum] = "3d619811bfbe7478bb984c16543d980cadd08586365a7bc25e59e3ca6384ff43"

EXTRA_OECONF += "--with-vendor-info=${DISTRO}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gladeui] = "--enable-gladeui,--disable-gladeui,glade3"
PACKAGECONFIG[startup-notification] = "--enable-startup-notification,--disable-startup-notification,startup-notification"

PACKAGES =+ "${PN}-gtk2 ${PN}-gtk3"
FILES_${PN}-gtk2 += "${libdir}/libxfce4ui-1.so.* ${libdir}/libxfce4kbd-private-2.so.*"
FILES_${PN}-gtk3 += "${libdir}/libxfce4ui-2.so.* ${libdir}/libxfce4kbd-private-3.so.*"

FILES_${PN}-dbg += "${libdir}/glade3/modules/.debug"
FILES_${PN}-dev += "${libdir}/glade3/modules/*.la \
                   ${datadir}/glade3/catalogs/*.in"
PACKAGES += "${PN}-glade"
FILES_${PN}-glade = "${libdir}/glade3 \
                     ${datadir}/glade3"

RDEPENDS_${PN}-gtk2 = "${PN}"
RDEPENDS_${PN}-gtk3 = "${PN}"
