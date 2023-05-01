SUMMARY = "Assistive Technology Service Provider Interface (dbus core)"

DESCRIPTION = "It provides a Service Provider Interface for the Assistive Technologies available on the GNOME platform and a library against which applications can be linked."

HOMEPAGE = "https://wiki.linuxfoundation.org/accessibility/d-bus"
BUGTRACKER = "http://bugzilla.gnome.org/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz"

SRC_URI[sha256sum] = "aa0c86c79f7a8d67bae49a5b7a5ab08430c608cffe6e33bf47a72f41ab03c3d0"

DEPENDS = "dbus glib-2.0 glib-2.0-native libxml2"

# For backwards compatibility
PROVIDES += "atk at-spi2-atk"
RPROVIDES:${PN} += "atk at-spi2-atk"

inherit meson gtk-doc gettext systemd pkgconfig upstream-version-is-even gobject-introspection

EXTRA_OEMESON = " -Dsystemd_user_dir=${systemd_user_unitdir} \
                  -Ddbus_daemon=${bindir}/dbus-daemon"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "-Dx11=yes,-Dx11=no,virtual/libx11 libxi libxtst"

GTKDOC_MESON_OPTION = "docs"
# The documentation doesn't build if X11 is disabled. Appears to be fixed post 2.46.0.
EXTRA_OEMESON += "${@bb.utils.contains("DISTRO_FEATURES", "x11", "", "-Ddocs=false", d)}"

GIR_MESON_OPTION = 'introspection'
GIR_MESON_ENABLE_FLAG = 'yes'
GIR_MESON_DISABLE_FLAG = 'no'

FILES:${PN} += "${libdir}/gnome-settings-daemon-3.0/gtk-modules/at-spi2-atk.desktop \
                ${libdir}/gtk-2.0/modules/libatk-bridge.so \
                ${datadir}/dbus-1/services/*.service \
                ${datadir}/dbus-1/accessibility-services/*.service \
                ${datadir}/defaults/at-spi2 \
                ${systemd_user_unitdir}/at-spi-dbus-bus.service \
                "
BBCLASSEXTEND = "native nativesdk"
