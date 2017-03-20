SUMMARY = "FRU properties config for ipmi-fru-parser"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-fru

SRC_URI += "file://extra-properties.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-properties"

S = "${WORKDIR}"

do_install() {

        DEST=${D}${properties_datadir}

        install -d ${DEST}
        install extra-properties.yaml ${DEST}/out.yaml
}

