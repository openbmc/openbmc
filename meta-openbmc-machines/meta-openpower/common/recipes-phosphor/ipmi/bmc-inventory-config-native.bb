SUMMARY = "BMC accessible FRU's inventory map for phosphor-ipmi-host"
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-read-bmc-config"
S = "${WORKDIR}"

do_install() {
    DEST=${D}${bmcfw_datadir}
    install -d ${DEST}
    install config.yaml ${DEST}
}
