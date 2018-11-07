SUMMARY = "Copy Witherspoon inventory cleanup yaml for inventory manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-inventory-manager

SRC_URI += "file://inventory-cleanup.yaml"

S = "${WORKDIR}"

do_install() {
        install -d ${D}${base_datadir}/events.d/
        install ${S}/inventory-cleanup.yaml ${D}${base_datadir}/events.d/inventory-cleanup.yaml
}
