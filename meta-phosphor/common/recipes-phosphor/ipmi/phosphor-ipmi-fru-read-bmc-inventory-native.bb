SUMMARY = "BMC accesible FRU inventory map for phosphor-ipmi-host"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-host

SRC_URI += "file://bmc-fru-config.yaml"

S = "${WORKDIR}"

PROVIDES += "virtual/phosphor-ipmi-fru-read-bmc-inventory"

do_install_append() {
    DEST=${D}${config_datadir}
    install -d ${DEST}
    install bmc-fru-config.yaml ${DEST}/bmc-fru-config.yaml
}

