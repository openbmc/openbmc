FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

NAMES = "io_board/bmc/ethernet motherboard"
ITEMSFMT = "system/chassis/{0}"
ITEMS_ESCAPEDFMT = "system-chassis-{0}"

ITEMS = "${@compose_list(d, 'ITEMSFMT', 'NAMES')}"
ITEMS_ESCAPED = "${@compose_list(d, 'ITEMS_ESCAPEDFMT', 'NAMES')}"

ENVS = "obmc/sync_inventory_item/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENVS', 'ITEMS')}"

TMPL = "obmc-sync-inventory-item@.service"
TGT = "${SYSTEMD_DEFAULT_TARGET}"
INSTFMT = "obmc-sync-inventory-item@{0}.service"
FMT = "../${TMPL}:${TGT}.wants/${INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'ITEMS_ESCAPED')}"
