FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

NAMES = "io_board motherboard"
ITEMS = "${@compose_list_zip(d, 'system/chassis/[0]', 'NAMES')}"
ITEMS_ESCAPED = "${@compose_list_zip(d, 'system-chassis-[0]', 'NAMES')}"

SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list_zip(d, 'obmc/sync_inventory_item/[0]', 'ITEMS')}"
SYSTEMD_GENLINKS_${PN} += "../obmc-sync-inventory-item@.service:${SYSTEMD_DEFAULT_TARGET}.wants/obmc-sync-inventory-item@[0].service:ITEMS_ESCAPED"
