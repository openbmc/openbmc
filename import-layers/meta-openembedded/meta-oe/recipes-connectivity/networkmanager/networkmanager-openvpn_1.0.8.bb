SUMMARY = "NetworkManager-openvpn-plugin"
SECTION = "net/misc"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=100d5a599bead70ddcd70dcd73f2e29c"

DEPENDS = "dbus dbus-glib networkmanager openvpn"

inherit gnomebase useradd gettext systemd

SRC_URI = "${GNOME_MIRROR}/NetworkManager-openvpn/${@gnome_verdir("${PV}")}/NetworkManager-openvpn-${PV}.tar.xz"

SRC_URI[md5sum] = "758a9951ad5e20a37c72cc7326c9c750"
SRC_URI[sha256sum] = "1b979519d72ba4d78e729d4856c5b53fad914ca7ee3ca91209ce489ba78912ac"

S = "${WORKDIR}/NetworkManager-openvpn-${PV}"

PACKAGECONFIG[gnome] = "--with-gnome,--without-gnome"

# Create user and group nm-openvpn that are needed since version 1.0.6
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system nm-openvpn"

FILES_${PN} += " \
    ${libdir}/NetworkManager/*.so \
"

RDEPENDS_${PN} = " \
    networkmanager \
    openvpn \
"
