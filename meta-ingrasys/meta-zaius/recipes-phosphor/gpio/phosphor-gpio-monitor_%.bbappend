FILESEXTRAPATHS_append_zaius := "${THISDIR}/${PN}:"

PCIE_CARD_E2B_INSTANCE = "pcie-card-e2b"

TMPL = "phosphor-gpio-presence@.service"
INSTFMT = "phosphor-gpio-presence@{0}.service"
TGT = "multi-user.target"
FMT = "../${TMPL}:${TGT}.requires/${INSTFMT}"

SYSTEMD_LINK_${PN}-presence_append_zaius += "${@compose_list(d, 'FMT', 'PCIE_CARD_E2B_INSTANCE')}"
SYSTEMD_ENVIRONMENT_FILE_${PN}-presence_append_zaius += "obmc/gpio/phosphor-pcie-card-e2b.conf"
