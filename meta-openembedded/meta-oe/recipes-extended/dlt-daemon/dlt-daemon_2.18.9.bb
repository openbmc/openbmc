SUMMARY = "Diagnostic Log and Trace"
DESCRIPTION = "This component provides a standardised log and trace interface, \
based on the standardised protocol specified in the AUTOSAR standard 4.0 DLT. \
This component can be used by GENIVI components and other applications as \
logging facility providing: \
- the DLT shared library \
- the DLT daemon, including startup scripts \
- the DLT daemon adaptors- the DLT client console utilities \
- the DLT test applications"
HOMEPAGE = "https://www.genivi.org/"
SECTION = "console/utils"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8184208060df880fe3137b93eb88aeea"

DEPENDS = "zlib gzip-native json-c"

SRC_URI = "git://github.com/GENIVI/${BPN}.git;protocol=https;branch=master \
           file://0002-Don-t-execute-processes-as-a-specific-user.patch \
           file://0004-Modify-systemd-config-directory.patch \
           file://481.patch \
           file://482.patch \
           "
SRCREV = "9a2312d3512a27620d41b9a325338b6e7b3d42de"

S = "${WORKDIR}/git"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' systemd systemd-watchdog systemd-journal dlt-examples dlt-adaptor dlt-adaptor-stdin dlt-adaptor-udp dlt-console ', '', d)} \
 udp-connection dlt-system dlt-filetransfer "
# dlt-dbus

# General options
PACKAGECONFIG[dlt-examples] = "-DWITH_DLT_EXAMPLES=ON,-DWITH_DLT_EXAMPLES=OFF,,dlt-daemon-systemd"

# Linux options
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=ON,-DWITH_SYSTEMD=OFF -DWITH_DLT_SYSTEM=OFF,systemd"
PACKAGECONFIG[systemd-watchdog] = "-DWITH_SYSTEMD_WATCHDOG=ON,-DWITH_SYSTEMD_WATCHDOG=OFF,systemd,libsystemd"
PACKAGECONFIG[systemd-journal] = "-DWITH_SYSTEMD_JOURNAL=ON,-DWITH_SYSTEMD_JOURNAL=OFF,systemd,libsystemd"
PACKAGECONFIG[dlt-dbus] = "-DWITH_DLT_DBUS=ON,-DWITH_DLT_DBUS=OFF,dbus,dbus-lib"
PACKAGECONFIG[udp-connection] = "-DWITH_UDP_CONNECTION=ON,-DWITH_UDP_CONNECTION=OFF"

# Command line options
PACKAGECONFIG[dlt-system] = "-DWITH_DLT_SYSTEM=ON,-DWITH_DLT_SYSTEM=OFF"
PACKAGECONFIG[dlt-adaptor] = "-DWITH_DLT_ADAPTOR=ON,-DWITH_DLT_ADAPTOR=OFF,,dlt-daemon-systemd"
PACKAGECONFIG[dlt-adaptor-stdin] = "-DWITH_DLT_ADAPTOR_STDIN=ON,-DWITH_DLT_ADAPTOR_STDIN=OFF,,dlt-daemon-systemd"
PACKAGECONFIG[dlt-adaptor-udp] = "-DWITH_DLT_ADAPTOR_UDP=ON,-DWITH_DLT_ADAPTOR_UDP=OFF,,dlt-daemon-systemd"
PACKAGECONFIG[dlt-filetransfer] = "-DWITH_DLT_FILETRANSFER=ON,-DWITH_DLT_FILETRANSFER=OFF"
PACKAGECONFIG[dlt-console] = "-DWITH_DLT_CONSOLE=ON,-DWITH_DLT_CONSOLE=OFF,,dlt-daemon-systemd"

inherit autotools gettext cmake pkgconfig systemd

EXTRA_OECMAKE += "-DWITH_EXTENDED_FILTERING=ON -DSYSTEMD_UNITDIR=${systemd_system_unitdir}"

PACKAGES += "${PN}-systemd"
SYSTEMD_PACKAGES = "${PN} ${PN}-systemd"
SYSTEMD_SERVICE:${PN} = " ${@bb.utils.contains('PACKAGECONFIG', 'systemd', 'dlt.service', '', d)} \
                          ${@bb.utils.contains('PACKAGECONFIG', 'dlt-system', 'dlt-system.service', '', d)} \
                          ${@bb.utils.contains('PACKAGECONFIG', 'dlt-dbus', 'dlt-dbus.service', '', d)}"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
SYSTEMD_SERVICE:${PN}-systemd = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'dlt-adaptor-udp', 'dlt-adaptor-udp.service', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'dlt-examples', 'dlt-example-user.service', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'dlt-examples dlt-console', 'dlt-receive.service', '', d)} \
"
SYSTEMD_AUTO_ENABLE:${PN}-systemd = "disable"

FILES:${PN}-doc += "${datadir}/dlt-filetransfer"

do_install:append() {
    rm -f ${D}${bindir}/dlt-test-*
}
