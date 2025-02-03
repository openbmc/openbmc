SUMMARY = "A Gtk client and libraries for SPICE remote desktop servers."
HOMEPAGE = "https://spice-space.org"
LICENSE = "LGPL-2.1-only & BSD-3-Clause & GPL-2.0-only"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=4fbd65380cdd255951079008b364516c \
	file://subprojects/spice-common/COPYING;md5=4b54a1fd55a448865a0b32d41598759d \
	file://subprojects/keycodemapdb/LICENSE.BSD;md5=5ae30ba4123bc4f2fa49aa0b0dce887b \
	file://subprojects/keycodemapdb/LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
"

SRCREV = "f04479c16f0969fb394ebe74b6eff74e560a42f0"

SRC_URI = "gitsm://gitlab.freedesktop.org/spice/spice-gtk.git;protocol=https;branch=master"

CVE_STATUS[CVE-2012-4425] = "fixed-version: fixed since 0.15.3"

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

RDEPENDS:${PN} = "python3-pyparsing python3-six hwdata"

inherit meson pkgconfig vala gobject-introspection features_check gtk-doc

REQUIRED_DISTRO_FEATURES = "opengl"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GTKDOC_MESON_OPTION = 'gtk_doc'
GTKDOC_MESON_ENABLE_FLAG = 'enabled'
GTKDOC_MESON_DISABLE_FLAG = 'disabled'

do_configure:prepend() {
	echo ${PV} > ${S}/.tarball-version
}

PACKAGECONFIG ??= "${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'vapi', '', d)} smartcard"
PACKAGECONFIG[vapi] = "-Dvapi=enabled,-Dvapi=disabled"
PACKAGECONFIG[smartcard] = "-Dsmartcard=enabled,-Dsmartcard=disabled,libcacard"
PACKAGECONFIG[webdav] = "-Dwebdav=enabled,-Dwebdav=disabled,phodav libsoup"

EXTRA_OEMESON = "-Dpie=true -Dusb-ids-path=${datadir}/hwdata/usb.ids "
EXTRA_OEMESON:append:libc-musl = " -Dcoroutine=libucontext"

LDFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', ' -Wl,--undefined-version', '', d)}"

FILES:${PN} += "${datadir}"
