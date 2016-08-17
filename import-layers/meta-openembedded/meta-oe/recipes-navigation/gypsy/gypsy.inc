SUMMARY = "GPS Multiplexing Daemon"
DESCRIPTION = "Gypsy is a GPS multiplexing daemon which allows \
multiple clients to access GPS data from multiple GPS sources \
concurrently.  Gypsy also hides the details of parsing NMEA from the \
client applications, passing the data as simple values for the clients \
to use."
LICENSE = "GPLv2.0 & LGPLv2.1"
SECTION = "console/network"
DEPENDS = "glib-2.0 dbus bluez4 dbus-glib libxslt-native libxslt"

PNBLACKLIST[gypsy] ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5 conflicts with bluez4 and bluez5 is selected in DISTRO_FEATURES', '', d)}"

inherit autotools pkgconfig gtk-doc
PACKAGES += "libgypsy"

FILES_${PN} = " \
  ${sysconfdir}/gypsy.conf \
  ${sysconfdir}/dbus-1 \
  ${libexecdir}/gypsy-daemon \
  ${datadir}/dbus-1 \
"

FILES_libgypsy = " \
  ${libdir}/libgypsy${SOLIBS} \
"
