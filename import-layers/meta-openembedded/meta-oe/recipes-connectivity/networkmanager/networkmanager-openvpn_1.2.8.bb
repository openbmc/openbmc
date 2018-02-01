SUMMARY = "NetworkManager-openvpn-plugin"
SECTION = "net/misc"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=100d5a599bead70ddcd70dcd73f2e29c"

DEPENDS = "dbus dbus-glib networkmanager openvpn intltool-native glib-2.0-native"

inherit gnomebase useradd gettext systemd

SRC_URI = "${GNOME_MIRROR}/NetworkManager-openvpn/${@gnome_verdir("${PV}")}/NetworkManager-openvpn-${PV}.tar.xz"
SRC_URI[md5sum] = "9f325be386aa906ff9b0b7c0bdf2a59a"
SRC_URI[sha256sum] = "3e0b4007f248d96df4b8eb5d0f937536044af7053debbbf525e67c9bc5d30654"

S = "${WORKDIR}/NetworkManager-openvpn-${PV}"

PACKAGECONFIG[gnome] = "--with-gnome,--without-gnome"

do_install_append () {
    rm -rf ${D}${libdir}/NetworkManager/*.la
}

# Create user and group nm-openvpn that are needed since version 1.0.6
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system nm-openvpn"

FILES_${PN} += " \
    ${libdir}/NetworkManager/*.so \
    ${libdir}/NetworkManager/VPN/nm-openvpn-service.name \
"

FILES_${PN}-staticdev += " \
    ${libdir}/NetworkManager/*.a \
"

RDEPENDS_${PN} = " \
    networkmanager \
    openvpn \
"
