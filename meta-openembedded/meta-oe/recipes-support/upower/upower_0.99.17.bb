SUMMARY = "UPower is an abstraction for enumerating power devices"
DESCRIPTION = "UPower is an abstraction for enumerating power devices, listening to device events and querying history and statistics."
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=0de8fbf1d97a140d1d93b9f14dcfbf08"

DEPENDS = " \
    intltool-native \
    libusb1 \
    libgudev \
    glib-2.0 \
    dbus-glib \
"

SRC_URI = "git://gitlab.freedesktop.org/upower/upower.git;protocol=https;branch=master"
SRCREV = "c889154ec8e3e2239db9260d48b2e198d72ba002"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "UPOWER_(?P<pver>\d+(\_\d+)+)"

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"
GTKDOC_MESON_OPTION = "gtk-doc"

inherit meson pkgconfig gtk-doc gettext gobject-introspection systemd

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[idevice] = "-Didevice=enabled,-Didevice=disabled,libimobiledevice libplist"
PACKAGECONFIG[systemd] = "-Dsystemdsystemunitdir=${systemd_system_unitdir},-Dsystemdsystemunitdir=no -Dudevrulesdir=${base_libdir}/udev/rules.d,systemd"

EXTRA_OEMESON = " \
    -Dos_backend=linux \
    -Dman=false \
"

SYSTEMD_SERVICE:${PN} = "upower.service"
# don't start on boot by default - dbus does that on demand
SYSTEMD_AUTO_ENABLE = "disable"

RDEPENDS:${PN} += "dbus"
RRECOMMENDS:${PN} += "pm-utils"
FILES:${PN} += " \
    ${datadir}/dbus-1/ \
    ${base_libdir}/udev/* \
"
