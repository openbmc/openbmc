FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

INSTANCES = "motherboard/uuid motherboard/bmc/ethernet"
ITEMS_FMT = "system/chassis/{0}"

ITEMS = "${@compose_list(d, 'ITEMS_FMT', 'INSTANCES')}"

ENV_FILES = "obmc/sync_inventory_item/{0}"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "${@compose_list(d, 'ENV_FILES', 'ITEMS')}"

TMPL = "obmc-sync-inventory-item@.service"
TGT = "${SYSTEMD_DEFAULT_TARGET}"

ETH_SVC = "system-chassis-motherboard-bmc-ethernet.service"
UUID_SVC = "system-chassis-motherboard-uuid.service"

ETH_DROPIN_DIR = "obmc-sync-inventory-item@${ETH_SVC}.d"
UUID_DROPIN_DIR = "obmc-sync-inventory-item@${UUID_SVC}.d"

SYSTEMD_OVERRIDE_${PN} += "mac_sync_inventory.conf:${ETH_DROPIN_DIR}/mac_sync_inventory.conf"
SYSTEMD_OVERRIDE_${PN} += "uuid_sync_inventory.conf:${UUID_DROPIN_DIR}/uuid_sync_inventory.conf"

SYSTEMD_LINK_${PN} += "../${TMPL}:${TGT}.wants/obmc-sync-inventory-item@${ETH_SVC}"
SYSTEMD_LINK_${PN} += "../${TMPL}:${TGT}.wants/obmc-sync-inventory-item@${UUID_SVC}"
