SUMMARY = "An HTTP library implementation in C"
HOMEPAGE = "http://www.gnome.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

SECTION = "x11/gnome/libs"

DEPENDS = "glib-2.0 gnutls libxml2 sqlite3 intltool-native"

# libsoup-gnome is entirely deprecated and just stubs in 2.42 onwards.  Enable
# by default but let it be easily disabled.
PACKAGECONFIG ??= "gnome"
PACKAGECONFIG[gnome] = "--with-gnome,--without-gnome"

SHRT_VER = "${@bb.data.getVar('PV',d,1).split('.')[0]}.${@bb.data.getVar('PV',d,1).split('.')[1]}"
SRC_URI = "${GNOME_MIRROR}/libsoup/${SHRT_VER}/libsoup-${PV}.tar.xz"

SRC_URI[md5sum] = "9a84d66e1b7ccd3bd340574b11eccc15"
SRC_URI[sha256sum] = "1e01365ac4af3817187ea847f9d3588c27eee01fc519a5a7cb212bb78b0f667b"

S = "${WORKDIR}/libsoup-${PV}"

inherit autotools gettext pkgconfig

# glib-networking is needed for SSL, proxies, etc.
RRECOMMENDS_${PN} = "glib-networking"
