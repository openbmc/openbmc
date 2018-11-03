SUMMARY = "Recipe to create AssetTag property in inventory manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-inventory-manager

PROVIDES += "virtual/phosphor-inventory-manager-assettag"

SRC_URI += "file://assettag.yaml"

S = "${WORKDIR}"

do_install() {
        # This recipe would provide the yaml for inventory manager to
        # create AssetTag property at startup

        install -d ${D}${base_datadir}/events.d/
        install assettag.yaml ${D}${base_datadir}/events.d/assettag.yaml
}
