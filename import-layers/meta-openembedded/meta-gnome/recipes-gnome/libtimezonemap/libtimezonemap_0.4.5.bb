DESCRIPTION = "Time zone map widget for Gtk+"
HOMEPAGE = "https://launchpad.net/timezonemap"
SECTION = "devel/lib"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://archive.ubuntu.com/ubuntu/pool/main/libt/${BPN}/${BPN}_${PV}.tar.gz \
"
SRC_URI[md5sum] = "054306fa998fe580f17b68aa1e16551b"
SRC_URI[sha256sum] = "327e64a17c676c1bcda3b6ba3394d3d01250e5ac9a49222b9ff5737d90b15383"


DEPENDS = "gtk+3 gdk-pixbuf libsoup-2.4 json-glib gnome-common-native \
"

B = "${S}"

inherit autotools pkgconfig gobject-introspection

do_configure_prepend() {
	(cd ${S}; NOCONFIGURE="yes" . ${S}/autogen.sh)
}
