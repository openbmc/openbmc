SUMMARY = "The Geolocation Service"
DESCRIPTION = "Geoclue is a D-Bus service that provides location information. \
The primary goal of the Geoclue project is to make creating location-aware applications \
as simple as possible, while the secondary goal is to ensure that no application \
can access location information without explicit permission from user."
LICENSE = "GPL-2.0-or-later"
SECTION = "console/network"

LIC_FILES_CHKSUM = "file://COPYING;md5=bdfdd4986a0853eb84eeba85f9d0c4d6"

DEPENDS = "glib-2.0 dbus json-glib libsoup-3.0 intltool-native"

inherit meson pkgconfig gtk-doc gobject-introspection vala useradd

SRCREV = "8a24f60969d4c235d9918796c38a6a9c42e10131"
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

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --home-dir ${sysconfdir}/polkit-1 --shell /bin/nologin polkitd"

do_install:append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'modem-gps', 'true', 'false', d)}; then
        # Fix up permissions on polkit rules.d to work with rpm4 constraints
        chmod 700 ${D}/${datadir}/polkit-1/rules.d
        chown polkitd:root ${D}/${datadir}/polkit-1/rules.d
    fi
}

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
