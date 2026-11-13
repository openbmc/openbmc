SUMMARY = "A Gtk client and libraries for SPICE remote desktop servers."
HOMEPAGE = "https://spice-space.org"
LICENSE = "BSD-3-Clause AND GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=4fbd65380cdd255951079008b364516c \
	file://subprojects/spice-common/COPYING;md5=4b54a1fd55a448865a0b32d41598759d \
	file://subprojects/keycodemapdb/LICENSE.BSD;md5=5ae30ba4123bc4f2fa49aa0b0dce887b \
	file://subprojects/keycodemapdb/LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
"

SRCREV = "f028f45a573926736223548887e18ac65dabb50b"

SRC_URI = "gitsm://gitlab.freedesktop.org/spice/spice-gtk.git;protocol=https;branch=master;tag=v${PV}"

CVE_STATUS[CVE-2012-4425] = "fixed-version: fixed since 0.15.3"

DEPENDS = "python3-pyparsing-native spice-protocol glib-2.0 pixman openssl jpeg zlib json-glib libcap-ng gstreamer1.0 gstreamer1.0-plugins-base udev"
DEPENDS:append:libc-musl = " libucontext"

inherit meson pkgconfig vala gobject-introspection features_check gi-docgen

REQUIRED_DISTRO_FEATURES = "opengl"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GIDOCGEN_MESON_ENABLE_FLAG = 'enabled'
GIDOCGEN_MESON_DISABLE_FLAG = 'disabled'

do_configure:prepend() {
	echo ${PV} > ${S}/.tarball-version
}

GTK_DEPS = "gtk+3 libepoxy libva ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-native wayland-protocols wayland', '', d)}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)} \
                   ${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'vapi', '', d)} \
				   gtk lz4 opus sasl smartcard usbredir"
PACKAGECONFIG[gtk] = "-Dgtk=enabled,-Dgtk=disabled,${GTK_DEPS}"
PACKAGECONFIG[lz4] = "-Dlz4=enabled,-Dlz4=disabled,lz4"
PACKAGECONFIG[opus] = "-Dopus=enabled,-Dopus=disabled,libopus"
PACKAGECONFIG[polkit] = "-Dpolkit=enabled,-Dpolkit=disabled,polkit acl"
PACKAGECONFIG[sasl] = "-Dsasl=enabled,-Dsasl=disabled,cyrus-sasl"
PACKAGECONFIG[smartcard] = "-Dsmartcard=enabled,-Dsmartcard=disabled,libcacard"
PACKAGECONFIG[usbredir] = "-Dusbredir=enabled,-Dusbredir=disabled,usbredir libusb1"
PACKAGECONFIG[vapi] = "-Dvapi=enabled,-Dvapi=disabled"
PACKAGECONFIG[webdav] = "-Dwebdav=enabled,-Dwebdav=disabled,phodav libsoup"

EXTRA_OEMESON = "-Dpie=true -Dusb-ids-path=${datadir}/hwdata/usb.ids "
EXTRA_OEMESON:append:libc-musl = " -Dcoroutine=libucontext"

LDFLAGS += "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', ' -Wl,--undefined-version', '', d)}"

FILES:${PN} += "${datadir}"

RDEPENDS:${PN} = "hwdata"
