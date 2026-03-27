inherit obmc-phosphor-utils
inherit systemd

SRC_URI:append:zaius = " \
    file://obmc/gpio/pcie-card-e2b.conf \
    "

FILESEXTRAPATHS:append:zaius := "${THISDIR}/${PN}:"

PCIE_CARD_E2B_INSTANCE = "pcie-card-e2b"

SYSTEMD_SERVICE_FMT:zaius = "phosphor-gpio-presence@{0}.service"
SYSTEMD_SERVICE:${PN}-presence:append:zaius = " ${@compose_list(d, 'SYSTEMD_SERVICE_FMT', 'PCIE_CARD_E2B_INSTANCE')}"

FILES:${PN}-presence:append:zaius = " \
    ${sysconfdir}/default/obmc/gpio/pcie-card-e2b.conf \
    "

do_install:append:zaius() {
    install -d ${D}${sysconfdir}/default/obmc/gpio/
    install -m 0644 ${UNPACKDIR}/obmc/gpio/pcie-card-e2b.conf \
        ${D}${sysconfdir}/default/obmc/gpio/
}
