DESCRIPTION = "UPower is an abstraction for enumerating power devices, listening to device events and querying history and statistics. "
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0de8fbf1d97a140d1d93b9f14dcfbf08"

DEPENDS = "intltool-native libusb1 libgudev glib-2.0 dbus-glib polkit"

SRC_URI = "http://upower.freedesktop.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "78800e1ac7f92b24aabdf433e38f75d2"
SRC_URI[sha256sum] = "9ca325a6ccef505529b268ebbbd9becd0ce65a65f6ac7ee31e2e5b17648037b0"

inherit autotools pkgconfig gettext gobject-introspection systemd

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
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
                ${datadir}/polkit-1/ \
                ${base_libdir}/udev/* \
"

FILES_${PN}-dbg += "${base_libdir}/udev/.debug"



