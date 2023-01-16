SUMMARY = "A Gtk client and libraries for SPICE remote desktop servers."
HOMEPAGE = "https://spice-space.org"
LICENSE = "LGPL-2.1-only & BSD-3-Clause & GPL-2.0-only"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=4fbd65380cdd255951079008b364516c \
	file://subprojects/spice-common/COPYING;md5=4b54a1fd55a448865a0b32d41598759d \
	file://subprojects/keycodemapdb/LICENSE.BSD;md5=5ae30ba4123bc4f2fa49aa0b0dce887b \
	file://subprojects/keycodemapdb/LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
"

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
DEPENDS:append:libc-musl = " libucontext"

RDEPENDS:${PN} = "python3-pyparsing python3-six"

inherit meson pkgconfig vala gobject-introspection features_check

REQUIRED_DISTRO_FEATURES = "opengl"

EXTRA_OEMESON = "-Dpie=true -Dvapi=enabled -Dintrospection=enabled"
EXTRA_OEMESON:append:libc-musl = " -Dcoroutine=libucontext"


FILES:${PN} += "${datadir}"
