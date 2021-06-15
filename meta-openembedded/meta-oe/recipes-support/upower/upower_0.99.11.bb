DESCRIPTION = "UPower is an abstraction for enumerating power devices, listening to device events and querying history and statistics. "
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0de8fbf1d97a140d1d93b9f14dcfbf08"

DEPENDS = "intltool-native libusb1 libgudev glib-2.0 dbus-glib"

SRC_URI = " \
    http://upower.freedesktop.org/releases/${BPN}-${PV}.tar.xz \
"
SRC_URI[md5sum] = "abe6acb617f11f2e8dbd9846fcf86e24"
SRC_URI[sha256sum] = "64b5ffbfccd5bdb15d925777979a4dbee1a957f9eaeb158dc76175267eddbdef"

inherit autotools pkgconfig gettext gobject-introspection systemd

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[idevice] = "--with-idevice,--without-idevice,libimobiledevice libplist"
PACKAGECONFIG[systemd] = "--with-systemdutildir=${systemd_unitdir} --with-systemdsystemunitdir=${systemd_system_unitdir}, \
                          --without-systemdutildir --without-systemdsystemunitdir,systemd"

EXTRA_OECONF = " --with-backend=linux"

SYSTEMD_SERVICE_${PN} = "upower.service"
# don't start on boot by default - dbus does that on demand
SYSTEMD_AUTO_ENABLE = "disable"

do_configure_prepend() {
    sed -i -e s:-nonet:\:g ${S}/doc/man/Makefile.am
    sed -i -e 's: doc : :g' ${S}/Makefile.am
}

RDEPENDS_${PN} += "dbus"
RRECOMMENDS_${PN} += "pm-utils"
FILES_${PN} += "${datadir}/dbus-1/ \
                ${base_libdir}/udev/* \
"
