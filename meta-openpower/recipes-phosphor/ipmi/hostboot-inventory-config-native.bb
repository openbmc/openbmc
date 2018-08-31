SUMMARY = "Hostboot hostfw inventory map for phosphor-ipmi-fru"
PR = "r1"

inherit native
inherit phosphor-ipmi-fru
inherit obmc-phosphor-license

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-hostfw-config"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${hostfw_datadir}

        install -d ${DEST}
        install config.yaml ${DEST}
}
