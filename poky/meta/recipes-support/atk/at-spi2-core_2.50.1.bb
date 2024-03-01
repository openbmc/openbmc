SUMMARY = "Assistive Technology Service Provider Interface (dbus core)"

DESCRIPTION = "It provides a Service Provider Interface for the Assistive Technologies available on the GNOME platform and a library against which applications can be linked."

HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
BUGTRACKER = "http://bugzilla.gnome.org/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz"

SRC_URI[sha256sum] = "5727b5c0687ac57ba8040e79bd6731b714a36b8fcf32190f236b8fb3698789e7"

DEPENDS = " \
	dbus \
	glib-2.0 \
	glib-2.0-native \
	libxml2 \
	${@'python3-sphinx-native' if d.getVar('GIDOCGEN_ENABLED') == 'True' else ''} \
"

# For backwards compatibility
PROVIDES += "atk at-spi2-atk"
RPROVIDES:${PN} += "atk at-spi2-atk"

inherit meson gi-docgen gettext systemd pkgconfig upstream-version-is-even gobject-introspection

EXTRA_OEMESON = " -Dsystemd_user_dir=${systemd_user_unitdir} \
                  -Ddbus_daemon=${bindir}/dbus-daemon"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "-Dx11=enabled,-Dx11=disabled,virtual/libx11 libxi libxtst"

GIDOCGEN_MESON_OPTION = "docs"
GIR_MESON_OPTION = 'introspection'
GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

FILES:${PN} += "${libdir}/gnome-settings-daemon-3.0/gtk-modules/at-spi2-atk.desktop \
                ${libdir}/gtk-2.0/modules/libatk-bridge.so \
                ${datadir}/dbus-1/services/*.service \
                ${datadir}/dbus-1/accessibility-services/*.service \
                ${datadir}/defaults/at-spi2 \
                ${systemd_user_unitdir}/at-spi-dbus-bus.service \
                "
BBCLASSEXTEND = "native nativesdk"
