LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

S = "${UNPACKDIR}"

RDEPENDS:${PN} += "bash"

SRC_URI += " \
    file://marvell-switch-init.service \
    file://marvell-switch-init \
    file://marvell-switch-poe-init.service \
    file://marvell-switch-poe-init \
    file://sgpio-state-init \
    file://sgpio-state-init.service \
    file://99-sgpio-state-init.rules \
    file://ventura2-fan-status-monitor \
    file://ventura2-fan-status-monitor.service \
    file://ventura2-sys-init.service \
    file://ventura2-early-sys-init \
    file://ncsi-state \
    file://ncsi-state.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN}:append = " \
    marvell-switch-init.service \
    marvell-switch-poe-init.service \
    sgpio-state-init.service \
    ventura2-fan-status-monitor.service \
    ventura2-sys-init.service \
    ncsi-state.service \
    "

do_install() {
    LIBEXECDIR_PN="${D}${libexecdir}/${PN}"
    install -d ${LIBEXECDIR_PN}
    install -m 0755 ${UNPACKDIR}/marvell-switch-init ${LIBEXECDIR_PN}
    install -m 0755 ${UNPACKDIR}/marvell-switch-poe-init ${LIBEXECDIR_PN}
    install -m 0755 ${UNPACKDIR}/sgpio-state-init ${LIBEXECDIR_PN}
    install -m 0755 ${UNPACKDIR}/ncsi-state ${LIBEXECDIR_PN}

    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/99-sgpio-state-init.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0755 ${UNPACKDIR}/ventura2-fan-status-monitor ${LIBEXECDIR_PN}
    install -m 0755 ${UNPACKDIR}/ventura2-early-sys-init ${LIBEXECDIR_PN}
}
