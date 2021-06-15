SUMMARY = "GTK+ frontend for the ConnMan network connection manager"
HOMEPAGE = "http://connman.net/"
SECTION = "libs/network"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://properties/main.c;beginline=1;endline=20;md5=50c77c81871308b033ab7a1504626afb \
                    file://common/connman-dbus.c;beginline=1;endline=20;md5=de6b485c0e717a0236402d220187717a"

DEPENDS = "gtk+3 dbus-glib dbus-glib-native intltool-native gettext-native"

# 0.7 tag
SRCREV = "cf3c325b23dae843c5499a113591cfbc98acb143"
SRC_URI = "git://github.com/connectivity/connman-gnome.git \
           file://0001-Removed-icon-from-connman-gnome-about-applet.patch \
           file://null_check_for_ipv4_config.patch \
           file://images/ \
           file://connman-gnome-fix-dbus-interface-name.patch \
           file://0001-Port-to-Gtk3.patch \
          "

S = "${WORKDIR}/git"

inherit autotools-brokensep gtk-icon-cache pkgconfig features_check
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

RDEPENDS_${PN} = "connman"

do_install_append() {
    install -m 0644 ${WORKDIR}/images/* ${D}/usr/share/icons/hicolor/22x22/apps/
}
