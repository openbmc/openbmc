SUMMARY = "The Geolocation Service"
DESCRIPTION = "Geoclue is a D-Bus service that provides location information. \
The primary goal of the Geoclue project is to make creating location-aware applications \
as simple as possible, while the secondary goal is to ensure that no application \
can access location information without explicit permission from user."
LICENSE = "GPLv2.0+"
SECTION = "console/network"

LIC_FILES_CHKSUM = "file://COPYING;md5=8114b83a0435d8136b47bd70111ce5cd"

DEPENDS = "glib-2.0 dbus json-glib libsoup-2.4 intltool-native gobject-introspection-native"

inherit autotools pkgconfig gtk-doc

SRC_URI = " \
    http://www.freedesktop.org/software/geoclue/releases/2.4/geoclue-${PV}.tar.xz \
"

SRC_URI[md5sum] = "d2a5b05f4bad032673fe23afbce27926"
SRC_URI[sha256sum] = "9c43fb9d0c12067ea64400500abb0640194947d4c2c55e38545afe5d9c5c315c"

export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

# Without this line, package is delcared a library and named libgeoclue*
AUTO_LIBNAME_PKGS = ""

PACKAGECONFIG ??= "3g modem-gps cdma nmea lib"
PACKAGECONFIG[3g] = "--enable-3g-source,--disable-3g-source,modemmanager"
PACKAGECONFIG[modem-gps] = "--enable-modem-gps-source,--disable-modem-gps-source,modemmanager"
PACKAGECONFIG[cdma] = "--enable-cdma-source,--disable-cdma-source,modemmanager"
PACKAGECONFIG[nmea] = "--enable-nmea-source,--disable-nmea-source,avahi"
PACKAGECONFIG[lib] = "--enable-libgeoclue,--disable-libgeoclue,gobject-introspection"

EXTRA_OECONF += " \
    --with-dbus-service-user=root \
    --with-dbus-sys-dir=${sysconfdir}/dbus-1/system.d \
    --enable-demo-agent=no \
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
