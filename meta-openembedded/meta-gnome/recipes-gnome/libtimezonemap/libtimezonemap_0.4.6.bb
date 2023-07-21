DESCRIPTION = "Time zone map widget for Gtk+"
HOMEPAGE = "https://launchpad.net/timezonemap"
SECTION = "devel/lib"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${DEBIAN_MIRROR}/main/libt/${BPN}/${BPN}_${PV}.orig.tar.gz;downloadfilename=${BP}.tar.gz"
SRC_URI[sha256sum] = "0d634cc2476d8f57d1ee1864bd4f442180ae4bf040a9ae4bf73b66bbd85d7195"

DEPENDS = "gtk+3 gdk-pixbuf libsoup-2.4 json-glib gnome-common-native"

B = "${S}"

inherit features_check autotools pkgconfig gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"

do_configure:prepend() {
	(cd ${S}; NOCONFIGURE="yes" . ${S}/autogen.sh)
}
