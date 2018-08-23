SUMMARY = "Romulus inventory map for phosphor-ipmi-host"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-host

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-read-inventory"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${config_datadir}
        install -d ${DEST}
        install config.yaml ${DEST}
}
