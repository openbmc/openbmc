SUMMARY = "Management Component Control Protocol utilities"
HOMEPAGE = "http://www.github.com/CodeConstruct/mctp"
SECTION = "net"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4cc91856b08b094b4f406a29dc61db21"

PV = "1.0+git${SRCPV}"

SRCREV = "669740432af525c19a6a41cec777406fbbc24836"

SRC_URI = "git://github.com/CodeConstruct/mctp;branch=main;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"

# mctpd will only be built if pkg-config detects libsystemd; in which case
# we'll want to declare the dep and install the service.
PACKAGECONFIG[systemd] = ",,systemd,libsystemd"
SYSTEMD_SERVICE:${PN} = "mctpd.service"

do_install:append () {
    if ${@bb.utils.contains('PACKAGECONFIG', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${S}/conf/mctpd.service \
                ${D}${systemd_system_unitdir}/mctpd.service
        install -d ${D}${datadir}/dbus-1/system.d
        install -m 0644 ${S}/conf/mctpd-dbus.conf \
                ${D}${datadir}/dbus-1/system.d/mctpd.conf
    fi
}

FILES:${PN} += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', \
            '${datadir}/dbus-1/system.d/mctpd.conf', '', d)} \
"
