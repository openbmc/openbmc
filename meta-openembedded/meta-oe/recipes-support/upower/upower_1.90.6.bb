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

SRC_URI = "https://gitlab.freedesktop.org/${BPN}/${BPN}/-/archive/v${PV}/${BPN}-v${PV}.tar.bz2"
SRC_URI[sha256sum] = "aed4e42a21307512ad236ad58d7ee4e0196670c8524a168a0edccdc32964ea0c"
S = "${WORKDIR}/${BPN}-v${PV}"

UPSTREAM_CHECK_URI = "https://gitlab.freedesktop.org/${BPN}/${BPN}/-/tags"
UPSTREAM_CHECK_REGEX = "v(?P<pver>\d+(\.\d+)+)"

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"
GTKDOC_MESON_OPTION = "gtk-doc"

inherit meson pkgconfig gtk-doc gettext gobject-introspection systemd

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd polkit', d)}"
PACKAGECONFIG[idevice] = "-Didevice=enabled,-Didevice=disabled,libimobiledevice libplist"
PACKAGECONFIG[systemd] = "-Dsystemdsystemunitdir=${systemd_system_unitdir},-Dsystemdsystemunitdir=no -Dudevrulesdir=${base_libdir}/udev/rules.d,systemd"
PACKAGECONFIG[polkit] = "-Dpolkit=enabled,-Dpolkit=disabled,polkit"

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
    ${datadir}/polkit-1/ \
    ${base_libdir}/udev/* \
"

do_install:append() {
    # remove integration tests scripts
    rm -rf ${D}${datadir}/installed-tests
    rm -rf ${D}${libexecdir}/upower
}
