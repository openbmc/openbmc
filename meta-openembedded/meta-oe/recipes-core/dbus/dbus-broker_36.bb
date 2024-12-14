SUMMARY = "dbus broker"
DESCRIPTION = "Drop-in replacement for dbus-daemon."
SECTION = "base"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b486c2338d225a1405d979ed2c15ce8"
DEPENDS = "\
    expat \
    systemd \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'audit (>= 3.0)', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'libselinux (>= 3.2)', '', d)} \
"

SRC_URI = "https://github.com/bus1/dbus-broker/releases/download/v${PV}/${BP}.tar.xz"
SRC_URI[sha256sum] = "d333d99bd2688135b6d6961e7ad1360099d186078781c87102230910ea4e162b"

UPSTREAM_CHECK_URI = "https://github.com/bus1/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+)"

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

inherit meson pkgconfig systemd features_check

EXTRA_OEMESON += "-Daudit=${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}"
EXTRA_OEMESON += "-Dselinux=${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'true', 'false', d)}"

REQUIRED_DISTRO_FEATURES = "systemd"

do_install:append() {
    install -d ${D}${sysconfdir}/systemd/user
    ln -s ${systemd_user_unitdir}/dbus-broker.service ${D}${sysconfdir}/systemd/user/dbus.service
}

RDEPENDS:${PN} += "dbus-common"

FILES:${PN} += "${nonarch_libdir}/systemd/catalog"
FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${systemd_user_unitdir}"
