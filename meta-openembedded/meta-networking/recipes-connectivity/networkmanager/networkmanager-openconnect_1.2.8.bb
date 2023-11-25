SUMMARY = "OpenConnect VPN client for NetworkManager"
SECTION = "net/misc"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=186e8b54342da4f753a62b7748c947db"

DEPENDS = "glib-2.0-native intltool-native libxml2 networkmanager openconnect"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase useradd

SRC_URI = "${GNOME_MIRROR}/NetworkManager-openconnect/${@gnome_verdir("${PV}")}/NetworkManager-openconnect-${PV}.tar.xz"

SRC_URI[sha256sum] = "5dedaa785d82d8e239ddd082bfac5250c691e964464be1807b6827263633cdcc"

S = "${WORKDIR}/NetworkManager-openconnect-${PV}"

# meta-gnome in layers is required using gnome:
PACKAGECONFIG[gnome] = "--with-gnome,--without-gnome,gtk+3 gcr3 libnma libsecret,"
PACKAGECONFIG[gtk4] = "--with-gtk4,--without-gtk4,gtk4,"

do_configure:append() {
    # network-manager-openconnect.metainfo.xml is created in source folder but
    # compile expects it in build folder. As long as nobody comes up with a
    # better solution just support build:
    if [ -e ${S}/appdata/network-manager-openconnect.metainfo.xml ]; then
        mkdir -p ${B}/appdata
        cp -f ${S}/appdata/network-manager-openconnect.metainfo.xml ${B}/appdata/
    fi
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system nm-openconnect"

FILES:${PN} += " \
    ${libdir}/NetworkManager/*.so \
    ${nonarch_libdir}/NetworkManager/VPN/nm-openconnect-service.name \
"

FILES:${PN}-staticdev += " \
    ${libdir}/NetworkManager/*.a \
"

RDEPENDS:${PN} = " \
    networkmanager \
    openconnect \
"
