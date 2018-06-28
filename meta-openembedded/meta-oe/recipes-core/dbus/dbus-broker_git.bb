SUMMARY = "dbus broker"
DESCRIPTION = "Drop-in replacement for dbus-daemon."

SECTION = "base"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b486c2338d225a1405d979ed2c15ce8"

DEPENDS = "dbus glib-2.0 expat"

PV = "13+git${SRCPV}"
SRCREV = "1165025e26c3b46160402841dadf08d3d42f5cbb"

SRC_URI = "git://github.com/bus1/dbus-broker;protocol=git \
           file://0001-Include-sys-wait.h-for-WEXITED-definition.patch \
           file://0002-Use-getenv-instead-of-secure_getenv-on-musl.patch \
           "

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd distro_features_check

EXTRA_OEMESON += "-Ddocs=false"

REQUIRED_DISTRO_FEATURES = "systemd"

SYSTEMD_SERVICE_${PN} = "${BPN}.service"

FILES_${PN} += "${systemd_system_unitdir}"
FILES_${PN} += "${systemd_user_unitdir}"

RDEPENDS_${PN} = "dbus"

BBCLASSEXTEND = "native"

