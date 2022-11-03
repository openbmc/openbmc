SUMMARY = "A Gtk client and libraries for SPICE remote desktop servers."
HOMEPAGE = "https://spice-space.org"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRCREV = "74e673d7c3d9cd281d85c691fbc520107066da01"

SRC_URI = "gitsm://gitlab.freedesktop.org/spice/spice-gtk.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

DEPENDS = " \
	${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland wayland-native wayland-protocols', '', d)} \
	acl \
	cyrus-sasl \
	gstreamer1.0 \
	gstreamer1.0-plugins-base \
	gstreamer1.0-vaapi \
	gtk+3 \
	jpeg \
	json-glib \
	libcap-ng \
	libepoxy \
	libopus \
	libusb1 \
	lz4 \
	pixman \
	python3-pyparsing-native \
	python3-six-native \
	spice-protocol \
	usbredir \
	usbutils \
	zlib \
"

RDEPENDS:${PN} = "python3-pyparsing python3-six"

inherit meson pkgconfig vala gobject-introspection

EXTRA_OEMESON = "-Dpie=true -Dvapi=enabled -Dintrospection=enabled"

FILES:${PN} += "${datadir}"
