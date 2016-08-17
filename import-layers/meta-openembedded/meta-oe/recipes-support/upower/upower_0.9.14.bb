DESCRIPTION = "UPower is an abstraction for enumerating power devices, listening to device events and querying history and statistics. "
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0de8fbf1d97a140d1d93b9f14dcfbf08"

PR = "r2"

DEPENDS = "intltool-native libusb1 libgudev glib-2.0 dbus-glib polkit"

SRC_URI = "http://upower.freedesktop.org/releases/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "18803f82102d56aac90949d1e4251645"
SRC_URI[sha256sum] = "81eb44c0453b264a737d32f763a31d5b1776f050a47d5be85fc5e9caf874a4c5"

inherit autotools pkgconfig gettext gobject-introspection

PACKAGECONFIG ??= ""
PACKAGECONFIG[idevice] = "--with-idevice,--without-idevice,libimobiledevice libplist"

EXTRA_OECONF = " --with-backend=linux"

do_configure_prepend() {
    sed -i -e s:-nonet:\:g ${S}/doc/man/Makefile.am
    sed -i -e 's: doc : :g' ${S}/Makefile.am
}    


RRECOMMENDS_${PN} += "pm-utils"
FILES_${PN} += "${datadir}/dbus-1/ \
                ${datadir}/polkit-1/ \
                ${base_libdir}/udev/* \
"

FILES_${PN}-dbg += "${base_libdir}/udev/.debug"



