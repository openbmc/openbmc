SUMMARY = "Assistive Technology Service Provider Interface (dbus core)"

DESCRIPTION = "It provides a Service Provider Interface for the Assistive Technologies available on the GNOME platform and a library against which applications can be linked."

HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
BUGTRACKER = "http://bugzilla.gnome.org/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

CVE_PRODUCT += "at-spi2-atk"

SRC_URI[archive.sha256sum] = "b0fabea6c9742eda8c9c675f9b8c1d1babba1da82da03ea1103710233717c1b0"

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

inherit gnomebase gi-docgen gettext systemd upstream-version-is-even gobject-introspection python3targetconfig

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
                ${systemd_user_unitdir}/at-spi-dbus-bus.service \
                ${PYTHON_SITEPACKAGES_DIR} \
                "
BBCLASSEXTEND = "native nativesdk"
