SUMMARY = "Diagnostic Log and Trace"
DESCRIPTION = "This component provides a standardised log and trace interface, \
based on the standardised protocol specified in the AUTOSAR standard 4.0 DLT. \
This component can be used by COVESA components and other applications as \
logging facility providing: \
- the DLT shared library \
- the DLT daemon, including startup scripts \
- the DLT daemon adaptors- the DLT client console utilities \
- the DLT test applications"
HOMEPAGE = "https://www.covesa.global/"
SECTION = "console/utils"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8184208060df880fe3137b93eb88aeea"

DEPENDS = "zlib gzip-native json-c"

SRC_URI = "git://github.com/COVESA/${BPN}.git;protocol=https;branch=master \
           file://0002-Don-t-execute-processes-as-a-specific-user.patch \
           file://0004-Modify-systemd-config-directory.patch \
           file://544.patch \
           "
SRCREV = "0f2d4cfffada6f8448a2cb27995b38eb4271044f"

S = "${WORKDIR}/git"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' systemd systemd-watchdog systemd-journal ', '', d)} \
 dlt-examples dlt-adaptor dlt-adaptor-stdin dlt-adaptor-udp dlt-console \
 udp-connection dlt-system dlt-filetransfer "
# dlt-dbus

# General options
PACKAGECONFIG[dlt-examples] = "-DWITH_DLT_EXAMPLES=ON,-DWITH_DLT_EXAMPLES=OFF"

# Linux options
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=ON,-DWITH_SYSTEMD=OFF -DWITH_DLT_SYSTEM=OFF,systemd"
PACKAGECONFIG[systemd-watchdog] = "-DWITH_SYSTEMD_WATCHDOG=ON,-DWITH_SYSTEMD_WATCHDOG=OFF,systemd,libsystemd"
PACKAGECONFIG[systemd-journal] = "-DWITH_SYSTEMD_JOURNAL=ON,-DWITH_SYSTEMD_JOURNAL=OFF,systemd,libsystemd"
PACKAGECONFIG[dlt-dbus] = "-DWITH_DLT_DBUS=ON,-DWITH_DLT_DBUS=OFF,dbus,dbus-lib"
PACKAGECONFIG[udp-connection] = "-DWITH_UDP_CONNECTION=ON,-DWITH_UDP_CONNECTION=OFF"

# Command line options
PACKAGECONFIG[dlt-system] = "-DWITH_DLT_SYSTEM=ON,-DWITH_DLT_SYSTEM=OFF"
PACKAGECONFIG[dlt-adaptor] = "-DWITH_DLT_ADAPTOR=ON,-DWITH_DLT_ADAPTOR=OFF"
PACKAGECONFIG[dlt-adaptor-stdin] = "-DWITH_DLT_ADAPTOR_STDIN=ON,-DWITH_DLT_ADAPTOR_STDIN=OFF"
PACKAGECONFIG[dlt-adaptor-udp] = "-DWITH_DLT_ADAPTOR_UDP=ON,-DWITH_DLT_ADAPTOR_UDP=OFF"
PACKAGECONFIG[dlt-filetransfer] = "-DWITH_DLT_FILETRANSFER=ON,-DWITH_DLT_FILETRANSFER=OFF"
PACKAGECONFIG[dlt-console] = "-DWITH_DLT_CONSOLE=ON,-DWITH_DLT_CONSOLE=OFF"

inherit autotools gettext cmake pkgconfig systemd

# -DWITH_DLT_COREDUMPHANDLER=ON this feature is too experimental, disable for now
#FILES:${PN} += "${libdir}/sysctl.d"
EXTRA_OECMAKE += "-DWITH_DLT_LOGSTORAGE_GZIP=ON -DWITH_EXTENDED_FILTERING=ON -DSYSTEMD_UNITDIR=${systemd_system_unitdir}"

PACKAGES += "${PN}-systemd"
SYSTEMD_PACKAGES = "${PN} ${PN}-systemd"
SYSTEMD_SERVICE:${PN} = " ${@bb.utils.contains('PACKAGECONFIG', 'systemd', 'dlt.service', '', d)} \
                          ${@bb.utils.contains('PACKAGECONFIG', 'systemd dlt-system', 'dlt-system.service', '', d)} \
                          ${@bb.utils.contains('PACKAGECONFIG', 'systemd dlt-dbus', 'dlt-dbus.service', '', d)}"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
SYSTEMD_SERVICE:${PN}-systemd = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'systemd dlt-adaptor-udp', 'dlt-adaptor-udp.service', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'systemd dlt-examples', 'dlt-example-user.service', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'systemd dlt-examples dlt-console', 'dlt-receive.service', '', d)} \
"
SYSTEMD_AUTO_ENABLE:${PN}-systemd = "disable"

FILES:${PN}-doc += "${datadir}/dlt-filetransfer"

do_install:append() {
    rm -f ${D}${bindir}/dlt-test-*
}
