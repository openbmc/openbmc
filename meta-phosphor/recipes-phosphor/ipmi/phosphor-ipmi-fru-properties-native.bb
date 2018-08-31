SUMMARY = "FRU properties config for ipmi-fru-parser"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-fru

SRC_URI += "file://extra-properties.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-properties"

S = "${WORKDIR}"

do_install() {
        # This recipe is supposed to create an output yaml file with
        # FRU property values extracted from the MRW. This example recipe
        # provides a sample output file.

        DEST=${D}${properties_datadir}
        install -d ${DEST}
        install extra-properties.yaml ${DEST}
}
