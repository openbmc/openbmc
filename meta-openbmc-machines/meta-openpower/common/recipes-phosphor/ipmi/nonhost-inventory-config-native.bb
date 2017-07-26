SUMMARY = "Non hostfw inventory map for phosphor-ipmi-host"
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-read-nonhost-config"
S = "${WORKDIR}"

do_install() {
    DEST=${D}${nonhostfw_datadir}
    install -d ${DEST}
    install config.yaml ${DEST}
}
