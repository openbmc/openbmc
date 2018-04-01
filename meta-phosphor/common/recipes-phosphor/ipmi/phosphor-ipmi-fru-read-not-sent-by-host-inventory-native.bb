SUMMARY = "The inventory map of frus not sent by host for phosphor-ipmi-host"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-host

SRC_URI += "file://fru-config-not-sent-by-host.yaml"

S = "${WORKDIR}"

PROVIDES += "virtual/phosphor-ipmi-fru-read-not-sent-by-host-inventory"

do_install_append() {
    DEST=${D}${config_datadir}
    install -d ${DEST}
    install fru-config-not-sent-by-host.yaml ${DEST}/fru-config-not-sent-by-host.yaml
}

