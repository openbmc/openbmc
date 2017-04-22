SUMMARY = "Generate fan presence YAML from the MRW"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan

DEPENDS += " \
           mrw-native \
           mrw-perl-tools-native \
           "

PROVIDES += "virtual/phosphor-fan-presence-config"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${presence_datadir}
        install -d ${DEST}

        ${bindir}/perl-native/perl \
            ${bindir}/gen_presence_yaml.pl \
            -i ${datadir}/obmc-mrw/${MACHINE}.xml \
            -o ${DEST}/config.yaml
}
