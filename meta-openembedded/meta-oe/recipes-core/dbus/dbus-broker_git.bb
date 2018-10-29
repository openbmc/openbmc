SUMMARY = "dbus broker"
DESCRIPTION = "Drop-in replacement for dbus-daemon."

SECTION = "base"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b486c2338d225a1405d979ed2c15ce8"

DEPENDS = "dbus glib-2.0 expat"

PV = "16+git${SRCPV}"
SRCREV = "fc874afa0992d0c75ec25acb43d344679f0ee7d2"

SRC_URI = "gitsm://github.com/bus1/dbus-broker;protocol=git"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd distro_features_check

EXTRA_OEMESON += "-Ddocs=false"

REQUIRED_DISTRO_FEATURES = "systemd"

SYSTEMD_SERVICE_${PN} = "${BPN}.service"

FILES_${PN} += "${systemd_system_unitdir}"
FILES_${PN} += "${systemd_user_unitdir}"

RDEPENDS_${PN} = "dbus"

BBCLASSEXTEND = "native"

