SUMMARY = "The Geolocation Service"
DESCRIPTION = "Geoclue is a D-Bus service that provides location information. \
The primary goal of the Geoclue project is to make creating location-aware applications \
as simple as possible, while the secondary goal is to ensure that no application \
can access location information without explicit permission from user."
LICENSE = "GPLv2.0+"
SECTION = "console/network"

LIC_FILES_CHKSUM = "file://COPYING;md5=bdfdd4986a0853eb84eeba85f9d0c4d6"

DEPENDS = "glib-2.0 dbus json-glib libsoup-2.4 intltool-native"

inherit meson pkgconfig gtk-doc gobject-introspection vala

SRCREV = "3bb60dd600a8b247fd6049e7e5b1c73c2c5fc0e8"
SRC_URI = "git://gitlab.freedesktop.org/geoclue/geoclue.git;protocol=https;nobranch=1"

S = "${WORKDIR}/git"

# Without this line, package is declared a library and named libgeoclue*
AUTO_LIBNAME_PKGS = ""

PACKAGECONFIG ??= "3g modem-gps cdma nmea lib"
PACKAGECONFIG[3g] = "-D3g-source=true,-D3g-source=false,modemmanager"
PACKAGECONFIG[modem-gps] = "-Dmodem-gps-source=true,-Dmodem-gps-source=false,modemmanager"
PACKAGECONFIG[cdma] = "-Dcdma-source=true,-Dcdma-source=false,modemmanager"
PACKAGECONFIG[nmea] = "-Dnmea-source=true,-Dnmea-source=false,avahi,avahi-daemon"
PACKAGECONFIG[lib] = "-Dlibgeoclue=true,-Dlibgeoclue=false,gobject-introspection"

GTKDOC_MESON_OPTION = "gtk-doc"

EXTRA_OEMESON += " \
    -Ddbus-sys-dir=${sysconfdir}/dbus-1/system.d \
    -Ddemo-agent=false \
"

FILES_${PN} += " \
    ${datadir}/dbus-1/system-services \
    ${libdir} \
    ${systemd_unitdir} \
    ${prefix}/libexec \
"

FILES_${PN}-dev += " \
    ${datadir}/dbus-1/interfaces \
    ${datadir}/gir-1.0 \
"
