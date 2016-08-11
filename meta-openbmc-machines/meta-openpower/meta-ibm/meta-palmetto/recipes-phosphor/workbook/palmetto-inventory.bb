SUMMARY = "Palmetto System Inventory"
DESCRIPTION = "Provides the inventory file for Palmetto"
PR = "r1"

inherit obmc-phosphor-license
inherit allarch

RPROVIDES_${PN} = "virtual-system-inventory-data"

SRC_URI += "file://palmetto_inventory.json"

FILES_${PN} += "${datadir}/inventory"

do_install() {
    install -d ${D}${datadir}/inventory
    install -m 0644 palmetto_inventory.json ${D}${datadir}/inventory/inventory.json
}

S = "${WORKDIR}"
