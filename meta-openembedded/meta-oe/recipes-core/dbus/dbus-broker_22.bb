SUMMARY = "dbus broker"
DESCRIPTION = "Drop-in replacement for dbus-daemon."

SECTION = "base"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b486c2338d225a1405d979ed2c15ce8"

SRC_URI = "https://github.com/bus1/dbus-broker/releases/download/v${PV}/dbus-broker-${PV}.tar.xz"
SRC_URI[md5sum] = "03c4730f2110b5492e1212c81af325dc"
SRC_URI[sha256sum] = "32f30700cefc997c479d75fcc42f3177940f1ebbee9c5a60c1e3ee39b90a9783"

UPSTREAM_CHECK_URI = "https://github.com/bus1/${BPN}/releases"

inherit meson pkgconfig systemd features_check

DEPENDS = "expat systemd"
DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'libselinux', '', d)}"
DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'audit', '', d)}"

REQUIRED_DISTRO_FEATURES = "systemd"

SYSTEMD_SERVICE_${PN} = "${BPN}.service"

FILES_${PN} += "${systemd_system_unitdir}"
FILES_${PN} += "${systemd_user_unitdir}"
FILES_${PN} += "${nonarch_libdir}/systemd/catalog"

EXTRA_OEMESON += " -Dselinux=${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}"
EXTRA_OEMESON += " -Daudit=${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}"

