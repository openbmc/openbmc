SUMMARY = "dbus broker"
DESCRIPTION = "Drop-in replacement for dbus-daemon."

SECTION = "base"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b486c2338d225a1405d979ed2c15ce8"

SRC_URI = "https://github.com/bus1/dbus-broker/releases/download/v${PV}/dbus-broker-${PV}.tar.xz"
SRC_URI[sha256sum] = "bea7f653e7251063c5f427e9e3f93562d38a0d8667ae6d49fb56f113605985de"

UPSTREAM_CHECK_URI = "https://github.com/bus1/${BPN}/releases"

inherit meson pkgconfig systemd features_check

DEPENDS = "expat systemd"
DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'libselinux (>= 3.2)', '', d)}"
DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'audit (>= 3.0)', '', d)}"

RDEPENDS:${PN} += "dbus-common"

REQUIRED_DISTRO_FEATURES = "systemd"

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${systemd_user_unitdir}"
FILES:${PN} += "${nonarch_libdir}/systemd/catalog"

EXTRA_OEMESON += " -Dselinux=${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}"
EXTRA_OEMESON += " -Daudit=${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}"

