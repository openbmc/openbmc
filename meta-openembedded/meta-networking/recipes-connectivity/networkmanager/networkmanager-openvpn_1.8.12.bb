SUMMARY = "NetworkManager-openvpn-plugin"
SECTION = "net/misc"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=100d5a599bead70ddcd70dcd73f2e29c"

DEPENDS = "dbus dbus-glib networkmanager openvpn intltool-native glib-2.0-native"

inherit gnomebase useradd gettext systemd

SRC_URI = "${GNOME_MIRROR}/NetworkManager-openvpn/${@gnome_verdir("${PV}")}/NetworkManager-openvpn-${PV}.tar.xz"

SRC_URI[md5sum] = "e8b1210011ece18d0278310fbff45af5"
SRC_URI[sha256sum] = "0efda8878aaf0e6eb5071a053aea5d7f9d42aac097b3ff89e7cbc9233f815318"

S = "${WORKDIR}/NetworkManager-openvpn-${PV}"

# meta-gnome in layers is required using gnome:
PACKAGECONFIG[gnome] = "--with-gnome,--without-gnome,gtk+3 libnma libsecret"

do_configure_append() {
    # network-manager-openvpn.metainfo.xml is created in source folder but
    # compile expects it in build folder. As long as nobody comes up with a
    # better solution just support build:
    if [ -e ${S}/appdata/network-manager-openvpn.metainfo.xml ]; then
        mkdir -p ${B}/appdata
        cp -f ${S}/appdata/network-manager-openvpn.metainfo.xml ${B}/appdata/
    fi
}

do_install_append () {
    rm -rf ${D}${libdir}/NetworkManager/*.la
}

# Create user and group nm-openvpn that are needed since version 1.0.6
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system nm-openvpn"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/NetworkManager/*.so \
    ${nonarch_libdir}/NetworkManager/VPN/nm-openvpn-service.name \
"

FILES_${PN}-staticdev += " \
    ${libdir}/NetworkManager/*.a \
"

RDEPENDS_${PN} = " \
    networkmanager \
    openvpn \
"
