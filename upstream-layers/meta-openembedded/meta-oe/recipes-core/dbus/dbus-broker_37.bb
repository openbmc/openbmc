SUMMARY = "dbus broker"
DESCRIPTION = "Drop-in replacement for dbus-daemon."
SECTION = "base"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b486c2338d225a1405d979ed2c15ce8"
DEPENDS = "\
    expat \
    systemd \
"

SRC_URI = "https://github.com/bus1/dbus-broker/releases/download/v${PV}/${BP}.tar.xz \
           file://0001-test-sockopt-loosen-verification-of-stale-pidfds.patch \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "f819a8db8795fa08c767612e3823fd594694a0990f2543ecf35d6a1a6bf2ab5b"

UPSTREAM_CHECK_URI = "https://github.com/bus1/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+)"

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

inherit meson pkgconfig systemd features_check ptest

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'audit selinux', '', d)} \
                   ${@bb.utils.contains('PTEST_ENABLED', '1', 'test', '', d)}"
PACKAGECONFIG[audit] = "-Daudit=true, -Daudit=false, audit (>= 3.0)"
PACKAGECONFIG[selinux] = "-Dselinux=true, -Dselinux=false, libselinux (>= 3.2)"
PACKAGECONFIG[test] = "-Dtests=true, -Dtests=false"

REQUIRED_DISTRO_FEATURES = "systemd"

do_install:append() {
    install -d ${D}${sysconfdir}/systemd/user
    ln -s ${systemd_user_unitdir}/dbus-broker.service ${D}${sysconfdir}/systemd/user/dbus.service
}

RCONFLICTS:${PN} = "dbus"
RDEPENDS:${PN} += "dbus-common dbus-tools"

FILES:${PN} += "${nonarch_libdir}/systemd/catalog"
FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN} += "${systemd_user_unitdir}"
FILES:${PN}-ptest += "${libdir}/${PN}/tests"

# test-sockopt fails to compile with musl without this flag
CFLAGS:append:libc-musl = "${@bb.utils.contains('PTEST_ENABLED', '1', ' -Wno-error=incompatible-pointer-types ', '', d)}"
