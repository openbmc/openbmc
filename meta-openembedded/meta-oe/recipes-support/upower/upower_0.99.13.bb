DESCRIPTION = "UPower is an abstraction for enumerating power devices, listening to device events and querying history and statistics. "
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0de8fbf1d97a140d1d93b9f14dcfbf08"

DEPENDS = "intltool-native libusb1 libgudev glib-2.0 dbus-glib"

SRC_URI = "git://gitlab.freedesktop.org/upower/upower.git;protocol=https;branch=master"
SRCREV = "0f6cc0a10be22d7ddd684e1cd851e4364a440494"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "UPOWER_(?P<pver>\d+(\_\d+)+)"

inherit autotools pkgconfig gtk-doc gettext gobject-introspection systemd

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[idevice] = "--with-idevice,--without-idevice,libimobiledevice libplist"
PACKAGECONFIG[systemd] = "--with-systemdutildir=${systemd_unitdir} --with-systemdsystemunitdir=${systemd_system_unitdir}, \
                          --without-systemdutildir --without-systemdsystemunitdir,systemd"

EXTRA_OECONF = " --with-backend=linux"

SYSTEMD_SERVICE:${PN} = "upower.service"
# don't start on boot by default - dbus does that on demand
SYSTEMD_AUTO_ENABLE = "disable"

do_configure:prepend() {
    touch ${S}/ABOUT-NLS
    mkdir -p ${S}/build-aux
    touch ${S}/build-aux/config.rpath
    sed -i -e s:-nonet:\:g ${S}/doc/man/Makefile.am
    sed -i -e 's: doc : :g' ${S}/Makefile.am
}

RDEPENDS:${PN} += "dbus"
RRECOMMENDS:${PN} += "pm-utils"
FILES:${PN} += "${datadir}/dbus-1/ \
                ${base_libdir}/udev/* \
"
