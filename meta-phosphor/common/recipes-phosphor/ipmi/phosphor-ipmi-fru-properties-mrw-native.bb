SUMMARY = "FRU properties config for ipmi-fru-parser"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-fru

SRC_URI += "file://config.yaml"

DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           "

PROVIDES += "virtual/phosphor-ipmi-fru-properties"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${properties_datadir}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_fru_properties.pl \
            -m ${datadir}/obmc-mrw/${MACHINE}.xml \
            -c config.yaml \
            -o ${DEST}/out.yaml
}
