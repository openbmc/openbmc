SUMMARY = "Vesnin IPMI to DBus Inventory mapping."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${YADROBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-fru

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-inventory"

S = "${WORKDIR}"

do_install() {
        # TODO: install this to inventory_datadir
        # after ipmi-fru-parser untangles the host
        # firmware config from the machine inventory.
        DEST=${D}${config_datadir}

        install -d ${DEST}
        install config.yaml ${DEST}
}
