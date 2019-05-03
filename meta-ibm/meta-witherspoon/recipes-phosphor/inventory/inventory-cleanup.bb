SUMMARY = "Copy the inventory cleanup yaml for inventory manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit phosphor-inventory-manager

S = "${WORKDIR}"

SRC_URI = "file://inventory-cleanup.yaml"

do_install() {
        install -D inventory-cleanup.yaml ${D}${base_datadir}/events.d/inventory-cleanup.yaml
}

FILES_${PN} += "${base_datadir}/events.d/inventory-cleanup.yaml"
