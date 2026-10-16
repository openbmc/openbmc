SUMMARY = "MCTP interface setup"
DESCRIPTION = "Installs a udev rule that brings up the CPU-facing MCTP-over-I3C interface, plus scripts to discover/rediscover its bus owner"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "mctp"
RDEPENDS:${PN} += "udev"

inherit systemd allarch

FILES:${PN} += "${systemd_system_unitdir}/mctp-cpu-reset-watch@.service"

# i3c bus 1 is the host CPU-facing endpoint; start its reset-watch instance
# automatically at boot.
SYSTEMD_SERVICE:${PN} += "mctp-cpu-reset-watch@1.service"

SRC_URI = " \
    file://mctp-i3c-common.sh \
    file://mctp-net-reset.sh \
    file://mctp-cpu-discover.sh \
    file://mctp-cpu-reset-watch.sh \
    file://mctp-cpu-reset-watch@.service \
    file://99-mctp-intf-setup.rules \
    "

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${sbindir}
    install -m 0644 ${S}/mctp-i3c-common.sh ${D}${sbindir}/mctp-i3c-common.sh
    install -m 0755 ${S}/mctp-net-reset.sh ${D}${sbindir}/mctp-net-reset.sh
    install -m 0755 ${S}/mctp-cpu-discover.sh ${D}${sbindir}/mctp-cpu-discover.sh
    install -m 0755 ${S}/mctp-cpu-reset-watch.sh ${D}${sbindir}/mctp-cpu-reset-watch.sh

    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${S}/99-mctp-intf-setup.rules ${D}${nonarch_base_libdir}/udev/rules.d/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/mctp-cpu-reset-watch@.service ${D}${systemd_system_unitdir}/mctp-cpu-reset-watch@.service
}
