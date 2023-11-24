SUMMARY = "NetworkManager-openvpn-plugin"
SECTION = "net/misc"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=100d5a599bead70ddcd70dcd73f2e29c"

DEPENDS = "dbus dbus-glib networkmanager openvpn intltool-native glib-2.0-native"

inherit gnomebase useradd gettext systemd

SRC_URI = "${GNOME_MIRROR}/NetworkManager-openvpn/${@gnome_verdir("${PV}")}/NetworkManager-openvpn-${PV}.tar.xz"

SRC_URI:append:libc-musl = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', ' file://0001-linker-scripts-Do-not-export-_IO_stdin_used.patch', '', d)}"

SRC_URI[sha256sum] = "62f0f2a8782221b923f212ac2a8ebbc1002efd6a90ee945dad4adfb56d076d21"

S = "${WORKDIR}/NetworkManager-openvpn-${PV}"

# meta-gnome in layers is required using gnome:
PACKAGECONFIG[gnome] = "--with-gnome,--without-gnome,gtk+3 libnma libsecret"

do_configure:append() {
    # network-manager-openvpn.metainfo.xml is created in source folder but
    # compile expects it in build folder. As long as nobody comes up with a
    # better solution just support build:
    if [ -e ${S}/appdata/network-manager-openvpn.metainfo.xml ]; then
        mkdir -p ${B}/appdata
        cp -f ${S}/appdata/network-manager-openvpn.metainfo.xml ${B}/appdata/
    fi
}

do_install:append () {
    rm -rf ${D}${libdir}/NetworkManager/*.la
}

# Create user and group nm-openvpn that are needed since version 1.0.6
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system nm-openvpn"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${libdir}/NetworkManager/*.so \
    ${nonarch_libdir}/NetworkManager/VPN/nm-openvpn-service.name \
"

FILES:${PN}-staticdev += " \
    ${libdir}/NetworkManager/*.a \
"

RDEPENDS:${PN} = " \
    networkmanager \
    openvpn \
"
