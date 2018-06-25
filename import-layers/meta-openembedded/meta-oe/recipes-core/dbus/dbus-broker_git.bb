SUMMARY = "dbus broker"
DESCRIPTION = "Drop-in replacement for dbus-daemon."

SECTION = "base"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b486c2338d225a1405d979ed2c15ce8"

DEPENDS = "dbus glib-2.0 expat"

PV = "9+git${SRCPV}"
SRCREV = "ccd06b284892182af569e69046262331150e3536"

SRC_URI = "git://github.com/bus1/dbus-broker;protocol=git"
SRC_URI += "file://0001-Comment-rst2man-related-stuff.patch"
SRC_URI += "file://0002-Correct-including-directory-for-conf.patch"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd distro_features_check

REQUIRED_DISTRO_FEATURES = "systemd"

SYSTEMD_SERVICE_${PN} = "${BPN}.service"

FILES_${PN} += "${systemd_system_unitdir}"
FILES_${PN} += "${systemd_user_unitdir}"

RDEPENDS_${PN} = "dbus"

BBCLASSEXTEND = "native"

