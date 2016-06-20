SUMMARY = "An HTTP library implementation in C"
HOMEPAGE = "https://wiki.gnome.org/Projects/libsoup"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/gnome/libs"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = "glib-2.0 glib-2.0-native libxml2 sqlite3 intltool-native"

SHRT_VER = "${@d.getVar('PV', True).split('.')[0]}.${@d.getVar('PV', True).split('.')[1]}"

SRC_URI = "${GNOME_MIRROR}/libsoup/${SHRT_VER}/libsoup-${PV}.tar.xz"

SRC_URI[md5sum] = "e4757d09012ed93822b1ee41435fec24"
SRC_URI[sha256sum] = "db55628b5c7d952945bb71b236469057c8dfb8dea0c271513579c6273c2093dc"

S = "${WORKDIR}/libsoup-${PV}"

inherit autotools gettext pkgconfig upstream-version-is-even gobject-introspection

# libsoup-gnome is entirely deprecated and just stubs in 2.42 onwards. Disable by default.
PACKAGECONFIG ??= ""
PACKAGECONFIG[gnome] = "--with-gnome,--without-gnome"

EXTRA_OECONF = "--disable-vala"

# When built without gnome support, libsoup-2.4 will contain only one shared lib
# and will therefore become subject to renaming by debian.bbclass. Prevent
# renaming in order to keep the package name consistent regardless of whether
# gnome support is enabled or disabled.
DEBIAN_NOAUTONAME_${PN} = "1"

# glib-networking is needed for SSL, proxies, etc.
RRECOMMENDS_${PN} = "glib-networking"
