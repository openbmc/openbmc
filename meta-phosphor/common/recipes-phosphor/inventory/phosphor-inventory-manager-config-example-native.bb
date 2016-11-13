SUMMARY = "Managed inventory with phosphor inventory manager example"
PR = "r1"

inherit native

require phosphor-inventory-manager.inc

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}${datadir}/phosphor-inventory-manager/
        cp -r ${S}/example/* ${D}${datadir}/phosphor-inventory-manager/
}
