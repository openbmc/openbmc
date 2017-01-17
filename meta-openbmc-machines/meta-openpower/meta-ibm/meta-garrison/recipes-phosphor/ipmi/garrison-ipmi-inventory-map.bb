SUMMARY = "Garrison IPMI to DBus Inventory mapping."
PR = "r1"

inherit native
inherit obmc-phosphor-license

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-config"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${datadir}/phosphor-ipmi-fru

        install -d ${DEST}
        install config.yaml ${DEST}
}
