SUMMARY = "FRU properties config for openpower-vpd-parser"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit openpower-fru-vpd

SRC_URI += "file://example.yaml"

PROVIDES += "virtual/openpower-fru-properties"

S = "${WORKDIR}"

do_install() {
        # This recipe is supposed to create an output yaml file with
        # FRU property values extracted from the MRW. This example recipe
        # provides a sample output file.

        DEST=${D}${properties_datadir}
        install -d ${DEST}
        install example.yaml ${DEST}/out.yaml
}
