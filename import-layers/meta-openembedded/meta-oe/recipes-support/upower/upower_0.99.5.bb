DESCRIPTION = "UPower is an abstraction for enumerating power devices, listening to device events and querying history and statistics. "
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0de8fbf1d97a140d1d93b9f14dcfbf08"

DEPENDS = "intltool-native libusb1 libgudev glib-2.0 dbus-glib polkit"

SRC_URI = "http://upower.freedesktop.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "ec57b4b7bf0af568f9a7a5603c921d97"
SRC_URI[sha256sum] = "78605664d027c788f6ab63c50950be6e86c6ba5d030c4cf35a6664337d87f3b2"

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
                ${datadir}/polkit-1/ \
                ${base_libdir}/udev/* \
"

FILES_${PN}-dbg += "${base_libdir}/udev/.debug"



