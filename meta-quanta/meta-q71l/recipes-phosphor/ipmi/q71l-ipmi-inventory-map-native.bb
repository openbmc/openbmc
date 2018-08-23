SUMMARY = "Quanta-Q71l IPMI to DBus Inventory mapping."
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-fru

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-inventory"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${config_datadir}

        install -d ${DEST}
        install config.yaml ${DEST}
}
