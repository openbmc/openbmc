SUMMARY = "The Geolocation Service"
DESCRIPTION = "Geoclue is a D-Bus service that provides location information. \
The primary goal of the Geoclue project is to make creating location-aware applications \
as simple as possible, while the secondary goal is to ensure that no application \
can access location information without explicit permission from user."
LICENSE = "GPL-2.0-or-later"
SECTION = "console/network"

LIC_FILES_CHKSUM = "file://COPYING;md5=bdfdd4986a0853eb84eeba85f9d0c4d6"

DEPENDS = "glib-2.0 dbus json-glib libsoup-3.0 intltool-native"

inherit meson pkgconfig gtk-doc gobject-introspection vala

SRCREV = "ab0a7a447ac037d5043aa04df3030796bf47d94d"
SRC_URI = "git://gitlab.freedesktop.org/geoclue/geoclue.git;protocol=https;branch=master \
    file://0001-libgeoclue-don-t-try-to-use-g-ir-scanner-when-intros.patch \
"

S = "${WORKDIR}/git"

# Without this line, package is declared a library and named libgeoclue*
AUTO_LIBNAME_PKGS = ""

PACKAGECONFIG ??= "3g modem-gps cdma nmea lib agent"
PACKAGECONFIG[3g] = "-D3g-source=true,-D3g-source=false,modemmanager"
PACKAGECONFIG[modem-gps] = "-Dmodem-gps-source=true,-Dmodem-gps-source=false,modemmanager"
PACKAGECONFIG[cdma] = "-Dcdma-source=true,-Dcdma-source=false,modemmanager"
PACKAGECONFIG[nmea] = "-Dnmea-source=true,-Dnmea-source=false,avahi,avahi-daemon"
PACKAGECONFIG[lib] = "-Dlibgeoclue=true,-Dlibgeoclue=false"
PACKAGECONFIG[agent] = "-Ddemo-agent=true,-Ddemo-agent=false,libnotify"

GTKDOC_MESON_OPTION = "gtk-doc"

EXTRA_OEMESON += " \
    -Ddbus-sys-dir=${sysconfdir}/dbus-1/system.d \
"

FILES:${PN} += " \
    ${datadir}/dbus-1/system-services \
    ${datadir}/polkit-1/rules.d \
    ${libdir} \
    ${systemd_unitdir} \
    ${prefix}/libexec \
"

FILES:${PN}-dev += " \
    ${datadir}/dbus-1/interfaces \
    ${datadir}/gir-1.0 \
"
